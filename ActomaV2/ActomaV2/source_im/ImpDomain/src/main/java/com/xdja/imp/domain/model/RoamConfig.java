package com.xdja.imp.domain.model;

/**
 * <p>Summary:从服务器获取到的漫游信息</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/11</p>
 * <p>Time:11:35</p>
 */
public class RoamConfig {

    /**
     * 漫游状态
     */
    private @ConstDef.RoamState int status;
    /**
     * 漫游时长
     */
    private int time;
    @ConstDef.RoamState
    public int getStatus() {
        return status;
    }

    public void setStatus(@ConstDef.RoamState int status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RoamConfig{" +
                "status=" + status +
                ", time=" + time +
                '}';
    }
}
