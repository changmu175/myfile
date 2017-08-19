package com.xdja.imsdk.db.helper;

import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/30 19:57                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class DelArgs {
    private List<String> tags;
    private List<Long> ids;
    private String tag;
    private int type;

    public DelArgs(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
