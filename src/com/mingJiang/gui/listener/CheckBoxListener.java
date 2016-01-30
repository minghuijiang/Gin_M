package com.mingJiang.gui.listener;

import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;

/**
 *  when programmatically change the checkbox selected state,
 * it will fire an item-change-event and state change event, 
 * without the action event. 
 * 
 * bind a checkBox with multiple component that will be enable or disable by the checkbox selection.
 * @author Ming Jiang
 */
public class CheckBoxListener implements java.awt.event.ItemListener {
    private final Component[] child;

    public CheckBoxListener(Component... args) {
        child = args;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source instanceof JCheckBox) {
            boolean isSelected= ((JCheckBox) source).isSelected();
            for(Component kid: child)
                kid.setEnabled(isSelected);
        }
    }

}
