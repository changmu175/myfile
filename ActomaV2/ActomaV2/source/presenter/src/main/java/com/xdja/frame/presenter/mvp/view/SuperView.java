package com.xdja.frame.presenter.mvp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.Vu;
import com.xdja.frame.presenter.mvp.annotation.ContentView;

import butterknife.ButterKnife;

/**
 * Created by fanjiandong on 2015/5/22.
 */
public class SuperView<T extends Command> implements Vu<T> {
    private View view;
    private T command;

    private Activity activity;

    @Override
    public void init(@NonNull LayoutInflater inflater, ViewGroup container) {
        ContentView contentView = getClass().getAnnotation(ContentView.class);
        if (contentView != null) {
            view = inflater.inflate(contentView.value(), container, false);
        } else {
            view = inflater.inflate(getLayoutRes(), container, false);
        }
        injectView();
    }

    @LayoutRes
    protected int getLayoutRes() {
        return -1;
    }

    protected void injectView() {
        ButterKnife.bind(this, view);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void setCommand(T Command) {
        this.command = Command;
    }

    @Override
    public <A extends Activity> void setActivity(A activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public Context getContext() {
        return this.activity;
    }

    public T getCommand() {
        return command;
    }

    /**
     * 获取String字符串
     *
     * @param res 字符串资源ID
     * @return 目标字符串
     */
    @NonNull
    public String getStringRes(@StringRes int res) {
        return getContext().getString(res);
    }

    /**
     * 获取Color颜色
     *
     * @param res 颜色资源ID
     * @return 目标颜色
     */
    public int getColorRes(@ColorRes int res) {
        return getContext().getResources().getColor(res);
    }

    /**
     * 获取Drawable
     *
     * @param res 图片资源ID
     * @return 目标Drawable
     */
    @Nullable
    public Drawable getDrawableRes(@DrawableRes int res) {
        return getContext().getResources().getDrawable(res);
    }

    /**
     * 获取Dimens
     *
     * @param res DimensID
     * @return 目标Dimens
     */
    @Nullable
    public int getDimensRes(@DimenRes int res) {
        return getContext().getResources().getDimensionPixelOffset(res);
    }
}
