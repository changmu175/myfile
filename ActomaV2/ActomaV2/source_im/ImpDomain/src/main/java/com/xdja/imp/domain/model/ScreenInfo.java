package com.xdja.imp.domain.model;

/**
 * Created by kgg on 2016/4/28.
 */
public class ScreenInfo {
    int width;  // 屏幕宽度（像素）
    int height;  // 屏幕高度（像素）
    float density;  // 屏幕密度（0.75 / 1.0 / 1.5）
    int densityDpi;// 屏幕密度DPI（120 / 160 / 240）

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(int densityDpi) {
        this.densityDpi = densityDpi;
    }
}
