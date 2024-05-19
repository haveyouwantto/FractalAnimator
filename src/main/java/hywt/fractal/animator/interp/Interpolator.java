package hywt.fractal.animator.interp;

public abstract class Interpolator {
    /**
     * Returns the interpolated value at the specified point.
     *
     * @param newX the point to interpolate at
     * @return the interpolated value at the specified point
     */
    public abstract double get(double newX);


    public abstract double getFirst();

    public abstract double getLast();

    public double getDuration(){
        return getLast();
    }

    public boolean isOutside(double x) {
        return x > getLast();
    }
}