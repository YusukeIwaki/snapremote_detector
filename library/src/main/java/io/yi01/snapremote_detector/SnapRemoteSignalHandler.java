package io.yi01.snapremote_detector;

import io.yi01.snapremote_detector.util.HzCounter;

/**
 * implementation of SnapRemote signal detection.
 *
 * You can use customized calibration value by overriding
 * the getSignalDetectorBuilderForA or getSignalDetectorBuilderForB method.
 */
public class SnapRemoteSignalHandler {

    public interface Callback {
        void onAccept(String name, double[] data);
        void onReject();
    }

    private final SignalDetector mDetectorA;
    private final SignalDetector mDetectorB;
    protected Callback mCallback;
    private final HzCounter mHzCounter;

    public SnapRemoteSignalHandler() {
        mDetectorA = getSignalDetectorBuilderForA().build();
        mDetectorB = getSignalDetectorBuilderForB().build();

        mHzCounter = new HzCounter(6, new HzCounter.Filter() {
            @Override
            public boolean accept(double maxHz) {
                return mDetectorA.filter(maxHz) || mDetectorB.filter(maxHz);
            }
        }, new HzCounter.Callback() {
            @Override
            public void onSeqCalled(double[] data) {
                if (mDetectorA.acceptFirst3(data) && mDetectorA.acceptLast3(data)) {
                    if (mCallback!=null) mCallback.onAccept("A", data);
                    return;
                }
                if (mDetectorB.acceptFirst3(data) && mDetectorB.acceptLast3(data)) {
                    if (mCallback!=null) mCallback.onAccept("B", data);
                    return;
                }

                if (mCallback!=null) mCallback.onReject();
            }
        });
    }

    public SnapRemoteSignalHandler(Callback callback) {
        this();
        setCallback(callback);
    }

    public final void setCallback(Callback callback) {
        mCallback = callback;
    }

    protected SignalDetector.Builder getSignalDetectorBuilderForA() {
        return new SignalDetector.ForA();
    }

    protected SignalDetector.Builder getSignalDetectorBuilderForB() {
        return new SignalDetector.ForB();
    }

    public boolean shouldCutOff(double hz) {
        return hz < 15127;
    }

    public final void putResult(double hz) {
        mHzCounter.putResult(hz);
    }

    public final void reset() {
        mHzCounter.reset();
    }
}
