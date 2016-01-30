/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mingJiang.util;

import com.mingJiang.gui.listener.TimerListener;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Ming Jiang
 */
public class PanelUtil {

	public static final int YES = 1;
	public static final int NO= 0;
    public static boolean showComfirmation(String message, Component parent, int timeout) {
        if (timeout > 0) {
            JLabel msg = new JLabel(String.format(message, timeout));
            new Timer(1000, new TimerListener(msg, message, timeout-1)).start();
            return JOptionPane.showConfirmDialog(parent, msg,"弹窗",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        } else {
            return JOptionPane.showConfirmDialog(parent, message,"弹窗",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }
    }
    
    public static boolean showComfirmation(String message, int timeout){
        return showComfirmation(message,null,timeout);
    }
    
    public static boolean showComfirmation(String message){
        return showComfirmation(message,null,0);
    }
    
    public static void showInfo(String message, Component parent, int timeout) {
        if (timeout > 0) {
            JLabel msg = new JLabel(String.format(message, timeout));
            new Timer(1000, new TimerListener(msg, message, timeout-1)).start();
            JOptionPane.showMessageDialog(parent, msg);
        } else {
            JOptionPane.showMessageDialog(parent, message);
        }
    }
    public static void showInfo(String message, int timeout){
         showInfo(message,null,timeout);
    }
    
    public static void showInfo(String message){
        showInfo(message,null,0);
    }
    
}
