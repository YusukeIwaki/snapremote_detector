package io.yi01.snapremote_detector.util;

import junit.framework.TestCase;

import org.junit.Test;

public class HzCounterTest extends TestCase {

    boolean mTarget;
    double[] mFirst3;

    @Test
    public void testSeqCountNotCalled() throws Exception {
        mTarget = true;
        HzCounter c = new HzCounter(4, new HzCounter.Filter() {
            @Override
            public boolean accept(double hz) {
                return hz<100;
            }
        }, new HzCounter.Callback() {
            @Override
            public void onSeqCalled(double[] data) {
                mTarget = false;
            }
        });
        c.putResult(10);
        c.putResult(10);
        c.putResult(10000);
        c.putResult(10);
        c.putResult(10);
        assertTrue(mTarget);
    }

    @Test
    public void testSeqCountCalled() throws Exception {
        mTarget = true;
        mFirst3 = new double[3];
        HzCounter c = new HzCounter(3, new HzCounter.Filter() {
            @Override
            public boolean accept(double hz) {
                return hz>100;
            }
        }, new HzCounter.Callback() {
            @Override
            public void onSeqCalled(double[] data) {
                mTarget = false;
                for (int i=0; i<3; i++) mFirst3[i]=data[i];
            }
        });
        c.putResult(10000);
        c.putResult(10);
        c.putResult(12345);
        c.putResult(23456);
        c.putResult(34567);
        assertFalse(mTarget);
        assertEquals(12345, (int)mFirst3[0]);
        assertEquals(23456, (int)mFirst3[1]);
        assertEquals(34567, (int)mFirst3[2]);
    }
}