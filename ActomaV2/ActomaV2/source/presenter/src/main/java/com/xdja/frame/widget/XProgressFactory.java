package com.xdja.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xdja.frame.presenter.R;

/**
 * Created by geyao on 2016/4/5.
 * 进度条
 */
public class XProgressFactory {
    /**
     * 含有提示信息的圆形进度条对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不可为空
     * @param message                提示信息 不可为空
     * @param cancelable             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     */
    public static XDialog showMessageCircleProgressDialog(@NonNull Context context,
                                                          @NonNull String message,
                                                          boolean cancelable,
                                                          boolean canceledOnTouchOutside) {
        XDialog circleProgressDialog = new XDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_circle_progress_dialog, null);
        circleProgressDialog.setView(view);
        TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
        messageView.setText(message);
        messageView.setVisibility(View.VISIBLE);
        circleProgressDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        circleProgressDialog.setCancelable(cancelable);
        circleProgressDialog.show();
        return circleProgressDialog;
    }

    /**
     * 圆形进度条对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不可为空
     * @param cancelable             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     */
    public static XDialog showCircleProgressDialog(@NonNull Context context,
                                                   boolean cancelable,
                                                   boolean canceledOnTouchOutside) {
        XDialog circleProgressDialog = new XDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_circle_progress_dialog, null);
        circleProgressDialog.setView(view);
        circleProgressDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        circleProgressDialog.setCancelable(cancelable);
        circleProgressDialog.show();
        return circleProgressDialog;
    }

    /**
     * 模糊进度条对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不可为空
     * @param cancelable             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     */
    public static XDialog showFuzzyProgressDialog(@NonNull Context context,
                                                  boolean cancelable,
                                                  boolean canceledOnTouchOutside) {
        XDialog progressDialog = new XDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_fuzzy_progress_dialog, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.fuzzy_progressBar);
        progressDialog.setView(view);
        progressDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 精准进度条对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不可为空
     * @param cancelable             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     */
    public static XDialog showAccurateProgressDialog(@NonNull Context context,
                                                     boolean cancelable,
                                                     boolean canceledOnTouchOutside) {
        XDialog progressDialog = new XDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_accurate_progress_dialog, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.accurate_progressBar);
        TextView progressText = (TextView) view.findViewById(R.id.accurate_textView);
        progressDialog.setView(view);
        progressDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
        return progressDialog;
    }

//    /**
//     * 设置进度条进度
//     *
//     * @param progress 进度 不可为空
//     */
//    public static void setProgress(@NonNull int progress) {
//        if (progressDialog != null) {
//            if (progressText != null) {
//                progressText.setText(progress + "%");
//            }
//            if (progressBar != null) {
//                progressBar.setProgress(progress);
//                if (progressBar.getProgress() >= 100) {
//                    progressDialog.dismiss();
//                }
//            }
//        }
//    }
}
