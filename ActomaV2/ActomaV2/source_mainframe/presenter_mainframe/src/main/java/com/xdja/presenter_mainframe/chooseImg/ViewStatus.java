package com.xdja.presenter_mainframe.chooseImg;

/**
 * <p>summary:当前视图状态封装类</p>
 * <p>description:</p>
 * <p>author:fanjiandong</p>
 * <p>time:2015/4/30 14:47</p>
 */
public class ViewStatus {
    /**
     * 状态栏的高度
     */
    private int statuBarHeight;
    /**
     * 标题栏的高度
     */
    private int titleBarHeight;

    /**
     * 状态栏 + 标题栏的高度
     */
    private int contentTop;

    public int getStatuBarHeight() {
        return statuBarHeight;
    }

    public void setStatuBarHeight(int statuBarHeight) {
        if (this.statuBarHeight <= 0)
            this.statuBarHeight = statuBarHeight;
    }

    public int getTitleBarHeight() {
        return titleBarHeight;
    }

    public void setTitleBarHeight(int titleBarHeight) {
        if (this.titleBarHeight <= 0)
            this.titleBarHeight = titleBarHeight;
    }

    public int getContentTop() {
        return contentTop;
    }

    public void setContentTop(int contentTop) {
        if (this.contentTop <= 0)
            this.contentTop = contentTop;
    }

    private ViewStatus() {

    }

    private static ViewStatus viewStatus;

    public static ViewStatus getViewStatus() {
        if (viewStatus == null)
            viewStatus = new ViewStatus();
        return viewStatus;
    }
}
