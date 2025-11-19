package hywt.fractal.animator;


import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.BinaryOperator;

public class FFmpegProcess {

    private Process ffmpeg;
    private OutputStream pipe;

    private ProcessBuilder builder;
    private BlockingQueue<Raster> frameQueue;
    private Thread consumerThread;

    public FFmpegProcess(int width, int height, double fps, String ffmpeg, File path, String[] additionalParam) {
        ArrayList<String> param = new ArrayList<>(List.of(
                ffmpeg,
                "-r", String.format("%.0f", fps),
                "-colorspace", "bt709",
                "-pix_fmt", "bgr24",
                "-f", "rawvideo",
                "-s", String.format("%dx%d", width, height),
                "-i", "-",
                "-y",
                path.getAbsolutePath()));
        param.addAll(param.size() - 2, List.of(additionalParam));
        System.out.println("Executing: "+ param.stream().reduce("", (s, s2) -> s+" "+s2));
        builder = new ProcessBuilder(param);
        builder.redirectErrorStream(true);
        frameQueue = new ArrayBlockingQueue<>(6);
    }

    public void start() throws IOException {
        if (ffmpeg == null) {
            ffmpeg = builder.start();
            pipe = ffmpeg.getOutputStream();

            // stdout重定向
            Runnable r = () -> {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getInputStream()));
                while (true) {
                    try {
                        if ((line = br.readLine()) == null) break;
                        System.out.println(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.start();
        } else throw new IllegalStateException("Process can only start once.");

        // Consumer Thread
        Runnable consumer = () -> {
            while (true) {
                try {
                    DataBufferByte frame = (DataBufferByte) frameQueue.take().getDataBuffer();
                    pipe.write(frame.getData());
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        };
        consumerThread = new Thread(consumer);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public synchronized void writeFrame(RenderedImage frame) throws IOException {
        if (frame == null) throw new NullPointerException("frame is null");

        DataBufferByte buffer = (DataBufferByte) frame.getData().getDataBuffer();
        pipe.write(buffer.getData());
    }

    public void submitFrame(RenderedImage frame) throws InterruptedException {
        // Check if consumer thread is alive
        if (consumerThread == null || !consumerThread.isAlive()) {
            throw new IllegalStateException("Consumer thread has terminated unexpectedly.");
        }

        if (frame == null) throw new NullPointerException("frame is null");

        Raster raster = frame.getData();
        frameQueue.put(raster);
    }

    public void finish() throws InterruptedException, IOException {
        pipe.close();
        ffmpeg.waitFor();
    }

    public Process getFfmpeg() {
        return ffmpeg;
    }
}
