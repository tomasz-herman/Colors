package com.hermant.colors;

import java.util.function.Function;

@FunctionalInterface
public interface Transformation {
    Color3f[] transform(Color3f input);

    default Transformation getRGBtoHSVTransform() {
        Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{new Color3f(coefficients[0]), new Color3f(coefficients[1]), new Color3f(coefficients[2])};
        return color -> {
            float min, max, delta;
            float h, s, v;
            min = Math.min(color.blue, Math.min(color.red, color.green));
            max = Math.max(color.blue, Math.max(color.red, color.green));
            delta = max - min;
            v = max;
            if (delta < 0.00001f) {
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
            if (color.red >= max) {
                h = (color.green - color.blue) / delta;
            } else if (color.green >= max) {
                h = 2.0f + (color.blue - color.red) / delta;
            } else {
                h = 4.0f + (color.red - color.green) / delta;
            }
            h /= 6.0f;
            if (h < 0.0f) h += 1.0f;
            return interpolate.apply(new float[]{h, s, v});
        };
    }

    default Transformation getRGBtoYCbCrTransform() {
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

    //TODO
    default Transformation getRGBtoLabTransform() {
        Color3f azure = new Color3f(0, 127, 255);
        Color3f orange = new Color3f(255, 127, 255);
        Color3f spring_green = new Color3f(0, 255, 127);
        Color3f rose = new Color3f(255, 0, 127);
        Function<float[], Color3f[]> interpolate = coefficients -> new Color3f[]{new Color3f(coefficients[0]), Color3f.interpolate(spring_green, rose, coefficients[1]), Color3f.interpolate(azure, orange, coefficients[2])};
        return color -> {
            float y, cb, cr;
            y = 0.0625f + 0.25678906f * color.red + 0.50412893f * color.green + 0.09790625f * color.blue;
            cb = 0.5f - 0.14822266f * color.red - 0.2909922f * color.green + 0.43921486f * color.blue;
            cr = 0.5f + 0.43921486f * color.red - 0.36778906f * color.green - 0.07142578f * color.blue;
            return interpolate.apply(new float[]{y, cb, cr});
        };
    }
}
