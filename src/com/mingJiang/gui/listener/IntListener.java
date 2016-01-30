/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.gui.listener;

import com.mingJiang.dataType.refPrimitive.Ref;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

/**
 * validate the int value in text field, if the number is inappropriate, change to default.
 * @author Ming Jiang
 */
public class IntListener extends TextFieldListener{

    private final Ref<Integer> val;
    private final int defVal;
    
    public IntListener(Ref<Integer> val, int def){
        this.val= val;
        defVal = def;
    }

    @Override
    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof JTextField) {
            int tmp = defVal;
            try{
                tmp = Integer.parseInt(((JTextField) source).getText());
            }catch(NumberFormatException ex){
                ((JTextField)source).setText(""+tmp);
            }
            val.setVal(tmp);
        }
    }
    
}
