package com.xdja.imp.domain.model;

/**
 * <p>Summary:会话本地设置/p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/11</p>
 * <p>Time:18:37</p>
 */
public class SessionConfig {

    private long id;

    /**
     * 会话ID
     */
    private String flag;
    /**
     * 是否置顶
     */
    private boolean isTop;
    /**
     * 是否消息免打扰
     */
    private boolean isNoDisturb;
    /**
     * 草稿信息
     */
    private String draft;
    /**
     *草稿创建时间
     */
    private long draftTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isNoDisturb() {
        return isNoDisturb;
    }

    public void setNoDisturb(boolean noDisturb) {
        isNoDisturb = noDisturb;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }
    public long getDraftTime() {
        return draftTime;
    }

    public void setDraftTime(long draftTime) {
        this.draftTime = draftTime;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SessionConfig{");
        sb.append("flag='").append(flag).append('\'');
        sb.append(", isTop=").append(isTop);
        sb.append(", isNoDisturb=").append(isNoDisturb);
        sb.append(", draft='").append(draft).append('\'');
        sb.append(", draftTime='").append(draftTime).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((flag == null) ? 0 : flag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SessionConfig other = (SessionConfig) obj;
        if (flag == null) {
            if (other.flag != null)
                return false;
        } else if (!flag.equals(other.flag)) {
            return false;
        }
        return true;
    }
}
