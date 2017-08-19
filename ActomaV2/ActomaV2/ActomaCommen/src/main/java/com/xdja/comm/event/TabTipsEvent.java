package com.xdja.comm.event;

import android.support.annotation.IntDef;

/**
 * <p>Summary:主页面小红点提示事件</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.event</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/22</p>
 * <p>Time:17:10</p>
 */
public class TabTipsEvent {
    /**
     * 聊天模块索引
     */
    public static final int INDEX_CHAT = 0;
    /**
     * 加密电话模块索引
     */
    public static final int INDEX_VOIP = 1;
    /**
     * 联系人模块索引
     */
    public static final int INDEX_CONTACT = 2;
    /**
     * 应用市场模块索引
     */
    public static final int INDEX_APP = 3;

    @IntDef(value = {INDEX_CHAT,INDEX_VOIP,INDEX_CONTACT,INDEX_APP})
    public @interface POINT_DEF{}

    /**
     * 要设置小红点的模块索引
     */
    private @POINT_DEF int index;

    /**
     *
     * @param index {@link #index}
     */
    public void setIndex(@POINT_DEF int index) {
        this.index = index;
    }

    /**
     *
     * @return {@link #index}
     */
    public int getIndex() {
        return index;
    }

    /**
     * 要显示的内容
     */
    private CharSequence content;
    /**
     * 是否显示小红点
     */
    private boolean isShowPoint;

    /**
     *
     * @return {@link #content}
     */
    public CharSequence getContent() {
        return content;
    }

    /**
     *
     * @param content {@link #content}
     */
    public void setContent(CharSequence content) {
        this.content = content;
    }

    /**
     *
     * @return {@link #isShowPoint}
     */
    public boolean isShowPoint() {
        return isShowPoint;
    }

    /**
     *
     * @param isShowPoint {@link #isShowPoint}
     */
    public void setIsShowPoint(boolean isShowPoint) {
        this.isShowPoint = isShowPoint;
    }
}
