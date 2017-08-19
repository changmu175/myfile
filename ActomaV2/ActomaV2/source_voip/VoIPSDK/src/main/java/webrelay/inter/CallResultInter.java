package webrelay.inter;



import webrelay.bean.CallSession;
import webrelay.bean.VOIPError;

/**
 * Created by guoyaxin on 2015/12/5.
 */
public interface CallResultInter {

    int NORMAL_HANGUP = 0;

    //初始化之后，返回呼叫信息
    void afterInit(CallSession callSession);

    //呼叫成功-
    void callSeccess(CallSession callSession);

    //挂断
    void callFinished(CallSession callSession, int reason);

    //异常
    void callException(VOIPError  voipError);


}
