package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;

public interface ScaleIndicator {
    void draw(Graphics g, FractalScale fractalScale, int width, int height);
}
