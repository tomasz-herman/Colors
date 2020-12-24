package com.hermant.colors;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.function.Function;

@FunctionalInterface
public interface Separation {
    Color3f[] separate(Color3f input);

    static Separation getRGBtoRGBSeparation() {
        Color3f black = new Color3f(0, 0, 0);
        Color3f red = new Color3f(255, 0, 0);
        Color3f green = new Color3f(0, 255, 0);
        Color3f blue = new Color3f(0, 0, 255);
        Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{Color3f.interpolate(black, red, coefficients[0]), Color3f.interpolate(black, green, coefficients[1]), Color3f.interpolate(black, blue, coefficients[2])};
        return color -> interpolate.apply(new float[]{color.red, color.green, color.blue});
    }

    static Separation getRGBtoHSVSeparation() {
        Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{new Color3f(coefficients[0]), new Color3f(coefficients[1]), new Color3f(coefficients[2])};
        return color -> {
            float min, max, delta;
            float h, s, v;
            min = Math.min(color.blue, Math.min(color.red, color.green));
            max = Math.max(color.blue, Math.max(color.red, color.green));
            delta = max - min;
            v = max;
            if (delta < 0.0000001f) {
                s = 0;
                h = 0;
                return interpolate.apply(new float[]{h, s, v});
            }
            if (max > 0.0f) {
                s = (delta / max);
            } else {
                s = 0.0f;
                h = Float.NaN;
                return interpolate.apply(new float[]{h, s, v});
            }
            if (color.red == max) {
                h = 6.0f + (color.green - color.blue) / delta;
            } else if (color.green == max) {
                h = 2.0f + (color.blue - color.red) / delta;
            } else {
                h = 4.0f + (color.red - color.green) / delta;
            }
            h = h / 6.0f;
            if (h < 0.0f) h += 1.0f;
            if (h > 1.0f) h -= 1.0f;
            return interpolate.apply(new float[]{h, s, v});
        };
    }

    static Separation getRGBtoYCbCrSeparation() {
        Color3f chartreuse = new Color3f(127, 255, 0);
        Color3f violet = new Color3f(127, 0, 255);
        Color3f spring_green = new Color3f(0, 255, 127);
        Color3f rose = new Color3f(255, 0, 127);
        Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{new Color3f(coefficients[0]), Color3f.interpolate(chartreuse, violet, coefficients[1]), Color3f.interpolate(spring_green, rose, coefficients[2])};
        return color -> {
            float y, cb, cr;
            y = 0.0625f + 0.25678906f * color.red + 0.50412893f * color.green + 0.09790625f * color.blue;
            cb = 0.5f - 0.14822266f * color.red - 0.2909922f * color.green + 0.43921486f * color.blue;
            cr = 0.5f + 0.43921486f * color.red - 0.36778906f * color.green - 0.07142578f * color.blue;
            return interpolate.apply(new float[]{y, cb, cr});
        };
    }

    /**
     * http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
     * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
     *
     * @return Function transformating RGB to L*ab
     */
    static Separation getRGBtoLabSeparation(float x_r, float y_r, float x_g, float y_g, float x_b, float y_b, float x_w, float y_w, float gamma) {
        final Color3f azure = new Color3f(0, 127, 255);
        final Color3f orange = new Color3f(255, 127, 0);
        final Color3f spring_green = new Color3f(0, 255, 127);
        final Color3f rose = new Color3f(255, 0, 127);
        final float X_r = x_r / y_r;
        final float Y_r = 1.0f;
        final float Z_r = (1.0f - x_r - y_r) / y_r;
        final float X_g = x_g / y_g;
        final float Y_g = 1.0f;
        final float Z_g = (1.0f - x_g - y_g) / y_g;
        final float X_b = x_b / y_b;
        final float Y_b = 1.0f;
        final float Z_b = (1.0f - x_b - y_b) / y_b;
        final float X_w = x_w / y_w;
        final float Y_w = 1.0f;
        final float Z_w = (1.0f - x_w - y_w) / y_w;
        final float e = 0.008856f;
        final float k = 903.3f;
        final Function<Float, Float> f = x -> (x > e) ? (float) Math.pow(x, 0.33333333f) : (k * x + 16f) / 116f;
        final Vector3f S = new Matrix3f(X_r, X_g, X_b, Y_r, Y_g, Y_b, Z_r, Z_g, Z_b).invert().transformTranspose(new Vector3f(X_w, Y_w, Z_w));
        final Matrix3f M = new Matrix3f(
                S.x * X_r, S.y * X_g, S.z * X_b,
                S.x * Y_r, S.y * Y_g, S.z * Y_b,
                S.x * Z_r, S.y * Z_g, S.z * Z_b
        );
        final Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{new Color3f(coefficients[0]), Color3f.interpolate(spring_green, rose, coefficients[1]), Color3f.interpolate(azure, orange, coefficients[2])};
        return color -> {
            Vector3f rgb = new Vector3f(color.red, color.green, color.blue);
            rgb.x = (float) Math.pow(rgb.x, gamma);
            rgb.y = (float) Math.pow(rgb.y, gamma);
            rgb.z = (float) Math.pow(rgb.z, gamma);
            rgb = rgb.mulTranspose(M);
            float f_x = f.apply(rgb.x / X_w);
            float f_y = f.apply(rgb.y / Y_w);
            float f_z = f.apply(rgb.z / Z_w);
            float L = 116f * f_y - 16f;
            float a = 500f * (f_x - f_y);
            float b = 200f * (f_y - f_z);
            return interpolate.apply(new float[]{L / 256f, (a + 128f) / 256f, (b + 128f) / 256f});
        };
    }
}
