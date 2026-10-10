package hywt.fractal.animator.keyframe;

import hywt.fractal.animator.Palette;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads a zoom sequence which Imagina wrote with "File -> Export zoom sequence".
 *
 * The export record (*.imz) names the frames and, in its "Location" line, the
 * *.im file which holds the coordinates they were computed at (records written
 * by an older version carry the coordinates themselves).  With that the zoom of
 * every frame follows from its index: half height of frame i = half height of
 * the location * Step^i, and the animator's zoom level is log2(2 / half height),
 * the depth Imagina records.  The frames themselves are the lossless compressed
 * raw maps (*.rawz) next to the record; plain pixel dumps (*.raw) of the same
 * size, as "Imagina.exe --decode" writes them, are accepted as well.
 *
 * A raw map holds the iteration value of every pixel, so the frames are drawn
 * with the simple escape time colouring the other loaders use; the palette and
 * the number of iterations between two colours can be chosen.
 */
public class ImaginaLoader extends ImageLoader {

    /** Used as the iteration limit when the record does not name one. */
    private static final double DEFAULT_ITERATIONS = 281474976710656.0;  // 2^48

    /** Iterations between two palette entries when none is given. */
    public static final double DEFAULT_DENSITY = 6.0;

    private static final Pattern FRAME_NAME = Pattern.compile("^(.*)-(\\d+)\\.(rawz|raw)$", Pattern.CASE_INSENSITIVE);

    /** Magic of an *.im file, which holds the coordinates of an export. */
    private static final long IM_MAGIC = 0x000A0D56504D49FFL;

    private final List<FractalImage> frameList = new ArrayList<>();
    private final Palette palette;
    private final double density;

    public ImaginaLoader(File record) throws IOException {
        this(record, new Palette(), DEFAULT_DENSITY);
    }

