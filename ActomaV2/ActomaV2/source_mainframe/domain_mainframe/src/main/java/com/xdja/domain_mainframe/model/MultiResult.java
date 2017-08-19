package com.xdja.domain_mainframe.model;

import java.util.Map;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:14:59</p>
 */
public class MultiResult<T> {

    /**
     * 执行状态
     */
    private int resultStatus;

    /**
     * 不同状态对应的结果
     */
    private Map<String,T> info;

    public int getResultStatus() {
        return resultStatus;
    }

    public MultiResult setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
        return this;
    }

    public Map<String, T> getInfo() {
        return info;
    }

    public MultiResult setInfo(Map<String, T> info) {
        this.info = info;
        return this;
    }

    @Override
    public String toString() {
        return "MultiResult{" +
                "resultStatus=" + resultStatus +
                ", info=" + info +
                '}';
    }
}
