package hywt.fractal.animator.keyframe;

import hywt.fractal.animator.Palette;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KFBLoader extends ImageLoader {
    List<FractalImage> frameList;
    static String regex = "\\d\\d\\d\\d\\d_([0-9Ee.\\-+]+)\\.(kfb)$";

    public KFBLoader(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles((dir1, name) -> name.matches(regex));
        if (files == null || files.length == 0) throw new FileNotFoundException("Directory invalid.");


        File kfr = dir.listFiles((dir1, name) -> name.matches(".*\\.kfr$"))[0];

        Palette p = new Palette();

        try (BufferedReader reader = new BufferedReader(new FileReader(kfr))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("Colors")) {
                    String[] bga = line.trim().split(":")[1].trim().split(",");
                    int[][] colors = new int[bga.length/3][3];
                    for (int i = 0; i < bga.length; i+=3) {
                        colors[i/3][0] = Integer.parseInt(bga[i+2]);
                        colors[i/3][1] = Integer.parseInt(bga[i+1]);
                        colors[i/3][2] = Integer.parseInt(bga[i]);
                    }
                    p = new Palette(colors);
                }
                // Process other lines as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Pattern pattern = Pattern.compile(regex);
        frameList = new LinkedList<>();
        Palette finalP = p;
        Arrays.stream(files).forEach(file -> {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find())
                frameList.add(new KFBImage(file, FractalScale.fromMagnification(matcher.group(1)), finalP));
        });

        Collections.sort(frameList);
    }

    @Override
    public FractalImage get(int index) {
        try {
            return frameList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return frameList.size();
    }

    class KFBImage extends FractalImage {
        int[][] iterations;
        double[][] phase;
        File kfb;
        int maxIter;
        BufferedImage image;
        Palette palette;

        public KFBImage(File file, FractalScale fractalScale, Palette palette) {
            super();
            kfb = file;
            scale = fractalScale;
            this.palette = palette;
        }

        @Override
        public BufferedImage getImage() throws Exception {
            return getImage(0);
        }

        @Override
        public BufferedImage getImage(double ratio) throws Exception {
            if (iterations == null) loadFile();

            return image;
        }

        @Override
        public void close() throws IOException {

        }

        private synchronized void loadFile() throws IOException {
            DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(kfb)));
            is.skip(3);
            System.out.println(kfb);
            maxIter = 0;

            int width = Integer.reverseBytes(is.readInt());
            int height = Integer.reverseBytes(is.readInt());

            iterations = new int[width][height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int it = Integer.reverseBytes(is.readInt());
                    maxIter = Math.max(maxIter, it);
                    iterations[x][y] = Math.max(it, 0);
                }
            }

            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int it = iterations[x][y];
                    image.setRGB(x, y, it == maxIter ? 0 : palette.getColor(it));
                }
            }

            is.close();
        }
    }
}
