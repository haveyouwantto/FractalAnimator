package hywt.fractal.animator.indicator;

import hywt.fractal.animator.Googology;
import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;

public class GoogologyIndicator implements ScaleIndicator{
    private Font font;
    private double scale;

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        g.setFont(font.deriveFont((float) (24 * scale)));

        String content = Googology.googologyFormatLog2(fractalScale.getZooms());

        int offset = (int) (24 * scale / 12);

        int x = width / 2;
        int y = (int) (24 * scale);

        FontMetrics metrics = g.getFontMetrics();
        int drawX = x - metrics.stringWidth(content) / 2;

        g.setColor(Color.BLACK);
        g.drawString(content, drawX + offset, y + offset);
        g.setColor(Color.WHITE);
        g.drawString(content, drawX, y);
    }

    @Override
    public void setScale(double scale) throws Exception {
        this.scale = scale;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }
}
