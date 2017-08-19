package webrelay.inter;

import webrelay.bean.CallSession;

/**
 * Created by guoyaxin on 2015/12/31.
 */
public interface PushListener {

    /**
     * 呼叫过来
     * @param callSession
     */
    void  onCallComing(CallSession callSession);

    /**
     * 主叫拒接
     * @param callSession
     */
    void callerReject(CallSession callSession);

    /**
     * 被叫拒接
     * @param callSession
     */
    void calleeReject(CallSession callSession);
}
