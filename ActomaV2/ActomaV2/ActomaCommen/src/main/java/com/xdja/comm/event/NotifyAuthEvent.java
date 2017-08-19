package com.xdja.comm.event;

/**
 * <p>Summary:认证Ticket的事件定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.event</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/10</p>
 * <p>Time:9:32</p>
 */
public class NotifyAuthEvent {
    /**
     * 已存在的Ticket
     */
    private String oldTicket;


    public String getOldTicket() {
        return oldTicket;
    }

    public void setOldTicket(String oldTicket) {
        this.oldTicket = oldTicket;
    }
}
