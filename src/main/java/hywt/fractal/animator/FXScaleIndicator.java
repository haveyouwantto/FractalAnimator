package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FXScaleIndicator implements ScaleIndicator {
    private Font font;
    private double scale;

    public FXScaleIndicator() throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Inconsolata.ttf"));
        scale = 2;
    }

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        g.setFont(font.deriveFont((float) (24 * scale)));

        String content = String.format("%.2f zooms", fractalScale.getZooms());

        int offset = (int) (24 * scale / 12);

        int x = (int) (width - 8 * scale);

        FontMetrics metrics = g.getFontMetrics();
        int drawX = x - metrics.stringWidth(content);

        g.setColor(Color.BLACK);
        g.drawString(content, drawX + offset, (int) (height - 8 * scale + offset));
        g.setColor(Color.WHITE);
        g.drawString(content, drawX, (int) (height - 8 * scale));
    }
}
