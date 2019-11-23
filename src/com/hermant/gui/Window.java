package com.hermant.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private String title;
    public Window(String title, int width, int height){
        this.title = title;
        Layout layout = new Layout();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setContentPane(layout.getMainPanel());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setMinimumSize(new Dimension(640, 480));
        setResizable(false);
        pack();
        revalidate();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setText(String text){
        setTitle(title + " " + text);
    }
}
