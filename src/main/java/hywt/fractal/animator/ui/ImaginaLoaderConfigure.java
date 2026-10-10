package hywt.fractal.animator.ui;

import hywt.fractal.animator.Localization;
import hywt.fractal.animator.Palette;
import hywt.fractal.animator.keyframe.ImageLoader;
import hywt.fractal.animator.keyframe.ImaginaLoader;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImaginaLoaderConfigure extends ImageLoaderConfigure {

    private ImageLoader manager;
    private JLabel label;
    private JTextField palette;
    private JTextField density;
    private File file;

    public void init() {
        setLayout(new BorderLayout());

        label = new JLabel(Localization.get("image.dir"));

        JTextArea prompt = new JTextArea();
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setColumns(36);
        prompt.setText(Localization.get("image.imagina.description"));
        prompt.setEnabled(false);
        add(prompt, BorderLayout.NORTH);

        JButton fileButton = new JButton(Localization.get("label.select"));
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setToolTipText(Localization.get("image.imagina.tooltip"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showOpenDialog(fileButton) == JFileChooser.APPROVE_OPTION)
                loadFile(fileChooser.getSelectedFile());
        });

        // Palette file: one #rgb colour per line, empty means the built in one.
        palette = new JTextField(10);
        palette.setToolTipText(Localization.get("image.imagina.palette.tooltip"));
        palette.addActionListener(e -> reload());

        JButton paletteButton = new JButton(Localization.get("label.browse"));
        paletteButton.setToolTipText(Localization.get("image.imagina.palette.tooltip"));
        paletteButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setToolTipText(Localization.get("image.imagina.palette.tooltip"));
            if (fileChooser.showOpenDialog(paletteButton) == JFileChooser.APPROVE_OPTION) {
                palette.setText(fileChooser.getSelectedFile().getAbsolutePath());
                reload();
            }
        });

        density = new JTextField(String.valueOf((int) ImaginaLoader.DEFAULT_DENSITY), 4);
        density.setToolTipText(Localization.get("image.imagina.density.tooltip"));
        density.addActionListener(e -> reload());

        JPanel directoryRow = new JPanel();
        directoryRow.add(label);
        directoryRow.add(fileButton);

        JPanel colourRow = new JPanel();
        colourRow.add(new JLabel(Localization.get("image.imagina.palette")));
        colourRow.add(palette);
        colourRow.add(paletteButton);
        colourRow.add(new JLabel(Localization.get("image.imagina.density")));
        colourRow.add(density);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(directoryRow);
        panel.add(colourRow);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public ImageLoader get() {
        return manager;
    }

    private void loadFile(File selectedFile) {
        try {
            manager = new ImaginaLoader(selectedFile, readPalette(), readDensity());
            label.setText(selectedFile.getAbsolutePath());
            load();
            file = selectedFile;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), Localization.get("label.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private Palette readPalette() throws Exception {
        String path = palette.getText().trim();
        if (path.isEmpty()) return new Palette();
        return Palette.load(new File(path));
    }

    private double readDensity() {
        try {
            double value = Double.parseDouble(density.getText().trim());
            if (value > 0) return value;
        } catch (NumberFormatException ignored) {
        }
        return ImaginaLoader.DEFAULT_DENSITY;
    }

    private void reload() {
        if (file != null) loadFile(file);
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject object = new JSONObject();
        if (file != null) {
            object.put("path", file);
            object.put("palette", palette.getText().trim());
            object.put("density", density.getText().trim());
        }
        return object;
    }

    @Override
    public void importJSON(JSONObject obj) {
        if (obj.has("palette")) palette.setText(obj.getString("palette"));
        if (obj.has("density")) density.setText(obj.getString("density"));
        if (obj.has("path")) loadFile(new File(obj.getString("path")));
    }
}
