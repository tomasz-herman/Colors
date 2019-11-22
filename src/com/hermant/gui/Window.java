package com.hermant.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private Layout layout;
    private String title;
    public Window(String title, int width, int height){
        this.title = title;
        layout = new Layout();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setContentPane(layout.getMain());
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
