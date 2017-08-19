/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  This file and this file only is also released under Apache license as an API file
 */

package com.csipsimple.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import com.xdja.comm.server.ActomaController;
import com.xdja.voipsdk.R;

/**
 * Represents state of a call session<br/>
 * This class helps to serialize/deserialize the state of the media layer <br/>
 * <b>Changing these fields has no effect on the sip call session </b>: it's
 * only a structured holder for datas <br/>
 */
public class  SipCallSession implements Parcelable {

    /**
     * Describe the control state of a call <br/>
     * <a target="_blank" href=
     * "http://www.pjsip.org/pjsip/docs/html/group__PJSIP__INV.htm#ga083ffd9c75c406c41f113479cc1ebc1c"
     * >Pjsip documentation</a>
     */
    public static class InvState {
        /**
         * The call is in an invalid state not syncrhonized with sip stack
         */
        public static final int INVALID = -1;
        /**
         * Before INVITE is sent or received
         */
        public static final int NULL = 0;
        /**
         * After INVITE is sent
         */
        public static final int CALLING = 1;
        /**
         * After INVITE is received.
         */
        public static final int INCOMING = 2;
        /**
         * After response with To tag.
         */
        public static final int EARLY = 3;
        /**
         * After 2xx is sent/received.
         */
        public static final int CONNECTING = 4;
        /**
         * After ACK is sent/received.
         */
        public static final int CONFIRMED = 5;
        /**
         * Session is terminated.
         */
        public static final int DISCONNECTED = 6;

        // Should not be constructed, just an older for int values
        // Not an enum because easier to pass to Parcelable
        private InvState() {
        }
    }
    
    /**
     * Option key to flag video use for the call. <br/>
     * The value must be a boolean.
     * 
     * @see Boolean
     */
    public static final String OPT_CALL_VIDEO = "opt_call_video";
    /**
     * Option key to add custom headers (with X- prefix). <br/>
     * The value must be a bundle with key representing header name, and value representing header value.
     * 
     * @see Bundle
     */
    public static final String OPT_CALL_EXTRA_HEADERS = "opt_call_extra_headers";

    /**
     * Describe the media state of the call <br/>
     * <a target="_blank" href=
     * "http://www.pjsip.org/pjsip/docs/html/group__PJSUA__LIB__CALL.htm#ga0608027241a5462d9f2736e3a6b8e3f4"
     * >Pjsip documentation</a>
     */
    public static class MediaState {
        /**
         * Call currently has no media
         */
        public static final int NONE = 0;
        /**
         * The media is active
         */
        public static final int ACTIVE = 1;
        /**
         * The media is currently put on hold by local endpoint
         */
        public static final int LOCAL_HOLD = 2;
        /**
         * The media is currently put on hold by remote endpoint
         */
        public static final int REMOTE_HOLD = 3;
        /**
         * The media has reported error (e.g. ICE negotiation)
         */
        public static final int ERROR = 4;

        // Should not be constructed, just an older for int values
        // Not an enum because easier to pass to Parcelable
        private MediaState() {
        }
    }

    /**
     * Status code of the sip call dialog Actually just shortcuts to SIP codes<br/>
     * <a target="_blank" href=
     * "http://www.pjsip.org/pjsip/docs/html/group__PJSIP__MSG__LINE.htm#gaf6d60351ee68ca0c87358db2e59b9376"
     * >Pjsip documentation</a>
     */
    public static class StatusCode {
        public static final int TRYING = 100;  //trying  接受请求，正在处理
        public static final int RINGING = 180; //ringing  振铃
        public static final int CALL_BEING_FORWARDED = 181; //呼叫正在前向
        public static final int QUEUED = 182; //队列中
        public static final int PROGRESS = 183;//会话进行
        public static final int OK = 200;  //会话成功
        public static final int ACCEPTED = 202; // 接受会话
        public static final int MULTIPLE_CHOICES = 300; //多重选择
        public static final int MOVED_PERMANENTLY = 301; //永久移动
        public static final int MOVED_TEMPORARILY = 302; //临时移动
        public static final int USE_PROXY = 305;         //用户代理
        public static final int ALTERNATIVE_SERVICE = 380; //替代服务
        public static final int BAD_REQUEST = 400;         //错误请求
        public static final int UNAUTHORIZED = 401;        //未授权
        public static final int PAYMENT_REQUIRED = 402;    //付费要求
        public static final int FORBIDDEN = 403;           //禁止
        public static final int NOT_FOUND = 404;           //未发现
        public static final int METHOD_NOT_ALLOWED = 405;   //方法不允许
        public static final int NOT_ACCEPTABLE = 406;       //不可接受
        public static final int INTERVAL_TOO_BRIEF = 423;   //间隔太短
        public static final int BUSY_HERE = 486;            //忙
        public static final int INTERNAL_SERVER_ERROR = 500; //服务器内部错误
        public static final int DECLINE = 603;               //丢弃


        // 对方安通账号不存在返回的错误码。需要sip服务器返回。Add by xjq, 20150807
        public static final int ACCOUNT_NOT_FOUND = 479;

        // TCP发送错误，需要增加异常提示否则会闪退。Add by xjq, 20150807
        public static final int SEND_FAILED = 477;

        // 服务器繁忙，无法处理当前请求。Add by xjq, 20150807
        public static final int SERVICE_UNAVAILABLE = 503;

