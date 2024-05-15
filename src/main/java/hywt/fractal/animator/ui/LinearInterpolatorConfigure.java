package hywt.fractal.animator.ui;

import hywt.fractal.animator.interp.KeyPoint;
import hywt.fractal.animator.interp.LinearInterpolator;

public class LinearInterpolatorConfigure extends InterpolatorConfigure<LinearInterpolator> {

    @Override
    public LinearInterpolator get() {
        double[] x = new double[pointList.size()];
        double[] y = new double[pointList.size()];
        for (int i = 0; i < x.length; i++) {
            KeyPoint point = pointList.get(i);
            x[i] = point.getX();
            y[i] = point.getY();
        }
        return new LinearInterpolator(x, y);
    }

    @Override
    public void init() throws Exception {
        super.init();
        addDefault();
    }
}
