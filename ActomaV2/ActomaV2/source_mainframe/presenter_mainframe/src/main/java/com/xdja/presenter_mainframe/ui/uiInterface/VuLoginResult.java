package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by ldy on 16/5/20.
 */
public interface VuLoginResult<P extends Command> extends ActivityVu<P> {
    void maxLoginCount(int maxLoginCount);
}
