package com.xdja.imp.domain.repository;

import android.support.annotation.NonNull;

import com.xdja.imp.domain.model.*;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * <p>Summary:IM模块接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:15:14</p>
 * 修改备注：
 * Task 2632, modify for share and forward function by ycm at 20161130
 */
public interface IMProxyRepository {
    /**
     * 初始化IMSDK
     *
     * @return 0:表示初始化成功，其他表示失败
     */
    Observable<Integer> initIMProxy();

    /**
     * IMSDK资源释放
     *
     * @return 0:表示初始化成功，其他表示失败
     */
    Observable<Integer> releaseIMProxy();

    /**
     * 为IMSDK设置相关属性
     *
     * @param param 属性相关数据
     * @return 操作结果
     */
    Observable<Boolean> setProxyConfig(Map<String, String> param);

    /**
     * 获取IMSDK相关属性设置
     *
     * @return 属性相关数据
     */
    Observable<String> getProxyConfig(final String key);

    /**
     * 获取会话列表
     * <p>注：begin=0，size=0 则返回所有会话（取决于本地数据库数据量）
     * begin=x, size=y 返回x会话开始之后y个会话
     * begin=x, size=0 返回会话Id为x的会话
     * begin=0, size=y 最后一个更新会话向前取y个会话
     * </p>
     *
     * @param begin 获取会话起始位置会话Id
     * @param size  从起始位置开始获取指定数量会话
     * @return 符合条件的会话组成的列表，以会话生成的时间为序以会话最后一次更新时间倒序返回
     */
    Observable<List<TalkListBean>> getTalkListBeans(String begin, int size);

    /**
     * 删除会话
     *
     * @param talkIds 需要删除的会话id集合
     * @return 0： 删除会话成功
     */
    Observable<Integer> deleteTalks(List<String> talkIds);

    /**
     * 添加自定义会话
     *
     * @param talkBean 用户自定义会话
     * @return 0：调用成功;其他：调用错误
     */
    Observable<TalkListBean> addCustomTalk(TalkListBean talkBean);

    /**
     * 添加自定义消息到会话消息列表
     *
     * @param msg    需要插入的消息
     * @return 0：操作成功;其他：失败
     */
    Observable<TalkMessageBean> addCustomMessage(TalkMessageBean msg);


    /**
     * 获取指定会话消息列表
     * <P>注：begin=0，size=0 则返回所有消息（本地数据库数据）
     * begin=x, size=y MsgId=x之后y条消息
     * begin=0, size=y 当前时间点后y条信息
     * </P>
     *
     * @param talkId 会话ID
     * @param begin  获取消息起始位置
     * @param size   从起始位置开始获取指定数量消息
     * @return 符合条件的消息组成的列表，时间倒序排列。排序时间可选，服务器接收时间，客户端接收时间，客户端发送时间
     */
    Observable<List<TalkMessageBean>> getMessageList(String talkId, long begin, int size);

    /**
     * 删除消息
     *
     * @param msgids 列表形式提供删除多条消息
     * @return 0：删除消息成功
     */
    Observable<Integer> deleteMessages(List<Long> msgids);


    /**
     * 获取会话中未读消息数量
     *
     * @param talkId 指定的会话Id
     * @return -1：调用异常;其他：当前会话中未读消息数量
     */
    Observable<Integer> getUnReadMsgCount(String talkId);

    /**
     * 清空会话未读消息数量
     *
     * @param talkId 会话ID
     * @return 0：调用成功；其他：错误待定
     */
    Observable<Integer> clearUnReadMsgCount(String talkId);


    /**
     * 清空所有会话
     * @return
     */
    Observable<Integer> clearAllData();
    /**
     * 获取所有会话未读消息总数
     *
     * @return -1：调用异常;其他：所有会话未读消息数量总和
     */
    Observable<Integer> getAllUnReadMsgCount();


    /**
     * 获取会话中的图片列表
     * <P>注：begin=0，size=-1 则返回所有图像</P>
     *
     * @param talkId 指定的会话Id
     * @param begin  获取图像起始位置
     * @param size   从起始位置开始获取指定数量图像信息
     * @return 会话中出现过的图片信息，会话中消息时间为序。ImageInfoBean至少包含如下信息：
     * 1.	缩略图 Url
     * 2.	原图 Url
     */
    //fix bug 3760 by licong, reView by zya, 2016/9/13
    Observable<List<TalkMessageBean>> getImageList(String talkId, int begin, int size);

    /**
     * 发送消息
     *
     * @param talkMessageBean 待发送的消息对象
     * @return 消息保存在数据库中的数据库id
     */
    Observable<TalkMessageBean> sendMessage(TalkMessageBean talkMessageBean);

    /**
     * 发送文本消息
     *
     * @param content 消息内容
     * @param to      消息接收方标识
     * @param isShan  是否为闪信
     * @param isGroup 是否为群组
     * @return 发送结果
     */
    Observable<TalkMessageBean> sendTextMessage(@NonNull String content,
                                                @NonNull String to,
                                                boolean isShan,
                                                boolean isGroup);

    /**
     * 发送自定义文本消息
     *
     * @param content 消息内容
     * @param to      消息接收方标识
     * @param isGroup 是否为群组
     * @return 发送结果
     */
    Observable<TalkMessageBean> sendCustomTextMessage(@NonNull final String content,
                                                       @NonNull final String to,
                                                       final boolean isGroup);

    /**
     * 发送文件消息（语音、图片、视频、文件等）
     *
     * @param to       消息接收方标识
     * @param isShan   是否为闪信
     * @param isGroup  是否为群组
     * @param fileInfo 文件內容
     * @return
     */
    Observable<TalkMessageBean> sendFileMessage(@NonNull String to,
                                                 boolean isShan,
                                                 boolean isGroup,
                                                 FileInfo fileInfo);

