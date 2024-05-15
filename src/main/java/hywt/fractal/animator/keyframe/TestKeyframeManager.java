package hywt.fractal.animator.keyframe;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class TestKeyframeManager extends KeyframeManager {
    private final List<TestFractalFrame> frames;

    public TestKeyframeManager(BigDecimal re, BigDecimal im, double magn, int iterations) {
        frames = new ArrayList<>();
        for (int i = -1; Math.pow(2, i) <= magn; i++) {
            frames.add(new TestFractalFrame(re, im, Math.pow(2, i), iterations));
        }
    }

    @Override
    public FractalFrame get(int index) {
        try {
            return frames.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return frames.size();
    }

    static class TestFractalFrame extends FractalFrame {

        public static final int[] PALETTE = {16007990, 16733986, 16750592, 16761095, 16771899,
                13491257, 9159498, 5025616, 38536, 48340, 240116, 2201331, 4149685,
                6765239, 10233776, 15277667};

        final BigDecimal re;
        final BigDecimal im;

        private BufferedImage image;
        private final int width;
        private final int height;
        private final double size;

        private final MathContext context;
        private final int iterations;
        private int[][] map;

        private int baseX;
        private int baseY;
        private int rebaseX;
        private int rebaseY;
        private int refs;

        public TestFractalFrame(BigDecimal re, BigDecimal im, double magn, int iterations) {
            this.scale = new FractalScale(Math.log(magn) / Math.log(2));
            this.size = 4 / magn;
            width = 1000;
            height = 1000;
            context = new MathContext((int) (Math.log10(magn) + 10));
            this.re = re;
            this.im = im;
            this.iterations = iterations;
            map = new int[height][width];
        }

        @Override
        public synchronized BufferedImage getImage() throws Exception {
            if (image == null || map == null) {
                image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                map = new int[height][width];

                baseX = width / 2;
                baseY = width / 2;
                refs = 0;

                drawImage(false);
            }
            return image;
        }

        private void drawImage(boolean rebase) {
            List<Double> ref;
            if (!rebase) ref = getReferenceOrbit(re, im);
            else {
                double[] off = getDelta(rebaseX, rebaseY, baseX, baseY);
                BigDecimal newRe = re.add(BigDecimal.valueOf(off[0]), context);
                BigDecimal newIm = im.add(BigDecimal.valueOf(off[1]), context);
                ref = getReferenceOrbit(newRe, newIm);
            }
            refs++;

            List<Integer[]> glitches = new ArrayList<>();

            for (int y = 0; y < height; y += 2) {
                for (int x = 0; x < width; x += 2) {
                    if (!rebase || map[y][x] < 0) {
                        double[] pos = rebase ? getDelta(x, y, rebaseX, rebaseY) : getDelta(x, y, baseX, baseY);
                        int iter = getIter(pos[0], pos[1], ref);

                        if (iter < 0) glitches.add(new Integer[]{x, y});
                        map[y][x] = iter;
                    }
                }
            }
            for (int y = 0; y < height; y += 2) {
                for (int x = 1; x < width; x += 2) {
                    if (!rebase || map[y][x] < 0) {
                        if (y < height - 1 && x < width - 1) {
                            int left = map[y][x - 1];
                            int right = map[y][x + 1];
                            if (left == right) {
                                map[y][x] = left;
                                continue;
                            }
                        }
                        double[] pos = rebase ? getDelta(x, y, rebaseX, rebaseY) : getDelta(x, y, baseX, baseY);
                        int iter = getIter(pos[0], pos[1], ref);

                        if (iter < 0) glitches.add(new Integer[]{x, y});
                        map[y][x] = iter;
                    }
                }
            }
            for (int y = 1; y < height; y += 2) {
                for (int x = 0; x < width; x++) {
                    if (!rebase || map[y][x] < 0) {
                        if (y < height - 1) {
                            int up = map[y - 1][x];
                            int down = map[y + 1][x];
                            if (up == down) {
                                map[y][x] = down;
                                continue;
                            }
                        }
                        double[] pos = rebase ? getDelta(x, y, rebaseX, rebaseY) : getDelta(x, y, baseX, baseY);
                        int iter = getIter(pos[0], pos[1], ref);

                        if (iter < 0) glitches.add(new Integer[]{x, y});
                        map[y][x] = iter;
                    }
                }
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    image.setRGB(x, y, (map[y][x] >= 0 && map[y][x] < iterations) ? PALETTE[map[y][x] % PALETTE.length] : 0);
                }
            }
            if (glitches.size() > 4 && refs < 70) {
                Integer[] xy = glitches.get(glitches.size() / 10);
                rebaseX = xy[0];
                rebaseY = xy[1];
                drawImage(true);
            }
        }


        private double[] getDelta(int x, int y, int baseX, int baseY) {
            return new double[]{
                    (double) (x - baseX) / width * this.size,
                    (double) (baseY - y) / height * this.size
            };
        }

        private List<Double> getReferenceOrbit(BigDecimal re, BigDecimal im) {
            BigDecimal re1 = BigDecimal.valueOf(0);
            BigDecimal im1 = BigDecimal.valueOf(0);

            List<Double> ref = new ArrayList<>();
            for (int i = 0; i < iterations; i++) {
                BigDecimal t = re1.pow(2, context).subtract(im1.pow(2, context), context).add(re, context);
                im1 = re1.multiply(im1, context).multiply(BigDecimal.valueOf(2), context).add(im, context);
                re1 = t;

                if (re1.pow(2, context).add(im1.pow(2), context).doubleValue() > 1073741824) break;
                ref.add(re1.doubleValue());
                ref.add(im1.doubleValue());
            }
            return ref;
        }

        private int getIter(double dRe, double dIm, List<Double> ref) {
            double re1 = dRe;
            double im1 = dIm;
            double tmp;

            for (int i = 0; i < ref.size(); i += 2) {
                double zRe = ref.get(i);
                double zIm = ref.get(i + 1);

                double valR = zRe + re1;
                double valI = zIm + im1;
                double val = Math.sqrt(valR * valR + valI * valI);
                if (val < Math.sqrt(zRe * zRe + zIm * zIm) * 1e-4) return -1;
                else if (val > 2) return i / 2;

                tmp = 2 * (zRe * re1 - zIm * im1) + (re1 * re1 - im1 * im1) + dRe;
                im1 = 2 * (zRe * im1 + zIm * re1 + re1 * im1) + dIm;
                re1 = tmp;
            }
            return ref.size() >= iterations * 2 - 1 ? iterations : -1;
        }

        @Override
        public void close() throws IOException {
            image = null;
            map = null;
        }
    }
}
