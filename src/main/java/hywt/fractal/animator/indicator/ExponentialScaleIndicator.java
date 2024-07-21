package hywt.fractal.animator.indicator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ExponentialScaleIndicator implements ScaleIndicator {
    private double scale;
    private Font font;

    public ExponentialScaleIndicator() {
        this.scale = 1;
    }

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        int baseScale = 64;
        int expScale = baseScale / 2;

        int margin = (int) (scale * 8);
        int offset = (int) (scale * 4);

        int drawY = (int) (height - scale) - margin;

        String ten = "10";

        g.setFont(font.deriveFont((float) (baseScale * scale)));
        FontMetrics metrics = g.getFontMetrics();
        int baseWidth = metrics.stringWidth(ten);

        double scaleOf10 = fractalScale.getLog10Scale();

        g.setColor(Color.BLACK);
        g.drawString(ten, margin + offset, drawY + offset);
        g.setFont(font.deriveFont((float) (expScale * scale)));
        g.drawString(String.format("%.1f", scaleOf10), margin + baseWidth + offset / 2, (int) (drawY - scale * expScale + offset / 2));

        g.setColor(Color.WHITE);
        g.setFont(font.deriveFont((float) (baseScale * scale)));
        g.drawString(ten, margin, drawY);
        g.setFont(font.deriveFont((float) (expScale * scale)));
        g.drawString(String.format("%.1f", scaleOf10), margin + baseWidth, (int) (drawY - scale * expScale));
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
