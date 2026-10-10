package hywt.fractal.animator.keyframe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Reader for the compressed raw map files (*.rawz) which Imagina's zoom
 * sequence export writes.
 *
 * The container is documented in Imagina's RawMapCodec.h: a 32 byte header
 * followed by a zlib deflated payload, which holds one filter byte per image
 * row and that row's residuals as zigzag encoded variable length integers.
 * The values are float32 bit patterns and are restored exactly.
 *
 * This is an independent implementation, so it does not pull Imagina's licence
 * (AGPL-3.0) into this project.
 */
public class RawzReader {

    /** A decoded raw map; the first row of the file comes first. */
    public static class Image {
        public final int width;
        public final int height;
        public final float[] pixels;

        public Image(int width, int height, float[] pixels) {
            this.width = width;
            this.height = height;
            this.pixels = pixels;
        }
    }

    private static final int HEADER_SIZE = 32;
    private static final long MAX_PIXELS = 400L * 1000L * 1000L;

    /** True when the file starts with the rawz magic, otherwise it is a plain dump. */
    public static boolean isRawz(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            byte[] magic = new byte[4];
            int read = in.read(magic);
            return read == 4 && magic[0] == 'R' && magic[1] == 'A' && magic[2] == 'Z' && magic[3] == '1';
        }
    }

    public static Image read(File file) throws IOException {
        byte[] data = readAll(file);
        if (data.length < HEADER_SIZE || data[0] != 'R' || data[1] != 'A' || data[2] != 'Z' || data[3] != '1')
            throw new IOException("not a compressed raw map: " + file.getName());

        ByteBuffer header = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int width = header.getInt(4);
        int height = header.getInt(8);
        int flags = header.getInt(12);
        int format = header.getInt(16);
        long plainSize = header.getInt(20) & 0xFFFFFFFFL;
        long storedSize = header.getLong(24);

        if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS)
            throw new IOException("invalid image size in " + file.getName());
        if (format != 0)
            throw new IOException("unknown value format in " + file.getName());
        if (storedSize + HEADER_SIZE > data.length)
            throw new IOException("truncated raw map: " + file.getName());

        byte[] payload;
        int start;
        int payloadSize;
        if ((flags & 1) != 0) {
            payload = inflate(data, HEADER_SIZE, (int) storedSize, (int) plainSize);
            start = 0;
            payloadSize = payload.length;
        } else {
            payload = data;
            start = HEADER_SIZE;
            payloadSize = (int) storedSize;
        }

        return decode(width, height, payload, start, payloadSize);
    }

    /**
     * Reads a plain pixel dump (width * height float32, as "Save raw pixel data"
     * and "Imagina.exe --decode" write them).
     */
    public static Image readPlain(File file, int width, int height) throws IOException {
        byte[] data = readAll(file);
        long expected = (long) width * height * 4;
        if (data.length != expected)
            throw new IOException(file.getName() + " is " + data.length + " bytes, expected " + expected);
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        float[] pixels = new float[width * height];
        buffer.asFloatBuffer().get(pixels);
        return new Image(width, height, pixels);
    }

    private static Image decode(int width, int height, byte[] payload, int start, int payloadSize) throws IOException {
        float[] pixels = new float[width * height];
        // The predictors work on the ordered bit patterns, so a row buffer keeps
        // those while the pixels come out as floats.
        int[] row = new int[width];
        int[] above = new int[width];
        int position = start;
        for (int y = 0; y < height; y++) {
            if (position >= start + payloadSize) throw new IOException("the raw map ends inside a row");
            int filter = payload[position++] & 0xFF;
            if (filter == 0) {
                if (position + (long) width * 4 > start + payloadSize) throw new IOException("the raw map ends inside a row");
                for (int x = 0; x < width; x++) {
                    int bits = getInt(payload, position);
                    position += 4;
                    row[x] = (bits < 0) ? ~bits : (bits | 0x80000000);
                    pixels[y * width + x] = Float.intBitsToFloat(bits);
                }
            } else if (filter <= 3) {
                int left = 0, aboveLeft = 0;
                for (int x = 0; x < width; x++) {
                    if (position >= start + payloadSize) throw new IOException("the raw map ends inside a row");
                    long[] read = readVarint(payload, position, start + payloadSize);
                    position = (int) read[0];
                    int encoded = (int) read[1];
                    int upper = y > 0 ? above[x] : 0;
                    int predictor;
                    if (filter == 1) predictor = left;
                    else if (filter == 2) predictor = upper;
                    else predictor = paeth(left, upper, aboveLeft);
                    int zigzag = (encoded >>> 1) ^ -(encoded & 1);
                    int ordered = predictor + zigzag;
                    row[x] = ordered;
                    int bits = (ordered < 0) ? (ordered & 0x7FFFFFFF) : ~ordered;
                    pixels[y * width + x] = Float.intBitsToFloat(bits);
                    left = ordered;
                    aboveLeft = upper;
                }
            } else {
                throw new IOException("unknown row filter in the raw map");
            }
            int[] swap = above;
            above = row;
            row = swap;
        }
        return new Image(width, height, pixels);
    }

    private static int paeth(int left, int above, int aboveLeft) {
        long l = left & 0xFFFFFFFFL, a = above & 0xFFFFFFFFL, al = aboveLeft & 0xFFFFFFFFL;
        long estimate = l + a - al;
        long dl = Math.abs(estimate - l), da = Math.abs(estimate - a), dal = Math.abs(estimate - al);
        if (dl <= da && dl <= dal) return left;
        if (da <= dal) return above;
        return aboveLeft;
    }

    /** Returns {new position, value} of a variable length integer. */
    private static long[] readVarint(byte[] data, int position, int end) throws IOException {
        int value = 0;
        for (int shift = 0; shift <= 28; shift += 7) {
            if (position >= end) throw new IOException("the raw map ends inside a residual");
            int b = data[position++] & 0xFF;
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) return new long[]{position, value & 0xFFFFFFFFL};
        }
        throw new IOException("invalid residual in the raw map");
    }

    private static int getInt(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16) | ((data[offset + 3] & 0xFF) << 24);
    }

    private static byte[] inflate(byte[] data, int offset, int storedSize, int plainSize) throws IOException {
        if (plainSize <= 0) throw new IOException("invalid payload size");
        byte[] out = new byte[plainSize];
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(data, offset, storedSize);
            int written = 0;
            while (written < plainSize && !inflater.finished()) {
                int n = inflater.inflate(out, written, plainSize - written);
                if (n == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary())
                        throw new IOException("the payload cannot be expanded");
                }
                written += n;
            }
            if (written != plainSize) throw new IOException("the payload does not expand to the recorded size");
        } catch (DataFormatException e) {
            throw new IOException("the payload cannot be expanded: " + e.getMessage());
        } finally {
            inflater.end();
        }
        return out;
    }

    private static byte[] readAll(File file) throws IOException {
        if (file.length() > Integer.MAX_VALUE) throw new IOException("the file is too large");
        try (InputStream in = new FileInputStream(file)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) Math.max(1024, file.length()));
            byte[] buffer = new byte[64 * 1024];
            int n;
            while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
            return out.toByteArray();
        }
    }
}
