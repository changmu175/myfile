package com.xdja.imsdk.model.internal.extra;
/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：网页消息额外信息     <br>
 * 创建时间：2017/3/20. 16:12  <br>
 * 修改记录：                 <br>
 *
 * @author ycm@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class WebExtraInfo {
    private String title;
    private String description;
    private String url;
    private String source;

    public WebExtraInfo(String title, String description, String url, String source) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
