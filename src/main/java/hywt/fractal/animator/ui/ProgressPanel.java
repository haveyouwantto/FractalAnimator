package hywt.fractal.animator.ui;

import hywt.fractal.animator.Utils;
import hywt.fractal.animator.VideoRenderer;

import javax.swing.*;

public class ProgressPanel extends JProgressBar {


    private Thread updateThread;
    private long startTime;

    public ProgressPanel() {
        super(0, 1000);
        setStringPainted(true);
    }

    public void start(VideoRenderer renderer) {
        setVisible(true);
        startTime = System.currentTimeMillis();
        updateThread = new Thread(() -> {
            try {
                while (!renderer.isFinished()) {
                    int a = renderer.getRenderedFrames();
                    int b = renderer.getTotalFrames();
                    double progress = (double) a / b;
                    long currentTime = System.currentTimeMillis();
                    setString(String.format("%.2f%%    Frame: %d / %d  Image: %d / %d  ETA: %s",
                            progress * 100,
                            a, b,
                            renderer.getRenderedKeyframes(), renderer.getTotalKeyframes(),
                            Utils.formatSeconds(((currentTime - startTime) / progress - (currentTime - startTime)) / 1000)
                    ));
                    setValue((int) (a * 1000.0 / b));
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {

            }
        });

        updateThread.start();
    }

    public void stop() {
        updateThread.interrupt();
    }
}
