package hywt.fractal.animator.keyframe;

import java.awt.image.BufferedImage;
import java.util.Iterator;

public class TestKeyframeManager extends KeyframeManager {
    private TestFractalFrame[] frames;

    private static final int FRAMES = 46;

    public TestKeyframeManager() {
        frames = new TestFractalFrame[FRAMES];
        for (int i = 0; i < FRAMES; i++) {
            frames[i] = new TestFractalFrame(i - 1);
        }
    }

    @Override
    public FractalFrame get(int index) {
        return index <= FRAMES - 1 ? frames[index] : null;
    }

    @Override
    public int size() {
        return FRAMES;
    }

    static class TestFractalFrame extends FractalFrame {

        public static final int[] PALETTE = {16007990, 16733986, 16750592, 16761095, 16771899,
                13491257, 9159498, 5025616, 38536, 48340, 240116, 2201331, 4149685,
                6765239, 10233776, 15277667};

        static final double CENTER_RE = -1.7400676582491037003466476;
        static final double CENTER_IM = 0.028165388865579982647603007;

        private BufferedImage image;

        public TestFractalFrame(double zooms) {
            this.scale = new FractalScale(zooms);
        }

        @Override
        public synchronized BufferedImage getImage() throws Exception {
            if (image == null) {
                image = new BufferedImage(1000, 1000, BufferedImage.TYPE_3BYTE_BGR);
                double scale = 4 / Math.pow(2, this.scale.getZooms());

                for (int y = 0; y < 1000; y++) {
                    double im = CENTER_IM - (y - 500) / 1000.0 * scale;
                    for (int x = 0; x < 1000; x++) {
                        double re = CENTER_RE - (500 - x) / 1000.0 * scale;

                        int iter = getIter(re, im);

                        image.setRGB(x, y, iter < 1000 ? PALETTE[iter % PALETTE.length] : 0);
                    }
                }
            }
            return image;
        }

        private int getIter(double re, double im) {
            double re1 = re;
            double im1 = im;
            double temp;
            for (int i = 0; i < 1000; i++) {
                if (re1 * re1 + im1 * im1 > 4) return i;
                temp = re1 * re1 - im1 * im1 + re;
                im1 = 2 * re1 * im1 + im;
                re1 = temp;
            }
            return 1000;
        }
    }
}
