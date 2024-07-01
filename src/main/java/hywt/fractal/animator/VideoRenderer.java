package hywt.fractal.animator;

import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.keyframe.FractalFrame;
import hywt.fractal.animator.keyframe.FractalScale;
import hywt.fractal.animator.keyframe.KeyframeManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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
    private final List<ScaleIndicator> indicators;

    // Multithreading
    private final ExecutorService service;
    private final BlockingQueue<BufferedImage> framePool;

    private int renderedFrames;
    private boolean finished;
    private double startTime;
    private double endTime;

    private int mergeFrames;
    private FFmpegProcess process;

    public VideoRenderer(int width, int height, double fps) {
        this.width = width;
        this.height = height;
        this.fps = fps;

        indicators = new LinkedList<>();

        int processors = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(processors);
        framePool = new ArrayBlockingQueue<>(processors * 2);
        for (int i = 0; i < processors * 2; i++) {
            framePool.add(new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
        }

        renderedFrames = 0;
        mergeFrames = 4;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public int getMergeFrames() {
        return mergeFrames;
    }

    public void setMergeFrames(int mergeFrames) {
        this.mergeFrames = mergeFrames;
    }

    public void ffmpegRender(KeyframeManager manager, String path) throws Exception {
        ffmpegRender(manager, path, "ffmpeg", new String[]{});
    }

    public void ffmpegRender(KeyframeManager manager, String path, String ffmpeg, String[] add) throws Exception {
        if (interpolator == null) throw new IllegalStateException("Interpolator not set.");
        process = new FFmpegProcess(width, height, fps, ffmpeg, path, add);
        process.start();

        startTime = 2;
        endTime = 2;

        double indicatorScale = width / 1920.0;
        for (ScaleIndicator indicator : indicators) {
            indicator.setScale(indicatorScale);
        }

        List<Double> initScales = Collections.nCopies((int) (fps * startTime), 0.0);
        renderFrame(initScales, process, manager.get(0), manager.get(1), manager.get(2));

        FractalFrame[] fractalFrames = new FractalFrame[mergeFrames];

        int frameNum = 0;
        double currentZoom;

        DataOutputStream fos = new DataOutputStream(new FileOutputStream("timeline.bin"));

        for (int i = 0; i < manager.size(); i++) {
            FractalFrame frame = manager.get(i);
            List<Double> scales = new ArrayList<>();

            while (true) {
                double t = frameNum * 1.0 / fps;
                double v = interpolator.get(t);

                fos.writeDouble(v); // debug

                currentZoom = v;
                if (currentZoom > i + 1 || interpolator.isOutside(t)) break;
                System.out.printf("%.2f %.2f\n", t, v);
                scales.add(v);
                frameNum++;
            }

            fractalFrames[0] = frame;
            for (int j = 1; j < mergeFrames; j++) {
                fractalFrames[j] = manager.get(i + j);
            }

            if (!scales.isEmpty()) {
                renderFrame(scales, process,
                        fractalFrames
                );
            }
            frame.close();
        }

        List<Double> endScales = Collections.nCopies((int) (fps * endTime), (double) (manager.size() - 1));
        renderFrame(endScales, process, manager.getLast());

        process.finish();
        service.shutdown();
        finished = true;
    }

    private void renderFrame(List<Double> factors, FFmpegProcess process, FractalFrame... frames)
            throws Exception {

        List<Future<BufferedImage>> futures = new LinkedList<>();

        int baseFactor = factors.get(0).intValue();

        BufferedImage[] images = new BufferedImage[frames.length];
        for (int i = 0; i < images.length; i++) {
            if (frames[i] != null)
                images[i] = frames[i].getImage();
        }

        double scaleFix = Math.log(width * 1.0 / height) / Math.log(2) - Math.log(images[0].getWidth() * 1.0 / images[0].getHeight()) / Math.log(2);

        for (double factor : factors) {
            Callable<BufferedImage> c = () -> {
                BufferedImage buffer = framePool.take();
                Graphics2D g2d = buffer.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int bgWidth = buffer.getWidth();
                int bgHeight = buffer.getHeight();

                for (int i = 0; i < images.length; i++) {
                    if (frames[i] != null) {
                        putImage(images[i], g2d, (factor - baseFactor) - i, bgWidth, bgHeight);
                    }
                }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                for (ScaleIndicator indicator : indicators) {
                    indicator.draw(g2d, new FractalScale(frames[0].getScale().getZooms() + (factor - baseFactor) + scaleFix), width, height);
                }

                return buffer;
            };

            futures.add(service.submit(c));
        }

        for (Future<BufferedImage> future : futures) {
            BufferedImage image = future.get();
            process.writeFrame(image);
            synchronized (this) {
                renderedFrames++;
            }
            framePool.add(image);
        }
    }

    private void putImage(BufferedImage image, Graphics2D g2d, double factor, int bgWidth, int bgHeight) {
        double scaleFactor = Math.pow(2, factor) * ((double) (Math.max(bgWidth, bgHeight)) / Math.max(image.getWidth(), image.getHeight()));
//        System.out.println();

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        double offsetX = (bgWidth - imgWidth * scaleFactor) / 2;
        double offsetY = (bgHeight - imgHeight * scaleFactor) / 2;

        float opacity = (float) Math.min(1, Math.pow(2, factor));

        g2d.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, opacity
                ));

        g2d.drawImage(image, new AffineTransform(scaleFactor, 0, 0, scaleFactor, offsetX, offsetY), null);
    }

    public void addScaleIndicator(ScaleIndicator sc) {
        indicators.add(sc);
    }

    public void removeScaleIndicator(ScaleIndicator sc) {
        indicators.remove(sc);
    }

    public synchronized int getRenderedFrames() {
        return renderedFrames;
    }

    public synchronized int getTotalFrames() {
        return (int) ((startTime + endTime + interpolator.getDuration()) * fps);
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public void abort() {
        if (process != null)
            process.getFfmpeg().destroy();
    }
}
