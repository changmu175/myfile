package com.xdja.comm.encrypt;

import java.util.List;

/**
 * Created by geyao on 2015/11/16.
 * 新策略-content
 */
public class NewStrategyContentBean {
    /**
     * 策略更新ID
     */
    private int strategyId;
    /**
     * 应用信息集合
     */
    private List<NewStrategyAppsBean> apps;

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public List<NewStrategyAppsBean> getApps() {
        return apps;
    }

    public void setApps(List<NewStrategyAppsBean> apps) {
        this.apps = apps;
    }

    @Override
    public String toString() {
        return "NewStrategyContentBean{" +
                "strategyId=" + strategyId +
                ", apps=" + apps +
                '}';
    }
}
