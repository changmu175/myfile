package com.xdja.imp.data.entity;

import android.support.annotation.IntDef;

import com.xdja.xutils.db.annotation.Column;
import com.xdja.xutils.db.annotation.Id;
import com.xdja.xutils.db.annotation.Table;
import com.xdja.xutils.db.annotation.Unique;

/**
 * <p>Summary:会话相关设置</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.entity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:13:53</p>
 */
@Table(name = "sessions")
public class SessionParam {
    public static final int ISTOP_FALE = 0;
    public static final int ISTOP_TRUE = 1;
    public static final int DEFAULT_ISTOP = ISTOP_FALE;

    public static final int ISDISTURB_FALE = 0;
    public static final int ISDISTURB_TRUE = 1;
    public static final int DEFAULT_ISDISTURB = ISDISTURB_FALE;

    /**
     * 勿扰模式待选择项
     */
    @IntDef(value = {ISDISTURB_FALE,ISDISTURB_TRUE})
    public @interface NoDisturbState{}

    /**
     * 置顶设置带选择项
     */
    @IntDef(value = {ISTOP_FALE,ISTOP_TRUE})
    public @interface SessionTopState{}

    /**
     * 数据库ID
     */
    @Id
    private long _id;
    /**
     * 会话唯一标示
     */
    @Unique
    @Column(column = "flag")
    private String flag;
    /**
     * 是否置顶
     */
    @Column(column = "isTop")
    private int isTop;
    /**
     * 是否消息免打扰
     */
    @Column(column = "isNoDisturb")
    private int isNoDisturb;
    /**
     * 草稿
     */
    @Column(column = "draft")
    private String draft;
    /**
     * 草稿创建时间
     */
    @Column(column = "draftTime")
    private long draftTime;
    /**
     * 扩展字段1
     */
    @Column(column = "ext2")
    private String ext2;
    /**
     * 扩展字段1
     */
    @Column(column = "ext3")
    private String ext3;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }


    @SessionTopState
    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(@SessionTopState int isTop) {
        this.isTop = isTop;
    }

    @NoDisturbState
    public int getIsNoDisturb() {
        return isNoDisturb;
    }

    public void setIsNoDisturb(@NoDisturbState int isNoDisturb) {
        this.isNoDisturb = isNoDisturb;
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

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    @Override
    public String toString() {
        return "SessionConfig{" +
//                "_id=" + _id +
                ", flag='" + flag + '\'' +
                ", isTop=" + isTop +
                ", isNoDisturb=" + isNoDisturb +
                ", draft='" + draft + '\'' +
                ", draftTime='" + draftTime + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", ext3='" + ext3 + '\'' +
                '}';
    }
}
