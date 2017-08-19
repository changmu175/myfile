package com.xdja.imp.data.cache;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:13:24</p>
 */
public class ConfigEntity {

    private String mxsEndpoint;

    private int deviceType = 0;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMxsEndpoint() {
        return mxsEndpoint;
    }

    public void setMxsEndpoint(String mxsEndpoint) {
        this.mxsEndpoint = mxsEndpoint;
    }
}
