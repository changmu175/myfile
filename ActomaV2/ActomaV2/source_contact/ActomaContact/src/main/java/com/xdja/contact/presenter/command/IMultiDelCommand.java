package com.xdja.contact.presenter.command;

import android.view.View;

import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.frame.presenter.mvp.Command;

import java.util.List;

/**
 * Created by xdjaxa on 2016/11/1.
 */
public interface IMultiDelCommand extends Command {
    void delMemberMulti();
    void startSearch(String key);
    void endSearch();
    List<UserInfo> getCurrentAdapterSource();
    int getOpenType();
    boolean isGroupOwner();
    void delMemberSingle(String account);
    void startCommonDetail(View view);
    void startChooseContact();
}
