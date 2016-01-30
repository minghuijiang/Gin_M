package com.mingJiang.util;

import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


public class Util {

    public static boolean debug = false;

    public static void debug(String msg) {
        if (debug) {
            showMessage(msg);
        }
    }

    public static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public static void pop(String text) {
        debug(text);
        System.out.println(text);
    }

    public static void close(Closeable obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (IOException e) {
        }
    }


    public static <T> void print(List<T> list) {
        if (list != null) {
            for (T line : list) {
                System.out.println(line);
            }
        } else {
            System.out.println("list is null");
        }
    }
    
    public static <T> void print(List<T> list,String id) {
        if (list != null) {
            for (T line : list) {
                System.out.println(id+"  "+line);
            }
        } else {
            System.out.println(id+"  "+"list is null");
        }
    }

    public static void print(byte[] list) {
        int i = 0;
        String s ="";
        for (byte val : list) {
        	i++;
            s+=(Integer.toHexString(val)+ " ");
            if (i % 16 == 0) {
                System.out.println(s);
                s="";
            }
        }
        System.out.println(s);
    }

    public static <T> void print(T[] list) {
        int i = 0;
        for (T val : list) {
            System.out.print(val + " ");
            if (i % 16 == 0) {
                System.out.println();
            }
        }
    }

}
