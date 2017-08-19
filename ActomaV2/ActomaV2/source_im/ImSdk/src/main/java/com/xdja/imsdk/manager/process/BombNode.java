package com.xdja.imsdk.manager.process;

import com.xdja.imsdk.model.IMMessage;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/3 17:38                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class BombNode {
    private IMMessage message;//要处理的闪信消息
    private long insertQueueTime;//放入队列的时间，使用开机时间
    private int delayTime;//消息等待处理的剩余时间
    private int wholeDelayTime;//消息需要等待的总时间

    /**
     * 构造
     * @param msg 闪信消息
     * @param insertTime 入队时间
     * @param delay 等待时间
     */
    public BombNode(IMMessage msg, long insertTime, int delay) {
        this.message = msg;
        this.insertQueueTime = insertTime;
        this.delayTime = delay;
        this.wholeDelayTime = delay;
    }

    public IMMessage getMessage() {
        return message;
    }

    public void setMessage(IMMessage message) {
        this.message = message;
    }

    public long getInsertQueueTime() {
        return insertQueueTime;
    }

    public void setInsertQueueTime(long insertQueueTime) {
        this.insertQueueTime = insertQueueTime;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getWholeDelayTime() {
        return wholeDelayTime;
    }

    public void setWholeDelayTime(int wholeDelayTime) {
        this.wholeDelayTime = wholeDelayTime;
    }

    @Override
    public String toString() {
        return "BombNode{" +
                "message=" + message +
                ", insertQueueTime=" + insertQueueTime +
                ", delayTime=" + delayTime +
                ", wholeDelayTime=" + wholeDelayTime +
                '}' + "\n";
    }
}
