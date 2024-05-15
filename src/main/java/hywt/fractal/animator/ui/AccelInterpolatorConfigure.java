package hywt.fractal.animator.ui;

import hywt.fractal.animator.interp.AccelInterpolator;
import hywt.fractal.animator.interp.KeyPoint;

public class AccelInterpolatorConfigure extends InterpolatorConfigure<AccelInterpolator> {
    @Override
    public AccelInterpolator get() {
        double[][] speedDef = new double[3][4];
        for (int i = 0; i < pointList.size(); i++) {
            KeyPoint point = pointList.get(i);
            speedDef[i][0] = point.getX();
            speedDef[i][1] = point.getY();
            speedDef[i][2] = point.getData("accT");
            speedDef[i][3] = point.getData("decT");
        }

        return new AccelInterpolator(speedDef);
    }

    @Override
    public void init() throws Exception {
        super.init();
        addField("accT","Acceleration Time", 5);
        addField("decT", "Deceleration Time",5);
        addDefault();
    }
}
