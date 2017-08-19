package com.xdja.comm.event;

/**
 * 描述当前类的作用
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-23 16:07
 */
public class LauncherUpdateEvent {
    private boolean isForceUpdate = false;

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }
}
