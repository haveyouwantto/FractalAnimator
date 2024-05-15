package hywt.fractal.animator.interp;

public class LinearInterpolator implements Interpolator {
    private double[] xValues;
    private double[] yValues;

    public LinearInterpolator(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    @Override
    public double get(double newX) {
        try {
            int i = findIndex(newX);
            double x1 = xValues[i];
            double y1 = yValues[i];
            double x2 = xValues[i + 1];
            double y2 = yValues[i + 1];
            double slope = (y2 - y1) / (x2 - x1);
            double yIntercept = y1 - slope * x1;
            return slope * newX + yIntercept;
        } catch (ArrayIndexOutOfBoundsException e) {
            return yValues[yValues.length - 1] + newX;
        }
    }

    @Override
    public double getDuration() {
        return xValues[xValues.length - 1];
    }

    private int findIndex(double newX) {
        int i = 0;
        while (i < xValues.length - 1 && newX > xValues[i + 1]) {
            i++;
        }
        return i;
    }
}