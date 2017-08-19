package com.securevoip.utils;

import android.annotation.SuppressLint;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.xdja.comm.data.HeadImgParamsBean;

/**
 * Created by zjc on 2015/10/29.
 * 拼接https可用的图片请求url
 */
public class BuildImageUrl {

    /**
     * 获取真正请求的Url
     *
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

        }
        return glideUrl;
    }

    private static LazyHeaders builderLazyHeaders(String fileID, String filelength) {
        LazyHeaders.Builder headersBuilder = new LazyHeaders.Builder();
        LazyHeaders headers = headersBuilder.addHeader("xmlData", buildFileAgentHeader(fileID
                , filelength)).build();
        return headers;
    }

    @SuppressLint("StringBufferReplaceableByStringBuilder")
    private static String buildFileAgentHeader(String fileID, String filelength) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version = \"1.0\" encoding =\"utf-8\"?><Root><Version>");
        sb.append("20130327");
        sb.append("</Version><Receiver>");
        sb.append("test");
        sb.append("</Receiver><FileID>");
        sb.append(fileID);
        sb.append("</FileID><FileRange>");
        sb.append("0-").append(filelength);
        sb.append("</FileRange></Root>");
        return sb.toString();
    }


}
