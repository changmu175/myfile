package com.xdja.frame.presenter.mvp.view;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.frame.presenter.R;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.widget.XDialog;

import butterknife.ButterKnife;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/9</p>
 * <p>Time:18:45</p>
 */
public class FragmentSuperView<T extends Command> extends SuperView<T> implements FragmentVu<T> {

    //耗时动画提示框
    private XDialog progressDialog;

    private Fragment fragment;

    @Override
    public <A extends Fragment> void setFragment(A fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public void onCreated() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * 显示耗时动画
     *
     * @param resId
     */
    public void showCommonProgressDialog(@StringRes int resId) {
        showCommonProgressDialog(getContext().getString(resId), false, true);
    }


    /**
     * 显示耗时动画
     */
    public void showCommonProgressDialog(String msg, boolean isTouch, boolean onKey) {
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
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(),getContext().getString(resId),Toast.LENGTH_SHORT).show();
    }
}
