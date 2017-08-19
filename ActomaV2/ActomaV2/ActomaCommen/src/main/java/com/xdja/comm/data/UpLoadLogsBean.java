package com.xdja.comm.data;

import java.util.ArrayList;

/**
 * Created by xrj on 2015-12-23 14:06:50
 * 日志上传请求参数对象
 */
public class UpLoadLogsBean {
    private UpLoadLogRequestBean upLoadLogRequestBean;

    private ArrayList<String> idList = null;

    public UpLoadLogRequestBean getUpLoadLogRequestBean() {
        return upLoadLogRequestBean;
    }

    public void setUpLoadLogRequestBean(UpLoadLogRequestBean upLoadLogRequestBean) {
        this.upLoadLogRequestBean = upLoadLogRequestBean;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<String> idList) {
        this.idList = idList;
    }
}

