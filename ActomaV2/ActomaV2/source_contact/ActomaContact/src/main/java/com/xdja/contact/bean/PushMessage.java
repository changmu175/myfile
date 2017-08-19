package com.xdja.contact.bean;

import android.content.Context;

/**
 * Created by wanghao on 2015/8/7.
 */
public class PushMessage {

    private final String SPLIT = "#";

    private Context context;

    /**
     * 推送业务类型
     */
    private String pushServiceType;
    /**
     * 比如群组业务会出现一些id
     * 例如:updateGroupNickname#{groupid}
     */
    private String arg1;
    private String arg2;


    public PushMessage(Context context,String pushServiceType){
        this.context = context;
        this.pushServiceType = pushServiceType;
        split(pushServiceType);
    }

    private void split(String body){
        if(body.indexOf(SPLIT) < 0)return;
        String[] array = body.split(SPLIT);
        if (array.length >=1) {
            setPushServiceType(array[0]);
            if (array.length >= 2) {
                setArg1(array[1]);
                if (array.length >= 3) {
                    setArg2(array[2]);
                }
            }
        }
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg) {
        this.arg1 = arg;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg) {
        this.arg2 = arg;
    }

    public String getPushServiceType() {
        return pushServiceType;
    }

    public void setPushServiceType(String pushServiceType) {
        this.pushServiceType = pushServiceType;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "push [PushTitle="+pushServiceType+", arg1="+arg1+", arg2="+arg2+"]";
    }
}
