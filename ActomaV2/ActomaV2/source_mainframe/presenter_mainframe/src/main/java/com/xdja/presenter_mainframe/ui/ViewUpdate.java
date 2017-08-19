package com.xdja.presenter_mainframe.ui;


import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.ui.uiInterface.UpdateVu;

/**
 * Created by chenbing 2015-7-29
 * 升级界面
 */
@ContentView(value = R.layout.activity_update)
public class ViewUpdate extends ActivityView<Command> implements UpdateVu {

    @Override
    public void onCreated() {
        super.onCreated();
    }

    @Override
    public void showProgress() {
        showCommonProgressDialog(getStringRes(R.string.update_progress_message));
    }

    @Override
    public void hideProgress() {
        dismissCommonProgressDialog();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.aboutactoma_versionupdate);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
