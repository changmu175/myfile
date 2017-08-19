package com.xdja.imp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频获取手机分辨率工具类    <br>
 * 创建时间：2017/2/10        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class DisplayUtils {

    /**
     * 获取屏幕原始尺寸宽度
     * @param context 上下文
     * @return 返回屏幕原始尺寸宽度
     */
    public static int getWidthPixels(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration cf = context.getResources().getConfiguration();
        int ori = cf.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            //noinspection SuspiciousNameCombination
            return displayMetrics.heightPixels;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    /**
     * dp2px
     * @param context 上下文
     * @param dpValue dp尺寸
     * @return 返回转化成px的尺寸
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * px2dp
     * @param context 上下文
     * @param pxValue px尺寸
     * @return 返回转化成dp的尺寸
     */
    @SuppressWarnings("unused")
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     * @param context 上下文
     * @return 返回屏幕原始尺寸高度，包括虚拟功能键高度
     */
    private static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取虚拟按键的高度
     * @param context 上下文
     * @return 返回虚拟按键高度
     */
    public static int getBottomStatusHeight(Context context){
        int totalHeight = getDpi(context);

        int contentHeight = getScreenHeight(context);
        return totalHeight  - contentHeight;
    }

    /**
     * 获得屏幕高度
     *
     * @param context 上下文
     * @return 返回屏幕高度
     */
    private static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
