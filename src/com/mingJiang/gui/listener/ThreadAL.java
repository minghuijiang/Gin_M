/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action Listener wrapper of thread.
 * @author Ming Jiang
 */
public class ThreadAL implements ActionListener{
    private Runnable run;
    
    public ThreadAL(Runnable run){
        this.run = run;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        run.run();
    }
    
}
