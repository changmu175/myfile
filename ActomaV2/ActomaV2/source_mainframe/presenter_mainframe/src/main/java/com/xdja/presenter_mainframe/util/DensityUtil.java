package com.xdja.presenter_mainframe.util;


import android.content.Context;

public class DensityUtil {

    private static float density = -1.0F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtil() {
    }

    public static float getDensity(Context context) {
        if (density <= 0.0F) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5f);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        if (widthPixels <= 0) {
            widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }


    public static int getScreenHeight(Context context) {
        if (heightPixels <= 0) {
            heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }
}
