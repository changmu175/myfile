package com.xdja.frame.presenter.mvp.view;

import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.Vu;


/**
 * Created by fanjiandong on 2015/5/22.
 * 和Acitivity生命周期相关的View层的生命周期定义
 *修改备注：
 *1)增加一个带Onkey参数的显示弹窗的方法 2016/12/07
 */
public interface ActivityVu<P extends Command> extends Vu<P> {

    void onCreated();

    void onResume();

    void onStart();

    void onRestart();

    void onPause();

    void onStop();

    void onDestroy();

    void onAttachedToWindow();

    void onDetachedFromWindow();

    boolean onCreateOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(MenuItem item);

    boolean onPrepareOptionsMenu(Menu menu);

    void showCommonProgressDialog(String msg);

    void showProgressDialog();

    void showCommonProgressDialog(@StringRes int resId);

    void dismissCommonProgressDialog();

    void showToast(String msg);

    void showToast(@StringRes int resId);
	//add by ycm 2016/12/07
    void showCommonProgressDialog(String msg, boolean onKey);
}
