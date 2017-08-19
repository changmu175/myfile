package com.xdja.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.frame.presenter.R;

/**
 * Created by geyao on 2016/4/1.
 * toast提示(不唯一)
 */
public class XToast {

    public static void show(@NonNull Context context, @StringRes int resId){
        showBottomWrongToast(context,context.getString(resId));
    }

    public static void show(@NonNull Context context, @NonNull String message){
        showBottomWrongToast(context,message);
    }

    public static void showOK(@NonNull Context context, @NonNull String message){
        showBottomRightToast(context,message);
    }

    /**
     * Toast下方错误提示
     *
     * @param context 上下文句柄 Ps:句柄用application级别的 不能为空
     * @param message 提示信息 不能为空
     */
    public static void showBottomWrongToast(@NonNull Context context, @NonNull String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_wrong_toast, null);
        float scale = context.getResources().getDisplayMetrics().density;
        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(message);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast下方正确提示
     *
     * @param context 上下文句柄 不能为空
     * @param message 提示信息 不能为空
     */
    public static void showBottomRightToast(@NonNull Context context, @NonNull String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_right_toast, null);
        float scale = context.getResources().getDisplayMetrics().density;
        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(message);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast上方错误提示
     *
     * @param context 上下文句柄 不能为空
     * @param message 提示信息 不能为空
     */
    public static void showErrorTop(@NonNull Context context, @NonNull String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_wrong_toast, null);
        float scale = context.getResources().getDisplayMetrics().density;
        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(message);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast下方正确提示
     *
     * @param context 上下文句柄 不能为空
     * @param message 提示信息 不能为空
     */
    public static void showTopRightToast(@NonNull Context context, @NonNull String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_right_toast, null);
        float scale = context.getResources().getDisplayMetrics().density;
        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(message);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }
}
