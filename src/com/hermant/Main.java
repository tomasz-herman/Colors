package com.hermant;

import com.hermant.colors.Color3f;
import com.hermant.colors.Separation;
import com.hermant.gui.Window;

public class Main {

    public static void main(String[] args) {
        new Window("Colors", 1600, 1050);
        Color3f red = new Color3f(32, 124, 53);
        Separation separation = Separation.getRGBtoYCbCrSeparation();
        var color = separation.separate(red);
        for (Color3f color3f : color) {
            System.out.println(color3f);
        }
    }
}
