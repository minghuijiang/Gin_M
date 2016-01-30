package com.mingJiang.gui;

public class DefaultMessagable implements Messagable {

    @Override
    public void setMsg(String s) {
        System.out.println("MSG: "+s);
    }

    @Override
    public void log(String s) {
        System.out.println("LOG: " + s);
    }

    @Override
    public void error(String s) {
        System.err.println("ERR: "+s);
    
    }

}
