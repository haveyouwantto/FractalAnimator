package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeLoader;
import hywt.fractal.animator.keyframe.TestKeyframeLoader;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class TestSequenceConfigure extends ManagerConfigure {
    private KeyframeLoader manager;
    private JTextArea reField;
    private JTextArea zoomField;
    private JTextArea imField;
    private JTextArea iterField;
    private JButton button;

    public TestSequenceConfigure() {
    }

    @Override
    public KeyframeLoader get() {
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
        reField = new JTextArea("-1.99999911758766165543764649311537154663");
        reField.setLineWrap(true);
        JLabel imLabel = new JLabel("Imag");
        imField = new JTextArea("-4.2402439547240753390707694210131039e-13");
        imField.setLineWrap(true);


        JLabel zoomLabel = new JLabel("Zoom");
        zoomField = new JTextArea("5.070602e+30");
        JLabel iterLabel = new JLabel("Iter");
        iterField = new JTextArea("1024");

        button = new JButton("Set");
        button.addActionListener(e -> {
                    try {
                        manager = new TestKeyframeLoader(
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


    @Override
    public JSONObject exportJSON() {
        JSONObject obj = new JSONObject();
        obj.put("real", reField.getText());
        obj.put("imag", imField.getText());
        obj.put("zoom", Double.parseDouble(zoomField.getText()));
        obj.put("iter", Integer.parseInt(iterField.getText()));
        return obj;
    }

    @Override
    public void importJSON(JSONObject obj) {
        reField.setText(obj.getString("real"));
        imField.setText(obj.getString("imag"));
        zoomField.setText(String.valueOf(obj.getDouble("zoom")));
        iterField.setText(String.valueOf(obj.getInt("iter")));
        button.doClick();
    }
}
