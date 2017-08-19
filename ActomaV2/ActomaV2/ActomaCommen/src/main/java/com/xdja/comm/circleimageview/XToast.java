package com.xdja.comm.circleimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.comm.R;
import com.xdja.comm.uitl.TextUtil;

@SuppressLint("InflateParams")
public class XToast {
    /**
     * Toast提示(错误)
     *
     * @param context 上下文句柄
     * @param message 提示信息
     */
    public static void show(Context context, String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, message));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast提示(错误) 根据资源ID
     * @param context
     * @param id
     */
    public static void show(Context context, int id) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, context.getResources().getString(id)));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast提示长时间(错误)
     *
     * @param context 上下文句柄
     * @param message 提示信息
     */
    public static void showLong(Context context, String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, message));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast提示(正确)
     *
     * @param context 上下文句柄
     * @param message 提示信息
     */
    public static void showOK(Context context, String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_ok_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, message));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast提示(错误)
     *
     * @param context 上下文句柄
     * @param message 提示信息
     */
    public static void showErrorTop(Context context, String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, message));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }

    /**
     * Toast提示(正确)
     *
     * @param context 上下文句柄
     * @param message 提示信息
     */
    public static void showOKTop(Context context, String message) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_ok_toast, null);
        final float scale = context.getResources().getDisplayMetrics().density;

        TextView contentView = (TextView) toastView.findViewById(R.id.toast_context);
        contentView.setText(TextUtil.getActomaText(context,
                TextUtil.ActomaImage.IMAGE_TOAST,
                0, 0, 0, message));
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, (int) (68 * scale + 0.5f));
        toast.show();
    }
}
