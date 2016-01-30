/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import com.mingJiang.util.PanelUtil;

/**
 *
 * @author Ming Jiang
 */
public class WarningAL implements ActionListener{
    
    private String msg;
    private int timeout;
    
    public WarningAL(String msg,int timeout){
        this.msg = msg;
        if(timeout>0){
            this.timeout= timeout;
            this.msg+="  %d秒后取消";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source  = e.getSource();
        if(source instanceof JCheckBox){
            boolean isSelect = ((JCheckBox)source).isSelected();
            if(isSelect){
                if(!PanelUtil.showComfirmation(msg, ((JCheckBox)source).getParent(), timeout))
                    ((JCheckBox)source).setSelected(false);
            }
        }
    }
    
}
