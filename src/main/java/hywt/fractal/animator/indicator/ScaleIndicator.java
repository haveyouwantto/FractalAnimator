package hywt.fractal.animator.indicator;

import hywt.fractal.animator.keyframe.FractalScale;

import java.awt.*;

public interface ScaleIndicator {
    void draw(Graphics g, FractalScale fractalScale, int width, int height);
    void setScale(double scale) throws Exception;

    void setFont(Font font);
}
