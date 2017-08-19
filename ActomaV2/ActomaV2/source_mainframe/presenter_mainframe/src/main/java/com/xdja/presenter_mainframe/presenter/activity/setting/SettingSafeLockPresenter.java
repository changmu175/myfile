package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.presenter_mainframe.cmd.SetSafeLockCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.uiInterface.SetSafeLockView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetSafeLock;

/**
 * Created by licong on 2016/11/23.
 * 安全锁未设置界面
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SettingSafeLockPresenter extends PresenterActivity<SetSafeLockCommand,VuSetSafeLock> implements SetSafeLockCommand {
    public static final int IS_SETTING_SAFE = 1;

    @NonNull
    @Override
    protected Class<? extends VuSetSafeLock> getVuClass() {
        return SetSafeLockView.class;
    }

    @NonNull
    @Override
    protected SetSafeLockCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void openCreateGesture() {
        Intent intent = new Intent (this,SettingGesturePresenter.class);
        intent.putExtra("isSettingSafe",IS_SETTING_SAFE);
        startActivity(intent);
        finish();

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this,AccountSafePresenter.class);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}


