package com.xdja.imp.messageNotification;

/**
 * Created by liyingqing on 15-8-8.
 */
public class NotificationConfigBean {


    /**
     * 是否提醒
     */
    private Boolean isRemind = true;
    /**
     * 是否启动提醒声音
     */
    private Boolean hasVoice = true;
    /**
     * 是否启动震动
     */
    private Boolean hasShake = true;

    public Boolean getHasVoice() {
        return hasVoice;
    }

    public void setHasVoice(Boolean hasVoice) {
        this.hasVoice = hasVoice;
    }

    public Boolean getHasShake() {
        return hasShake;
    }

    public void setHasShake(Boolean hasShake) {
        this.hasShake = hasShake;
    }

    public Boolean getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(Boolean isRemind) {
        this.isRemind = isRemind;
    }
}
