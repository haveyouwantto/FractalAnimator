package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OdometerIndicator implements ScaleIndicator {
    private BufferedImage image;
    private Graphics2D g;
    private Color bgColor;

    private static final int MARGIN = 6;
    private static final int SCALE = 48;
    private static final int DIGITS = 6;

    public OdometerIndicator() throws IOException, FontFormatException {
        image = new BufferedImage(SCALE / 2 * DIGITS + MARGIN * (DIGITS + 1), SCALE, BufferedImage.TYPE_3BYTE_BGR);
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Inconsolata.ttf")).deriveFont((float) SCALE);
        g.setFont(font);

        bgColor = new Color(20, 20, 20);
    }

    @Override
    public synchronized void draw(Graphics g, FractalScale fractalScale, int width, int height) {
        this.g.setColor(bgColor);
        this.g.fillRect(0, 0, image.getWidth(), image.getHeight());
        this.g.setColor(Color.WHITE);

        double scale = fractalScale.getLog10Zooms();
        int scaleInt = (int) scale;

        double percent = scale % 1;

        int[] digits = new int[DIGITS];

        for (int i = 0; i < DIGITS; i++) {
            int digit = scaleInt % 10;
            digits[i] = digit;
            scaleInt /= 10;

            boolean isNine = true;

            for (int j = i - 1; j >= 0; j--) {
                if (digits[j] != 9) {
                    isNine = false;
                    break;
                }
            }

            if (i == 0 || isNine) {
                int yPos = (int) (SCALE - 8 - percent * SCALE);

                this.g.drawString(String.valueOf(digit), image.getWidth() - SCALE / 2 * (i + 1) - MARGIN * (i + 1), yPos);

                int next = digit + 1;
                if (next > 9) next = 0;
                this.g.drawString(String.valueOf(next), image.getWidth() - SCALE / 2 * (i + 1) - MARGIN * (i + 1), yPos + SCALE);
            } else {
                this.g.drawString(String.valueOf(digit), image.getWidth() - SCALE / 2 * (i + 1) - MARGIN * (i + 1), SCALE - 8);
            }
        }

        g.drawImage(image, width / 2 - image.getWidth() / 2, height - image.getHeight(), null);
    }

}
