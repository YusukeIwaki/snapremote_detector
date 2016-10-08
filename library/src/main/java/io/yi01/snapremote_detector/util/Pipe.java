package io.yi01.snapremote_detector.util;

public class Pipe {
    public interface Callback {
        void onDataPrepared(short[] data);
    }

    private final ArrayQueue mBuffer;
    private final Callback mCallback;

    public Pipe(int size, Callback callback) {
        mBuffer = new ArrayQueue(size);
        mCallback = callback;
    }

    public void write(short[] data) {
        writeInner(data, 0);
    }

    private void writeInner(short[] data, int start) {
        int rest = mBuffer.size()-mBuffer.length();
        if (start+rest > data.length) {
            for (int i=start; i<data.length; i++) mBuffer.enqueue(data[i]);
        }
        else {
            for (int i=start; i<start+rest; i++) mBuffer.enqueue(data[i]);
            if (mCallback!=null) mCallback.onDataPrepared(mBuffer.flush());
            writeInner(data, start+rest);
        }
    }
}