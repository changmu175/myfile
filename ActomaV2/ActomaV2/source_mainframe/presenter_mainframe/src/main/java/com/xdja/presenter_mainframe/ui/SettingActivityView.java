package com.xdja.presenter_mainframe.ui;

import android.support.v4.app.FragmentActivity;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.presenter.fragement.SettingFragementPresenter;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_setting)
public class SettingActivityView extends ActivityView<Command> {
    @Override
    public void onCreated() {
        super.onCreated();
        ((FragmentActivity)getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_setting_activity_root,new SettingFragementPresenter())
                .commit();
    }

    @Override
    protected int getToolbarType() {
        return ActivityView.ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_setting);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

}
