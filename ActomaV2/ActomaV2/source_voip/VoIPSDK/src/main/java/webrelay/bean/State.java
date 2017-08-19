package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/29.
 *
 * 呼叫当前状态
 *
 */
public enum State {
    /**
     * 未呼叫之前
     */
    INVALID,
    /**
     * 呼叫
     */
    CALLING,

    /**
     * 来电
     */
    INCOMING,

    /**
     * 正在接听
     */
    CONECTTING,

    /**
     * 接通
     */
    CONFIRMED,

    /**
     * 正在挂断
     */
    DISCONNECTING,
    /**
     * 挂断
     */
    DISCONNECTED

}
