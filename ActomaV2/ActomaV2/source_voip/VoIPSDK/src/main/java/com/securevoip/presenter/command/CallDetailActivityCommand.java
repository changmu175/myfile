package com.securevoip.presenter.command;

import android.content.Context;
import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by gbc on 2015/7/24.
 */
public interface CallDetailActivityCommand extends Command {
    void VoipCall(String actomaAccount);
    void SendIMMsg();
    void ClearCallLog(String actomaAccount);
    void reloadCallLog();
    void toContactDetail(Context context, String actomaAccount);
}
