package com.xdja.imp.util;

/**
 * <p>Summary:消息展示接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.util</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/12</p>
 * <p>Time:10:38</p>
 */
public interface MsgDisplay {
    /**
     * 展示文本消息
     * @param textMsg 文本消息
     */
    void display(String textMsg);

    /**
     * 展示文本消息
     * @param resId 文件资源Id
     */
    void display(int resId);

    /**
     * 展示文本消息
     * @param resId 文件资源Id
     * @param x 自定义位置x轴
     * @param y 自定义位置y轴
     * @param type 显示类型long/short
     */
    void display(int resId, int gravity, int x, int y, int type);
}
