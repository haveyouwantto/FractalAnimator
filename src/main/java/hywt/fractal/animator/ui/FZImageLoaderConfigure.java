package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.FZImageLoader;
import hywt.fractal.animator.keyframe.ImageLoader;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class FZImageLoaderConfigure extends ImageLoaderConfigure {
    private ImageLoader manager;
    private JLabel label;
    private File file;

    public void init() {
        setLayout(new BorderLayout());

        label = new JLabel("Select a directory:");


        JTextArea prompt = new JTextArea();
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);
        prompt.setText("Fractal Zoomer image sequence importer");
        prompt.setEnabled(false);
        add(prompt, BorderLayout.NORTH);

        JButton fileButton = new JButton("Select");
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setToolTipText("Fractal Zoomer sequence");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Fractal Zoomer output directory", "ds"));
            int result = fileChooser.showOpenDialog(fileButton);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    loadFile(selectedFile);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        return manager;
    }

    private void loadFile(File selectedFile) throws Exception {
        manager = new FZImageLoader(selectedFile);
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
        try {
            loadFile(new File(obj.getString("path")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
