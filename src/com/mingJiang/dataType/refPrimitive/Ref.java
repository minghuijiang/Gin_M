package com.mingJiang.dataType.refPrimitive;

/**
 * create a object wrapper to pass primitive object as reference.
 *
 * @author Ming Jiang
 *
 * @param <T>
 */
public class Ref<T> {

    private T val;

    public Ref(T b) {
        setVal(b);
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }

    public String toString() {
        return val.toString();
    }
}
