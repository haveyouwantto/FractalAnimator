package hywt.fractal.animator.ui;

import hywt.fractal.animator.VideoRenderer;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JFrame {

    private final JProgressBar bar;
    VideoRenderer renderer;

    public ProgressDialog(VideoRenderer renderer) {
        setTitle("Render progress");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        bar = new JProgressBar(0, 1000);
        bar.setPreferredSize(new Dimension(240,30));
        bar.setStringPainted(true);
        add(bar);

        pack();

        this.renderer = renderer;
    }

    public void start() {
        setVisible(true);
        Thread updateThread = new Thread(() -> {
            try {
                while (!renderer.isFinished()) {
                    int a = renderer.getRenderedFrames();
                    int b = renderer.getTotalFrames();
                    bar.setString(a + " / " + b);
                    bar.setValue((int) (a*1000.0/b));
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });

        updateThread.start();
    }
}
