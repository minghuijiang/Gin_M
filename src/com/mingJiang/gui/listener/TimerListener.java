/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Ming Jiang
 */
public class TimerListener implements ActionListener{

    private int timeout;
    private JLabel msg;
    private String format;
    
    public TimerListener(JLabel msg, String formatStr,int time){
        timeout = time;
        this.msg = msg;
        this.format = formatStr;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (timeout > 0) {
               msg.setText(String.format(format, timeout));
               timeout--;
               
            } else {
               ((Timer)e.getSource()).stop();
               Window win = SwingUtilities.getWindowAncestor(msg);
               if(win!=null)
               win.dispose();
            }}
    
}
