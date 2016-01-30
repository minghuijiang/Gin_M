package com.mingJiang.data;

public class Location {

    public static String getCaller(int i) {
        return Thread.currentThread().getStackTrace()[i].toString();
    }
}
