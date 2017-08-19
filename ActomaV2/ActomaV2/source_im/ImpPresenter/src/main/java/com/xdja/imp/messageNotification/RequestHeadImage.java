package com.xdja.imp.messageNotification;

import android.annotation.SuppressLint;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.xdja.comm.data.HeadImgParamsBean;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by liyingqing on 15-10-13.
 */
public class RequestHeadImage {
    /**
     * 获取真正请求的Url
     * @param url
     * @return
     */
    public static GlideUrl getGlideUrl(String url) {
        GlideUrl glideUrl = null;
        try {
            //截取字符串 获取 host 文件id 文件大小
            HeadImgParamsBean bean = HeadImgParamsBean.getParams(url);
            //按照参数规范 要去掉host末尾的/符号
            glideUrl = new GlideUrl(bean.getHost() + "?" + bean.getFileId()
                    , builderLazyHeaders(bean.getFileId(), bean.getSize()));
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return glideUrl;
    }

    private static LazyHeaders builderLazyHeaders(String fileID, String filelength) {
        LazyHeaders.Builder headersBuilder = new LazyHeaders.Builder();
        return headersBuilder.addHeader("xmlData", buildFileAgentHeader(fileID
                , filelength)).build();
    }
    @SuppressLint("StringConcatenationInsideStringBufferAppend")
    private static String buildFileAgentHeader(String fileID, String filelength) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version = \"1.0\" encoding =\"utf-8\"?><Root><Version>");
        sb.append("20130327");
        sb.append("</Version><Receiver>");
        sb.append("test");
        sb.append("</Receiver><FileID>");
        sb.append(fileID);
        sb.append("</FileID><FileRange>");
        sb.append("0-" + filelength);
        sb.append("</FileRange></Root>");
        return sb.toString();
    }
}
