package hywt.fractal.animator.keyframe;

import java.util.Iterator;

public abstract class ImageLoader implements Iterable<FractalImage> {
    public abstract FractalImage get(int index);

    public abstract int size();

    public FractalImage acquire(int index) {
        try {
            return get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public FractalImage getFrameAtZoom(double zoom) {
        for (int i = 0; i < size(); i++) {
            FractalImage frame = get(i);
            if (frame.getScale().getZooms() > zoom) return get(i - 1);
        }
        return get(size() - 1);
    }

    @Override
    public Iterator<FractalImage> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public FractalImage next() {
                return get(index++);
            }
        };
    }

    public FractalImage getFirst() {
        return get(0);
    }

    public FractalImage getLast() {
        return get(size() - 1);
    }

    public FractalScale getFirstScale(){
        return getFirst().getScale();
    }

    public FractalScale getLastScale(){
        return getLast().getScale();
    }
}
