package com.xdja.imp.domain.model;

import android.text.TextUtils;

/**
 * 项目名称：ActomaV2
 * 类描述：分享信息封装
 * 创建人：yuchangmu
 * 创建时间：2017/1/5.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ShareInfo {
    private String source;
    private String title;//标题
    private String content;//正文内容
    private String webUrl;//网址
    private String fileUri;//文件uri
    private int shareType;//分享类型
    public static final int SHARE_FILE = 0x01;//文件分享
    public static final int SHARE_TEXT = 0x02;//文本分享
    public static final int SHARE_WEB = 0x03;//网页分享

    public ShareInfo(String title, String content, String webUrl, String fileUri, String source) {
        this.title = title;
        this.content = content;
        this.webUrl = webUrl;
        this.fileUri = fileUri;
        this.source = source;
        this.shareType = initShareType();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public int getShareType() {
        return shareType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private int initShareType() {
        if (TextUtils.isEmpty(this.webUrl)) {
            if (fileUri != null) {
                return SHARE_FILE;
            } else {
                return SHARE_TEXT;
            }
        }
        return SHARE_WEB;
    }
}

