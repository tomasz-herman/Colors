package com.hermant;

import com.hermant.gui.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        new Window("Colors", 1600, 1050);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("./res/cat.png"));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        assert image != null;
        System.out.println(Arrays.toString(image.getData().getPixel(0, 0, (double[]) null)));
    }
}
