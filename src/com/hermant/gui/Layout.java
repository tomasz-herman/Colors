package com.hermant.gui;

import com.hermant.colors.Color3f;
import com.hermant.colors.Separation;
import com.hermant.graphics.Canvas;
import com.hermant.graphics.Texture;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
    private JButton load_button;
    private JButton save_button;
    private JComboBox separation_combo_box;
    private JButton separate_button;
    private JComboBox color_profile_combo_box;
    private JComboBox illuminant_combo_box;
    private JSpinner x_r_spinner;
    private JSpinner y_r_spinner;
    private JSpinner x_g_spinner;
    private JSpinner y_g_spinner;
    private JSpinner x_b_spinner;
    private JSpinner y_b_spinner;
    private JSpinner x_w_spinner;
    private JSpinner y_w_spinner;
    private JSpinner gamma_spinner;
    private JFileChooser input_chooser;
    private JFileChooser output_chooser;
    private Texture texture;
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
    private final Map<String, Supplier<Separation>> SEPARATION_FUNCTIONS = Map.of(
            "RGB", Separation::getRGBtoRGBSeparation,
            "YCbCr", Separation::getRGBtoYCbCrSeparation,
            "HSV", Separation::getRGBtoHSVSeparation,
            "Lab", () -> Separation.getRGBtoLabSeparation(
                    (float) (double) x_r_spinner.getModel().getValue(), (float) (double) y_r_spinner.getModel().getValue(),
                    (float) (double) x_g_spinner.getModel().getValue(), (float) (double) y_g_spinner.getModel().getValue(),
                    (float) (double) x_b_spinner.getModel().getValue(), (float) (double) y_b_spinner.getModel().getValue(),
                    (float) (double) x_w_spinner.getModel().getValue(), (float) (double) y_w_spinner.getModel().getValue(),
                    (float) (double) gamma_spinner.getModel().getValue())
    );

    private Canvas input_canvas;
    private Canvas output_canvas_0;
    private Canvas output_canvas_1;
    private Canvas output_canvas_2;


    public Layout() {
        setPanels();
        setupFileChoosers();
    }

    private void setPanels() {
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
        separation_combo_box.addActionListener(e -> setOutputNames(Objects.requireNonNull(separation_combo_box.getSelectedItem()).toString()));
        x_r_spinner.setModel(new SpinnerNumberModel(0.64f, 0.0f, 1.0f, 0.01f));
        y_r_spinner.setModel(new SpinnerNumberModel(0.33f, 0.0f, 1.0f, 0.01f));
        x_g_spinner.setModel(new SpinnerNumberModel(0.3f, 0.0f, 1.0f, 0.01f));
        y_g_spinner.setModel(new SpinnerNumberModel(0.6f, 0.0f, 1.0f, 0.01f));
        x_b_spinner.setModel(new SpinnerNumberModel(0.15f, 0.0f, 1.0f, 0.01f));
        y_b_spinner.setModel(new SpinnerNumberModel(0.06f, 0.0f, 1.0f, 0.01f));
        x_w_spinner.setModel(new SpinnerNumberModel(0.33f, 0.0f, 1.0f, 0.01f));
        y_w_spinner.setModel(new SpinnerNumberModel(0.33f, 0.0f, 1.0f, 0.01f));
        gamma_spinner.setModel(new SpinnerNumberModel(1.0f, 0.0f, 10.0f, 0.1f));
        save_button.setMnemonic('s');
        load_button.setMnemonic('l');
        save_button.addActionListener(e -> {
            int returnVal = output_chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = output_chooser.getSelectedFile();
                String resource = file.getPath();
            }
        });
        load_button.addActionListener(e -> {
            int returnVal = input_chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = input_chooser.getSelectedFile();
                String resource = file.getPath();
                try {
                    texture = new Texture(resource);
                    for (int i = 0; i < input_canvas.getWidth(); i++) {
                        for (int j = 0; j < input_canvas.getHeight(); j++) {
                            input_canvas.setPixel(i, j, texture.getSampleBilinearInterpolation((i + 0.5f) / input_canvas.getWidth(), (j + 0.5f) / input_canvas.getHeight()));
                        }
                    }
                    input_canvas.repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        separate_button.addActionListener(e -> {
            if (texture != null) {
                Separation separation = SEPARATION_FUNCTIONS.get(Objects.requireNonNull(separation_combo_box.getSelectedItem()).toString()).get();
                for (int i = 0; i < output_canvas_0.getWidth(); i++) {
                    for (int j = 0; j < output_canvas_0.getHeight(); j++) {
                        Color3f color = new Color3f(texture.getSampleBilinearInterpolation((i + 0.5f) / output_canvas_0.getWidth(), (j + 0.5f) / output_canvas_0.getHeight()));
                        var colors = separation.separate(color);
                        output_canvas_0.setPixel(i, j, colors[0]);
                        output_canvas_1.setPixel(i, j, colors[1]);
                        output_canvas_2.setPixel(i, j, colors[2]);
                    }
                }
                output_canvas_0.repaint();
                output_canvas_1.repaint();
                output_canvas_2.repaint();
            }
        });
    }

    public Container getMainPanel() {
        return main_panel;
    }

    public void setOutputNames(String separation) {
        String[] values = SEPARATIONS.get(separation);
        if (values == null) return;
        output_panel_0_border.setBorder(new TitledBorder(values[0]));
        output_panel_1_border.setBorder(new TitledBorder(values[1]));
        output_panel_2_border.setBorder(new TitledBorder(values[2]));
    }

    private void setupFileChoosers() {
        File workingDirectory = new File(System.getProperty("user.dir"));
        input_chooser = new JFileChooser();
        output_chooser = new JFileChooser();
        input_chooser.setCurrentDirectory(workingDirectory);
        output_chooser.setCurrentDirectory(workingDirectory);
    }

}
