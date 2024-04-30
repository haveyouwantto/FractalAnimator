package hywt.fractal.animator.keyframe;

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
}
