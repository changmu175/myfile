package com.xdja.comm.circleimageview;

import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;


/**
 * Created by geyao on 2015/8/4.
 * 头像参数对象
 */
public class HeadImgParamsBean {
    /**
     * 头像host
     */
    private String host;
    /**
     * 头像id
     */
    private String fileId;
    /**
     * 头像大小
     */
    private String size;

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public String getFileId() {
        return fileId;
    }

    private void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSize() {
        return size;
    }

    private void setSize(String size) {
        this.size = size;
    }

    /**
     * 获取头像参数对象
     *
     * @param url 原地址(也就是服务器返回的头像图片地址)
     * @return 头像参数对象 内含头像控件加载图片所需的三个参数
     */
    public static HeadImgParamsBean getParams(String url) {
        HeadImgParamsBean bean = new HeadImgParamsBean();
        if (TextUtils.isEmpty(url)) {
            return bean;
        }
        try {
            String[] list = url.split("/");
            //截取字符串 获取 host 文件id 文件大小
            String fileID = list[list.length - 2];//头像加载所需的id
            String fileLength = list[list.length - 1];//头像加载所需的大小
            String host = url.replace("/" + fileID + "/" + fileLength, "");
            bean.setHost(host);
            bean.setFileId(fileID);
            bean.setSize(fileLength);
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return bean;
    }
}
