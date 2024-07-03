package hywt.fractal.animator.ui;

import hywt.fractal.animator.VideoRenderer;

import javax.swing.*;

public class ProgressPanel extends JProgressBar {


    private Thread updateThread;

    public ProgressPanel() {
        super(0, 1000);
        setStringPainted(true);
    }

    public void start(VideoRenderer renderer) {
        setVisible(true);
        updateThread = new Thread(() -> {
            try {
                while (!renderer.isFinished()) {
                    int a = renderer.getRenderedFrames();
                    int b = renderer.getTotalFrames();
                    setString(String.format("%.2f%%    Frame: %d / %d  Image: %d / %d", a * 100.0 / b, a, b, renderer.getRenderedKeyframes(), renderer.getTotalKeyframes()));
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
