package com.xdja.comm.event;

import android.content.Context;

/**
 * <p>Summary:登录成功后发送的事件定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.event</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/10</p>
 * <p>Time:18:43</p>
 */
public class OnLineEvent {
    /**
     * 登录用户的ticket
     */
    private String ticket;
    /**
     * 登录用户的安通账户
     */
    private String atAccount;
    /**
     * 上下文句柄
     */
    private Context context;

    /**
     *
     * @return {@link #context}
     */
    public Context getContext() {
        return context;
    }

    /**
     *
     * @param context {@link #context}
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     *
     * @return {@link #ticket}
     */
    public String getTicket() {
        return ticket;
    }

    /**
     *
     * @param ticket  {@link #ticket}
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     *
     * @return  {@link #atAccount}
     */
    public String getAtAccount() {
        return atAccount;
    }

    /**
     *
     * @param atAccount {@link #atAccount}
     */
    public void setAtAccount(String atAccount) {
        this.atAccount = atAccount;
    }
}
