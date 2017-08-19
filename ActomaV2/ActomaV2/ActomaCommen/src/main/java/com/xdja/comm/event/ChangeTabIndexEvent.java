package com.xdja.comm.event;

/**
 * <p>Summary:修改主界面当前显示的Fragment的事件定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.event</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/22</p>
 * <p>Time:17:28</p>
 */
public class ChangeTabIndexEvent {
    /**
     * 要显示的界面的索引
     */
    private @TabTipsEvent.POINT_DEF  int index;

    /**
     *
     * @return {@link #index}
     */
    @TabTipsEvent.POINT_DEF
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index {@link #index}
     */
    public void setIndex(@TabTipsEvent.POINT_DEF int index) {
        this.index = index;
    }
}
