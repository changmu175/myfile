package webrelay.inter;

import webrelay.bean.CallSession;

/**
 * Created by guoyaxin on 2015/12/11.
 *
 * 更新呼叫界面接口
 */
public interface UpdateUIListener {


    //主叫拒接
    void callerReject(CallSession callSession);

    //被叫拒接
    void calledSubscriberReject(CallSession callSession);

}
