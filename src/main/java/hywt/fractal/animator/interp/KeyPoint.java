package hywt.fractal.animator.interp;

import java.util.Map;
import java.util.Set;

public class KeyPoint implements Comparable<KeyPoint> {
    private Map<String, Double> data;

    public KeyPoint(Map<String, Double> data) {
        this.data = data;
    }

    public double getX() {
        return data.get("time");
    }

    public double getY() {
        return data.get("frame");
    }

    public double getData(String key) {
        return data.get(key);
    }

    @Override
    public int compareTo(KeyPoint o) {
        return Double.compare(getX(), o.getX());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[" + data.get("time") + "] ");
        data.forEach((k, v) -> builder.append(k).append(": ").append(v).append(" | "));
        return builder.toString();
    }

    public Set<Map.Entry<String, Double>> getData() {
        return data.entrySet();
    }
}