    /**
     * @param record  the export record (*.imz), or a directory holding one
     * @param palette the colours to draw the frames with
     * @param density iterations between two palette entries
     */
    public ImaginaLoader(File record, Palette palette, double density) throws IOException {
        this.palette = palette != null ? palette : new Palette();
        this.density = density > 0 ? density : DEFAULT_DENSITY;
        File file = record;
        if (file.isDirectory()) {
            File[] found = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".imz"));
            if (found == null || found.length == 0) throw new FileNotFoundException("no export record (*.imz) in " + file);
            file = found[0];
        }
        if (!file.isFile()) throw new FileNotFoundException("no such export record: " + file);

        int width = 0, height = 0;
        double step = 2;
        double iterations = 0;
        String location = null, baseHalfH = null;
        List<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colon = line.indexOf(':');
                if (colon < 0 || line.startsWith("#")) continue;
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                switch (key) {
                    case "Width": width = Integer.parseInt(value); break;
                    case "Height": height = Integer.parseInt(value); break;
                    case "Step": step = Double.parseDouble(value); break;
                    case "Iterations": iterations = Double.parseDouble(value); break;
                    case "Location": location = value; break;
                    case "CenterHalfH": baseHalfH = value; break;
                    case "Frame": {
                        String[] parts = value.split("\\s+");
                        if (parts.length > 0) indices.add(Integer.parseInt(parts[0]));
                        break;
                    }
                    default: break;
                }
            }
        }

        if (width <= 0 || height <= 0)
            throw new IOException("the export record has no image size: " + file.getName());
        if (!(step > 1))
            throw new IOException("the export record has an invalid zoom step: " + file.getName());
        if (location == null && baseHalfH == null)
            throw new IOException("the export record has no location: " + file.getName());
        double log2Base;
        if (location != null) {
            // The coordinates are in an *.im file named by the record, next to it.
            File directory = file.getAbsoluteFile().getParentFile();
            log2Base = readLocationHalfHeightLog2(new File(location).isAbsolute()
                    ? new File(location) : new File(directory, location));
        } else {
            log2Base = log2(new BigDecimal(baseHalfH));
            if (Double.isNaN(log2Base) || Double.isInfinite(log2Base) || log2Base > 1000)
                throw new IOException("the export record has an invalid half height (" + baseHalfH + "), "
                        + "it was probably written by an old version of Imagina: " + file.getName());
        }
        double log2Step = Math.log(step) / Math.log(2);

        // The frames are named after the record, with the index the record lists.
        String name = file.getName();
        String base = name.toLowerCase().endsWith(".imz") ? name.substring(0, name.length() - 4) : name;
        File directory = file.getAbsoluteFile().getParentFile();
        if (indices.isEmpty()) indices = frameIndices(directory, base);
        Collections.sort(indices);
        double maxIter = iterations > 0 ? iterations : DEFAULT_ITERATIONS;

        for (int index : indices) {
            File frame = frameFile(directory, base, index);
            if (frame == null) continue;
            // Half height of this key frame is CenterHalfH * Step^index, and the
            // animator's zoom level is log2(2 / half height), the depth Imagina
            // records.  Frame 0 is the deepest view, so the level falls with the
            // index as the frames get wider.
            double zooms = 1 - log2Base - index * log2Step;
            frameList.add(new ImaginaImage(frame, new FractalScale(zooms), width, height, maxIter));
        }
        if (frameList.isEmpty())
            throw new FileNotFoundException("no raw map files (" + base + "-NNNN.rawz) next to " + file.getName());

        Collections.sort(frameList);
    }

    /**
     * log2 of the half height which an *.im location file holds, the size of the
     * view an export started at.  The file is the one "File -> Save" writes: a
     * header (magic, reserved, the offset of the location), then the half height
     * as a double mantissa with a binary exponent, the iteration limit and the
     * centre coordinates as arbitrary precision values, which are not needed
     * here.
     */
    private static double readLocationHalfHeightLog2(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        if (data.length < 32)
            throw new IOException(file.getName() + " is too short to be a location file");
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.getLong(0) != IM_MAGIC)
            throw new IOException(file.getName() + " is not an Imagina location file (*.im)");
        long offset = buffer.getLong(16);
        if (offset < 0 || offset + 16 > data.length)
            throw new IOException(file.getName() + " has no location in it");
        double mantissa = buffer.getDouble((int) offset);
        long exponent = buffer.getLong((int) offset + 8);
        if (!(mantissa > 0))
            throw new IOException(file.getName() + " has an invalid half height");
        return Math.log(mantissa) / Math.log(2) + exponent;
    }

    private static List<Integer> frameIndices(File directory, String base) {
        List<Integer> indices = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File candidate : files) {
                Matcher matcher = FRAME_NAME.matcher(candidate.getName());
                if (matcher.matches() && matcher.group(1).equalsIgnoreCase(base))
                    indices.add(Integer.parseInt(matcher.group(2)));
            }
        }
        return indices;
    }

    private static File frameFile(File directory, String base, int index) {
        File rawz = new File(directory, String.format("%s-%04d.rawz", base, index));
        if (rawz.isFile()) return rawz;
        File raw = new File(directory, String.format("%s-%04d.raw", base, index));
        if (raw.isFile()) return raw;
        return null;
    }

    /** log2 of a positive decimal value, kept finite for very small numbers. */
    private static double log2(BigDecimal value) {
        if (value.signum() <= 0) return Double.NaN;
        double correction = 0;
        while (value.doubleValue() == 0.0 && value.scale() < 100000) {
            value = value.scaleByPowerOfTen(100);
            correction -= 100 * Math.log(10) / Math.log(2);
        }
        return Math.log(value.doubleValue()) / Math.log(2) + correction;
    }

    @Override
    public FractalImage get(int index) {
        return frameList.get(index);
    }

    @Override
    public int size() {
        return frameList.size();
    }

    class ImaginaImage extends FractalImage {
        private final File file;
        private final int width, height;
        private final double maxIter;
        private BufferedImage image;

        ImaginaImage(File file, FractalScale scale, int width, int height, double maxIter) {
            this.file = file;
            this.width = width;
            this.height = height;
            this.maxIter = maxIter;
            this.scale = scale;
        }

        @Override
        public BufferedImage getImage() throws Exception {
            return getImage(0);
        }

        @Override
        public BufferedImage getImage(double ratio) throws Exception {
            if (image == null) image = render();
            return image;
        }

        @Override
        public void close() {
            image = null;
        }

        private synchronized BufferedImage render() throws IOException {
            float[] pixels = RawzReader.isRawz(file)
                    ? RawzReader.read(file).pixels
                    : RawzReader.readPlain(file, width, height).pixels;
            if (pixels.length != width * height)
                throw new IOException(file.getName() + " does not match the size in the export record");

            BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double value = pixels[y * width + x];
                    // The pixels which stayed in the set are written as +inf,
                    // the rest is drawn by its iteration value (NaN and +inf fail
                    // the test and come out black, like the other loaders).
                    int rgb = 0;
                    if (value >= 0 && value < maxIter)
                        rgb = palette.getColor(value, density);
                    out.setRGB(x, y, rgb);
                }
            }
            return out;
        }
    }
}
