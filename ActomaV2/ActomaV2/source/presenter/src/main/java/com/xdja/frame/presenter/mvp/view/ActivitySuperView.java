package com.xdja.frame.presenter.mvp.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.frame.presenter.R;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.widget.XDialog;


/**
 * Created by fanjiandong on 2015/5/22.
 *修改备注：
 *1)增加一个带Onkey参数的显示弹窗的方法 2016/12/07
 */
@SuppressLint("InflateParams")
public class ActivitySuperView<T extends Command> extends SuperView<T> implements ActivityVu<T> {
    //耗时动画提示框
    private XDialog progressDialog;

    private Dialog mNoTitleDialog;
    @Override
    public void onCreated() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * 显示系统样式的圆形进度对话框
     *
     * @param msg 显示的内容
     */
    public void showCommonProgressDialog(String msg) {
        showCommonProgressDialog(msg, false, true);
    }
	
	// add by ycm: 给弹窗设置成按返回键不取消 2016/12/07 [start]
    /**
     * 显示耗时动画
     *
     * @param
     */
    @Override
    public void showCommonProgressDialog(String msg, boolean onKey) {
        showCommonProgressDialog(msg, false, onKey);
    }
	// add by ycm: 给弹窗设置成按返回键不取消 2016/12/07 [start]
	
    public void showProgressDialog() {
        showProgressDialog(false, true);
    }

    public void showProgressDialog(boolean isTouch, boolean onKey) {
        //modify by alh@xdja.com to fix bug: 1975 2016-07-26 start(rummager:wangchao1)
        if (getActivity() == null || getActivity().isFinishing() || (progressDialog != null && progressDialog.isShowing())) {
            return;
        }
        //modify by alh@xdja.com to fix bug: 1975 2016-07-26 end (rummager:wangchao1)
        if (mNoTitleDialog == null) {
            mNoTitleDialog = new Dialog(getContext() ,R.style.NoBackgroudDialog);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_circle_progress_notext_dialog, null);
            mNoTitleDialog.setContentView(view);
        }
        mNoTitleDialog.setCanceledOnTouchOutside(isTouch);
        mNoTitleDialog.setCancelable(onKey);
        if (!mNoTitleDialog.isShowing()) {
            mNoTitleDialog.show();
        }
    }

    /**
     * 显示耗时动画
     *
     * @param resId
     */
    public void showCommonProgressDialog(@StringRes int resId) {
        showCommonProgressDialog(getStringRes(resId), false, true);
    }


    /**
     * 显示耗时动画
     */
    public void showCommonProgressDialog(String msg, boolean isTouch, boolean onKey) {
        //modify by alh@xdja.com to fix bug: 1975 2016-07-26 start(rummager:wangchao1)
        if (getActivity() == null || getActivity().isFinishing() || (mNoTitleDialog != null && mNoTitleDialog.isShowing())) {
            return;
        }
        //modify by alh@xdja.com to fix bug: 1975 2016-07-26 end (rummager:wangchao1)
        if (progressDialog == null) {
            progressDialog = new XDialog(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_circle_progress_dialog, null);
            progressDialog.setView(view);
        }
        //填写标题
        View view = progressDialog.getView();
        if (view != null) {
            TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(msg);
        }
        progressDialog.setCanceledOnTouchOutside(isTouch);
        progressDialog.setCancelable(onKey);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 取消耗时动画
     */
    public void dismissCommonProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !getActivity().isFinishing()) {//add by wal@xdja.com for 4129
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (mNoTitleDialog != null && mNoTitleDialog.isShowing() && !getActivity().isFinishing()) {//add by wal@xdja.com for 4129
            mNoTitleDialog.dismiss();
            mNoTitleDialog = null;
        }

    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(),getStringRes(resId),Toast.LENGTH_SHORT).show();
    }
}
