package com.xdja.comm.event;

/**
 * 描述当前类的作用
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-23 16:13
 */
public class UpdateNickNameEvent {
    private String newNickName;

    public String getNewNickName() {
        return newNickName;
    }

    public void setNewNickName(String newNickName) {
        this.newNickName = newNickName;
    }
}
