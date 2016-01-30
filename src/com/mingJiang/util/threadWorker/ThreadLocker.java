package com.mingJiang.util.threadWorker;
/**
 * @deprecated
 * @author Ming Jiang
 *
 */
public class ThreadLocker {

    private int worker;
    private final Object locker;
    private int current;

    public ThreadLocker(int num) {
        worker = num;
        current = 0;
        locker = new Object();
    }

    public void setWorker(int num) {
        worker = num;
    }

    public void getWorker() {
        synchronized (locker) {
            if (current >= worker) {
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                }
            }
            	System.out.println("new worker: "+current);
            current++;
        }
    }

    public void releaseWorker() {
        //	System.out.println("try release");
        synchronized (locker) {
            	System.out.println("release: "+current);
            current--;
            if (current < worker) {
                locker.notify();
            }
        }

    }

    public boolean isEmpty() {
        return current == 0;
    }
}
