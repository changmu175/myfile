package com.xdja.imp.frame.imp.component;

/**
 * 消息通知结构
 */
public class NotificationBean {
    /**
     * 账号
     */
    private String account;

    /**
     * 账号对应的名称
     */
    private String accountName;

    /**
     * 消息数量
     */
    private int    msgCount;


    /**
     * 头像
     */
    private String imageUrl;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (account == null || o == null) {
            return false;
        }
        if (o instanceof NotificationBean) {
            NotificationBean notificationBean = (NotificationBean) o;
            if (notificationBean.getAccount().equals(account)) {
                return true;
            }
        } else {
            if (o instanceof String) {
                String tempId = (String) o;
                if (tempId.equals(account)) {
                    return true;
                }
            }
            return false;
        }
        return super.equals(o);
    }
}
