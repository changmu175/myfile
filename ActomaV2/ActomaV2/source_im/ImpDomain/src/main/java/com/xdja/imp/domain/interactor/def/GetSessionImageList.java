package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * Created by guorong on 2016/7/5.
 */
public interface GetSessionImageList extends Interactor<List<TalkMessageBean>>{

    /**
     * 根据会话获取会话列表中的图片
     * @param talkId 回话Id
     * @return
     */
    GetSessionImageList get(String talkId);
}
