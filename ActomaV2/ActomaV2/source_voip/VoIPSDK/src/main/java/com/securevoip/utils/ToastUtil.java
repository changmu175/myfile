package com.securevoip.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.voipsdk.R;

/**
 * Created by xdja-zjc on 2015/3/31.
 * 多次点击弹出的Toast不会累计显示时间，而是刷新
 */
public class ToastUtil {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showNoRepeatToast(Context context, String s) {
        if (toast == null) {
            /*toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();*/
            toast = new Toast(context);
            View toastView  = LayoutInflater.from(context).inflate(R.layout.view_custom_toast,null);
            final float scale = context.getResources().getDisplayMetrics().density;

            TextView contentView = (TextView)toastView.findViewById(R.id.toast_context);
            contentView.setText(s);
            toast.setView(toastView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,(int)(68 * scale + 0.5f));
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                //toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showNoRepeatToast(Context context, int resId) {
        showNoRepeatToast(context, context.getString(resId));
    }

    public static void showToast(Context context, String message) {
        //modify by alh@xdja.com to fix bug: 1456 2016-07-13 start (rummager : self)
        if (context == null){
            return;
        }
        //modify by alh@xdja.com to fix bug: 1456 2016-07-13 start (rummager : self)
        Toast toast = new Toast(context);
        View toastView  = LayoutInflater.from(context).inflate(R.layout.view_custom_toast,null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView)toastView.findViewById(R.id.toast_context);
        contentView.setText(message);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,(int)(68 * scale + 0.5f));
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

}
