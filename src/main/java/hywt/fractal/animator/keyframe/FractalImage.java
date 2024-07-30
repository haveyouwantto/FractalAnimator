package hywt.fractal.animator.keyframe;

import java.awt.image.BufferedImage;
import java.io.Closeable;

public abstract class FractalImage implements Comparable<FractalImage>, Closeable {
    FractalScale scale;

    public FractalScale getScale() {
        return scale;
    }

    @Override
    public int compareTo(FractalImage o) {
        return Double.compare(scale.getZooms(), o.scale.getZooms());
    }

    public abstract BufferedImage getImage() throws Exception;

    public BufferedImage getImage(double ratio) throws Exception {
        return getImage();
    }

    @Override
    public String toString() {
        return "FractalFrame{" +
                "scale=" + scale +
                '}';
    }
}
