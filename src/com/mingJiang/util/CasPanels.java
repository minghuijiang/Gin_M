package com.mingJiang.util;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CasPanels extends JPanel {

    private static final long serialVersionUID = 5116039426214015675L;
    /**
     * locker control the return value
     */
    private final Object locker;
    /**
     * image label
     */
    private JLabel imageLabel;
    /**
     * input field ,implement with key listener, enter key will have same effect
     * as submit button
     */
    private JTextField field;
    /**
     * submit button,
     */
    private JButton button;
    /**
     * the image url;
     */
    private String url;

    public CasPanels(String imageUrl, Object lock) {
        url = imageUrl;
        //control the value return;
        locker = lock;
        setComponents();
    }

    /**
     * update the image by url,
     *
     * @param image
     */
    public void setURL(String image) {
        url = image;
        updateImage();
    }

    /**
     * update image by Image Object, may cause the image display unfinish.
     *
     * @param image
     */
    public void setImage(Image image) {
        imageLabel.setIcon(new ImageIcon(image));
        field.setText("");
        repaint();
    }

    public void setImage(byte[] b) {
        imageLabel.setIcon(new ImageIcon(b));
        field.setText("");
        repaint();
    }

    /**
     * download image from url, and repaint the new image;
     */
    public void updateImage() {

        try {
            imageLabel.setIcon(new ImageIcon(new URL(url)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        field.setText("");
        repaint();
    }

    /**
     * initialized component.
     */
    public void setComponents() {

        //create label
        imageLabel = new JLabel("");

        field = new JTextField(10);
        button = new JButton("提交");
        // add button listener 
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                releaseLock();
            }
        });
        // add keyListener
        field.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    releaseLock();
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        setLayouts();
        setVisible(true);
    }

    // set layout
    public void setLayouts() {
        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addComponent(imageLabel, -1, -1, -1).addContainerGap())
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addComponent(field, -1, -1, -1).addGap(5)
                        .addComponent(button, -1, -1, -1).addContainerGap())
                .addGap(5));

        layout.setVerticalGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(imageLabel, -1, -1, -1).addGap(10)
                .addGroup(layout.createParallelGroup()
                        .addComponent(field, -1, -1, -1)
                        .addComponent(button, -1, -1, -1))
                .addContainerGap());
        this.setLayout(layout);
    }

    /**
     * release lock, return the cas string
     */
    public void releaseLock() {
        synchronized (locker) {
            locker.notifyAll();
        }
    }

    /**
     * lock the method until button is clicked or enter is pressed
     *
     * @return
     */

    public String getCas() {
        synchronized (locker) {
            try {
                locker.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return field.getText();
    }

}
