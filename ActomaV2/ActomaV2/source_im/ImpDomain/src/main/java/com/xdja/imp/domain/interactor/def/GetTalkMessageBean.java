package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：从数据库获取短视频信息     <br>
 * 创建时间：2016/12/26     <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public interface GetTalkMessageBean extends Interactor<TalkMessageBean>{

    /**
     * 根据消息ID获取这条消息所有信息
     * @param talkId 消息Id
     * @return GetTalkMessageBean 返回当前消息id指定短视频信息
     */
    GetTalkMessageBean get(String talkId);
}
