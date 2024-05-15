package hywt.fractal.animator.interp;

import java.util.Map;
import java.util.TreeMap;

public class AccelInterpolator implements Interpolator {
    TreeMap<Double, TurningPoint> points;

    public AccelInterpolator(double[][] speedDef) {
        points = new TreeMap<>();
        for (double[] def : speedDef) {
            points.put(def[0], new TurningPoint(def[1], def[2], def[3]));
        }
        System.out.println(points);
    }

    @Override
    public double get(double newX) {
        Map.Entry<Double, TurningPoint> a = points.lowerEntry(newX);
        if (a == null) a = points.firstEntry();
        Map.Entry<Double, TurningPoint> b = points.higherEntry(a.getKey());
        if (b == null) b = points.lastEntry();

        double startTrans = a.getValue().startTrans;
        double endTrans = a.getValue().endTrans;

        double duration = b.getKey() - a.getKey();

        if (duration > 0)
            return interpolate(a.getValue().zooms, b.getValue().zooms, (newX - a.getKey()) / duration, startTrans / duration, endTrans / duration);
        else return b.getValue().zooms + newX;
    }

    @Override
    public double getDuration() {
        return points.lastKey();
    }

    private double interpolate(double a, double b, double x, double t1, double t2) {

        if (t1 + t2 > 1) {
            throw new IndexOutOfBoundsException(t1 + " + " + t2 + " > 1");
        }

        double c = 1 - t1 - t2; // Upper base
        double d = 1; // Lower base

        double h = 2 / (c + d); // Height
        double scale = b - a; // Actual value difference

        double f = 1 - t2; // Second turning point

        if (x <= t1) {
            double ratio = x / t1;
            return a + (x * (h * ratio) / 2) * scale; // Triangle
        } else if (x <= 1 - t2) {
            return a + (h * t1 / 2 + h * (x - t1)) * scale; // Triangle + Rectangle
        } else {
            double ratio = (x - f) / t2;
            return a + (h * t1 / 2 + h * c + ((h + (h * (1 - ratio))) * (x - f) / 2)) * scale; // Triangle + Rectangle +
            // Trapezoid
        }
    }

    private record TurningPoint(double zooms, double startTrans, double endTrans) {
    }
}