        /*
         * PJSIP_SC_PROXY_AUTHENTICATION_REQUIRED = 407,
         * PJSIP_SC_REQUEST_TIMEOUT = 408, PJSIP_SC_GONE = 410,
         * PJSIP_SC_REQUEST_ENTITY_TOO_LARGE = 413,
         * PJSIP_SC_REQUEST_URI_TOO_LONG = 414, PJSIP_SC_UNSUPPORTED_MEDIA_TYPE
         * = 415, PJSIP_SC_UNSUPPORTED_URI_SCHEME = 416, PJSIP_SC_BAD_EXTENSION
         * = 420, PJSIP_SC_EXTENSION_REQUIRED = 421,
         * PJSIP_SC_SESSION_TIMER_TOO_SMALL = 422,
         * PJSIP_SC_TEMPORARILY_UNAVAILABLE = 480,
         * PJSIP_SC_CALL_TSX_DOES_NOT_EXIST = 481, PJSIP_SC_LOOP_DETECTED = 482,
         * PJSIP_SC_TOO_MANY_HOPS = 483, PJSIP_SC_ADDRESS_INCOMPLETE = 484,
         * PJSIP_AC_AMBIGUOUS = 485, PJSIP_SC_BUSY_HERE = 486,
         * PJSIP_SC_REQUEST_TERMINATED = 487, PJSIP_SC_NOT_ACCEPTABLE_HERE =
         * 488, PJSIP_SC_BAD_EVENT = 489, PJSIP_SC_REQUEST_UPDATED = 490,
         * PJSIP_SC_REQUEST_PENDING = 491, PJSIP_SC_UNDECIPHERABLE = 493,
         * PJSIP_SC_INTERNAL_SERVER_ERROR = 500, PJSIP_SC_NOT_IMPLEMENTED = 501,
         * PJSIP_SC_BAD_GATEWAY = 502, PJSIP_SC_SERVICE_UNAVAILABLE = 503,
         * PJSIP_SC_SERVER_TIMEOUT = 504, PJSIP_SC_VERSION_NOT_SUPPORTED = 505,
         * PJSIP_SC_MESSAGE_TOO_LARGE = 513, PJSIP_SC_PRECONDITION_FAILURE =
         * 580, PJSIP_SC_BUSY_EVERYWHERE = 600, PJSIP_SC_DOES_NOT_EXIST_ANYWHERE
         * = 604, PJSIP_SC_NOT_ACCEPTABLE_ANYWHERE = 606,
         */
    }

    //客户端显示中文提示-Mod-Lixin-0414
    //400-800的错误码
    public static class StatusCommentReplace{

    	//CODE
    	public static final int BAD_REQUEST_CODE = 400;
    	public static final int UNAUTHORIZED_CODE = 401;
    	public static final int PAYMENT_REQUIRED_CODE = 402;
    	public static final int FORBIDDEN_CODE = 403;
    	public static final int NOT_FOUND_CODE = 404;
    	public static final int METHOD_NOT_ALLOWED_CODE = 405;
    	public static final int NOT_ACCEPTABLE_CODE = 406;
    	public static final int PROXY_AUTHENTICATION_REQUIRED_CODE = 407;
        public static final int REQUEST_TIMEOUT_CODE = 408;
        public static final int GONE_CODE = 410;
        public static final int REQUEST_ENTITY_TOO_LARGE_CODE = 413;
        public static final int REQUEST_URI_TOO_LARGE_CODE = 414;
        public static final int UNSUPPORTED_MEDIA_TYPE_CODE = 415;
        public static final int UNSUPPORTED_URI_SCHEME_CODE = 416;
        public static final int BAD_EXTENSION_CODE = 420;
        public static final int EXTENSION_REQUIRED_CODE = 421;
        public static final int SESSION_TIMER_TOO_SMALL_CODE = 422;
        public static final int INTERVAL_TOO_BRIEF_CODE = 423;
        public static final int TEMPORARILY_UNAVAILABLE_CODE = 480;
        public static final int CALL_TRANSACTION_DONOT_EXIST_CODE = 481;
        public static final int LOOP_DETECTED_CODE = 482;
        public static final int TOO_MANY_HOPS_CODE = 483;
        public static final int ADDRESS_INCOMPLETED_CODE = 484;
        public static final int AMBIGIOUS_CODE = 485;
        public static final int BUSY_HERE_CODE = 486;
        public static final int REQUEST_TERMINATED_CODE = 487;
        public static final int NOT_ACCEPTED_HERE_CODE = 488;
        public static final int BAD_EVENT_CODE = 489;
        public static final int REQUEST_UPDATED_CODE = 490;
        public static final int REQUEST_PENDDING_CODE = 491;
        public static final int UNDECIPHERABLE_CODE = 493;
        
        public static final int INTERNAL_SERVER_ERROR_CODE = 500;
        public static final int NOT_IMPLEMENTED_CODE = 501;
        public static final int BAD_GATEWAY_CODE = 502;
        public static final int SERVICE_UNAVAILABLE_CODE = 503;
        public static final int SERVER_TIMEOUT_CODE = 504;
        public static final int VERSION_NOT_SUPPORTED_CODE = 505;
        public static final int MESSAGE_TOO_LARGE_CODE = 513;
        public static final int PRECONDITION_FAILURE_CODE = 580;
        
        public static final int BUSY_EVERYWHERE_CODE = 600;
        public static final int DECLINE_CODE = 603;
        public static final int NOT_EXIST_ANYWHERE_CODE = 604;
        public static final int NOT_ACCPTED_CODE = 606;
        
        public static final int NO_RESPONSE_FROM_SERVER_CODE = 701;
        public static final int UNABLE_RESOLVE_SERVER_CODE = 702;
        public static final int ERROR_SENDING_MSG_SERVER_CODE = 703;
        
        /** Begin:add by xjq 增加自定义SIP状态码 20140819 **/
        public static final int NETWORK_DISCONNECT = 1000;
        public static final int START_RECONNECT = 1001;
        public static final int RECONNECT_SUCCESS = 1002;
        public static final int NETWORK_DELAY = 1003;
        /**
         * 主叫呼叫超时
         */
        public static final int UAC_CALLING_TIMEOUT = 1004;

        /**
	* 被叫超时
	*/
        public static final int UAS_CALLING_TIMEOUT = 1005;
        public static final int CALL_DURATION_EXCEED = 1006;
        public static final int UAC_REMOTE_BUSY = 1007;
        public static final int CUST_HANGUP_DOING = 1008;
        public static final int CUST_HANGUP_END = 1009;
        /** End:add by xjq 增加自定义SIP状态码 20140819 **/

