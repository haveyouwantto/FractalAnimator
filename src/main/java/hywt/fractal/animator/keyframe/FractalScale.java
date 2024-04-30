package hywt.fractal.animator.keyframe;

public class FractalScale {
    private final double zooms;

    public FractalScale(double zooms) {
        this.zooms = zooms;
    }

    public double getZooms() {
        return zooms;
    }

    public double getLog10Zooms() {
        return Math.log10(2) * zooms;
    }

    public String getMagnification(int precision) {
        double log10Zoom = getLog10Zooms();

        if (log10Zoom <= 7) {
            return String.format(("%." + precision + "f"), Math.pow(10, log10Zoom));
        } else {
            return String.format(("%." + precision + "fE%+d"), Math.pow(10, log10Zoom - (int) (log10Zoom)), (int) (log10Zoom));
        }
    }

    public String getMagnification() {
        return getMagnification(2);
    }

    public static FractalScale fromMagnification(String str) {
        String[] parts = str.toLowerCase().split("e");
        double base = Double.parseDouble(parts[0]);

        if (base == 0) {
            return new FractalScale(-99999999);
        }

        int exponent = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        double log2Base = Math.log(base) / Math.log(2);

        return new FractalScale(log2Base + Math.log(10) / Math.log(2) * exponent);
    }

    public static FractalScale fromSize(String str) {
        String[] parts = str.toLowerCase().split("e");
        double base = Double.parseDouble(parts[0]);

        if (base == 0) {
            return new FractalScale(-99999999);
        }

        int exponent = -(parts.length > 1 ? Integer.parseInt(parts[1]) : 0);

        double log2Base = -(Math.log(base) / Math.log(2) - 2);

        return new FractalScale(log2Base + Math.log(10) / Math.log(2) * exponent);
    }

    @Override
    public String toString() {
        return getMagnification();
    }
}
