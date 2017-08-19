package com.xdja.presenter_mainframe.ui.uiInterface;

import android.graphics.Bitmap;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.WriteRegistrationInfoCommand;

/**
 * Created by ldy on 16/4/15.
 */
public interface VuWriteRegistrationInfo extends ActivityVu<WriteRegistrationInfoCommand> {
    /**
     * 显示用户裁剪的头像
     *
     * @param bitmap 用户裁剪后的头像
     */
    void showUserImage(Bitmap bitmap);

    void loadUserImage(String url);
}
