package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class TestSequenceConfigure extends ManagerConfigure {
    private KeyframeManager manager;

    public TestSequenceConfigure() {
    }

    @Override
    public KeyframeManager get() {
        return manager;
    }

    @Override
    public void init() throws Exception {
        setLayout(new BorderLayout());

        JTextArea prompt = new JTextArea();
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setText("Simple Mandelbrot generator for quick testing purpose.");
        prompt.setEnabled(false);

        JLabel reLabel = new JLabel("Real");
        JTextArea reField = new JTextArea("-1.99999911758766165543764649311537154663");
        reField.setLineWrap(true);
        JLabel imLabel = new JLabel("Imag");
        JTextArea imField = new JTextArea("-4.2402439547240753390707694210131039e-13");
        imField.setLineWrap(true);


        JLabel zoomLabel = new JLabel("Zoom");
        JTextArea zoomField = new JTextArea("5.070602e+30");
        JLabel iterLabel = new JLabel("Iter");
        JTextArea iterField = new JTextArea("1024");

        JButton button = new JButton("Set");
        button.addActionListener(e -> {
                    try {
                        manager = new TestKeyframeManager(
                                new BigDecimal(reField.getText()), new BigDecimal(imField.getText()), Double.parseDouble(zoomField.getText()), Integer.parseInt(iterField.getText()));
                        load();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        add(prompt, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(reLabel);
        panel.add(reField);
        panel.add(imLabel);
        panel.add(imField);
        panel.add(zoomLabel);
        panel.add(zoomField);
        panel.add(iterLabel);
        panel.add(iterField);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(panel);

        add(button, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);


    }
}
