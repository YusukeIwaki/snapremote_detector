package io.yi01.snapremote_detector.util;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class ArrayQueueTest extends TestCase {
    private ArrayQueue mQueue;

    @Before
    public void setUp() throws Exception {
        mQueue = new ArrayQueue(10);

    }

    @Test
    public void testQueue() throws Exception {
        mQueue.flush();

        for(int i=0; i<6; i++) mQueue.enqueue((short)i);
        assertEquals(6, mQueue.length());
        assertEquals(10, mQueue.size());

        for(int i=0; i<3; i++) {
            assertEquals(i, mQueue.dequeue());
        }
        assertEquals(3, mQueue.length());

        for(int i=0; i<7; i++) mQueue.enqueue((short)i);
        assertEquals(10, mQueue.length());
        assertEquals(10, mQueue.size());

        short[] expected = new short[]{3,4,5,0,1,2,3,4,5,6};
        short[] out = mQueue.flush();

        assertEquals(0, mQueue.length());
        assertEquals(10, mQueue.size());
        for(int i=0;i<10;i++) {
            assertEquals(expected[i], out[i]);
        }
    }
}