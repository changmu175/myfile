package com.xdja.safeauth.request;

/**
 * Created by THZ on 2016/5/23.
 */
public class GetRandomBean {

    private String challengeStr;

    private String index;

    private long period;

    public String getChallengeStr() {
        return challengeStr;
    }

    public void setChallengeStr(String challengeStr) {
        this.challengeStr = challengeStr;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
