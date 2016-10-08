package io.yi01.snapremote_detector;

public class CalibrationData {
    public ApproximateValue high;
    public ApproximateValue base;

    public CalibrationData() {
        high = new ApproximateValue();
        base = new ApproximateValue();
    }

    public CalibrationData(ApproximateValue high, ApproximateValue base) {
        this.high = high;
        this.base = base;
    }
}
