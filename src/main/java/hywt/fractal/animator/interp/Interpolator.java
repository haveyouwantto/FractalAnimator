package hywt.fractal.animator.interp;

public interface Interpolator {
    /**
     * Returns the interpolated value at the specified point.
     *
     * @param newX the point to interpolate at
     * @return the interpolated value at the specified point
     */
    double get(double newX);

}