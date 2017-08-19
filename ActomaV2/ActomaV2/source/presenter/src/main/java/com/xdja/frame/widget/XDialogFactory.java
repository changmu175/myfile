package com.xdja.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by geyao on 2016/4/5.
 * 对话框
 */
public class XDialogFactory {
    /**
     * 含有标题,提示信息,两个按钮的对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不能为空
     * @param title                  标题 不能为空
     * @param message                提示信息 不能为空
     * @param cancelAble             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     * @param positive               positive按钮显示文本 不可为空
     * @param positiveListener       positive按钮点击监听事件 可以为空 若为空则默认关闭对话框
     * @param negative               negative按钮显示文本 不可为空
     * @param negativeListener       negative按钮点击监听事件 可以为空 若为空则默认关闭对话框
     */
    public static void showTitleMessage2BtnDialog(@NonNull Context context,
                                                  @NonNull String title,
                                                  @NonNull String message,
                                                  boolean cancelAble,
                                                  boolean canceledOnTouchOutside,
                                                  @NonNull String positive,
                                                  @Nullable final View.OnClickListener positiveListener,
                                                  @NonNull String negative,
                                                  @Nullable final View.OnClickListener negativeListener) {

        final XDialog dialog = new XDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setPositiveButton(positive, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                positiveListener.onClick(v);
            }
        });
        dialog.setNegativeButton(negative, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                negativeListener.onClick(v);
            }
        });
        dialog.show();
    }

    /**
     * 含有标题,提示信息,一个按钮的对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不能为空
     * @param title                  标题 不能为空
     * @param message                提示信息 不能为空
     * @param cancelAble             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     * @param positive               positive按钮显示文本 不可为空
     * @param positiveListener       positive按钮点击监听事件 可以为空 若为空则默认关闭对话框
     */
    public static void showTitleMessage1BtnDialog(@NonNull Context context,
                                           @NonNull String title,
                                           @NonNull String message,
                                           boolean cancelAble,
                                           boolean canceledOnTouchOutside,
                                           @NonNull String positive,
                                           @Nullable final View.OnClickListener positiveListener) {
        final XDialog dialog = new XDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setPositiveButton(positive, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                positiveListener.onClick(v);
            }
        });
        dialog.show();
    }

    /**
     * 含有提示信息,两个按钮的对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不能为空
     * @param message                提示信息 不能为空
     * @param cancelAble             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     * @param positive               positive按钮显示文本 不可为空
     * @param positiveListener       positive按钮点击监听事件 可以为空 若为空则默认关闭对话框
     * @param negative               negative按钮显示文本 不可为空
     * @param negativeListener       negative按钮点击监听事件 可以为空 若为空则默认关闭对话框
     */
    public static void showMessage2BtnDialog(@NonNull Context context,
                                      @NonNull String message,
                                      boolean cancelAble,
                                      boolean canceledOnTouchOutside,
                                      @NonNull String positive,
                                      @Nullable final View.OnClickListener positiveListener,
                                      @NonNull String negative,
                                      @Nullable final View.OnClickListener negativeListener) {
        final XDialog dialog = new XDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setPositiveButton(positive, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                positiveListener.onClick(v);
            }
        });
        dialog.setNegativeButton(negative, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                negativeListener.onClick(v);
            }
        });
        dialog.show();
    }

    /**
     * 含有提示信息,一个按钮的对话框
     *
     * @param context                上下文句柄 Ps:句柄用UI界面的句柄 不能为空
     * @param message                提示信息 不能为空
     * @param cancelAble             设置返回键是否可取消
     * @param canceledOnTouchOutside 设置是否外部点击可取消
     * @param positive               positive按钮显示文本 不可为空
     * @param positiveListener       positive按钮点击监听事件 可以为空 若为空则默认关闭对话框
     */
    public static void showMessage1BtnDialog(@NonNull Context context,
                                      @NonNull String message,
                                      boolean cancelAble,
                                      boolean canceledOnTouchOutside,
                                      @NonNull String positive,
                                      @Nullable final View.OnClickListener positiveListener) {
        final XDialog dialog = new XDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setPositiveButton(positive, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                positiveListener.onClick(v);
            }
        });
        dialog.show();
    }
}
