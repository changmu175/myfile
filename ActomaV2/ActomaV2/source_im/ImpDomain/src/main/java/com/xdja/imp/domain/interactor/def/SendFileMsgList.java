package com.xdja.imp.domain.interactor.def;

import android.app.Activity;

import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * Created by xdjaxa on 2016/8/11.
 */
public interface SendFileMsgList extends Interactor<TalkMessageBean> {

    /**
     * 发送消息
     * @param to
     * @param isShan
     * @param isGroup
     * @param fileInfoList
     * @return
     */
    SendFileMsgList send(Activity context, String to, boolean isShan, boolean isGroup, List<FileInfo> fileInfoList);
}
