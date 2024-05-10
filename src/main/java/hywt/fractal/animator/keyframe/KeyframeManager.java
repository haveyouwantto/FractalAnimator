package hywt.fractal.animator.keyframe;

import java.util.Iterator;

public abstract class KeyframeManager implements Iterable<FractalFrame> {
    public abstract FractalFrame get(int index);

    public abstract int size();

    public FractalFrame getFrameAtZoom(double zoom) {
        for (int i = 0; i < size(); i++) {
            FractalFrame frame = get(i);
            if (frame.getScale().getZooms() > zoom) return get(i - 1);
        }
        return get(size() - 1);
    }

    @Override
    public Iterator<FractalFrame> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public FractalFrame next() {
                return get(index++);
            }
        };
    }

    public FractalFrame getFirst() {
        return get(0);
    }

    public FractalFrame getLast() {
        return get(size() - 1);
    }

    public FractalScale getFirstScale(){
        return getFirst().getScale();
    }

    public FractalScale getLastScale(){
        return getLast().getScale();
    }
}
