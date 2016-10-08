package io.yi01.snapremote_detector;

public class ApproximateValue {
    public int min;
    public int max;
    public int tolerance;

    public ApproximateValue() {}

    public ApproximateValue(int min, int max, int tolerance) {
        this.min = min;
        this.max = max;
        this.tolerance = tolerance;
    }
}
