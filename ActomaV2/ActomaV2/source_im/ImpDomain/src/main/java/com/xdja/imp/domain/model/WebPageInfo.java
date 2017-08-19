package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：ActomaV2
 * 类描述：网页信息实体封装
 * 创建人：yuchangmu
 * 创建时间：2016/12/19.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class WebPageInfo extends FileInfo implements Parcelable{
    private String title;// 标题
    private String description;// 描述
    private String webUri;// 网址链接
    private String source;// 来源

    public WebPageInfo() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(String webUri) {
        this.webUri = webUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private WebPageInfo(Parcel in) {
        super(in);
        title = in.readString();
        description = in.readString();
        webUri = in.readString();
        source = in.readString();
    }

    public static final Creator<WebPageInfo> CREATOR = new Creator<WebPageInfo>() {
        @Override
        public WebPageInfo createFromParcel(Parcel in) {
            return new WebPageInfo(in);
        }

        @Override
        public WebPageInfo[] newArray(int size) {
            return new WebPageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(webUri);
        dest.writeString(source);
    }
}
