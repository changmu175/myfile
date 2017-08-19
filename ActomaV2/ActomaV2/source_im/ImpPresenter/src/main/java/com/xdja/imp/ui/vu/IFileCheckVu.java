package com.xdja.imp.ui.vu;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IChatDetailFileCheckCommand;

/**
 * Created by guorong on 2016/11/30.
 */
public interface IFileCheckVu extends ActivityVu<IChatDetailFileCheckCommand> {
    void updateDownloadRate(int percent);

    void downloadFileFinish();

    void setData();

    void changeViewSate(int state);

    void setCtrlBtnText(int res);
}
