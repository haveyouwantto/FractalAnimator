package hywt.fractal.animator.interp;

public class QuadraticInterpolator extends Interpolator {
    private final double[] xValues;
    private final double[] yValues;

    public QuadraticInterpolator(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    @Override
    public double get(double newX) {
        try {
            int i = findIndex(newX);
            if (i == 0) i = 1; // Ensure we have at least three points for interpolation
            if (i >= xValues.length - 1) i = xValues.length - 2; // Avoid going out of bounds

            double x0 = xValues[i - 1];
            double y0 = yValues[i - 1];
            double x1 = xValues[i];
            double y1 = yValues[i];
            double x2 = xValues[i + 1];
            double y2 = yValues[i + 1];

            return ((newX - x1) * (newX - x2)) / ((x0 - x1) * (x0 - x2)) * y0 +
                    ((newX - x0) * (newX - x2)) / ((x1 - x0) * (x1 - x2)) * y1 +
                    ((newX - x0) * (newX - x1)) / ((x2 - x0) * (x2 - x1)) * y2;
        } catch (ArrayIndexOutOfBoundsException e) {
            return yValues[yValues.length - 1] + newX;
        }
    }

    @Override
    public double getFirst() {
        return xValues[0];
    }

    @Override
    public double getLast() {
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