    /**
     *发送文件消息（语音、图片、视频、文件等）
     * @param to      消息接收方标识
     * @param isShan  是否为闪信
     * @param isGroup 是否为群组
     * @param fileInfoList 文件內容
     * @return
     */
    Observable<TalkMessageBean> sendFileMessage(@NonNull String to,
                                                boolean isShan,
                                                boolean isGroup,
                                                List<FileInfo> fileInfoList);

    /**
     *发送网页消息
     * @param to      消息接收方标识
     * @param isShan  是否为闪信
     * @param isGroup 是否为群组
     * @param fileInfoList 文件內容
     * @return
     */
    Observable<TalkMessageBean> sendWebMessage(@NonNull String to,
                                               boolean isShan,
                                               boolean isGroup,
                                               WebPageInfo fileInfoList);
    /**
     *发送語音消息
     * @param to      消息接收方标识
     * @param isShan  是否为闪信
     * @param isGroup 是否为群组
     * @param fileInfo 文件內容
     * @return
     * @deprecated 文件发送接口统一 @sendFileMessage
     */
    Observable<TalkMessageBean> sendVoiceMessage(@NonNull String to,
                                                 boolean isShan,
                                                 boolean isGroup,
                                                 FileInfo fileInfo);

    /**
     * 暂停文件上传
     *
     * @param fileInfo 需要暂停的文件对象
     * @return 0：暂停文件上传成功;1：暂停处理失败
     */
    Observable<Integer> pauseFileSending(FileInfo fileInfo);


    /**
     * 重新开始文件上传
     *
     * @param fileInfo 需要暂停的文件对象
     * @return 0：恢复文件上传;1：恢复文件上传处理失败
     */
    Observable<Integer> resumeFileSend(FileInfo fileInfo);


    /**
     * 暂停文件下载
     *
     * @param fileInfo 需要暂停的文件对象
     * @return 0：回调函数未处理
     */
    Observable<Integer> pauseFileReceiving(FileInfo fileInfo);


    /**
     * 重新开始文件下载
     *
     * @param fileInfo 需要暂停的文件对象
     * @return 0：恢复文件下载;1：恢复文件下载处理失败
     */
    Observable<Integer> resumeFileReceive(FileInfo fileInfo);


    /**
     * 更新消息状态
     *
     * @param message 消息对象
     * @param mState  将要更新的状态
     * @return 0：状态更新成功;其他： 错误待定
     */
    Observable<Integer> changeMessageState(TalkMessageBean message, @ConstDef.MsgState int mState);

    /**
     * 消息重发
     *
     * @param msg 需要重发的消息信息
     * @return 0：重发成功;其他：错误待定
     */
    Observable<Integer> resendMsg(TalkMessageBean msg);

    /**
     * 清空某个会话的所有消息
     *
     * @param talkId 会话ID
     * @return 结果
     */
    Observable<Integer> clearAllMsgByTalkId(String talkId);

    /**
     * 初始化会话回调接口
     */
    Observable<Integer> registSessionCallBack();

    /**
     * 初始化消息回调
     */
    Observable<Integer> registMessageCallBack();

    /**
     * 初始化文件回调
     */
    Observable<Integer> registFileCallBack();

    /**
     * 注册所有回调
     *
     * @return
     */
    Observable<Integer> registAllCallBack();

    /**
     * 注销会话回调
     *
     * @return
     */
    Observable<Integer> unRegistSessionCallBack();

    /**
     * 注销消息回调
     *
     * @return
     */
    Observable<Integer> unRegistMessageCallBack();

    /**
     * 注销文件回调
     *
     * @return
     */
    Observable<Integer> unRegistFileCallBack();

    /**
     * 注销所有回调
     *
     * @return
     */
    Observable<Integer> unRegistAllCallBack();


    Observable<Integer> downloadFile(List<FileInfo> fileInfo);

    /**
     * 查询本机所有的图片
     * @return 本地图片信息列表
     */
    Observable<List<LocalPictureInfo>> queryLocalPictures();

    /**
     * 根据所有文件类型，查询本地文件
     * @param fileType 文件类型
     * @return
     */
    Observable<Map<String, List<LocalFileInfo>>> queryLocalFiles(int fileType);

    /**
     * 查询最近聊天文件
     * @return
     */
    Observable<Map<String, List<LocalFileInfo>>> queryLastFiles();

    /**
     * 根据本地图片列表转化为图片信息列表，主要是生成缩略图等相关信息，用来发送图片消息
     * @param pictureList 本地图片列表
     * @return 图片文件信息列表
     */
    Observable<List<FileInfo>> getImageFileList(List<LocalPictureInfo> pictureList);

    //add by ycm 20161110[start]
    /**
     * 转发图片时，直接获取所转发图片的缩略图，高清缩略图等相关信息，用来转发图片消息
     * @param pictureList
     * @return
     */
    Observable<List<FileInfo>> getImageFileListForForward(List<FileInfo> pictureList, boolean isOriginal);
    //add by ycm 20161110[end]

    //add by zya
    Observable<Map<HistoryFileCategory,List<TalkMessageBean>>> getAllHistoryFileInfoWithTalkId(String talkId);
    // add by ycm 2071/02/15
    Observable<Integer> getVersion(String account, String ticket);

    //add by licong for 网络模块刷新
    Observable<Integer> synService();
	
    Observable<TalkMessageBean> getMessageById(String msgId);
	
	// add by ycm for 压缩网页文件
    Observable<List<WebPageInfo>> getCompressFileList(List<WebPageInfo> fileInfoList);
}
