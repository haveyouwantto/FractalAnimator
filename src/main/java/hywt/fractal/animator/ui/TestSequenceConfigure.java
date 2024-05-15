package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;

import javax.swing.*;
import java.awt.*;

public class TestSequenceConfigure extends ManagerConfigure{
    private KeyframeManager manager;

    public TestSequenceConfigure() {
        manager = new TestKeyframeManager();
    }
    @Override
    public KeyframeManager get() {
        return manager;
    }

    @Override
    public void init() throws Exception {
        load();

        setLayout(new BorderLayout());

        JTextArea field = new JTextArea();
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        field.setText("Simple Mandelbrot generator for quick testing purpose.");
        field.setEnabled(false);

        add(field, BorderLayout.CENTER);
    }
}
