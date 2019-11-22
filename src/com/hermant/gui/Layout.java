package com.hermant.gui;

import com.hermant.colors.Transformation;
import com.hermant.graphics.Canvas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Layout {

    private JPanel main_panel;
    private JPanel output_panel_0_border;
    private JPanel output_panel_1_border;
    private JPanel output_panel_2_border;
    private JPanel input_panel_border;
    private JPanel input_panel;
    private JPanel output_panel_0;
    private JPanel output_panel_1;
    private JPanel output_panel_2;
    private static final int INPUT_PANEL_HEIGHT = 512;
    private static final int INPUT_PANEL_WIDTH = 768;
    private static final int OUTPUT_PANEL_HEIGHT = 384;
    private static final int OUTPUT_PANEL_WIDTH = 384 * 3 / 2;
    private static final Map<String, String[]> SEPARATIONS = Map.of(
            "YCbCr", new String[]{"Y", "Cb", "Cr"},
            "HSV", new String[]{"H", "S", "V"},
            "RGB", new String[]{"R", "G", "B"},
            "Lab", new String[]{"L", "a", "b"}
    );
    private static final Map<String, Transformation> TRANSFORMATIONS;

    static {
        TRANSFORMATIONS = new HashMap<>();
        TRANSFORMATIONS.put("YCbCr", null);
        TRANSFORMATIONS.put("HSV", null);
    }

    private Canvas input_canvas;
    private Canvas output_canvas_0;
    private Canvas output_canvas_1;
    private Canvas output_canvas_2;


    public Layout() {
        setPanels();
    }

    private void setPanels(){
        output_panel_0.setLayout(new GridLayout());
        output_panel_1.setLayout(new GridLayout());
        output_panel_2.setLayout(new GridLayout());
        input_panel.setLayout(new GridLayout());
        output_panel_0.revalidate();
        output_panel_1.revalidate();
        output_panel_2.revalidate();
        input_panel.revalidate();
        output_panel_0.add(output_canvas_0 = new com.hermant.graphics.Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        output_panel_1.add(output_canvas_1 = new com.hermant.graphics.Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        output_panel_2.add(output_canvas_2 = new com.hermant.graphics.Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        input_panel.add(input_canvas = new com.hermant.graphics.Canvas(INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT));
        setOutputNames("HSV");
    }

    public Container getMain() {
        return main_panel;
    }

    public void setOutputNames(String separation){
        String[] values = SEPARATIONS.get(separation);
        if(values == null) return;
        output_panel_0_border.setBorder(new TitledBorder(values[0]));
        output_panel_1_border.setBorder(new TitledBorder(values[1]));
        output_panel_2_border.setBorder(new TitledBorder(values[2]));
    }

}
