package io.yi01.snapremote_detector;

public class SignalDetector {

    // general data structure >

    private final CalibrationData mCalibrationData;

    private SignalDetector() { mCalibrationData = new CalibrationData(); } // not intended to use!

    public SignalDetector(CalibrationData calibrationData) {
        mCalibrationData = calibrationData;
    }

    public boolean filter(double hz) {
        return filter(hz, mCalibrationData.base) || filter(hz, mCalibrationData.high);
    }

    public boolean acceptFirst3(double[] data) {
        int okCount=0;
        for (int i=0;i<3;i++) {
            double d = data[i];
            if (accept(d, mCalibrationData.high)) okCount++;
        }
        return okCount>=2;
    }

    public boolean acceptLast3(double[] data) {
        int len = data.length;
        int okCount=0;
        for (int i=0;i<3;i++) {
            double d = data[len-1-i];
            if (accept(d, mCalibrationData.base)) okCount++;
        }
        return okCount>=2;
    }

    private boolean filter(double hz, ApproximateValue v) {
        return hz >= v.min-v.tolerance && hz <= v.max+v.tolerance;
    }

    private boolean accept(double hz, ApproximateValue v) {
        return hz >= v.min && hz <= v.max;
    }

    // < general data structure


    // SnapRemote-specific implementation >

    public static abstract class Builder {
        CalibrationData calibrationData;

        public SignalDetector build() {
            return new SignalDetector(calibrationData);
        }

        public Builder min(int hz) {
            calibrationData.high.min = hz;
            return this;
        }

        public Builder max(int hz) {
            calibrationData.high.max = hz;
            return this;
        }

        public Builder tolerance(int tolerance) {
            calibrationData.high.tolerance = tolerance;
            return this;
        }

        public Builder baseMin(int hz) {
            calibrationData.base.min = hz;
            return this;
        }

        public Builder baseMax(int hz) {
            calibrationData.base.max = hz;
            return this;
        }

        public Builder baseTolerance(int tolerance) {
            calibrationData.base.tolerance = tolerance;
            return this;
        }

    }

    public static class ForA extends Builder {
        public ForA() {
            calibrationData = new CalibrationData(
                    new ApproximateValue(15880, 15902, 22),
                    new ApproximateValue(15568, 15611, 33)
            );
        }
    }

    public static class ForB extends Builder {
        public ForB() {
            calibrationData = new CalibrationData(
                    new ApproximateValue(16591, 16634, 0),
                    new ApproximateValue(15568, 15611, 33)
            );
        }
    }

    // < SnapRemote-specific implementation

}
