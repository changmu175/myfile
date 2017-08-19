package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.command</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:14:01</p>
 */
public interface ChatListCommand extends Command {
    /**
     * 列表项被点击
     * @param position 被点击的位置
     */
    void onListItemClick(int position);
    /**
     * 列表项被长按
     * @param position 被长按的位置
     */
    boolean onListItemLongClick(int position);
}
