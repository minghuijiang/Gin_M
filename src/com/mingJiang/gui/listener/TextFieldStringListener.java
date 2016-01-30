/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import com.mingJiang.dataType.refPrimitive.Ref;

/**
 * Update String ref when text changed.
 * @author Ming Jiang
 */
public class TextFieldStringListener extends TextFieldListener{

    private Ref<String> val ; 
    
    public TextFieldStringListener(Ref<String> val){
        this.val = val;
    }

    @Override
    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof JTextField) {
            val.setVal(((JTextField) source).getText());
        }
    
    }
    
}
