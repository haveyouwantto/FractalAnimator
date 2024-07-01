package hywt.fractal.animator.ui;

import hywt.fractal.animator.interp.KeyPoint;
import hywt.fractal.animator.interp.SlopeAccelInterpolator;

import java.util.Arrays;

public class SlopeAccelInterpolatorConfigure extends InterpolatorConfigure<SlopeAccelInterpolator> {
    @Override
    public SlopeAccelInterpolator get() {
        double[][] speedDef = new double[pointList.size()][2];
        for (int i = 0; i < pointList.size(); i++) {
            KeyPoint point = pointList.get(i);
            speedDef[i][0] = point.getX();
            speedDef[i][1] = point.getY();
        }
        System.out.println(Arrays.toString(speedDef));

        return new SlopeAccelInterpolator(speedDef);
    }

    @Override
    public void init() throws Exception {
        super.init();
        addDefault();
    }
}
