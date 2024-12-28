package hywt.fractal.animator.ui;

import hywt.fractal.animator.Localization;
import hywt.fractal.animator.keyframe.ImageLoader;
import hywt.fractal.animator.keyframe.KFPNGImageLoader;
import hywt.fractal.animator.keyframe.MapleMandelMakerLoader;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class MapleMandelMakerLoaderConfigure extends ImageLoaderConfigure {
    private File file;
    ImageLoader loader;
    JLabel label;


    public void init() {
        setLayout(new BorderLayout());

        label = new JLabel(Localization.get("image.maple.config"));


        JTextArea prompt = new JTextArea();
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setText(Localization.get("image.maple.description"));
        prompt.setEnabled(false);
        add(prompt,BorderLayout.NORTH);

        JButton fileButton = new JButton(Localization.get("label.select"));
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setToolTipText(Localization.get("image.maple.tooltip"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(Localization.get("image.maple.file"), "prop"));
            int result = fileChooser.showOpenDialog(fileButton);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    loadFile(selectedFile);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), Localization.get("label.error"), JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(fileButton);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public ImageLoader get() {
        return loader;
    }

    private void loadFile(File selectedFile) throws Exception {
        loader = new MapleMandelMakerLoader(selectedFile);
        label.setText(selectedFile.getAbsolutePath());
        load();
        file = selectedFile;
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject object = new JSONObject();
        object.put("path", file);
        return object;
    }

    @Override
    public void importJSON(JSONObject obj) {

    }
}
