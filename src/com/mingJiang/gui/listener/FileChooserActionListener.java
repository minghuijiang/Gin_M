package com.mingJiang.gui.listener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
public class FileChooserActionListener implements ActionListener{
	// text field display the choose file path
	private JTextField textField;
	
	public FileChooserActionListener(JTextField field){
		textField = field;
	}

	// when select button button is clicked, pop up the file chooser
        @Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(new File(textField.getText()).getAbsolutePath());
                if(System.getProperty("os.name").startsWith("Mac OS X")){
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }else{
                    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                }
		if(fc.showOpenDialog(textField.getParent().getParent())==JFileChooser.APPROVE_OPTION)
			textField.setText(fc.getSelectedFile().getAbsolutePath());
	}
	

}
