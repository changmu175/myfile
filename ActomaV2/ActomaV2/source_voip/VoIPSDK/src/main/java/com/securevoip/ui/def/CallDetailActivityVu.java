package com.securevoip.ui.def;

import com.securevoip.presenter.adapter.calldetail.CallDetailsAdapter;
import com.securevoip.presenter.command.CallDetailActivityCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by gbc on 2015/7/24.
 */
public interface CallDetailActivityVu extends ActivityVu<CallDetailActivityCommand> {
    void setAdapter(CallDetailsAdapter adapter);
    void setDisplayName(String showName);
    void setActomaAccount(String actomaAccount);
    void setContactPhoto(String aactomaAccount);
}
