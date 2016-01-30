package com.mingJiang.data;

public class Pair<T, E> {

    private T key;
    private E obj;

    public Pair(T k, E o) {
        key = k;
        obj = o;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public E getObj() {
        return obj;
    }

    public void setObj(E obj) {
        this.obj = obj;
    }

    public String toString() {
        return key + "=\"" + obj + "\"";
    }

}
