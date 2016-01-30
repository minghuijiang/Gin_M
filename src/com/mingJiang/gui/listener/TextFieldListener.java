/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/**
 * Select all text when focus on the text field.
 * @author Ming Jiang
 */
public class TextFieldListener implements FocusListener{
    @Override
    public void focusGained(FocusEvent e) {
         Object source = e.getSource();
        if (source instanceof JTextField) {
              ((JTextField) source).selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        
    }
    
}
