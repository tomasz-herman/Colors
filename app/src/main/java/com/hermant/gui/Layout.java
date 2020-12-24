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
        output_panel_0.add(output_canvas_0 = new Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        output_panel_1.add(output_canvas_1 = new Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        output_panel_2.add(output_canvas_2 = new Canvas(OUTPUT_PANEL_WIDTH, OUTPUT_PANEL_HEIGHT));
        input_panel.add(input_canvas = new Canvas(INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT));
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main_panel = new JPanel();
        main_panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        main_panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "output", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        output_panel_0_border = new JPanel();
        output_panel_0_border.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(output_panel_0_border, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        output_panel_0_border.setBorder(BorderFactory.createTitledBorder(null, "R", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        output_panel_0 = new JPanel();
        output_panel_0.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        output_panel_0_border.add(output_panel_0, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(576, 384), null, new Dimension(576, 384), 0, false));
        output_panel_1_border = new JPanel();
        output_panel_1_border.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(output_panel_1_border, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        output_panel_1_border.setBorder(BorderFactory.createTitledBorder(null, "G", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        output_panel_1 = new JPanel();
        output_panel_1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        output_panel_1_border.add(output_panel_1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(576, 384), null, new Dimension(576, 384), 0, false));
        output_panel_2_border = new JPanel();
        output_panel_2_border.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(output_panel_2_border, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        output_panel_2_border.setBorder(BorderFactory.createTitledBorder(null, "B", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        output_panel_2 = new JPanel();
        output_panel_2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        output_panel_2_border.add(output_panel_2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(576, 384), null, new Dimension(576, 384), 0, false));
        input_panel_border = new JPanel();
        input_panel_border.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        main_panel.add(input_panel_border, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, false));
        input_panel_border.setBorder(BorderFactory.createTitledBorder(null, "input", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        input_panel = new JPanel();
        input_panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        input_panel_border.add(input_panel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(768, 512), new Dimension(24, 536), new Dimension(768, 512), 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        main_panel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "buttons", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        load_button = new JButton();
        load_button.setText("Load...");
        panel3.add(load_button, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        separation_combo_box = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("RGB");
        defaultComboBoxModel1.addElement("YCbCr");
        defaultComboBoxModel1.addElement("HSV");
        defaultComboBoxModel1.addElement("Lab");
        separation_combo_box.setModel(defaultComboBoxModel1);
        panel3.add(separation_combo_box, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(null, "Lab settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Predefined color profile");
        panel4.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Predefined illuminant");
        panel4.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        color_profile_combo_box = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("sRGB");
        defaultComboBoxModel2.addElement("Adobe RGB");
        defaultComboBoxModel2.addElement("Apple RGB");
        defaultComboBoxModel2.addElement("CIE RGB");
        defaultComboBoxModel2.addElement("Wide Gamut");
        defaultComboBoxModel2.addElement("PAL/SECAM");
        color_profile_combo_box.setModel(defaultComboBoxModel2);
        panel4.add(color_profile_combo_box, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        illuminant_combo_box = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("A");
        defaultComboBoxModel3.addElement("B");
        defaultComboBoxModel3.addElement("C");
        defaultComboBoxModel3.addElement("D50");
        defaultComboBoxModel3.addElement("D55");
        defaultComboBoxModel3.addElement("D65");
        defaultComboBoxModel3.addElement("D75");
        defaultComboBoxModel3.addElement("9300K");
        defaultComboBoxModel3.addElement("E");
        defaultComboBoxModel3.addElement("F2");
        defaultComboBoxModel3.addElement("F7");
        defaultComboBoxModel3.addElement("F11");
        illuminant_combo_box.setModel(defaultComboBoxModel3);
        panel4.add(illuminant_combo_box, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Chromaticity");
        panel5.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(4);
        label4.setHorizontalTextPosition(10);
        label4.setText("x");
        panel5.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(11);
        label5.setHorizontalTextPosition(11);
        label5.setText("y");
        panel5.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Red primary");
        panel5.add(label6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Green primary");
        panel5.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Blue primary");
        panel5.add(label8, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("White point");
        panel5.add(label9, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Gamma");
        panel5.add(label10, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        x_r_spinner = new JSpinner();
        panel5.add(x_r_spinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        y_r_spinner = new JSpinner();
        panel5.add(y_r_spinner, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        x_g_spinner = new JSpinner();
        panel5.add(x_g_spinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        y_g_spinner = new JSpinner();
        panel5.add(y_g_spinner, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        x_b_spinner = new JSpinner();
        panel5.add(x_b_spinner, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        y_b_spinner = new JSpinner();
        panel5.add(y_b_spinner, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        x_w_spinner = new JSpinner();
        panel5.add(x_w_spinner, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        y_w_spinner = new JSpinner();
        panel5.add(y_w_spinner, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gamma_spinner = new JSpinner();
        panel5.add(gamma_spinner, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        separate_button = new JButton();
        separate_button.setText("Separate");
        panel3.add(separate_button, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        save_button = new JButton();
        save_button.setText("Save....");
        panel3.add(save_button, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main_panel;
    }
}
