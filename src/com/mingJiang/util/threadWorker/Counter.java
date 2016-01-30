package com.mingJiang.util.threadWorker;

/**
 * @deprecated
 * @author Ming Jiang
 *
 */
public class Counter {

    private int max;
    private int num;

    /**
     * a new counter object with max set to max
     *
     * @param max
     */
    public Counter(int max) {
        if (max < 1) {
            throw new IllegalArgumentException("Counter max can not be less than 0");
        }
        this.max = max;
        num = 1;
    }

    public void reset() {
        num = 1;
    }

    /**
     * set the maximum of counter
     *
     * @param max
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * set the current count of counter
     *
     * @param num
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * check if the count equals max, if yes return true, else count increase by
     * one, return false;
     *
     * @return
     */
    public synchronized boolean isFinish() {
        System.out.println("count " + num + "   max: " + max);
        if (num == max) {
            return true;
        }
        num++;
        return false;
    }
}
