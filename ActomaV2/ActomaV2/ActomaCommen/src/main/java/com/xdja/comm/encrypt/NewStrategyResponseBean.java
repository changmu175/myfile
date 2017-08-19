package com.xdja.comm.encrypt;

import java.util.List;

/**
 * Created by geyao on 2015/11/16.
 * 新策略-response
 */
public class NewStrategyResponseBean {
    /**
     * 是否有更新；true-有更新，false-无更新
     */
    private Boolean update;
    /**
     * 是否有更多；true-有，false-无
     */
    private Boolean hasMore;
    /**
     * 策略内容集合
     */
    private List<NewStrategyContentBean> content;

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<NewStrategyContentBean> getContent() {
        return content;
    }

    public void setContent(List<NewStrategyContentBean> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NewStrategyResponseBean{" +
                "update=" + update +
                ", hasMore=" + hasMore +
                ", content=" + content +
                '}';
    }
}