        /**Begin:sunyunlei 添加VOIP 对方密信号不存在或者未开通 错误码 20150302**/
        public static final int ACCOUNT_NOT_FOUND_CODE = 479;
        /**End:sunyunlei 添加VOIP 对方密信号不存在或者未开通 错误码 20150302**/

        // Send failed error code. Add by xjq, 2015/3/11
        public static final int SEND_FAILED_CODE = 477; // TCP Conect init rror

    	//COMMENT
        public static final String BAD_REQUEST_COMMENT = ActomaController.getApp().getString(R.string.BAD_REQUEST_COMMENT);
        public static final String UNAUTHORIZED_COMMENT = ActomaController.getApp().getString(R.string.UNAUTHORIZED_COMMENT);
        public static final String PAYMENT_REQUIRED_COMMENT = ActomaController.getApp().getString(R.string.PAYMENT_REQUIRED_COMMENT);
        public static final String FORBIDDEN_COMMENT = ActomaController.getApp().getString(R.string.FORBIDDEN_COMMENT);
        public static final String NOT_FOUND_COMMENT = ActomaController.getApp().getString(R.string.NOT_FOUND_COMMENT);
        public static final String METHOD_NOT_ALLOWED_COMMENT = ActomaController.getApp().getString(R.string.METHOD_NOT_ALLOWED_COMMENT);
        public static final String NOT_ACCEPTABLE_COMMENT = ActomaController.getApp().getString(R.string.NOT_ACCEPTABLE_COMMENT);
        public static final String PROXY_AUTHENTICATION_REQUIRED_COMMENT = ActomaController.getApp().getString(R.string.PROXY_AUTHENTICATION_REQUIRED_COMMENT);
        public static final String REQUEST_TIMEOUT_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_TIMEOUT_COMMENT);
        public static final String GONE_COMMENT = ActomaController.getApp().getString(R.string.GONE_COMMENT);
        public static final String REQUEST_ENTITY_TOO_LARGE_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_ENTITY_TOO_LARGE_COMMENT);
        public static final String REQUEST_URI_TOO_LARGE_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_URI_TOO_LARGE_COMMENT);
        public static final String UNSUPPORTED_MEDIA_TYPE_COMMENT = ActomaController.getApp().getString(R.string.UNSUPPORTED_MEDIA_TYPE_COMMENT);
        public static final String UNSUPPORTED_URI_SCHEME_COMMENT = ActomaController.getApp().getString(R.string.UNSUPPORTED_URI_SCHEME_COMMENT);
        public static final String BAD_EXTENSION_COMMENT = ActomaController.getApp().getString(R.string.BAD_EXTENSION_COMMENT);
        public static final String EXTENSION_REQUIRED_COMMENT = ActomaController.getApp().getString(R.string.EXTENSION_REQUIRED_COMMENT);
        public static final String SESSION_TIMER_TOO_SMALL_COMMENT = ActomaController.getApp().getString(R.string.SESSION_TIMER_TOO_SMALL_COMMENT);
        public static final String INTERVAL_TOO_BRIEF_COMMENT = ActomaController.getApp().getString(R.string.INTERVAL_TOO_BRIEF_COMMENT);
        public static final String TEMPORARILY_UNAVAILABLE_COMMENT = ActomaController.getApp().getString(R.string.TEMPORARILY_UNAVAILABLE_COMMENT);
        public static final String CALL_TRANSACTION_DONOT_EXIST_COMMENT = ActomaController.getApp().getString(R.string.CALL_TRANSACTION_DONOT_EXIST_COMMENT);
        public static final String LOOP_DETECTED_COMMENT = ActomaController.getApp().getString(R.string.LOOP_DETECTED_COMMENT);
        public static final String TOO_MANY_HOPS_COMMENT = ActomaController.getApp().getString(R.string.TOO_MANY_HOPS_COMMENT);
        public static final String ADDRESS_INCOMPLETED_COMMENT = ActomaController.getApp().getString(R.string.ADDRESS_INCOMPLETED_COMMENT);
        public static final String AMBIGIOUS_COMMENT = ActomaController.getApp().getString(R.string.AMBIGIOUS_COMMENT);
        public static final String BUSY_HERE_COMMENT = ActomaController.getApp().getString(R.string.BUSY_HERE_COMMENT);
        public static final String REQUEST_TERMINATED_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_TERMINATED_COMMENT);
        public static final String NOT_ACCEPTED_HERE_COMMENT = ActomaController.getApp().getString(R.string.NOT_ACCEPTED_HERE_COMMENT);
        public static final String BAD_EVENT_COMMENT = ActomaController.getApp().getString(R.string.BAD_EVENT_COMMENT);
        public static final String REQUEST_UPDATED_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_UPDATED_COMMENT);
        public static final String REQUEST_PENDDING_COMMENT = ActomaController.getApp().getString(R.string.REQUEST_PENDDING_COMMENT);
        public static final String UNDECIPHERABLE_COMMENT = ActomaController.getApp().getString(R.string.UNDECIPHERABLE_COMMENT);

        public static final String INTERNAL_SERVER_ERROR_COMMENT = ActomaController.getApp().getString(R.string.INTERNAL_SERVER_ERROR_COMMENT);
        public static final String NOT_IMPLEMENTED_COMMENT = ActomaController.getApp().getString(R.string.NOT_IMPLEMENTED_COMMENT);
        public static final String BAD_GATEWAY_COMMENT = ActomaController.getApp().getString(R.string.BAD_GATEWAY_COMMENT);
        public static final String SERVICE_UNAVAILABLE_COMMENT = ActomaController.getApp().getString(R.string.SERVICE_UNAVAILABLE_COMMENT);
        public static final String SERVER_TIMEOUT_COMMENT = ActomaController.getApp().getString(R.string.SERVER_TIMEOUT_COMMENT);
        public static final String VERSION_NOT_SUPPORTED_COMMENT = ActomaController.getApp().getString(R.string.VERSION_NOT_SUPPORTED_COMMENT);
        public static final String MESSAGE_TOO_LARGE_COMMENT = ActomaController.getApp().getString(R.string.MESSAGE_TOO_LARGE_COMMENT);
        public static final String PRECONDITION_FAILURE_COMMENT = ActomaController.getApp().getString(R.string.PRECONDITION_FAILURE_COMMENT);

        public static final String BUSY_EVERYWHERE_COMMENT = ActomaController.getApp().getString(R.string.BUSY_EVERYWHERE_COMMENT);
        public static final String DECLINE_COMMENT = ActomaController.getApp().getString(R.string.DECLINE_COMMENT);
        public static final String NOT_EXIST_ANYWHERE_COMMENT = ActomaController.getApp().getString(R.string.NOT_EXIST_ANYWHERE_COMMENT);
        public static final String NOT_ACCPTED_COMMENT = ActomaController.getApp().getString(R.string.NOT_ACCPTED_COMMENT);

        public static final String NO_RESPONSE_FROM_SERVER_COMMENT = ActomaController.getApp().getString(R.string.NO_RESPONSE_FROM_SERVER_COMMENT);
        public static final String UNABLE_RESOLVE_SERVER_COMMENT = ActomaController.getApp().getString(R.string.UNABLE_RESOLVE_SERVER_COMMENT);
        public static final String ERROR_SENDING_MSG_SERVER_COMMENT = ActomaController.getApp().getString(R.string.ERROR_SENDING_MSG_SERVER_COMMENT);

        /** Begin:add by xjq 增加自定义SIP状态描述字符 20140819 **/
        public static final String NETWORK_DISCONNECT_COMMENT = ActomaController.getApp().getString(R.string.NETWORK_DISCONNECT_COMMENT);
        public static final String START_RECONNECT_COMMENT = ActomaController.getApp().getString(R.string.START_RECONNECT_COMMENT);
        public static final String RECONNECT_SUCCESS_COMMENT = ActomaController.getApp().getString(R.string.RECONNECT_SUCCESS_COMMENT ); // Must not empty, use space.Add by xjq, 2015/3/11
        public static final String NETWORK_DELAY_COMMENT = ActomaController.getApp().getString(R.string.NETWORK_DELAY_COMMENT);
        public static final String UAC_REMOTE_BUSY_COMMENT = ActomaController.getApp().getString(R.string.UAC_REMOTE_BUSY_COMMENT);
        /** End:add by xjq 增加自定义SIP状态描述字符 20140819 **/

        /**Begin:sunyunlei 添加VOIP 对方密信号不存在或者未开通 错误码 20150302**/
        public static final String ACCOUNT_NOT_FOUND_COMMENT = ActomaController.getApp().getString(R.string.ACCOUNT_NOT_FOUND_COMMENT);
        /**End:sunyunlei 添加VOIP 对方密信号不存在或者未开通 错误码 20150302**/

        // Send failed comment. Add by xjq, 2015/3/11
        public static final String SEND_FAILED_COMMENT = ActomaController.getApp().getString(R.string.SEND_FAILED_COMMENT);// 提示暂时和404统一

        //zjc 20150820 这个由正在挂断改为挂断
        //zjc 20150856 挂断改回正在挂断
        public static final String HANGUP_DOING = ActomaController.getApp().getString(R.string.HANGUP_DOING);
        public static final String HANGUP_END = ActomaController.getApp().getString(R.string.HANGUP_END);

        public static String ReplaceStatusComment(int code){
        	switch(code){
        	case BAD_REQUEST_CODE:
        		 return BAD_REQUEST_COMMENT;
        	case UNAUTHORIZED_CODE:
        		 return UNAUTHORIZED_COMMENT;
        	case PAYMENT_REQUIRED_CODE:
        		return PAYMENT_REQUIRED_COMMENT;
        	case FORBIDDEN_CODE:
        		return FORBIDDEN_COMMENT;
        	case NOT_FOUND_CODE:
        		return NOT_FOUND_COMMENT;
        	case METHOD_NOT_ALLOWED_CODE:
        		return METHOD_NOT_ALLOWED_COMMENT;
        	case NOT_ACCEPTABLE_CODE:
        		return NOT_ACCEPTABLE_COMMENT;
        	case PROXY_AUTHENTICATION_REQUIRED_CODE:
        		return PROXY_AUTHENTICATION_REQUIRED_COMMENT;
        	case REQUEST_TIMEOUT_CODE:
        		return REQUEST_TIMEOUT_COMMENT;
        	case GONE_CODE:
        		return GONE_COMMENT;
        	case REQUEST_ENTITY_TOO_LARGE_CODE:
        		return REQUEST_ENTITY_TOO_LARGE_COMMENT;
        	case REQUEST_URI_TOO_LARGE_CODE:
        		return REQUEST_URI_TOO_LARGE_COMMENT;
        	case UNSUPPORTED_MEDIA_TYPE_CODE:
        		return UNSUPPORTED_MEDIA_TYPE_COMMENT;
        	case UNSUPPORTED_URI_SCHEME_CODE:
        		return UNSUPPORTED_URI_SCHEME_COMMENT;
        	case BAD_EXTENSION_CODE:
        		return BAD_EXTENSION_COMMENT;
        	case EXTENSION_REQUIRED_CODE:
        		return EXTENSION_REQUIRED_COMMENT;
        	case SESSION_TIMER_TOO_SMALL_CODE:
        		return SESSION_TIMER_TOO_SMALL_COMMENT;
        	case INTERVAL_TOO_BRIEF_CODE:
        		return INTERVAL_TOO_BRIEF_COMMENT;
        	case CALL_TRANSACTION_DONOT_EXIST_CODE:
        		return TEMPORARILY_UNAVAILABLE_COMMENT;
        	case TEMPORARILY_UNAVAILABLE_CODE:
        		return CALL_TRANSACTION_DONOT_EXIST_COMMENT;
        	case LOOP_DETECTED_CODE:
        		return LOOP_DETECTED_COMMENT;
        	case TOO_MANY_HOPS_CODE:
        		return TOO_MANY_HOPS_COMMENT;
        	case ADDRESS_INCOMPLETED_CODE:
        		return ADDRESS_INCOMPLETED_COMMENT;
        	case AMBIGIOUS_CODE:
        		return AMBIGIOUS_COMMENT;
        	case BUSY_HERE_CODE:
        		return BUSY_HERE_COMMENT;
        	case REQUEST_TERMINATED_CODE:
        		return REQUEST_TERMINATED_COMMENT;
        	case NOT_ACCEPTED_HERE_CODE:
        		return NOT_ACCEPTED_HERE_COMMENT;
        	case BAD_EVENT_CODE:
        		return BAD_EVENT_COMMENT;
        	case REQUEST_UPDATED_CODE:
        		return REQUEST_UPDATED_COMMENT;
        	case REQUEST_PENDDING_CODE:
        		return REQUEST_PENDDING_COMMENT;
        	case UNDECIPHERABLE_CODE:
        		return UNDECIPHERABLE_COMMENT;
        	case INTERNAL_SERVER_ERROR_CODE:
        		return INTERNAL_SERVER_ERROR_COMMENT;
        	case NOT_IMPLEMENTED_CODE:
        		return NOT_IMPLEMENTED_COMMENT;
        	case BAD_GATEWAY_CODE:
        		return BAD_GATEWAY_COMMENT;
        	case SERVICE_UNAVAILABLE_CODE:
        		return SERVICE_UNAVAILABLE_COMMENT;
        	case SERVER_TIMEOUT_CODE:
        		return SERVER_TIMEOUT_COMMENT;
        	case VERSION_NOT_SUPPORTED_CODE:
        		return VERSION_NOT_SUPPORTED_COMMENT;
        	case MESSAGE_TOO_LARGE_CODE:
        		return MESSAGE_TOO_LARGE_COMMENT;
        	case PRECONDITION_FAILURE_CODE:
        		return PRECONDITION_FAILURE_COMMENT;
        	case BUSY_EVERYWHERE_CODE:
        		return BUSY_EVERYWHERE_COMMENT;
        	case DECLINE_CODE:
        		return DECLINE_COMMENT;
        	case NOT_EXIST_ANYWHERE_CODE:
        		return NOT_EXIST_ANYWHERE_COMMENT;
        	case NOT_ACCPTED_CODE:
        		return NOT_ACCPTED_COMMENT;
        	case NO_RESPONSE_FROM_SERVER_CODE:
        		return NO_RESPONSE_FROM_SERVER_COMMENT;
        	case UNABLE_RESOLVE_SERVER_CODE:
        		return UNABLE_RESOLVE_SERVER_COMMENT;
        	case ERROR_SENDING_MSG_SERVER_CODE:
        		return ERROR_SENDING_MSG_SERVER_COMMENT;        	
        	
        	/** Begin:add by xjq 增加自定义SIP状态 20140819 **/
        	case NETWORK_DISCONNECT:
        		return NETWORK_DISCONNECT_COMMENT;
        	case START_RECONNECT:
        		return START_RECONNECT_COMMENT;
        	case RECONNECT_SUCCESS:
        		return RECONNECT_SUCCESS_COMMENT;
        	case UAC_REMOTE_BUSY:
        		return UAC_REMOTE_BUSY_COMMENT;

            // 被叫方密信号不存在，提示用户对方未开通安通账号。Add by xjq, 20150807
            case ACCOUNT_NOT_FOUND_CODE:
                return ACCOUNT_NOT_FOUND_COMMENT;

            // TCP发送错误。Add by xjq, 20150807
            case SEND_FAILED_CODE:
                return SEND_FAILED_COMMENT;

            // 正在挂断提示语。Add by gbc, 20150728
            case CUST_HANGUP_DOING:
                return HANGUP_DOING;

            // 通话结束提示语。Add by gbc, 20150728
            case CUST_HANGUP_END:
                return HANGUP_END;

        	}

        	return "";
        }
         
    }

    /**
     * The call signaling is not secure
     */
    public static int TRANSPORT_SECURE_NONE = 0;
    /**
     * The call signaling is secure until it arrives on server. After, nothing ensures how it goes.
     */
    public static int TRANSPORT_SECURE_TO_SERVER = 1;
    /**
     * The call signaling is supposed to be secured end to end.
     */
    public static int TRANSPORT_SECURE_FULL = 2;

    
    /**
     * Id of an invalid or not existant call
     */
    public static final int INVALID_CALL_ID = -1;

    /**
     * Primary key for the parcelable object
     */
    public int primaryKey = -1;
    /**
     * The starting time of the call
     */
    protected long callStart = 0;
    
    // Add role virable. Add by xjq, 2015/3/4
    /**
     * Role UAC.
     * 主叫
     */
    public static final int PJSIP_ROLE_UAC = 0;
    /**
     * Role UAS.
     * 被叫
     */
    public static final int PJSIP_ROLE_UAS = 1;
    
    protected int callId = INVALID_CALL_ID;
    protected int callState = InvState.INVALID;
    protected String remoteContact;
    protected boolean isIncoming;
    protected int confPort = -1;
    protected long accId = SipProfile.INVALID_ID;
    protected int mediaStatus = MediaState.NONE;
    protected boolean mediaSecure = false;
    protected int transportSecure = 0;
    protected boolean mediaHasVideoStream = false;
    protected long connectStart = 0;
    protected int lastStatusCode = 0;
    protected String lastStatusComment = "";
    protected int lastReasonCode = 0;
    protected String mediaSecureInfo = "";
    protected boolean canRecord = false;
    protected boolean isRecording = false;
    protected boolean zrtpSASVerified = false;
    protected boolean hasZrtp = false;
    
    /** Begin:If this call session is hanging. add by xjq,2015/1/23 */
    protected boolean isHanging = false;
    /** End:If this call session is hanging. add by xjq,2015/1/23 */
    
    // 呼叫角色：主叫还是被叫？ add by xjq, 2015/3/4
    protected int role = PJSIP_ROLE_UAC;
    
    // If this call is over. Add by xjq, 2015/2/26
    protected boolean isOver = true;

    // If this call is a missed call when busy. Add by xjq, 2015/4/15
    protected boolean isMissed = false;

    // If this call need reconnect when network recovers. Add by xjq, 2015/4/15
    protected boolean needReconnect = false;

    // 该呼叫的call-id字符串  add by xjq 2016-01-14
    protected String callIdString = null;


    // 推送session对应的callid add by xjq
    protected String extraCallId = null;

    // 是否提醒录音设备被占用 xjq
    protected boolean notifyRecDevice = true;

    // 是否开始了状态统计
    protected boolean statisticsStarted = false;

    // 未收到数据包数目
    protected int noPktRxCnt = 0;

    // 记录上一次call state
    protected int lastCallState = InvState.INVALID;

    // 记录接收数据的统计信息
    protected RxStatistics rxStatistics = new RxStatistics();

    // 记录发送数据的统计信息
    protected TxStatistics txStatistics = new TxStatistics();


    public boolean isLowBand() {
        return lowBand;
    }

    public void setLowBand(boolean lowBand) {
        this.lowBand = lowBand;
    }

    // 是否窄带
    protected boolean lowBand = false;




    public enum TrafficCompany {
        Byte,
        KiloByte,
        MByte,
    }

    public class TxStatistics {
        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
        public void setCompany(TrafficCompany company) {
            this.company = company;
        }
        public TrafficCompany getCompany() {
            return company;
        }
        float value;
        TrafficCompany company;
    }


    public class RxStatistics {
        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
        public TrafficCompany getCompany() {
            return company;
        }
        public void setCompany(TrafficCompany company) {
            this.company = company;
        }
        float value;
        TrafficCompany company;
    }

    /**
     * Construct from parcelable <br/>
     * Only used by {@link #CREATOR}
     * 
     * @param in parcelable to build from
     */
    private SipCallSession(Parcel in) {
        initFromParcel(in);
    }

    /**
     * Constructor for a sip call session state object <br/>
     * It will contains default values for all flags This class as no
     * setter/getter for members flags <br/>
     * It's aim is to allow to serialize/deserialize easily the state of a sip
     * call, <n>not to modify it</b>
     */
    public SipCallSession() {
        // Nothing to do in default constructor
    }

    /**
     * Constructor by copy
     * @param callInfo
     */
    public SipCallSession(SipCallSession callInfo) {
         Parcel p = Parcel.obtain();
         callInfo.writeToParcel(p, 0);
         p.setDataPosition(0);
         initFromParcel(p);
         p.recycle();
    }
    
    protected void initFromParcel(Parcel in) {
        primaryKey = in.readInt();
        callId = in.readInt();
        callState = in.readInt();
        mediaStatus = in.readInt();
        remoteContact = in.readString();
        isIncoming = (in.readInt() == 1);
        confPort = in.readInt();
        accId = in.readInt();
        lastStatusCode = in.readInt();
        mediaSecureInfo = in.readString();
        connectStart = in.readLong();
        callStart = in.readLong();
        mediaSecure = (in.readInt() == 1);
        lastStatusComment = in.readString();
        mediaHasVideoStream = (in.readInt() == 1);
        canRecord = (in.readInt() == 1);
        isRecording = (in.readInt() == 1);
        hasZrtp = (in.readInt() == 1);
        zrtpSASVerified = (in.readInt() == 1);
        transportSecure = (in.readInt());
        lastReasonCode = in.readInt();
        
        //客户端显示中文提示-Mod-Lixin-0414
        //400-800的错误码
        if(lastStatusCode >= 400 && lastStatusCode < 800)
        	lastStatusComment=StatusCommentReplace.ReplaceStatusComment(lastStatusCode);
        
        /** Begin:If this call session is hanging. add by xjq,2015/1/23 */
        isHanging = (in.readInt() == 1);
        /** End:If this call session is hanging. add by xjq,2015/1/23 */
        
        // If this call is over. Add by xjq, 2015/2/26
        isOver = (in.readInt() == 1);

        // If this call is missed. Add by xjq, 2015/4/15
        isMissed = (in.readInt() == 1);

        // If this call need reconnect. Add by xjq, 2015/4/15
        needReconnect = (in.readInt() == 1);

        // Call role:UAC or UAS? Add by xjq, 2015/4/15
        role = in.readInt();

        // 会话call-id字符串 xjq 2016-01-14
        callIdString = in.readString();

        // 推送会话的callid xjq 2016-01-25
        extraCallId = in.readString();

        // notify rec device xjq
        notifyRecDevice = (in.readInt() == 1);

        // no pkt rx count
        noPktRxCnt = in.readInt();

        // low band xjq
        lowBand = (in.readInt() == 1);

    }

    /**
     * @see Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }


    // Getter and setter for extra call id string xjq 2016-01-25
    public String getExtraCallId() {
        return extraCallId;
    }

    public void setExtraCallId(String extraCallId) {
        this.extraCallId = extraCallId;
    }


    //need show record device inuse notify xjq

    public boolean isNotifyRecDevice() {
        return notifyRecDevice;
    }

    public void setNotifyRecDevice(boolean notifyRecDevice) {
        this.notifyRecDevice = notifyRecDevice;
    }
    /**
     * @see Parcelable#writeToParcel(Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(primaryKey);
        dest.writeInt(callId);
        dest.writeInt(callState);
        dest.writeInt(mediaStatus);
        dest.writeString(remoteContact);
        dest.writeInt(isIncoming() ? 1 : 0);
        dest.writeInt(confPort);
        dest.writeInt((int) accId);
        dest.writeInt(lastStatusCode);
        dest.writeString(mediaSecureInfo);
        dest.writeLong(connectStart);
        dest.writeLong(callStart);
        dest.writeInt(mediaSecure ? 1 : 0);
        dest.writeString(getLastStatusComment());
        dest.writeInt(mediaHasVideo() ? 1 : 0);
        dest.writeInt(canRecord ? 1 : 0);
        dest.writeInt(isRecording ? 1 : 0);
        dest.writeInt(hasZrtp ? 1 : 0);
        dest.writeInt(zrtpSASVerified ? 1 : 0);
        dest.writeInt(transportSecure);
        dest.writeInt(lastReasonCode);
        
        // is haning?
        dest.writeInt(isHanging ? 1 : 0);

        // is over?
        dest.writeInt(isOver ? 1 : 0);

        // is missed?
        dest.writeInt(isMissed? 1 : 0);

        // need reconnect?
        dest.writeInt(needReconnect? 1 : 0);

        // role?
        dest.writeInt(role);

        // call id string
        dest.writeString(callIdString);

        // extra call id string xjq 2016-01-25
        dest.writeString(extraCallId);

        // notify rec device
        dest.writeInt(notifyRecDevice?1:0);

        // no pkt rx cnt
        dest.writeInt(noPktRxCnt);

        // low band
        dest.writeInt(lowBand? 1:0);
    }

    /**
     * Parcelable creator. So that it can be passed as an argument of the aidl
     * interface
     */
    public static final Parcelable.Creator<SipCallSession> CREATOR = new Parcelable.Creator<SipCallSession>() {
        public SipCallSession createFromParcel(Parcel in) {
            return new SipCallSession(in);
        }

        public SipCallSession[] newArray(int size) {
            return new SipCallSession[size];
        }
    };

    /**
     * A sip call session is equal to another if both means the same callId
     */
    @Override
    public boolean equals(Object o) {
        if (o!=null && o == this) {
            return true;
        }
        if (!(o instanceof SipCallSession)) {
            return false;
        }
        SipCallSession ci = (SipCallSession) o;
        if (ci.getCallId() == callId) {
            return true;
        }
        return false;
    }

    // Getters / Setters
    /**
     * Get the call id of this call info
     * 
     * @return id of this call
     */
    public int getCallId() {
        return callId;
    }

    /**
     * Get the call state of this call info
     * 
     * @return the invitation state
     * @see InvState
     */
    public int getCallState() {
        return callState;
    }

    /** 20160908-mengbo-start: force to set callstate disconnected **/
    /**
     * force to set callstate disconnected
     */
    public void setCallStateDisconnected() {
        callState = InvState.DISCONNECTED;
    }
    /** 20160908-mengbo-end **/

    public int getMediaStatus() {
        return mediaStatus;
    }

    /**
     * Get the remote Contact for this call info
     * 
     * @return string representing the remote contact
     */
    public String getRemoteContact() {
        return remoteContact;
    }

    /**
     * Get the call way
     * 
     * @return true if the remote party was the caller
     */
    public boolean isIncoming() {
        return isIncoming;
    }

    /**
     * Get the start time of the connection of the call
     * 
     * @return duration in milliseconds
     * @see SystemClock#elapsedRealtime()
     */
    public long getConnectStart() {
        return connectStart;
    }

    /**
     * Check if the call state indicates that it is an active call in
     * progress. 
     * This is equivalent to state incoming or early or calling or confirmed or connecting
     * 
     * @return true if the call can be considered as in progress/active
     */
    public boolean isActive() {
        return ((callState == InvState.INCOMING || callState == InvState.EARLY ||
                callState == InvState.CALLING || callState == InvState.CONFIRMED || callState == InvState.CONNECTING) && !this.isHanging); // Hanging call is not active
    }
    
    /**
     * Chef if the call state indicates that it's an ongoing call.
     * This is equivalent to state confirmed.
     * @return true if the call can be considered as ongoing.
     */
    public boolean isOngoing() {
        return callState == InvState.CONFIRMED;
    }

    /**
     * Get the sounds conference board port <br/>
     * <a target="_blank" href=
     * "http://www.pjsip.org/pjsip/docs/html/group__PJSUA__LIB__BASE.htm#gaf5d44947e4e62dc31dfde88884534385"
     * >Pjsip documentation</a>
     * 
     * @return the conf port of the audio media of this call
     */
    public int getConfPort() {
        return confPort;
    }

    /**
     * Get the identifier of the account corresponding to this call <br/>
     * This identifier is the one you have in {@link SipProfile#id} <br/>
     * It may return {@link SipProfile#INVALID_ID} if no account detected for
     * this call. <i>Example, case of peer to peer call</i>
     * 
     * @return The {@link SipProfile#id} of the account use for this call
     */
    public long getAccId() {
        return accId;
    }

    /**
     * Get the secure level of the signaling of the call.
     * 
     * @return one of {@link #TRANSPORT_SECURE_NONE}, {@link #TRANSPORT_SECURE_TO_SERVER}, {@link #TRANSPORT_SECURE_FULL}
     */
    public int getTransportSecureLevel() {
        return transportSecure;
    }
    
    /**
     * Get the secure level of the media of the call
     * 
     * @return true if the call has a <b>media</b> encrypted
     */
    public boolean isMediaSecure() {
        return mediaSecure;
    }

    /**
     * Get the information about the <b>media</b> security of this call
     * 
     * @return the information about the <b>media</b> security
     */
    public String getMediaSecureInfo() {
        return mediaSecureInfo;
    }

    /**
     * Get the information about local held state of this call
     * 是否本地保持，有普通电话打入时，通话会被保持
     * @return the information about local held state of media
     */
    public boolean isLocalHeld() {
        return mediaStatus == SipCallSession.MediaState.LOCAL_HOLD;
    }

    /**
     * Get the information about remote held state of this call
     * 
     * @return the information about remote held state of media
     */
    public boolean isRemoteHeld() {
        return (mediaStatus == SipCallSession.MediaState.NONE && isActive() && !isBeforeConfirmed());
    }

    /**
     * Check if the specific call info indicates that it is a call that has not yet been confirmed by both ends.<br/>
     * In other worlds if the call is in state, calling, incoming early or connecting.
     * 
     * @return true if the call can be considered not yet been confirmed
     */
    public boolean isBeforeConfirmed() {
        return (callState == InvState.CALLING || callState == InvState.INCOMING
                || callState == InvState.EARLY || callState == InvState.CONNECTING);
    }

    // 当前状态是否eraly。Add by xjq, 20150807
    public boolean isEarly(){
        return callState == InvState.EARLY;
    }


    // 当前状态是否connecting。Add by xjq, 20150807
    public boolean isConnecting() {
        return callState == InvState.CONNECTING;
    }


    /**
     * Check if the specific call info indicates that it is a call that has been ended<br/>
     * In other worlds if the call is in state, disconnected, invalid or null
     * 
     * @return true if the call can be considered as already ended
     */
    public boolean isAfterEnded() {
        return (callState == InvState.DISCONNECTED || callState == InvState.INVALID || callState == InvState.NULL);
    }

    /**
     * Get the latest status code of the sip dialog corresponding to this call
     * call
     * 
     * @return the status code
     * @see SipCallSession.StatusCode
     */
    public int getLastStatusCode() {
        return lastStatusCode;
    }

    /**
     * Get the last status comment of the sip dialog corresponding to this call
     * 
     * @return the last status comment string from server
     */
    public String getLastStatusComment() {
        return lastStatusComment;
    }

    /**
     * Get the latest SIP reason code if any. 
     * For now only supports 200 (if SIP reason is set to 200) or 0 in other cases (no SIP reason / sip reason set to something different).
     * 
     * @return the status code
     */
    public int getLastReasonCode() {
        return lastReasonCode;
    }
    
    /**
     * Get whether the call has a video media stream connected
     * 
     * @return true if the call has a video media stream
     */
    public boolean mediaHasVideo() {
        return mediaHasVideoStream;
    }

    /**
     * Get the current call recording status for this call.
     * 
     * @return true if we are currently recording this call to a file
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * Get the capability to record the call to a file.
     * 
     * @return true if it should be possible to record the call to a file
     */
    public boolean canRecord() {
        return canRecord;
    }

    /**
     * @return the zrtpSASVerified
     */
    public boolean isZrtpSASVerified() {
        return zrtpSASVerified;
    }

    /**
     * @return whether call has Zrtp encryption active
     */
    public boolean getHasZrtp() {
        return hasZrtp;
    }

    /**
     * Get the start time of the call.
     * @return the callStart start time of the call.
     */
    public long getCallStart() {
        return callStart;
    }

    public void setCallStart(long time) {
        callStart = time;
    }
    // Getter and setter for hanging state. Add by xjq, 2015/3/3
    public boolean isHanging() {
    	return isHanging;
    }
    
    public void setIsHanging(boolean isHanging) {
    	this.isHanging = isHanging;
    }

    // Getter and setter for over state. Add by xjq, 2015/2/26
    public boolean isOver() {
    	return isOver;
    }
    
    public void setOver(boolean isOver) {
    	this.isOver = isOver;
    }

    // Getter and setter for missed state. Add by xjq, 2015/2/26
    public boolean isMissed() {
        return isMissed;
    }

    public void setMissed(boolean isMissed) {
        this.isMissed = isMissed;
    }
    // Getter and setter for reconnect state. Add by xjq, 2015/2/26
    public boolean needReconnect() {
        return needReconnect;
    }

    public void setReconnect(boolean needReconnect) {
        this.needReconnect = needReconnect;
    }
    // Getter for incoming state. Add by xjq, 2015/3/3
    public void setIncoming(boolean isIncoming) {
    	this.isIncoming = isIncoming;
    }
    
    // Getter and setter for role. Add by xjq, 2015/3/4
    public int getRole() {
    	return this.role; 
    }
    
    public void setRole(int role) {
    	this.role = role;
    }

    // Getter and setter for call-id string. Add by xjq, 2016-01-14
    public String getCallIdString(){
        return this.callIdString;
    }

    public void setCallIdString(String callIdString) {
        this.callIdString = callIdString;
    }

    // xjq
    public boolean isStatisticsStarted() {
        return statisticsStarted;
    }

    public void setStatisticsStarted(boolean statisticsStarted) {
        this.statisticsStarted = statisticsStarted;
    }
    public int getNoPktRxCnt() {
        return noPktRxCnt;
    }

    public void setNoPktRxCnt(int noPktRxCnt) {
        this.noPktRxCnt = noPktRxCnt;
    }

    public void setRxStatistics(int value, TrafficCompany company) {
        this.rxStatistics.setValue(value);
        this.rxStatistics.setCompany(company);
    }

    public RxStatistics getRxStatistics() {
        return rxStatistics;
    }

    public void setTxStatistics(int value, TrafficCompany company) {
        this.txStatistics.setValue(value);
        this.txStatistics.setCompany(company);
    }

    public TxStatistics getTxStatistics() {
        return txStatistics;
    }

     @Override
     public String toString() {
          StringBuilder sb = new StringBuilder();
          sb.append("SipCallSession{");
          sb.append("lastStatusCode=");
          sb.append(lastStatusCode);
          sb.append(", lastStatusComment='");
          sb.append(lastStatusComment);
          sb.append("\n");
          sb.append(", callState=");
          sb.append(callState);
          sb.append(", callId=");
          sb.append(callId);
          sb.append("}'");
          return sb.toString();
     }
}
