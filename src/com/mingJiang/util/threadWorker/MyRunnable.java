package com.mingJiang.util.threadWorker;
/**
 * @deprecated
 * @author Ming Jiang
 *
 */
public abstract class MyRunnable<T> {

    //object to be process
    protected T obj;

    //default constructor, do nothing
    public MyRunnable() {
    }

    // set object and begin run.
    public void run(T t) {
        obj = t;
        this.run();
    }
	// need to be implements for sub class
    // the main task to be performed
    public abstract void run();

    public T getObj() {
        return obj;
    }

    // get instance of the sub class
    public abstract MyRunnable<T> getInstance();
}
