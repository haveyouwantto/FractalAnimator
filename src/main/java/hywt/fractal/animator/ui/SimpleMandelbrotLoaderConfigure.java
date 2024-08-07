package hywt.fractal.animator.ui;

import hywt.fractal.animator.Localization;
import hywt.fractal.animator.keyframe.ImageLoader;
import hywt.fractal.animator.keyframe.SimpleMandelbrotLoader;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class SimpleMandelbrotLoaderConfigure extends ImageLoaderConfigure {
    private ImageLoader manager;
    private JTextArea reField;
    private JTextArea magnField;
    private JTextArea imField;
    private JTextArea iterField;
    private JButton button;

    public SimpleMandelbrotLoaderConfigure() {
    }

    @Override
    public ImageLoader get() {
        return manager;
    }

    @Override
    public void init() throws Exception {
        setLayout(new BorderLayout());

        JTextArea prompt = new JTextArea();
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setText(Localization.get("image.mandelbrot.description"));
        prompt.setEnabled(false);

        JLabel reLabel = new JLabel(Localization.get("image.mandelbrot.real"));
        reField = new JTextArea("-1.99999911758766165543764649311537154663");
        reField.setLineWrap(true);
        JLabel imLabel = new JLabel(Localization.get("image.mandelbrot.imag"));
        imField = new JTextArea("-4.2402439547240753390707694210131039e-13");
        imField.setLineWrap(true);


        JLabel magnLabel = new JLabel(Localization.get("image.mandelbrot.magn"));
        magnField = new JTextArea("5.070602e+30");
        JLabel iterLabel = new JLabel(Localization.get("image.mandelbrot.iter"));
        iterField = new JTextArea("1024");

        button = new JButton(Localization.get("label.set"));
        button.addActionListener(e -> {
                    try {
                        manager = new SimpleMandelbrotLoader(
                                new BigDecimal(reField.getText()), new BigDecimal(imField.getText()), Double.parseDouble(magnField.getText()), Integer.parseInt(iterField.getText()));
                        load();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, Localization.get("message.invalidnum"), Localization.get("message.error"), JOptionPane.ERROR_MESSAGE);
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
        panel.add(magnLabel);
        panel.add(magnField);
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
        obj.put("magn", Double.parseDouble(magnField.getText()));
        obj.put("iter", Integer.parseInt(iterField.getText()));
        return obj;
    }

    @Override
    public void importJSON(JSONObject obj) {
        reField.setText(obj.getString("real"));
        imField.setText(obj.getString("imag"));
        magnField.setText(String.valueOf(obj.getDouble("magn")));
        iterField.setText(String.valueOf(obj.getInt("iter")));
        button.doClick();
    }
}
