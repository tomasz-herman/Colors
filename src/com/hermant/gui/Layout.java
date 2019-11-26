package com.hermant.gui;

import com.hermant.colors.Color3f;
import com.hermant.colors.Separation;
import com.hermant.graphics.Canvas;
import com.hermant.graphics.Texture;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    private static final Map<String, double[]> ILLUMINANTS = Map.ofEntries(Map.entry("A", new double[]{0.44757f, 0.40744f}), Map.entry("B", new double[]{0.34840f, 0.3516f}), Map.entry("C", new double[]{0.31006f, 0.31615f}), Map.entry("D50", new double[]{0.34567f, 0.3585f}), Map.entry("D55", new double[]{0.33242f, 0.34743f}), Map.entry("D65", new double[]{0.31273f, 0.32902f}), Map.entry("D75", new double[]{0.29902f, 0.31485f}), Map.entry("9300K", new double[]{0.2848f, 0.2932f}), Map.entry("E", new double[]{0.33333f, 0.33333f}), Map.entry("F2", new double[]{0.37207f, 0.37512f}), Map.entry("F7", new double[]{0.31285f, 0.32918f}), Map.entry("F11", new double[]{0.38054f, 0.37692f}));

    private static final Map<String, double[]> COLOR_SPACES = Map.of(
            "sRGB", new double[]{0.64, 0.33, 0.3, 0.6, 0.15, 0.06, 0.3127, 0.3290, 2.2},
            "Adobe RGB", new double[]{0.64, 0.33, 0.21, 0.71, 0.15, 0.06, 0.3127, 0.329, 2.2},
            "Apple RGB", new double[]{0.625, 0.340, 0.28, 0.595, 0.155, 0.07, 0.3127, 0.329, 1.8},
            "CIE RGB", new double[]{0.735, 0.265, 0.274, 0.717, 0.167, 0.009, 0.3333, 0.3333, 2.2},
            "Wide Gamut", new double[]{0.7347, 0.2653, 0.1152, 0.8264, 0.1566, 0.0177, 0.3457, 0.3585, 1.2},
            "PAL/SECAM", new double[]{0.64, 0.33, 0.29, 0.6, 0.15, 0.06, 0.3127, 0.329, 1.95}
    );

    private Canvas input_canvas;
    private Canvas output_canvas_0;
    private Canvas output_canvas_1;
    private Canvas output_canvas_2;


    Layout() {
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
        x_r_spinner.setModel(new SpinnerNumberModel(0.64f, 0.0001f, 1.0f, 0.01f));
        y_r_spinner.setModel(new SpinnerNumberModel(0.33f, 0.0001f, 1.0f, 0.01f));
        x_g_spinner.setModel(new SpinnerNumberModel(0.3f, 0.001f, 1.0f, 0.01f));
        y_g_spinner.setModel(new SpinnerNumberModel(0.6f, 0.001f, 1.0f, 0.01f));
        x_b_spinner.setModel(new SpinnerNumberModel(0.15f, 0.001f, 1.0f, 0.01f));
        y_b_spinner.setModel(new SpinnerNumberModel(0.06f, 0.001f, 1.0f, 0.01f));
        x_w_spinner.setModel(new SpinnerNumberModel(0.33f, 0.001f, 1.0f, 0.01f));
        y_w_spinner.setModel(new SpinnerNumberModel(0.33f, 0.001f, 1.0f, 0.01f));
        gamma_spinner.setModel(new SpinnerNumberModel(1.0f, 0.001f, 10.0f, 0.1f));
        save_button.setMnemonic('s');
        load_button.setMnemonic('l');
        save_button.addActionListener(e -> {
            int returnVal = output_chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = output_chooser.getSelectedFile();
                String resource = file.getPath();
                write(resource);
            }
        });
        load_button.addActionListener(e -> {
            int returnVal = input_chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = input_chooser.getSelectedFile();
                String resource = file.getPath();
                input_panel_border.setBorder(new TitledBorder(resource));
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
                setOutputNames(Objects.requireNonNull(separation_combo_box.getSelectedItem()).toString());
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
        illuminant_combo_box.addActionListener(e -> setWhitePoint(Objects.requireNonNull(illuminant_combo_box.getSelectedItem()).toString()));
        color_profile_combo_box.addActionListener(e -> setColorSpace(Objects.requireNonNull(color_profile_combo_box.getSelectedItem()).toString()));
    }

    Container getMainPanel() {
        return main_panel;
    }

    private void setOutputNames(String separation) {
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
        input_chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".png") || filename.endsWith(".jpg")
                            || filename.endsWith(".jpeg") || filename.endsWith(".bmp");
                }
            }

            @Override
            public String getDescription() {
                return "Images (*.png, *.jpg, *.bmp, *.BMP, .*JPG, .*jpeg, *.PNG, .*JPEG)";
            }
        });
    }

    private void setWhitePoint(String illuminant) {
        double[] values = ILLUMINANTS.get(illuminant);
        if (values == null) return;
        x_w_spinner.getModel().setValue(values[0]);
        y_w_spinner.getModel().setValue(values[1]);
    }

    private void setColorSpace(String colorSpace) {
        double[] values = COLOR_SPACES.get(colorSpace);
        if (values == null) {
            System.out.println(colorSpace + " not found");
            return;
        }
        x_r_spinner.getModel().setValue(values[0]);
        y_r_spinner.getModel().setValue(values[1]);
        x_g_spinner.getModel().setValue(values[2]);
        y_g_spinner.getModel().setValue(values[3]);
        x_b_spinner.getModel().setValue(values[4]);
        y_b_spinner.getModel().setValue(values[5]);
        x_w_spinner.getModel().setValue(values[6]);
        y_w_spinner.getModel().setValue(values[7]);
        gamma_spinner.getModel().setValue(values[8]);
    }

    private void write(String path) {
        if (texture != null) {
            setOutputNames(Objects.requireNonNull(separation_combo_box.getSelectedItem()).toString());
            Separation separation = SEPARATION_FUNCTIONS.get(Objects.requireNonNull(separation_combo_box.getSelectedItem()).toString()).get();
            BufferedImage image = new BufferedImage(texture.getWidth() * 2, texture.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < texture.getWidth(); i++) {
                for (int j = 0; j < texture.getHeight(); j++) {
                    Color3f color = new Color3f(texture.get(i, j));
                    var colors = separation.separate(color);
                    image.setRGB(i, j, color.getRGB());
                    image.setRGB(i + texture.getWidth(), j, colors[0].getRGB());
                    image.setRGB(i, j + texture.getHeight(), colors[1].getRGB());
                    image.setRGB(i + texture.getWidth(), j + texture.getHeight(), colors[2].getRGB());
                }
            }
            File file = new File(path);
            try {
                String format = null;
                if (path.contains(".")) {
                    var tokens = path.split("\\.");
                    format = tokens[tokens.length - 1];
                }
                if (format == null) throw new IOException("No format specified!");
                ImageIO.write(image, format, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
