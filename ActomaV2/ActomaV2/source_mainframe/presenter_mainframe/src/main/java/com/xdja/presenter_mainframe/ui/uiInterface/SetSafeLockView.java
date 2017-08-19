package com.xdja.presenter_mainframe.ui.uiInterface;


import android.widget.Button;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetSafeLockCommand;
import com.xdja.presenter_mainframe.ui.ActivityView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by licong on 2016/11/24.
 */
@ContentView(R.layout.activity_not_setting_safe_lock)
public class SetSafeLockView extends ActivityView<SetSafeLockCommand> implements VuSetSafeLock {

    @Bind(R.id.open_safe_switch)
    Button openSafeSwitch;

    @Bind(R.id.safe_no_way_open)
    TextView safeNoWayOpen;

    @Override
    public void onCreated() {
        super.onCreated();
        safeNoWayOpen.setText(BitmapUtils.formatAnTongSpanContent(getStringRes(R.string.safe_no_way_open),
                getActivity(), 1.0f, BitmapUtils.AN_TONG_DETAIL_PLUS));
    }

    @OnClick(R.id.open_safe_switch)
    public void clickOpenGestureButton() {
        getCommand().openCreateGesture();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }


    @Override
    public String getTitleStr(){
        return getStringRes(R.string.safe_lock);
    }

}
