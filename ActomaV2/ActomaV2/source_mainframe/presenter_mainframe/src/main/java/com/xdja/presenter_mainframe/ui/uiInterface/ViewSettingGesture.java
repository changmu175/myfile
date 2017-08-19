package com.xdja.presenter_mainframe.ui.uiInterface;

import android.view.View;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SettingGestureCommand;
import com.xdja.presenter_mainframe.ui.ActivityView;
import com.xdja.presenter_mainframe.widget.LockPatternView;

import butterknife.Bind;

/**
 * Created by licong on 2016/11/25.
 */
@ContentView(R.layout.setting_gesture_password)
public class ViewSettingGesture extends ActivityView<SettingGestureCommand> implements SettingGestureVu{

    @Bind(R.id.gesturepwd_unlock_text)
    TextView gestureUnLockText;

    @Bind(R.id.re_gesturepwd_unlock_text)
    TextView reGestureUnlockText;

    @Bind(R.id.gesturepwd_unlock_lockview)
    LockPatternView lockPatternView;



    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }


    @Override
    public String getTitleStr(){
        return getStringRes(R.string.setting_gesture_password);
    }

    @Override
    public LockPatternView getLockPatternView() {
        return lockPatternView;
    }

    @Override
    public void setText() {
        if (getCommand().getGestureCount() == 1) {
            gestureUnLockText.setVisibility(View.VISIBLE);
            reGestureUnlockText.setVisibility(View.GONE);
        } else if (getCommand().getGestureCount() > 1){
            gestureUnLockText.setVisibility(View.GONE);
            reGestureUnlockText.setVisibility(View.VISIBLE);
        }
    }



}
