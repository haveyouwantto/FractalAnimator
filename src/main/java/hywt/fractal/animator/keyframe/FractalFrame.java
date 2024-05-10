package hywt.fractal.animator.keyframe;

import java.awt.image.BufferedImage;
import java.io.Closeable;

public abstract class FractalFrame implements Comparable<FractalFrame>, Closeable {
    FractalScale scale;

    public FractalScale getScale() {
        return scale;
    }

    @Override
    public int compareTo(FractalFrame o) {
        return Double.compare(scale.getZooms(), o.scale.getZooms());
    }

    public abstract BufferedImage getImage() throws Exception;

    @Override
    public String toString() {
        return "FractalFrame{" +
                "scale=" + scale +
                '}';
    }
}
