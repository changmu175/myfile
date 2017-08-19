package webrelay;

import android.content.Context;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.dependence.uitls.LogUtil;

import okhttp3.Request;
import util.JsonUtil;
import util.SPUtil;
import webrelay.bean.CallSession;
import webrelay.bean.DescBean;
import webrelay.bean.ErrorBean;
import webrelay.bean.ErrorCode;
import webrelay.bean.FailedBean;
import webrelay.bean.MsgType;
import webrelay.bean.StatusCode;
import webrelay.bean.SuccessBean;
import webrelay.bean.TicketError;
import webrelay.bean.VOIPError;
import webrelay.inter.CallResultInter;


/**
 * Created by guoyaxin on 2015/12/5.
 *
 */
public class VOIPApi {

    private static final String TAG = "VOIPApi";

    private CallResultInter mCallResultInter;
    private Context mContext;
    public static String ACC_REG_STATE="ACC_REG_STATE";
    public static final int REG_SUCESS = 1;
    public static final int REG_FAILED = 0;


    public static final int CALL_MODE_PN = 0;
    public static final int CALL_MODE_SIP = 1;

    public VOIPApi(Context cxt,CallResultInter callResultInter){
        mContext=cxt;
        mCallResultInter=callResultInter;
    }

    /**
     *
     * @param callSession  呼叫CallSession
     */
    public void initCall(final CallSession callSession,String ticket){

        JsonCallback<SuccessBean,FailedBean,TicketError> jsonCallback=new JsonCallback<SuccessBean, FailedBean, TicketError>() {
            @Override
            public void onNetworkError(Request request, Exception e) {
                LogUtil.getUtils(TAG).d("initCall:"+e.getMessage());
                VOIPError voipError=new VOIPError( VOIPError.ErrorType.ERROR_INITINAL);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onNetworkError(Exception e) {
                LogUtil.getUtils(TAG).d("initCall:"+e.getMessage());
                VOIPError voipError=new VOIPError( VOIPError.ErrorType.ERROR_INITINAL);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onSuccess(SuccessBean successBean)  {
                String desc=successBean.getResult().getDesc();
                DescBean descBean=JsonUtil.jsonToObject(desc, DescBean.class);
                String asServerAddr = descBean.getHost() + ":" + descBean.getPort();
                SPUtil.saveVoIPLocalAddr(mContext,asServerAddr);
                callSession.setAs(asServerAddr);
                mCallResultInter.afterInit(callSession);
            }

            @Override
            public void onFailure(FailedBean failedBean) {

            }

            @Override
            public void onTicketError(TicketError ticketError) {

            }
        };
        // 首先获取AS服务器地址，并发送register消息
        VOIPBase.getInstance().getAddress(mContext, callSession.getSrc(),ticket,jsonCallback);
    }




    /**
     * 推送呼叫对方
     * @param callSession
     */
    public void pnCall(final CallSession callSession,String ticket) {


        JsonCallback<SuccessBean,FailedBean,TicketError> jsonCallback=new JsonCallback<SuccessBean, FailedBean, TicketError>() {
            @Override
            public void onNetworkError(Request request, Exception e) {
                LogUtil.getUtils(TAG).e("pnCall onNetworkError message:" + e.getMessage());
                VOIPError voipError=new VOIPError(VOIPError.ErrorType.ERROR_CALLING);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onNetworkError(Exception e) {
                LogUtil.getUtils(TAG).e("pnCall onNetworkError message:" + e.getMessage());
                VOIPError voipError=new VOIPError(VOIPError.ErrorType.ERROR_CALLING);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onSuccess(SuccessBean successBean) {
                mCallResultInter.callSeccess(callSession);
            }

            @Override
            public void onFailure(FailedBean failedBean)  {
                ErrorBean errorBean=failedBean.getError();
                String code=errorBean.getCode();
                LogUtil.getUtils(TAG).e("pnCall onFailure code:" + code);
                switch(code){
                    case ErrorCode.VOIP_NOT_FRIENDS:
                        mCallResultInter.callFinished(callSession,StatusCode.NOT_FRIENDS);
                        break;
                    case ErrorCode.INNER_ERROR:
                    case ErrorCode.NOT_WORKING:
                    case ErrorCode.PARAMS_ERROR:
                    case ErrorCode.RESULT_NOT_EXISITS:
                    case ErrorCode.USER_STATE_ERROR:
                    case ErrorCode.VOIP_CENTER_ERROR:
                    case ErrorCode.VOIP_SEVER_ERROR:
                    case ErrorCode.PM_ERROR:
                        //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. Start
                        mCallResultInter.callFinished(callSession, StatusCode.VOIP_SEVER_ERROR);//voip 服务器异常 wxf
                        break;
                        //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. End
                    case ErrorCode.MX_SEVER_ERROR:
                        //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123 . review by mengbo. Start
                        mCallResultInter.callFinished(callSession,StatusCode.MX_SEVER_ERROR);  // mx 服务器异常 wxf
                        break;
                        //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123 . review by mengbo. End
                    case ErrorCode.GET_DEVICE_ERROR:
                    case ErrorCode.USER_OFFLINE:
                        mCallResultInter.callFinished(callSession, StatusCode.CALLEE_OFFLINE);// 被叫不在线 xjq
                        break;
                }
            }

            @Override
            public void onTicketError(TicketError ticketError) {
                //gbc 接入层校验ticket失效，返回到登陆界面。 20160725
                LogUtil.getUtils(TAG).e("pnCall-----------------voip ticket invalid------------------");
                mCallResultInter.callFinished(callSession,StatusCode.TICKET_INVID);
                BusProvider.getMainProvider().post(new TicketAuthErrorEvent());
            }
        };
        VOIPBase.getInstance().sendMsg(mContext, callSession, MsgType.MSG_MAKE_CALL.mType,ticket, jsonCallback);
    }
    /**
     * 挂断电话
     * @param callSession
     */
    public void hangup( final CallSession callSession,String type,String ticket){



        JsonCallback<SuccessBean,FailedBean,TicketError> jsonCallback=new JsonCallback<SuccessBean, FailedBean, TicketError>() {
            @Override
            public void onNetworkError(Request request, Exception e) {
                LogUtil.getUtils(TAG).e("hangup onNetworkError message:" + e.getMessage());
                VOIPError voipError=new VOIPError( VOIPError.ErrorType.ERROR_REJECT);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onNetworkError(Exception e) {
                LogUtil.getUtils(TAG).e("hangup onNetworkError message:" + e.getMessage());
                VOIPError voipError=new VOIPError( VOIPError.ErrorType.ERROR_REJECT);
                mCallResultInter.callException(voipError);
            }

            @Override
            public void onSuccess(SuccessBean successBean) {
                mCallResultInter.callFinished(callSession, CallResultInter.NORMAL_HANGUP);
            }

            @Override
            public void onFailure(FailedBean failedBean){
                ErrorBean errorBean=failedBean.getError();
                if(errorBean != null){
                    String code=errorBean.getCode();
                    if(code != null){
                        LogUtil.getUtils(TAG).e("hangup onFailure code:" + code);
                        return;
                    }
                }
                LogUtil.getUtils(TAG).e("hangup onFailure");
            }

            @Override
            public void onTicketError(TicketError ticketError) {
                LogUtil.getUtils(TAG).e("hangup-----------------voip ticket invalid------------------");
            }
        };
        VOIPBase.getInstance().sendMsg(mContext, callSession,type, ticket, jsonCallback);

    }


}
