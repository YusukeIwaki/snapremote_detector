package io.yi01.snapremote_detector.util;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class PipeTest extends TestCase {
    @Test
    public void testPipe10() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        final short[] in = new short[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        Pipe pipe = new Pipe(10, new Pipe.Callback() {
            @Override
            public void onDataPrepared(short[] data) {
                assertEquals(10, data.length);
                for (int i = 0; i < 10; i++) {
                    assertEquals(in[i], data[i]);
                }
                semaphore.release();
            }
        });
        pipe.write(in);
        semaphore.acquire();
    }

    @Test
    public void testPipe3() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        final short[] in = new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Short[][] expected = {
                new Short[]{0,1,2},
                new Short[]{3,4,5},
                new Short[]{6,7,8},
                new Short[]{9,10,0},
                new Short[]{1,2,3},
                new Short[]{4,5,6},
                new Short[]{7,8,9}
        };
        final ArrayBlockingQueue<Short[]> expectedList = new ArrayBlockingQueue<>(100);
        for (Short[] l: expected) expectedList.offer(l);

        Pipe pipe = new Pipe(3, new Pipe.Callback() {
            @Override
            public void onDataPrepared(short[] data) {
                assertEquals(3, data.length);
                Short[] e = expectedList.poll();
                for(int i=0; i<3; i++) {
                    assertEquals((short)e[i], data[i]);
                }
                semaphore.release();
            }
        });
        pipe.write(in);
        semaphore.acquire();
        semaphore.acquire();
        semaphore.acquire();
        pipe.write(in);
        semaphore.acquire();
        semaphore.acquire();
        semaphore.acquire();
        semaphore.acquire();
    }
}