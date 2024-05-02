package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FXScaleIndicator implements ScaleIndicator {
    private Font font;

    public FXScaleIndicator() throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Inconsolata.ttf"));
    }

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        g.setFont(font.deriveFont(24f));

        String content = String.format("%.2f zooms", fractalScale.getZooms());

        int offset = 24 / 12;

        int x = width - 8;

        FontMetrics metrics = g.getFontMetrics();
        int drawX = x - metrics.stringWidth(content);

        g.setColor(Color.BLACK);
        g.drawString(content, drawX + offset, height - 8 + offset);
        g.setColor(Color.WHITE);
        g.drawString(content, drawX, height - 8);
    }
}
