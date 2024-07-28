package hywt.fractal.animator.ui;

import hywt.fractal.animator.Localization;
import hywt.fractal.animator.keyframe.FractalScale;
import hywt.fractal.animator.keyframe.ImageLoader;
import hywt.fractal.animator.keyframe.FractalImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class ImageBrowser extends JFrame {
    private final JSpinner spinner;
    private ImageLoader manager;
    private JLabel infoLabel;
    private ImagePanel imgDisplay;
    private int ord;

    public ImageBrowser(ImageLoader manager) throws Exception {
        this.manager = manager;
        this.ord = 0;

        setTitle(Localization.get("image_browser.title"));
        setPreferredSize(new Dimension(854, 480));
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });

        imgDisplay = new ImagePanel();
        getContentPane().add(imgDisplay, BorderLayout.CENTER);

        JPanel controls = new JPanel();

        infoLabel = new JLabel();

        controls.add(infoLabel);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        controls.add(separator);

        JLabel label = new JLabel(Localization.get("image_browser.image"));
        controls.add(label);

        spinner = new JSpinner();
        spinner.setPreferredSize(new Dimension(100, 30));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    ord = (Integer) spinner.getValue();
                    showFrame(ord);
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(spinner, Localization.get("message.image_missing"), Localization.get("message.error"), JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        controls.add(spinner);

        getContentPane().add(controls, BorderLayout.SOUTH);

        showFrame(0);

        pack();
    }

    public void zoomIn(){
        ord++;
        if (ord >= manager.size()) ord = 0;
        try {
            showFrame(ord);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void zoomOut() {
        ord--;
        if (ord < 0) ord = manager.size() - 1;
        try {
            showFrame(ord);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void showFrame(int index) throws Exception {
        FractalImage frame = manager.get(index);
        FractalScale scale = frame.getScale();
        infoLabel.setText(String.format(Localization.get("image_browser.format"),scale.getZooms(), scale.getMagnification()));
        imgDisplay.setImg(frame.getImage());
        imgDisplay.updateUI();

        spinner.setValue(ord);
        frame.close();
    }

    static class ImagePanel extends JPanel {

        private BufferedImage img;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Dimension dimension = this.getSize();
                int imgWidth = img.getWidth(null);
                int imgHeight = img.getHeight(null);
                double aspectRatio = (double) imgWidth / imgHeight;

                int newWidth = dimension.width;
                int newHeight = (int) (dimension.width / aspectRatio);

                if (newHeight > dimension.height) {
                    newHeight = dimension.height;
                    newWidth = (int) (dimension.height * aspectRatio);
                }

                int x = (dimension.width - newWidth) / 2;
                int y = (dimension.height - newHeight) / 2;

                g.drawImage(img, x, y, newWidth, newHeight, null);
            }
        }

        public void setImg(BufferedImage img) {
            this.img = img;
        }
    }
}
