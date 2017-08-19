package com.xdja.presenter_mainframe.bean;

/**
 * Created by ALH on 2016/7/23.
 */
public class AboutChipsInfoBean {
    /**
     * 颁发给
     */
    private String awardTo;
    /**
     * 颁发者
     */
    private String award;
    /**
     * 有效期
     */
    private String date;

    public String getAwardTo() {
        return awardTo;
    }

    public void setAwardTo(String awardTo) {
        this.awardTo = awardTo;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AboutChipsInfoBean{" +
                "awardTo='" + awardTo + '\'' +
                ", award='" + award + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
