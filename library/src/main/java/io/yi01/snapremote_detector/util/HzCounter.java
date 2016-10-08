package io.yi01.snapremote_detector.util;

public class HzCounter {
    public interface Filter {
        boolean accept(double hz);
    }
    public interface Callback {
        void onSeqCalled(double[] data);
    }

    private final int mCountMax;
    private final Filter mFilter;
    private final Callback mCallback;
    private int mCount;
    private double[] mData;
    public HzCounter(int count, Filter filter, Callback callback) {
        mCountMax = count;
        mFilter = filter;
        mCallback = callback;
        mData = new double[count];
        reset();
    }

    public void reset() {
        mCount = 0;
    }

    public void putResult(double hz) {
        if (mFilter.accept(hz)) {
            mData[mCount] = hz;
            if (++mCount>=mCountMax) {
                if (mCallback!=null) mCallback.onSeqCalled(mData);
                reset();
            }
        }
        else reset();
    }

}
