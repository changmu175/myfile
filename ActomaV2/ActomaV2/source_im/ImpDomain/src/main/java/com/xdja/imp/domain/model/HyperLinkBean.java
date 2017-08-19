package com.xdja.imp.domain.model;

/**
 * 项目名称：ActomaV2
 * 类描述：超链接的Bean
 * 创建人：yuchangmu
 * 创建时间：2016/12/5.
 * 修改人：yuchangmu
 * 修改时间：2016/12/5
 * 修改备注：
 *1) Task 2632 for share and forward function by ycm 20161205
 */
public class HyperLinkBean {
    private String hyperlink;
    private int startPosition;
    private int endPosition;

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    private int linkType;

}
