package hywt.fractal.animator.interp;


import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SlopeAccelInterpolator extends Interpolator {
    TreeMap<Double, TurningPoint> points;

    public SlopeAccelInterpolator(double[][] speedDef) {
        points = new TreeMap<>();
        for (double[] def : speedDef) {
            points.put(def[0], new TurningPoint(def[1]));
        }
        System.out.println(points);
    }

    @Override
    public double get(double newX) {
        Map.Entry<Double, TurningPoint> a = points.lowerEntry(newX);
        if (a == null) a = points.firstEntry();
        Map.Entry<Double, TurningPoint> b = points.higherEntry(a.getKey());
        if (b == null) b = points.lastEntry();

        double duration = b.getKey() - a.getKey();

        if (duration > 0) {
            double slope1;
            if (a.getKey().equals(points.firstKey())) {
                slope1 = 0;
            } else {
                Map.Entry<Double,TurningPoint> c = points.lowerEntry(a.getKey());
                slope1 = (a.getValue().zooms - c.getValue().zooms) / (a.getKey() - c.getKey());
            }

            double slope2;
            if (b.getKey().equals(points.lastKey())) {
                slope2 = 0;
            } else {
                slope2 = (b.getValue().zooms - a.getValue().zooms) / (b.getKey() - a.getKey());
            }

            return interpolate(
                    a.getKey(), b.getKey(),
                    a.getValue().zooms,
                    b.getValue().zooms,
                    (newX - a.getKey()) / duration,
                    slope1, slope2, 0.5,0.5
            );
        }
        else return b.getValue().zooms + newX;
    }

    @Override
    public double getFirst() {
        return points.firstKey();
    }

    @Override
    public double getLast() {
        return points.lastKey();
    }

    public double interpolate(double x1, double x2, double y1, double y2, double x, double k1, double k2, double a, double b) {
        if (a + b > 1) {
            throw new IllegalArgumentException("a + b must not be greater than 1");
        }

        double d = x2 - x1; // Length of the interval

        // Normalize y1 and y2
        double y1o = y1;
        y1 /= d;
        y2 /= d;

        double S = y2 - y1; // Slope change over the interval

        // Calculate the height of the shape (yt)
        double yt = (2 * S - a * k1 - b * k2) / (2 - a - b);

        // Calculate start and end slopes
        double startSlope = (a * k1) / a;
        double endSlope = (yt * a) / a;

        // Check if slopes have reversed signs
        boolean reversedSign = isReversedSign(startSlope, endSlope);

        double integral = y1o;

        // Perform integration based on the sign of slopes
        if (reversedSign) {
            if (k2 == 0) {
                double part1p = S / (k1 / 2);

                double t = Math.min(x, part1p);
                integral += (((-k1) * (t * t)) / (2 * part1p) + k1 * t) * d;

            } else {
                double part1p = S / k1;
                double bottom = S / 2;

                double t = Math.min(x, part1p);
                integral += (((-k1) * (t * t)) / (2 * part1p) + k1 * t) * d;

                t = x;
                integral += bottom * t * d;
            }
        } else {
            double t = Math.min(x, a);
            integral += (((yt - k1) * (t * t)) / (2 * a) + k1 * t) * d;

            t = Math.max(0, Math.min(x - a, 1 - a - b));
            integral += yt * t * d;

            t = Math.max(0, Math.min(x - 1 + b, 1));
            integral += -d * ((t * ((t - 2 * b) * yt - k2 * t)) / (2 * b));
        }

        return integral;
    }

    // Function to check if slopes have reversed signs
    public static boolean isReversedSign(double a, double b) {
        return (a >= 0) ^ (b >= 0);
    }

    private record TurningPoint(double zooms) {
    }
}
