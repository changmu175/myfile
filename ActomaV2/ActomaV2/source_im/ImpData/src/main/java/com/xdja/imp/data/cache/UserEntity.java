package com.xdja.imp.data.cache;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:19</p>
 */
public class UserEntity {
    /**
     * 业务相关帐号
     */
    private String account ;
    /**
     * ticket
     */
    private String ticket;

    /**
     * @return {@link #account}
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account {@link #account}
     */
    public void setAccount(String account) {
        this.account = account;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
