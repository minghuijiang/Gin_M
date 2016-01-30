/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import com.mingJiang.dataType.refPrimitive.Ref;

/**
 * Take a Integer ref object, and update the value everytime comboBox been changed.
 * 
 * @author Ming Jiang
 */
public class ComboBoxListener implements ItemListener{

    private Ref<Integer> index;
    
    public ComboBoxListener(Ref<Integer> ref){
        index = ref;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if(source instanceof JComboBox){
            int in = ((JComboBox<?>)source).getSelectedIndex();
            index.setVal(in);
        }
    }
 
}
