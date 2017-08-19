package com.xdja.imp.data.repository.im;

import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * <p>Summary:OttO事件定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/28</p>
 * <p>Time:10:44</p>
 */
public class IMProxyEvent {

    /**
     * SDK初始化完成事件
     */
    public static class OnInitFinishedEvent {
        @Override
        public String toString() {
            return "OnInitFinishedEvent";
        }
    }

    public static class OnInitFailedEvent {
        @Override
        public String toString() {
            return "OnInitFailedEvent";
        }
    }

    /**
     * 不是好友关系
     */
    public static class OnFriendForbid{
        @Override
        public String toString() {
            return super.toString();
        }
    }
    /**
     * 消息相关的事件定义
     */
    public static class MessageEvent{
        /**
         * 聊天对象的账号
         */
        private String msgAccount;
        /**
         * 消息对象
         */
        private TalkMessageBean talkMessageBean;

        private List<TalkMessageBean> talkMessageBeansList;

        public String getMsgAccount() {
            return msgAccount;
        }

        public void setMsgAccount(String msgAccount) {
            this.msgAccount = msgAccount;
        }

        public TalkMessageBean getTalkMessageBean() {
            return talkMessageBean;
        }

        public void setTalkMessageBean(TalkMessageBean talkMessageBean) {
            this.talkMessageBean = talkMessageBean;
        }

        public List<TalkMessageBean> getTalkMessageBeansList() {
            return talkMessageBeansList;
        }

        public void setTalkMessageBeansList(List<TalkMessageBean> talkMessageBeansList) {
            this.talkMessageBeansList = talkMessageBeansList;
        }

        @Override
        public String toString() {
            return "MessageEvent{" +
                    "msgAccount=" + msgAccount +
                    ", talkMessageBean=" + talkMessageBean +
                    '}';
        }
    }


    public static class GetSingleListBeanDisturb extends MessageEvent{

        private TalkListBean listBean;

        private int talkType;

        public int getTalkType() {
            return talkType;
        }

        public void setTalkType(int talkType) {
            this.talkType = talkType;
        }

        public TalkListBean getListBean() {
            return listBean;
        }

        public void setListBean(TalkListBean listBean) {
            this.listBean = listBean;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("GetSingleListBeanDisturb{");
            sb.append("talkType=").append(talkType);
            sb.append('}');
            return sb.toString();
        }

    }

    /**
     * 修改闪信状态的事件
     */
    public static class DestroyedEvent extends MessageEvent {
        private String path;

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * 创建新消息事件
     */
    public static class ReceiveNewMessageEvent extends MessageEvent{

//        private long talkAccount;
//
//        public long getTalkAccount() {
//            return talkAccount;
//        }
//
//        public void setTalkAccount(long talkAccount) {
//            this.talkAccount = talkAccount;
//        }
//
//        @Override
//        public String toString() {
//            final StringBuffer sb = new StringBuffer("ReceiveNewMessageEvent{");
//            sb.append("talkAccount=").append(talkAccount);
//            sb.append('}');
//            return sb.toString();
//        }
    }

    /**
     * 发送消息成功事件
     */
    public static class SendNewMessageEvent extends MessageEvent{}

    /**
     * 删除消息事件
     */
    public static class DeleteMessageEvent extends MessageEvent{}

    /**
     * 刷新单条消息事件
     */
    public static class RefreshSingleMessageEvent extends MessageEvent{}

    /**
     * 刷新消息列表事件
     */
    public static class RefreshMessageListEvent{

        @Override
        public String toString() {
            return "RefreshMessageListEvent";
        }
    }

    /**
     * 会话相关的事件定义
     */
    public static class TalkEvent{
        /**
         * 会话ID
         */
        private String talkId;
        /**
         * 会话对象
         */
        private TalkListBean talkListBean;

        public String getTalkId() {
            return talkId;
        }

        public void setTalkId(String talkId) {
            this.talkId = talkId;
        }

        public TalkListBean getTalkListBean() {
            return talkListBean;
        }

        public void setTalkListBean(TalkListBean talkListBean) {
            this.talkListBean = talkListBean;
        }

        @Override
        public String toString() {
            return "TalkEvent{" +
                    "talkListBean=" + talkListBean +
                    ", talkAccount=" + talkId +
                    '}';
        }
    }

    /**
     * 创建新会话事件定义
     */
    public static class CreateNewTalkEvent extends TalkEvent{}

    /**
     * 删除会话事件定义
     */
    public static class DeleteTalkEvent extends TalkEvent{}

    //add by zya@xdja.com,20161011,fix bug 4392
    /**
     * 删除是集团通讯录成员的好友
     */
    public static class DeleteTalkEventWithDepartment extends TalkEvent{}
    //end by zya@xdja.com
    /**
     * 刷新单条会话事件定义
     */
    public static class RefreshSingleTalkEvent extends TalkEvent{}

    /**
     * 刷新会话列表事件定义
     */
    public static class RefreshTalkListEvent{

