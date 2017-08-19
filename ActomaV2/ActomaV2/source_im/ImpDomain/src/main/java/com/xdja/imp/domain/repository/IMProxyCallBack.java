package com.xdja.imp.domain.repository;

import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * <p>Summary:IM业务层数据回调句柄</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/19</p>
 * <p>Time:16:19</p>
 */
public interface IMProxyCallBack {
    /**
     * 会话列表变更回调事件
     *
     * @param talkId 有变更的会话
     * @param action 更改动作
     * @return 回调函数是否处理
     */
//    @ConstDef.CallBackHandleState
//    int onTalkListChanged(long talkId, @ConstDef.ActionRefCon int action);

    /**
     * 消息列表变更回调事件
     *
     * @param msgId  消息ID
     * @param action 变更动作
     * @return 回调函数是否处理
     */
//    @ConstDef.CallBackHandleState
//    int onMessageListChanged(long msgId, @ConstDef.ActionRefMsg int action);

    /**
     * 创建新的会话事件回调
     *
     * @param talkListBean 已增加会话对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int onCreateNewTalk(TalkListBean talkListBean);

    /**
     * 删除会话事件回调
     *
     * @param talkListBean 待删除会话对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int onDeleteTalk(TalkListBean talkListBean);

    /**
     * 刷新单条会话回调
     *
     * @param talkListBean 待刷新会话对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int refreshSingleTalk(TalkListBean talkListBean);

    /**
     * 刷新会话列表事件回调
     *
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int refreshTalkList();

    /**
     * 接收到新消息
     *
     * @param talkMessageBean 消息对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int onReceiveNewMessage(String account, List<TalkMessageBean> talkMessageBean);


    /**
     * 新消息提醒，用于状态栏
     * @param talkMessageBean
     * @return
     */
    @ConstDef.CallBackHandleState
    int onRemainNewMessage(TalkListBean talker, List<TalkMessageBean> talkMessageBean);
//    /**
//     * 发送新消息成功
//     *
//     * @param talkMessageBean 消息对象
//     * @return 回调函数是否处理
//     */
//    @ConstDef.CallBackHandleState
//    int onSendMessageComplete(TalkMessageBean talkMessageBean);

    /**
     * 删除消息
     *
     * @param talkMessageBean 待删除消息对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int onDeleteMessage(String account, TalkMessageBean talkMessageBean);

    /**
     * 刷新单条消息
     *
     * @param talkMessageBean 待刷新消息对象
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int onRefreshSingleMessage(String account, TalkMessageBean talkMessageBean);

    /**
     * 消息被修改
     *
     * @return 回调函数是否处理
     */
    @ConstDef.CallBackHandleState
    int refreshMessageList();

    /**
     * 发送文件消息，文件上传进度更新
     *
     * @param talkId   发送文件的会话信息
     * @param msgId    发送文件的消息信息
     * @param fileInfo 当前发送的文件信息
     * @param progress 文件上传进度百分比
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onSendFileProgressUpdate(String talkId, long msgId, FileInfo fileInfo, int progress);

    /**
     * 发送文件消息，文件上传完成
     *
     * @param talkId   发送文件的会话信息
     * @param msgId    发送文件的消息信息
     * @param fileInfo 当前发送的文件信息
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onSendFileFinished(String talkId, long msgId, FileInfo fileInfo);

    /**
     * 发送文件消息失败
     *
     * @param talkId   发送文件的会话信息
     * @param msgId    发送文件的消息信息
     * @param fileInfo 当前发送的文件信息
     * @param code     错误码
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onSendFileFaild(String talkId, long msgId, FileInfo fileInfo, int code);

    /**
     * 接收文件消息，文件下载进度更新
     *
     * @param talkId   接收文件的会话信息
     * @param msgId    接收文件的消息信息
     * @param fileInfo 当前接收的文件信息
     * @param progress 文件下载进度百分比
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onReceiveFileProgressUpdate(String talkId, long msgId, FileInfo fileInfo, int progress);

    /**
     * 接收文件消息，文件下载完成
     *
     * @param talkId   接收文件的会话信息
     * @param msgId    接收文件的消息信息
     * @param fileInfo 当前接收的文件信息
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onReceiveFileFinished(String talkId, long msgId, FileInfo fileInfo);

    /**
     * 接收文件消息失败
     *
     * @param talkId   接收文件的会话信息
     * @param msgId    接收文件的消息信息
     * @param fileInfo 当前接收的文件信息
     * @param code     错误码
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onReceiveFileFaild(String talkId, long msgId, FileInfo fileInfo, int code);

    /**
     * 接收文件消息暂停
     *
     * @param talkId   接收文件的会话信息
     * @param msgId    接收文件的消息信息
     * @param fileInfo 当前接收的文件信息
     * @return 0 回调函数未处理， 1 回调函数已处理
     */
    @ConstDef.CallBackHandleState
    int onReceiveFilePaused(String talkId, long msgId, FileInfo fileInfo);

    /**
     * 初始化SDK完成
     */
    @ConstDef.CallBackHandleState
    int onInitFinished();

    @ConstDef.CallBackHandleState
    int onInitFailed();

//    /**
//     *不是好友关系，没有权限
//     * @return
//     */
//    @ConstDef.CallBackHandleState
//    int onServerForbid();
}
