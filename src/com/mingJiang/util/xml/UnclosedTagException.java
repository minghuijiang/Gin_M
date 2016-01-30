package com.mingJiang.util.xml;

import java.io.IOException;

public class UnclosedTagException extends IOException {

    private static final long serialVersionUID = 8392522699297819929L;

    public UnclosedTagException(String string) {
        super(string);
    }

}
