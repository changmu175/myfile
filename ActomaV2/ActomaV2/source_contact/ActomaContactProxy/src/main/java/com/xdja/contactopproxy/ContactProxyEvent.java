package com.xdja.contactopproxy;

import java.util.ArrayList;

/**
 * Created by liyingqing on 16-3-22.
 */
public class ContactProxyEvent {

    /**
     * 获取群组信息回调
     */
    public static class GetGroupInfoEvent{

        /**
         * 群组ID
         */
        String groupId;

        /**
         * 群组成员个数
         */
        int membersCount;  //add by ysp

        public String getoupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public int getMembersCount() {
            return membersCount;
        }

        public void setMembersCount(int membersCount) {
            this.membersCount = membersCount;
        }

        public GetGroupInfoEvent(String groupId, int membersCount){
            this.groupId = groupId;
            this.membersCount = membersCount;
        }
    }

    /**
     * 群组信息变更回调
     */
    public static class GroupUpdateEvent{
        /**
         * 群组ID
         */
        String groupId;
        public String getoupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        public GroupUpdateEvent(String groupId){
            this.groupId = groupId;
        }
    }

    /**
     * 主动退出群成功后，需要删除消息的回调
     */
    public static class QuitGroupNeedClearMessageEvent{
        /**
         * 群组ID
         */
        String groupId;
        public String getoupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        public QuitGroupNeedClearMessageEvent(String groupId){
            this.groupId = groupId;
        }
    }

    /**
     * 群组通知类型消息的回调
     */
    public static class GroupSystemMessageEvent{


        /**
         * 群组ID
         */
        String groupId;

        /**
         * 创建群组的提醒消息内容
         */
        String messageStr;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getMessageStr() {
            return messageStr;
        }

        public void setMessageStr(String messageStr) {
            this.messageStr = messageStr;
        }

    }

    /**
     * 退出并解散群回调
     */
    public static class QuitAndDismissEvent{
        boolean result;
        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
        public QuitAndDismissEvent(boolean result){
            this.result = result;
        }
    }


    /**
     * 接受好友请求回调
     */
    public static class AcceptFriendEvent{
        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        String account;
        /**
         * 创建接受好友的提醒消息内容 by wal@xdja.com
         */
        String messageStr;
        public String getMessageStr() {
            return messageStr;
        }
        public void setMessageStr(String messageStr) {
            this.messageStr = messageStr;
        }

        public AcceptFriendEvent(String account){
            this.account = account;
        }
    }

    /**
     * 主动删除好友关系，需要清空会话的事件
     */
    public static class DeletFriendClearTalkEvent{
        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        String account;
        public DeletFriendClearTalkEvent(String account){
            this.account = account;
        }
    }

    /**
     * 修改备注
     */
    public static class RemarkUpdateEvent{
        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        String account;
        String showName;
    }

    /**
     * 发起方收到对方同意
     */
    public static class ReceiveAcceptFrientEvent{
        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        String account;
        String message;
    }

    public static class NickNameUpdateEvent{

        public ArrayList<String> getAccounts() {
            return accounts;
        }

        public void setAccounts(ArrayList<String> accounts) {
            this.accounts = new ArrayList<>();
            this.accounts.addAll(accounts);
        }

        ArrayList<String>accounts;

    }

}
