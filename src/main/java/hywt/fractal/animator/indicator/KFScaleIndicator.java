package hywt.fractal.animator.indicator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.io.IOException;

public class KFScaleIndicator implements ScaleIndicator {
    private Font font;
    private double scale;

    public KFScaleIndicator() throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResource("assets/fonts/Roboto-Regular.ttf").openStream());
        scale = 1;
    }

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        g.setFont(font.deriveFont((float) (48 * scale)));

        String content = "Zoom: " + fractalScale.getMagnification();
        int offset = (int) (48 * scale / 12);
        int x = (int) (8 * scale);
        g.setColor(Color.BLACK);
        g.drawString(content, x + offset, (int) (48 * scale + offset));
        g.setColor(Color.WHITE);
        g.drawString(content, x, (int) (48 * scale));
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }
}
