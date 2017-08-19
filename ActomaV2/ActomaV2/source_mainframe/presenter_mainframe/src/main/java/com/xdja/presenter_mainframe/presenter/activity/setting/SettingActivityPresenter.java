package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.support.annotation.NonNull;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.SettingActivityView;

/**
 * Created by ldy on 16/4/29.
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SettingActivityPresenter extends PresenterActivity<Command,ActivityVu> implements Command {

    @NonNull
    @Override
    protected Class<? extends ActivityVu> getVuClass() {
        return SettingActivityView.class;
    }

    @NonNull
    @Override
    protected Command getCommand() {
        return this;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this, MainFramePresenter.class);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
//            return true;
//        }
//        return false;
//    }
}
