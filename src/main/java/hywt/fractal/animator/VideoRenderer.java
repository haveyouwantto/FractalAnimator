package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalFrame;
import hywt.fractal.animator.keyframe.FractalScale;
import hywt.fractal.animator.keyframe.KeyframeManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class VideoRenderer {
    private final int width;
    private final int height;
    private final double fps;
    private Interpolator interpolator;
    private List<ScaleIndicator> indicators;

    // Multithreading
    private final ExecutorService service;
    private final BlockingQueue<BufferedImage> framePool;

    private int frameNum;

    public VideoRenderer(int width, int height, double fps) {
        this.width = width;
        this.height = height;
        this.fps = fps;

        indicators = new LinkedList<>();

        int processors = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(processors);
        framePool = new ArrayBlockingQueue<>(processors + 2);
        for (int i = 0; i < processors + 1; i++) {
            framePool.add(new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
        }
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void ffmpegRender(KeyframeManager manager) throws Exception {
        if (interpolator == null) throw new IllegalStateException("Interpolator not set.");
        FFmpegProcess process = new FFmpegProcess(width, height, fps, "", "");
        process.start();

        double startTime = 2;
        double endTime = 2;
        List<Double> initScales = Collections.nCopies((int) (fps * startTime), 0.0);
        renderFrame(initScales, process, manager.get(0));

        for (int i = 0; i < manager.size(); i++) {
            FractalFrame frame = manager.get(i);
            List<Double> scales = new ArrayList<>();
            double zooms = frame.getScale().getZooms() + 1;

            while (interpolator.isInRange(zooms)) {
                double t = frameNum * 1.0 / fps;
                double v = interpolator.get(t);
                if (v > zooms) {
                    break;
                }
                scales.add(v);
                frameNum++;
            }

            if (!scales.isEmpty()) {
                renderFrame(scales, process, frame, manager.get(i + 1));
            }
        }

        List<Double> endScales = Collections.nCopies((int) (fps * endTime), manager.get(manager.size() - 1).getScale().getZooms());
        renderFrame(endScales, process, manager.get(manager.size() - 1));

        process.finish();
    }


    public void renderFrame(List<Double> factors, FFmpegProcess process, FractalFrame... frames)
            throws Exception {
        List<Future<BufferedImage>> futures = new LinkedList<>();

        int baseFactor = factors.get(0).intValue();

        BufferedImage[] images = new BufferedImage[frames.length];
        for (int i = 0; i < images.length; i++) {
            images[i] = frames[i].getImage();
        }

        for (double factor : factors) {
            Callable<BufferedImage> c = () -> {
                BufferedImage buffer = framePool.take();
                Graphics2D g2d = buffer.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                // RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                int bgWidth = buffer.getWidth();
                int bgHeight = buffer.getHeight();


                for (int i = 0; i < images.length; i++) {
                    putImage(images[i], g2d, (factor - baseFactor) - i, bgWidth, bgHeight);
                }

                for (ScaleIndicator indicator: indicators) {
                    indicator.draw(g2d, new FractalScale(factor), width, height);
                }

                return buffer;
            };

            futures.add(service.submit(c));
        }

        for (Future<BufferedImage> future : futures) {
            BufferedImage image = future.get();
            process.writeFrame(image);
            framePool.add(image);
        }
    }

    private void putImage(BufferedImage image, Graphics2D g2d, double factor, int bgWidth, int bgHeight) {
        double scaleFactor = Math.pow(2, factor);

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        double offsetX = (bgWidth - imgWidth * scaleFactor) / 2;
        double offsetY = (bgHeight - imgHeight * scaleFactor) / 2;

        g2d.drawImage(image, new AffineTransform(scaleFactor, 0, 0, scaleFactor, offsetX, offsetY), null);
    }

    public void addScaleIndicator(ScaleIndicator sc){
        indicators.add(sc);
    }

    public void removeScaleIndicator(ScaleIndicator sc){
        indicators.remove(sc);
    }
}
