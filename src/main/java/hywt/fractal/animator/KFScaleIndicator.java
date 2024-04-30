package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class KFScaleIndicator implements ScaleIndicator {
    private Font font;

    public KFScaleIndicator() throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Inconsolata.ttf"));
    }

    @Override
    public void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        g.setFont(font.deriveFont(48f));

        String content = "Zoom: " + fractalScale.getMagnification();
        int offset = 48 / 12;
        g.setColor(Color.BLACK);
        g.drawString(content, 8 + offset, 48 + offset);
        g.setColor(Color.WHITE);
        g.drawString(content, 8, 48);
    }
}
