package com.xdja.contact.util;

import android.content.Context;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.contact.R;

/**
 * Created by wanghao on 2015/8/9.
 */
public class NotificationParams {

    private Context context;
    /**通知栏标题*/
    private String contentTitle;
    /**通知栏内容*/
    private String contentText;

    private int notificationId;

    NotificationParams(){
        this.context = ActomaController.getApp();
        //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
        this.notificationId = NotifiParamUtil.CONTACT_NOTIFI_ID;
        //[E]modify by xienana for notification id @2016/10/11 [review by tangsha]
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getContentText() {
        return contentText;
    }

    private void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public static class Builder{

        NotificationParams notificationParams;

        public Builder(){
            notificationParams = new NotificationParams();
        }

        public Builder setTitle(String title){
            notificationParams.setContentTitle(title);
            return this;
        }

        public Builder setContent(String content){
            notificationParams.setContentText(content);
            return this;
        }

        /**
         * token失效过期时，有好友请求添加使用此param
         * @return
         */
        public NotificationParams tokenExpired(){
            setTitle(ActomaController.getApp().getString(R.string.new_friend_request_title));
            setContent(ActomaController.getApp().getString(R.string.new_friend_request_content));
            return notificationParams;
        }

        public NotificationParams normalParams(String content){
            setTitle(ActomaController.getApp().getString(R.string.new_friend_request_title));
            setContent(String.format(ActomaController.getApp().getResources().getString(R.string.request_friend), content));
            return notificationParams;
        }

    }

}
