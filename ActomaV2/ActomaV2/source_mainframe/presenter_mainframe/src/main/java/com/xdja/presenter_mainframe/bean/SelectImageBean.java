package com.xdja.presenter_mainframe.bean;

import com.xdja.presenter_mainframe.chooseImg.ImageRelInfoBean;

/**
 * Created by ALH on 2016/8/12.
 */
public class SelectImageBean {
    /**
     * 图片详情
     */
    private ImageRelInfoBean infoBean;
    /**
     * 是否选中
     */
    private boolean isCheck;

    public ImageRelInfoBean getInfoBean() {
        return infoBean;
    }

    public void setInfoBean(ImageRelInfoBean infoBean) {
        this.infoBean = infoBean;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "SelectImageBean{" +
                "infoBean=" + infoBean +
                ", isCheck=" + isCheck +
                '}';
    }
}
