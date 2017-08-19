package com.xdja.presenter_mainframe.ui.uiInterface;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.OpenSafeLockCommand;
import com.xdja.presenter_mainframe.ui.ActivityView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by licong on 2016/11/24.
 */
@ContentView(R.layout.activity_open_safe_lock)
public class ViewOpenSafeLock extends ActivityView<OpenSafeLockCommand> implements OpenSafeLockVu{

    /**
     * 安全锁通知单选框
     */
    @Bind(R.id.safe_lock_checkbox)
    CheckBox safeLockCheckbox;
    /**
     * 锁屏锁定界面
     */
    @Bind(R.id.lock_screen_checkbox)
    CheckBox lockScreenCheckBox;
    /**
     * 后台运行锁定
     */
    @Bind(R.id.background_lock_checkbox)
    CheckBox backgroundLockCheckBox;

    @Bind(R.id.modified_gesture)
    LinearLayout modifiedGesture;
    /**
     * 两种锁定方式所在布局
     */
    @Bind(R.id.safe_lock_screen_background_layout)
    LinearLayout safeLockScreenBackgroundLayout;

    @Bind(R.id.text_safe_lock)
    TextView textSafeLock;

    @Bind(R.id.text_background_lock)
    TextView textBackgroundLock;

    private final float SMALL_SCALL = 0.75f;

    @Override
    public void onCreated() {
        super.onCreated();
        textSafeLock.setText(BitmapUtils.formatAnTongSpanContent(getStringRes(R.string.safe_phone_lock_actoma),
                getActivity(), SMALL_SCALL, BitmapUtils.AN_TONG_DETAIL_PLUS));
        textBackgroundLock.setText(BitmapUtils.formatAnTongSpanContent(getStringRes(R.string.safe_lock_background_lock_actoma),
                getActivity(), SMALL_SCALL, BitmapUtils.AN_TONG_DETAIL_PLUS));

    }

    /**
     * 安全锁单选框点击事件
     */
    @OnClick(R.id.safe_lock)
    public void safeLockClick() {
        safeLockCheckbox.setChecked(!safeLockCheckbox.isChecked());
        //如果上边安全锁模式总开关关闭，下部跟着关闭
        if (!safeLockCheckbox.isChecked()) {
            safeLockScreenBackgroundLayout.setVisibility(View.GONE);
            safeLockScreenBackgroundLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        } else {//如果总开关开启，下部按钮恢复上次选中的状态
            safeLockScreenBackgroundLayout.setVisibility(View.VISIBLE);
            safeLockScreenBackgroundLayout.setBackgroundColor(getColorRes(R.color.listview_bg_withgroup));
        }
        getCommand().safeLock(safeLockCheckbox.isChecked());
    }

    @OnClick(R.id.lock_screen)
    public void lockCreenClick() {
        lockScreenCheckBox.setChecked(true);
        backgroundLockCheckBox.setChecked(false);
        lockScreenCheckBox.setVisibility(View.VISIBLE);
        backgroundLockCheckBox.setVisibility(View.GONE);
        getCommand().lockSreen(true);
        getCommand().backgroundLock(false);
    }

    @OnClick(R.id.background_lock)
    public void backgroundLockClick() {
        backgroundLockCheckBox.setChecked(true);
        lockScreenCheckBox.setChecked(false);
        backgroundLockCheckBox.setVisibility(View.VISIBLE);
        lockScreenCheckBox.setVisibility(View.GONE);
        getCommand().backgroundLock(true);
        getCommand().lockSreen(false);
    }

    @OnClick(R.id.midified_safe_lock)
    public void setModifiedGestureClick() {
        getCommand().modifiedGesture();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }


    @Override
    public String getTitleStr(){
        return getStringRes(R.string.safe_lock);
    }

    @Override
    public void setSafeLock(boolean isOn) {
        safeLockCheckbox.setChecked(isOn);
        if (!isOn) {
            safeLockScreenBackgroundLayout.setVisibility(View.GONE);
            safeLockScreenBackgroundLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        } else {//如果总开关开启，下部按钮恢复上次选中的状态
            safeLockScreenBackgroundLayout.setVisibility(View.VISIBLE);
            safeLockScreenBackgroundLayout.setBackgroundColor(getColorRes(R.color.listview_bg_withgroup));
        }
    }

    @Override
    public void setLockSreen(boolean isOn) {
        if (isOn) {
            lockScreenCheckBox.setVisibility(View.VISIBLE);
            backgroundLockCheckBox.setVisibility(View.GONE);
        } else {
            lockScreenCheckBox.setVisibility(View.GONE);
            backgroundLockCheckBox.setVisibility(View.VISIBLE);
        }
        lockScreenCheckBox.setChecked(isOn);
    }

    @Override
    public void setBackgroundLock(boolean isOn) {
        if (isOn) {
            backgroundLockCheckBox.setVisibility(View.VISIBLE);
            lockScreenCheckBox.setVisibility(View.GONE);
        } else {
            backgroundLockCheckBox.setVisibility(View.GONE);
            lockScreenCheckBox.setVisibility(View.VISIBLE);
        }
        backgroundLockCheckBox.setChecked(isOn);
    }

}
