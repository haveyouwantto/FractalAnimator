package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.FractalScale;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.FractalFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class FrameBrowser extends JFrame {
    private KeyframeManager manager;
    private JLabel infoLabel;
    private JLabel frameNum;
    private ImagePanel imgDisplay;
    private int ord;

    public FrameBrowser(KeyframeManager manager) throws Exception {
        this.manager = manager;
        this.ord = 0;

        setTitle("Keyframe Browser");
        setPreferredSize(new Dimension(854, 480));
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        JButton prevBtn = new JButton("<");
        prevBtn.addActionListener(e -> zoomOut());
        controls.add(prevBtn);

        frameNum = new JLabel();
        controls.add(frameNum);

        JButton nextBtn = new JButton(">");
        nextBtn.addActionListener(e -> zoomIn());
        controls.add(nextBtn);

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
        FractalFrame frame = manager.get(index);
        FractalScale scale = frame.getScale();
        infoLabel.setText(String.format("%.2f zooms | Magn: %s",scale.getZooms(), scale.getMagnification()));
        imgDisplay.setImg((BufferedImage) frame.getImage());
        imgDisplay.updateUI();

        frameNum.setText(String.valueOf(ord));
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
