package com.xdja.imp.event;

/**
 * <p>Summary:会话状态更改事件</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.event</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/5</p>
 * <p>Time:14:27</p>
 */
public class SessionChangedEvent {

    /**
     * 会话标识
     */
    private String flag;

    /**
     * 获取会话ID
     *
     * @return 会话ID
     */
    public String getFlag() {
        return flag;
    }

    /**
     * 设置会话ID
     *
     * @param flag 会话ID
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * 置顶状态发生更改事件
     */
    public static class TopStateChangedEvent extends SessionChangedEvent {
        /**
         * 是否置顶
         */
        private boolean isTop;

        public void setTop(boolean top) {
            isTop = top;
        }

        public boolean isTop() {
            return isTop;
        }
    }

    /**
     * 勿扰模式状态发生改变事件
     */
    public static class NodisturbStateChangedEvent extends SessionChangedEvent{
        private boolean isNoDisturb;

        public void setNoDisturb(boolean noDisturb) {
            isNoDisturb = noDisturb;
        }

        public boolean isNoDisturb() {
            return isNoDisturb;
        }
    }

    /**
     * 消息被清空事件
     */
    public static class MessageCleardEvent extends SessionChangedEvent{}
}
