package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/11.
 * 通话的消息类型
 */
public enum MsgType {
    /**
     * 拉取历史消息
     */
    MSG_PULL_HISTORY("PULL_HISTORY_MSG"),

    /**
     * 通知被叫发起呼叫
     */
    MSG_MAKE_CALL("VNCL"),

    /**
     * 被叫接听之前主叫挂断电话
     */
    MSG_CALLER_REJECT("VRCL"),

    /**
     * 被叫响铃了，但拒接
     */
    MSG_CALLED_SUBSCRIBER_REJECT("VRCK");

    public String mType;
    MsgType(String type){
         mType=type;
    }
}
