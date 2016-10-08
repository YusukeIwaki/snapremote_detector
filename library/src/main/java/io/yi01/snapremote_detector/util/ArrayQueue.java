package io.yi01.snapremote_detector.util;

public class ArrayQueue {
    private short[] elements;
    private int head = 0;
    private int tail = 0;
    private int num = 0;
    Object lock = new Object();

    public ArrayQueue(int numElements) {
        elements = new short[numElements];
        num = 0;
    }

    public void enqueue(short e) {
        synchronized (lock) {
            elements[tail] = e;
            tail = (tail + 1) % elements.length;
            num++;
        }
    }

    public short dequeue() {
        synchronized (lock) {
            short result = elements[head];
            head = (head + 1) % elements.length;
            num--;
            return result;
        }
    }

    public int size() {
        return elements.length;
    }

    public int length() {
        return num;
    }

    public short[] flush() {
        synchronized (lock) {
            short[] out = new short[num];
            if (num==0) return out;

            if (tail <= head) {
                System.arraycopy(elements, head, out, 0, elements.length-head);
                System.arraycopy(elements, 0, out, elements.length-head, tail);
            }
            else {
                System.arraycopy(elements, head, out, 0, tail-head);
            }
            num = 0;
            tail=head;
            return out;
        }
    }
}
