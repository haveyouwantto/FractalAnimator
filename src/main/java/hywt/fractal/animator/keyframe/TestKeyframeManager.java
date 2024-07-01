package hywt.fractal.animator.keyframe;

import hywt.fractal.animator.Palette;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestKeyframeManager extends KeyframeManager {
    private final List<TestFractalFrame> frames;

    private final ExecutorService service;

    public TestKeyframeManager(BigDecimal re, BigDecimal im, double magn, int iterations) {
        frames = new ArrayList<>();
        int processors = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(processors);
        for (int i = -1; Math.pow(2, i) <= magn; i++) {
            frames.add(new TestFractalFrame(re, im, Math.pow(2, i), iterations, service));
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

        private ExecutorService service;

        public TestFractalFrame(BigDecimal re, BigDecimal im, double magn, int iterations, ExecutorService service) {
            this.scale = new FractalScale(Math.log(magn) / Math.log(2));
            this.size = 4 / magn;
            width = 1000;
            height = 1000;
            context = new MathContext((int) (Math.log10(magn) + 10));
            this.re = re;
            this.im = im;
            this.iterations = iterations;
            map = new int[height][width];
            this.service = service;
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

            List<Integer[]> glitches = Collections.synchronizedList(new ArrayList<>());

            List<Future<?>> futures = new ArrayList<>();

            for (int y = 0; y < height; y += 2) {
                int finalY = y;
                futures.add(service.submit(() -> {
                    for (int x = 0; x < width; x += 2) {
                        if (!rebase || map[finalY][x] < 0) {
                            double[] pos = rebase ? getDelta(x, finalY, rebaseX, rebaseY) : getDelta(x, finalY, baseX, baseY);
                            int iter = getIter(pos[0], pos[1], ref);

                            if (iter < 0) glitches.add(new Integer[]{x, finalY});
                            map[finalY][x] = iter;
                        }
                    }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int y = 0; y < height; y += 2) {
                int finalY = y;
                futures.add(service.submit(() -> {
                    for (int x = 1; x < width; x += 2) {
                        if (!rebase || map[finalY][x] < 0) {
                            if (finalY < height - 1 && x < width - 1) {
                                int left = map[finalY][x - 1];
                                int right = map[finalY][x + 1];
                                if (left == right) {
                                    map[finalY][x] = left;
                                    continue;
                                }
                            }
                            double[] pos = rebase ? getDelta(x, finalY, rebaseX, rebaseY) : getDelta(x, finalY, baseX, baseY);
                            int iter = getIter(pos[0], pos[1], ref);

                            if (iter < 0) glitches.add(new Integer[]{x, finalY});
                            map[finalY][x] = iter;
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int y = 1; y < height; y += 2) {
                int finalY = y;
                futures.add(service.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        if (!rebase || map[finalY][x] < 0) {
                            if (finalY < height - 1) {
                                int up = map[finalY - 1][x];
                                int down = map[finalY + 1][x];
                                if (up == down) {
                                    map[finalY][x] = down;
                                    continue;
                                }
                            }
                            double[] pos = rebase ? getDelta(x, finalY, rebaseX, rebaseY) : getDelta(x, finalY, baseX, baseY);
                            int iter = getIter(pos[0], pos[1], ref);

                            if (iter < 0) glitches.add(new Integer[]{x, finalY});
                            map[finalY][x] = iter;
                        }
                    }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    image.setRGB(x, y, (map[y][x] >= 0 && map[y][x] < iterations) ? Palette.getColor(map[y][x]) : 0);
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