        /**
         * 设置界面删除全部消息标志
         * @return
         */
        //fix bug by licong, review by zya,2016/8/9
        private boolean isDeleteAllMsg;

        public boolean isDeleteAllMsg() {
            return isDeleteAllMsg;
        }

        public void setDeleteAllMsg(boolean deleteAllMsg) {
            isDeleteAllMsg = deleteAllMsg;
        }

        @Override
        public String toString() {
            return "RefreshTalkListEvent" +
                    "isDeleteAllMsg=" + isDeleteAllMsg + '}' ;
        }//end
    }

    /**
     * 文件相关事件定义
     */
    public static class FileEvent{
        /**
         * 文件ID
         */
        private long fileId;
        /**
         * 所属消息ID
         */
        private long attachedMsgId;
        /**
         * 所属会话ID
         */
        private String attachedTalkId;
        /**
         * 文件对象
         */
        private FileInfo fileInfo;

        public FileInfo getFileInfo() {
            return fileInfo;
        }

        public void setFileInfo(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        public long getFileId() {
            return fileId;
        }

        public void setFileId(long fileId) {
            this.fileId = fileId;
        }

        public long getAttachedMsgId() {
            return attachedMsgId;
        }

        public void setAttachedMsgId(long attachedMsgId) {
            this.attachedMsgId = attachedMsgId;
        }

        public String getAttachedTalkId() {
            return attachedTalkId;
        }

        public void setAttachedTalkId(String attachedTalkId) {
            this.attachedTalkId = attachedTalkId;
        }

        @Override
        public String toString() {
            return "FileEvent{" +
                    "fileId=" + fileId +
                    ", attachedMsgId=" + attachedMsgId +
                    ", attachedTalkId=" + attachedTalkId +
                    ", fileInfo=" + fileInfo +
                    '}';
        }
    }

    /**
     * 文件发送失败事件
     */
    public static class SendFileFailedEvent extends FileEvent{}

    /**
     * 文件接收失败事件
     */
    public static class ReceiveFileFailedEvent extends FileEvent{}


    //add by zya,decrypt file failed event.

    public static class DecryptFileFailedEvent extends FileEvent{}

    /**
     * 文件发送完成事件
     */
    public static class SendFileFinishedEvent extends FileEvent{}

    /**
     * 文件接收完成事件
     */
    public static class ReceiveFileFinishedEvent extends FileEvent{}

    /**
     * 文件传输进度更新事件
     */
    public static class FileProgressUpdateEvent extends FileEvent{
        /**
         * 文件传输百分比
         */
        private int percent;

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }
    }

    /**
     * 文件发送进度更新事件
     */
    public static class SendFileProgressUpdateEvent extends FileProgressUpdateEvent{}

    /**
     * 接收文件进度更新事件
     */
    public static class ReceiveFileProgressUpdateEvent extends FileProgressUpdateEvent{}


    /**
     * 接收文件暂停事件
     * */
    public static class ReceiveFilePaused extends FileEvent{}

    /**
     * 已选消息指示器事件
     */
    public static class IndicatorEvent{

        private int selectCnt;

        public int getSelectCnt() {
            return selectCnt;
        }

        public void setSelectCnt(int selectCnt) {
            this.selectCnt = selectCnt;
        }

        @Override
        public String toString() {
            return "IndicatorEvent{" +
                    "selectCnt=" + selectCnt +
                    '}';
        }
    }

    /**
     * 刷新标题指示器事件
     */
    public static class RefreshIndicatorEvent extends IndicatorEvent{}


    /**
     * 开始预览图片事件
     */
    public static class PicSelectEvent{
    }

    /**
     * 预览选中图片事件
     */
    public static class PicturePreviewEvent{

        private int currentIndex;


        public int getCurrentIndex() {
            return currentIndex;
        }


        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }
    }

    /**
     * 预览选中图片事件跳转至会话详情预览界面
     */
    public static class PictureToChatdetailEvent{

        private String filePath;


        public String getFilePath() {
            return filePath;
        }


        public void setFilePath(String fileName) {
            this.filePath = fileName;
        }
    }

    public static class ChatDeailPicDelete{

    }

    /**
     * 删除选中图片事件
     */
    public static class DeletePictureEvent extends PicSelectEvent{}

    /**
     * 发送已经选中的图片
     */
    public static class sendSelectedPictures extends PicSelectEvent{}

    /**
     * 语音播放事件
     */
    public static class PlayVoiceEvent {
        //播放文件路径
        String filePath;
        //播放状态
        String stateCode;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }
    }

    /**
     * 已选文件事件
     */
    public static class FileSelectedEvent {
        LocalFileInfo fileInfo;

        public FileSelectedEvent(LocalFileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        public LocalFileInfo getFileInfo() {
            return fileInfo;
        }

        public void setFileInfo(LocalFileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }
    }

    //add by zya
    public static class HistoryRefreshSelectHintEvent extends MessageEvent{}
}
