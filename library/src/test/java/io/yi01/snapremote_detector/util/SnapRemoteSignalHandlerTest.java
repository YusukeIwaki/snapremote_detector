package io.yi01.snapremote_detector.util;

import junit.framework.TestCase;

import org.junit.Test;

import io.yi01.snapremote_detector.SignalDetector;
import io.yi01.snapremote_detector.SnapRemoteSignalHandler;

public class SnapRemoteSignalHandlerTest extends TestCase {

    private boolean mReceived;

    @Test
    public void testAsignals() throws Exception {
        int[][] datas = new int[][]{
                new int[]{15859,15891,15891,15600,15590,15590},
                new int[]{15859,15891,15891,15600,15590,15590},
                new int[]{15913,15891,15891,15590,15590,15590},

                new int[]{15891,15891,15590,15590,15590,15590},
                new int[]{15891,15891,15891,15579,15568,15568},
                new int[]{15891,15891,15590,15579,15568,15568},
                new int[]{15891,15891,15579,15590,15568,15600}
        };

        int i=0;
        for (int[] data : datas) {
            mReceived = false;
            SnapRemoteSignalHandler c = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
                @Override
                public void onAccept(String name, double[] data) {
                    if ("A".equals(name)) mReceived = true;
                }

                @Override
                public void onReject() {
                }
            });

            for(int d: data) c.putResult(d);
            assertTrue(++i+"番目", mReceived);
        }
    }

    @Test
    public void testBsignals() throws Exception {
        int[][] datas = new int[][]{
                new int[]{16612,16602,16602,15590,15590,15546},
                new int[]{16602,16591,16591,15568,15579,15568},
                new int[]{15546,16602,16602,15590,15590,15590},
                new int[]{16623,16602,16602,15590,15590,15600},
                new int[]{16623,16602,16602,15590,15590,15600},

                new int[]{16602,16612,16602,15590,15590,15590},
                new int[]{16602,16602,15590,15579,15557,15579},
                new int[]{16634,16634,16634,15611,15611,15611},
                new int[]{16623,16623,16634,15611,15622,15611},
                new int[]{16602,16602,15579,15579,15568,15568},
                new int[]{16602,16602,16602,15579,15600,15568}
        };

        int i=0;
        for (int[] data : datas) {
            mReceived = false;
            SnapRemoteSignalHandler c = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
                @Override
                public void onAccept(String name, double[] data) {
                    if ("B".equals(name)) mReceived = true;
                }

                @Override
                public void onReject() {
                }
            });

            for(int d: data) c.putResult(d);
            assertTrue(++i+"番目", mReceived);
        }
    }

    public void testNonacceptableSignals() throws Exception {
        int[][] datas = new int[][]{
                new int[]{15934,15622,16612,15826,15848,15622},
                new int[]{15503,15837,15536,15579,15557,15514},
                new int[]{15611,15600,15600,15611,15611,15622},
                new int[]{15622,15633,16655,16655,15622,15600},
                new int[]{15557,15579,15557,15536,15557,15546},
                new int[]{15557,15557,15546,15568,15557,15579},
                new int[]{15536,15546,15546,15536,15525,15536},
                new int[]{15525,15536,15536,15536,15536,15536},
                new int[]{15536,16580,15546,15546,15536,15557},
                new int[]{15816,15826,15816,15816,15816,15816},
                new int[]{15816,15816,15816,15805,15805,15880},
                new int[]{15826,15826,15826,15826,15816,15816},
                new int[]{15837,15837,15837,15837,15826,15826},
                new int[]{15837,15837,15837,15837,15837,15837},
                new int[]{15848,15848,15848,15848,15837,15837},
                new int[]{15848,15848,15848,15848,15848,15848},
                new int[]{15859,15859,15859,15859,15859,15859},
                new int[]{15859,15859,15859,15859,15859,15859},
                new int[]{15848,15859,15859,15859,15859,15859},
                new int[]{15859,15859,15859,15859,15859,15859},
                new int[]{15546,15869,15633,15923,15869,15848}
        };

        int i=0;
        for (int[] data : datas) {
            mReceived = false;
            SnapRemoteSignalHandler c = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
                @Override
                public void onAccept(String name, double[] data) {
                    mReceived = true;
                }

                @Override
                public void onReject() {
                }
            });

            for(int d: data) c.putResult(d);
            assertFalse(++i+"番目", mReceived);
        }
    }

    public void testSignalsAfterCalibration() throws Exception {
        SnapRemoteSignalHandler c1 = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
            @Override
            public void onAccept(String name, double[] data) {
                mReceived = true;
            }

            @Override
            public void onReject() {
            }
        });

        SnapRemoteSignalHandler c2 = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
            @Override
            public void onAccept(String name, double[] data) {
                mReceived = true;
            }

            @Override
            public void onReject() {
            }
        }){
            @Override
            protected SignalDetector.Builder getSignalDetectorBuilderForA() {
                return super.getSignalDetectorBuilderForA()
                        .min(15860)
                        .tolerance(22);
            }
        };

        mReceived = false;
        c1.putResult(15860);
        c1.putResult(15891);
        c1.putResult(15612);
        c1.putResult(15590);
        c1.putResult(15568);
        c1.putResult(15600);
        assertFalse("キャリブレーションなし", mReceived);

        mReceived = false;
        c2.putResult(15860);
        c2.putResult(15891);
        c2.putResult(15612);
        c2.putResult(15590);
        c2.putResult(15568);
        c2.putResult(15600);
        assertTrue("キャリブレーションあり", mReceived);

    }

}