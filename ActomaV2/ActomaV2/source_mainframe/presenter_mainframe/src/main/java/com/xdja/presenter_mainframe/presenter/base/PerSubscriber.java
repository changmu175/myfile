package com.xdja.presenter_mainframe.presenter.base;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm_mainframe.error.BusinessException;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.dependence.exeptions.ClientException;
import com.xdja.dependence.exeptions.NetworkException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.SafeCardException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.dependence.exeptions.matcher.OkMatcher;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.main.ExceptionHandler;
import com.xdja.frame.main.OkSubscriber;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.widget.XDialog;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.safeauth.cip.CipManager;
import com.xdja.safeauth.exception.CipException;
import com.xdja.safeauth.exception.NetException;
import com.xdja.safeauth.okhttp.SafeAuthInterceptor;
import com.xdja.unitepin.UnitePinManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.presenter.base</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:15:27</p>
 */
public class PerSubscriber<T> extends OkSubscriber<T> {

    static Map<String, String> checkExcMap = new HashMap<>();

    static Map<String, String> clientExcMap = new HashMap<>();

    static Map<String, String> networkExcMap = new HashMap<>();

    static Map<String, String> safeCardExcMap = new HashMap<>();

    static Map<String, String> serverExcMap = new HashMap<>();

    static Map<String, String> ckmsExcMap = new HashMap<>();

    static Map<String, String> businessExcMap = new HashMap<>();

    static {

        checkExcMap.put(CheckException.CODE_PARAMES_NOTVALID, ActomaController.getApp().getString(R.string.parameter_illegality));
        checkExcMap.put(CheckException.CODE_ACCOUNT_NONE, ActomaController.getApp().getString(R.string.account_cannot_be_empty));
        checkExcMap.put(CheckException.CODE_PASSWORD_FORMAT, ActomaController.getApp().getString(R.string.password_format));
        checkExcMap.put(CheckException.CODE_PASSWORD_NONE,  ActomaController.getApp().getString(R.string.pwd_cannot_be_empty));
        checkExcMap.put(CheckException.CODE_PASSWORD_DISCORD, ActomaController.getApp().getString(R.string.different_two_password));
        checkExcMap.put(CheckException.DEVICE_ID_NOT_PROVIDE, ActomaController.getApp().getString(R.string.no_detect_secure_chip_1));

        clientExcMap.put(ClientException.CODE_UNKOWN_EXCEPTION, ActomaController.getApp().getString(R.string.client_error));

        networkExcMap.put(NetworkException.CODE_NETWORK_CONN_FAILD, ActomaController.getApp().getString(R.string.network_connect_failed));
        networkExcMap.put(NetworkException.CODE_SSLHANDLE_FAILD, ActomaController.getApp().getString(R.string.server_busy));

      //  serverExcMap.put(ServerException.CODE_UNKNOW_SERVER_EXCEPTION, ActomaController.getApp().getString(R.string.server_abnormal));

        serverExcMap.put(ServerException.ACCOUNT_SEAL, ActomaController.getApp().getString(R.string.account_closure));
        serverExcMap.put(ServerException.ACCOUNT_FREEZE, ActomaController.getApp().getString(R.string.account_frozen));
        serverExcMap.put(ServerException.ACCOUNT_LOGOUT, ActomaController.getApp().getString(R.string.account_cancelled));

        serverExcMap.put(ServerException.MOBILE_NOT_REGISTER, ActomaController.getApp().getString(R.string.mobile_phone_number_is_not_registered));
        serverExcMap.put(ServerException.TRANSCEND_SEND_TIMES, ActomaController.getApp().getString(R.string.verify_code_max_count));
        serverExcMap.put(ServerException.FAIL_SEND_MESSAGE, ActomaController.getApp().getString(R.string.mms_send_failed));
        //modify by alh@xdja.com to fix bug: 711 2016-06-28 start (rummager : wangchao1)
        serverExcMap.put(ServerException.ACCOUNT_NOT_ACCORDANCE, ActomaController.getApp().getString(R.string.verification_is_not_through));
        //modify by alh@xdja.com to fix bug: 711 2016-06-28 end (rummager : wangchao1)
        serverExcMap.put(ServerException.DEVICE_NOT_ACCORDANCE, ActomaController.getApp().getString(R.string.device_not_credit));
        serverExcMap.put(ServerException.INNER_AUTH_CODE_INVALID, ActomaController.getApp().getString(R.string.more_than_operation_time_limit));
        serverExcMap.put(ServerException.OLD_ACCOUNT_DEVICE_NOT_RELATION, ActomaController.getApp().getString(R.string.unknow_reason_406));
        serverExcMap.put(ServerException.ACCOUNT_DEVICE_NOT_RELATION, ActomaController.getApp().getString(R.string.account_device_not_relation));
        serverExcMap.put(ServerException.ACCOUNT_ALREADY_BIND_MOBILE, ActomaController.getApp().getString(R.string.account_already_bind_mobile));
        serverExcMap.put(ServerException.ACCOUNT_ALREADY_SET_MOBILE, ActomaController.getApp().getString(R.string.unknow_reason_406));
        serverExcMap.put(ServerException.ACCOUNT_TRANSFER_FAIL, ActomaController.getApp().getString(R.string.account_transfer_fail));
        serverExcMap.put(ServerException.AUTH_CODE_ERROR, ActomaController.getApp().getString(R.string.auth_code_error));
        serverExcMap.put(ServerException.ALREADY_SET_CUSTOMIZE_ACCOUNT, ActomaController.getApp().getString(R.string.set_custom_account_error));
        serverExcMap.put(ServerException.CUSTOMIZE_ACCOUNT_EXISTS, ActomaController.getApp().getString(R.string.account_exist));
        serverExcMap.put(ServerException.DEVICE_IS_UN_AUTHORIZE, ActomaController.getApp().getString(R.string.device_not_credit));
        serverExcMap.put(ServerException.E40001, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40002, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40003, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40004, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40005, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40006, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40007, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40008, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40009, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40010, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40011, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40012, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40013, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40014, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40015, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40016, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40017, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40018, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40019, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40020, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40021, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40022, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40023, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40024, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40025, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40026, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40027, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40028, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40029, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40030, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40031, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40032, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40033, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40034, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40035, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40036, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E41001, ActomaController.getApp().getString(R.string.account_format_illegal));
        serverExcMap.put(ServerException.E41002, ActomaController.getApp().getString(R.string.mobile_format_illegal));
        serverExcMap.put(ServerException.E41003, ActomaController.getApp().getString(R.string.customize_account_format_illegal));
        serverExcMap.put(ServerException.E41004, ActomaController.getApp().getString(R.string.old_account_format_illegal));
        serverExcMap.put(ServerException.E41005, ActomaController.getApp().getString(R.string.new_account_format_illegal));
        serverExcMap.put(ServerException.E41006, ActomaController.getApp().getString(R.string.friends_mobile_number_illegal));
        serverExcMap.put(ServerException.E41007, ActomaController.getApp().getString(R.string.friends_mobile_format_illegal));
        serverExcMap.put(ServerException.E41008, ActomaController.getApp().getString(R.string.friends_mobile_repeat));
        serverExcMap.put(ServerException.E41009, ActomaController.getApp().getString(R.string.client_type_illegal));
        serverExcMap.put(ServerException.E41010, ActomaController.getApp().getString(R.string.login_type_illegal));
        serverExcMap.put(ServerException.E41011, ActomaController.getApp().getString(R.string.os_name_illegal));
        serverExcMap.put(ServerException.E41012, ActomaController.getApp().getString(R.string.dst_account_format_illegal));
        serverExcMap.put(ServerException.E41013, ActomaController.getApp().getString(R.string.identify_format_illegal));
        serverExcMap.put(ServerException.E41014, ActomaController.getApp().getString(R.string.ticket_invalid));
        serverExcMap.put(ServerException.E41015, ActomaController.getApp().getString(R.string.request_method_not_match));
        serverExcMap.put(ServerException.E50000, ActomaController.getApp().getString(R.string.get_account_failed));
        serverExcMap.put(ServerException.E40037, ActomaController.getApp().getString(R.string.device_id_no_exist));
        serverExcMap.put(ServerException.E40301, ActomaController.getApp().getString(R.string.device_id_no_corresponding));
        serverExcMap.put(ServerException.E40302, ActomaController.getApp().getString(R.string.service_not_open));

        ckmsExcMap.put(CkmsException.CODE_CKMS_INIT_NET_ERROR , ActomaController.getApp().getString(R.string.initialize_net_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_INIT_TIME_ERROR , ActomaController.getApp().getString(R.string.initialize_time_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_SERVER_ERROR, ActomaController.getApp().getString(R.string.encryption_initialize_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_AUTH_DEVICE_ERROR, ActomaController.getApp().getString(R.string.authorization_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_VERSION_ERROR , ActomaController.getApp().getString(R.string.version_not_matched));

        safeCardExcMap.put(SafeCardException.ERROR_UNKNOWN , ActomaApplication.getInstance().getString(R.string.get_chip_id_failed));

        businessExcMap.put(BusinessException.ERROR_OBTAIN_SAVE_CONFIG_FAILD,ActomaController.getApp().getResources().getString(R.string.get_save_configure_info_failed));
        businessExcMap.put(BusinessException.ERROR_DRIVER_NOT_EXIST, ActomaController.getApp().getResources().getString(R.string.no_detect_chip_drive));
        businessExcMap.put(BusinessException.ERROR_CHIP_NOT_EXIST, ActomaController.getApp().getResources().getString(R.string.no_detect_secure_chip));
        businessExcMap.put(BusinessException.ERROR_CHIP_ACTIVIE_FAILD, ActomaController.getApp().getResources().getString(R.string.secure_chip_active_failed));
        businessExcMap.put(BusinessException.CODE_EXEC_FAILD, ActomaController.getApp().getString(R.string.back_inner_verify_is_null));
        businessExcMap.put(BusinessException.CODE_EXEC_EMPTY_FAILD, ActomaController.getApp().getString(R.string.no_search_info_about_inner_verify));

    }

    private OkMatcher okMatcher;

    public PerSubscriber(ExceptionHandler handler) {
        super();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-12-20 add. fix bug 6971 . review by wangchao1. Start
        initMap();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-12-20 add. fix bug 6971 . review by wangchao1. End
        this.okMatcher = new OkMatcher();
        this.okMatcher.registUserMessageMapper(CheckException.class, new HashMap<>(checkExcMap));
        this.okMatcher.registUserMessageMapper(ClientException.class, new HashMap<>(clientExcMap));
        this.okMatcher.registUserMessageMapper(NetworkException.class, new HashMap<>(networkExcMap));
        this.okMatcher.registUserMessageMapper(SafeCardException.class, new HashMap<>(safeCardExcMap));
        this.okMatcher.registUserMessageMapper(ServerException.class, new HashMap<>(serverExcMap));
        this.okMatcher.registUserMessageMapper(CkmsException.class, new HashMap<>(ckmsExcMap));
        this.okMatcher.registUserMessageMapper(BusinessException.class, new HashMap<>(businessExcMap));
        setMatcher(this.okMatcher);
        setHandler(handler);
    }

    private void initMap(){
        checkExcMap.put(CheckException.CODE_PARAMES_NOTVALID, ActomaController.getApp().getString(R.string.parameter_illegality));
        checkExcMap.put(CheckException.CODE_ACCOUNT_NONE, ActomaController.getApp().getString(R.string.account_cannot_be_empty));
        checkExcMap.put(CheckException.CODE_PASSWORD_FORMAT, ActomaController.getApp().getString(R.string.password_format));
        checkExcMap.put(CheckException.CODE_PASSWORD_NONE,  ActomaController.getApp().getString(R.string.pwd_cannot_be_empty));
        checkExcMap.put(CheckException.CODE_PASSWORD_DISCORD, ActomaController.getApp().getString(R.string.different_two_password));
        checkExcMap.put(CheckException.DEVICE_ID_NOT_PROVIDE, ActomaController.getApp().getString(R.string.no_detect_secure_chip_1));

        clientExcMap.put(ClientException.CODE_UNKOWN_EXCEPTION, ActomaController.getApp().getString(R.string.client_error));

        networkExcMap.put(NetworkException.CODE_NETWORK_CONN_FAILD, ActomaController.getApp().getString(R.string.network_connect_failed));
        networkExcMap.put(NetworkException.CODE_SSLHANDLE_FAILD, ActomaController.getApp().getString(R.string.server_busy));

        //serverExcMap.put(ServerException.CODE_UNKNOW_SERVER_EXCEPTION, ActomaController.getApp().getString(R.string.server_abnormal));

        serverExcMap.put(ServerException.ACCOUNT_SEAL, ActomaController.getApp().getString(R.string.account_closure));
        serverExcMap.put(ServerException.ACCOUNT_FREEZE, ActomaController.getApp().getString(R.string.account_frozen));
        serverExcMap.put(ServerException.ACCOUNT_LOGOUT, ActomaController.getApp().getString(R.string.account_cancelled));

        serverExcMap.put(ServerException.MOBILE_NOT_REGISTER, ActomaController.getApp().getString(R.string.mobile_phone_number_is_not_registered));
        serverExcMap.put(ServerException.TRANSCEND_SEND_TIMES, ActomaController.getApp().getString(R.string.verify_code_max_count));
        serverExcMap.put(ServerException.FAIL_SEND_MESSAGE, ActomaController.getApp().getString(R.string.mms_send_failed));
        //modify by alh@xdja.com to fix bug: 711 2016-06-28 start (rummager : wangchao1)
        serverExcMap.put(ServerException.ACCOUNT_NOT_ACCORDANCE, ActomaController.getApp().getString(R.string.verification_is_not_through));
        //modify by alh@xdja.com to fix bug: 711 2016-06-28 end (rummager : wangchao1)
        serverExcMap.put(ServerException.DEVICE_NOT_ACCORDANCE, ActomaController.getApp().getString(R.string.device_not_credit));
        serverExcMap.put(ServerException.INNER_AUTH_CODE_INVALID, ActomaController.getApp().getString(R.string.more_than_operation_time_limit));
        serverExcMap.put(ServerException.OLD_ACCOUNT_DEVICE_NOT_RELATION, ActomaController.getApp().getString(R.string.unknow_reason_406));
        serverExcMap.put(ServerException.ACCOUNT_DEVICE_NOT_RELATION, ActomaController.getApp().getString(R.string.account_device_not_relation));
        serverExcMap.put(ServerException.ACCOUNT_ALREADY_BIND_MOBILE, ActomaController.getApp().getString(R.string.account_already_bind_mobile));
        serverExcMap.put(ServerException.ACCOUNT_ALREADY_SET_MOBILE, ActomaController.getApp().getString(R.string.unknow_reason_406));
        serverExcMap.put(ServerException.ACCOUNT_TRANSFER_FAIL, ActomaController.getApp().getString(R.string.account_transfer_fail));
        serverExcMap.put(ServerException.AUTH_CODE_ERROR, ActomaController.getApp().getString(R.string.auth_code_error));
        serverExcMap.put(ServerException.ALREADY_SET_CUSTOMIZE_ACCOUNT, ActomaController.getApp().getString(R.string.set_custom_account_error));
        serverExcMap.put(ServerException.CUSTOMIZE_ACCOUNT_EXISTS, ActomaController.getApp().getString(R.string.account_exist));
        serverExcMap.put(ServerException.DEVICE_IS_UN_AUTHORIZE, ActomaController.getApp().getString(R.string.device_not_credit));
        serverExcMap.put(ServerException.E40001, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40002, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40003, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40004, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40005, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40006, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40007, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40008, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40009, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40010, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40011, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40012, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40013, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40014, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40015, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40016, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40017, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40018, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40019, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40020, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40021, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40022, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40023, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40024, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40025, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40026, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40027, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40028, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40029, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40030, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40031, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40032, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40033, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40034, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40035, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E40036, ActomaController.getApp().getString(R.string.request_params_error));
        serverExcMap.put(ServerException.E41001, ActomaController.getApp().getString(R.string.account_format_illegal));
        serverExcMap.put(ServerException.E41002, ActomaController.getApp().getString(R.string.mobile_format_illegal));
        serverExcMap.put(ServerException.E41003, ActomaController.getApp().getString(R.string.customize_account_format_illegal));
        serverExcMap.put(ServerException.E41004, ActomaController.getApp().getString(R.string.old_account_format_illegal));
        serverExcMap.put(ServerException.E41005, ActomaController.getApp().getString(R.string.new_account_format_illegal));
        serverExcMap.put(ServerException.E41006, ActomaController.getApp().getString(R.string.friends_mobile_number_illegal));
        serverExcMap.put(ServerException.E41007, ActomaController.getApp().getString(R.string.friends_mobile_format_illegal));
        serverExcMap.put(ServerException.E41008, ActomaController.getApp().getString(R.string.friends_mobile_repeat));
        serverExcMap.put(ServerException.E41009, ActomaController.getApp().getString(R.string.client_type_illegal));
        serverExcMap.put(ServerException.E41010, ActomaController.getApp().getString(R.string.login_type_illegal));
        serverExcMap.put(ServerException.E41011, ActomaController.getApp().getString(R.string.os_name_illegal));
        serverExcMap.put(ServerException.E41012, ActomaController.getApp().getString(R.string.dst_account_format_illegal));
        serverExcMap.put(ServerException.E41013, ActomaController.getApp().getString(R.string.identify_format_illegal));
        serverExcMap.put(ServerException.E41014, ActomaController.getApp().getString(R.string.ticket_invalid));
        serverExcMap.put(ServerException.E41015, ActomaController.getApp().getString(R.string.request_method_not_match));
        serverExcMap.put(ServerException.E50000, ActomaController.getApp().getString(R.string.get_account_failed));
        serverExcMap.put(ServerException.E40037, ActomaController.getApp().getString(R.string.device_id_no_exist));
        serverExcMap.put(ServerException.E40301, ActomaController.getApp().getString(R.string.device_id_no_corresponding));
        serverExcMap.put(ServerException.E40302, ActomaController.getApp().getString(R.string.service_not_open));

        ckmsExcMap.put(CkmsException.CODE_CKMS_INIT_NET_ERROR , ActomaController.getApp().getString(R.string.initialize_net_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_INIT_TIME_ERROR , ActomaController.getApp().getString(R.string.initialize_time_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_SERVER_ERROR, ActomaController.getApp().getString(R.string.encryption_initialize_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_AUTH_DEVICE_ERROR, ActomaController.getApp().getString(R.string.authorization_failed));
        ckmsExcMap.put(CkmsException.CODE_CKMS_VERSION_ERROR , ActomaController.getApp().getString(R.string.version_not_matched));

        safeCardExcMap.put(SafeCardException.ERROR_UNKNOWN , ActomaApplication.getInstance().getString(R.string.get_chip_id_failed));

        businessExcMap.put(BusinessException.ERROR_OBTAIN_SAVE_CONFIG_FAILD,ActomaController.getApp().getResources().getString(R.string.get_save_configure_info_failed));
        businessExcMap.put(BusinessException.ERROR_DRIVER_NOT_EXIST, ActomaController.getApp().getResources().getString(R.string.no_detect_chip_drive));
        businessExcMap.put(BusinessException.ERROR_CHIP_NOT_EXIST, ActomaController.getApp().getResources().getString(R.string.no_detect_secure_chip));
        businessExcMap.put(BusinessException.ERROR_CHIP_ACTIVIE_FAILD, ActomaController.getApp().getResources().getString(R.string.secure_chip_active_failed));
        businessExcMap.put(BusinessException.CODE_EXEC_FAILD, ActomaController.getApp().getString(R.string.back_inner_verify_is_null));
        businessExcMap.put(BusinessException.CODE_EXEC_EMPTY_FAILD, ActomaController.getApp().getString(R.string.no_search_info_about_inner_verify));
    }

    public PerSubscriber(ExceptionHandler handler, String mark) {
        this(handler);
        setMark(mark);
    }


    /**
     * @param userMsg 如果为""则可只注册不显示
     */
    public <T extends OkException> PerSubscriber registUserMsg(@NonNull Class<T> cls,
                                                               @NonNull String errorCode,
                                                               @NonNull String userMsg) {
        this.okMatcher.registUserMessage(cls, errorCode, userMsg);
        return this;
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ServerException) {
            if (((ServerException) e).getOkCode().equals(ServerException.TICKET_IS_INVALID)) {
                LogoutHelper logoutHelper = new LogoutHelper();
                logoutHelper.diskLogout();
                logoutHelper.navigateToLoginWithExit();
                Activity topActivity = ActivityStack.getInstanse().getTopActivity();
                if (topActivity != null) {
                    //// TODO: 16/5/27 需要产品定义提示内容
                    XToast.show(topActivity, ActomaController.getApp().getString(R.string.login_past_due));
                }
                LogUtil.getUtils().e("OkException : " + e);
                return;
                //modify by alh@xdja.com to fix bug: 553 and 523 2016-07-08 start (rummager : anlihuang)
            }else if (((ServerException) e).getOkCode().equals(ServerException.INNER_AUTH_CODE_INVALID)){
                XToast.show(ActomaApplication.getInstance(), R.string.inner_authCode_invalid);
                LogUtil.getUtils().e("ServerException : " + e);
                return;
                //modify by alh@xdja.com to fix bug: 553 and 523 2016-07-08 end (rummager : anlihuang)
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-12 add. fix bug 2281 . review by wangchao1. Start
            }else if (((ServerException) e).getOkCode().equals(ServerException.AUTH_CODE_ERROR)){
                XToast.show(ActomaApplication.getInstance(), R.string.auth_code_error);
                LogUtil.getUtils().e("ServerException : " + e);
                return;
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-12 add. fix bug 2281 . review by wangchao1. End
            }
        }else if (e instanceof com.xdja.safeauth.exception.BusinessException){
            //modify by alh@xdja.com to fix bug: 1248 and 427 2016-07-07 start (rummager : fanjiandong)
            LogUtil.getUtils().e("BusinessException : " + e);
            //modify by alh@xdja.com to fix bug: 1816 2016-07-21 start (rummager : wangchao1)
            if ("3".equals(((com.xdja.safeauth.exception.BusinessException) e).getErrorCode())){
                Activity topActivity = ActivityStack.getInstanse().getTopActivity();
                if (topActivity != null) {
                    final XDialog xDialog = new XDialog(topActivity);
                    xDialog.setCanceledOnTouchOutside(false);
                    xDialog.setCancelable(false);
                    xDialog.setTitle(R.string.prompt).setMessage(ActomaApplication.getInstance().getString(R.string
                            .device_not_activate, ((com.xdja.safeauth.exception.BusinessException) e).getErrorCode(), CipManager.getInstance()
                            .getCardId())).setPositiveButton(ActomaApplication.getInstance().getString(com.xdja.comm
                            .R.string.security_password_layout_confim), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            xDialog.dismiss();
                        }
                    }).show();
                }
                return;
                //modify by alh@xdja.com to fix bug: 1816 2016-07-21 end (rummager : wangchao1)
            }
            XToast.show(ActomaApplication.getInstance(), R.string.safe_card_error);
            return;
        }else if (e instanceof CipException){
            Activity topActivity = ActivityStack.getInstanse().getTopActivity();
            String errorCode =  ((CipException) e).getErrorCode();
            //modify by alh@xdja.com to fix bug: 1411 2016-07-14 start (rummager:self)
			//todo alh  为什么使用魔术数字
            if ("-16".equals(errorCode)){
                if (topActivity != null) {
                    final XDialog xDialog = new XDialog(topActivity);
                    xDialog.setCanceledOnTouchOutside(false);
                    xDialog.setCancelable(false);
                    xDialog.setTitle(R.string.safe_card_lock_prompt).setMessage(ActomaApplication.getInstance()
                            .getString(R.string.safe_card_lock)).setPositiveButton(ActomaApplication.getInstance()
                            .getString(R.string.unlock), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            xDialog.dismiss();
                            UnitePinManager.getInstance().startUnlockActivity(TFCardManager.ROLE_R);
                        }
                    }).setNegativeButton(ActomaApplication.getInstance().getString(R.string.cancel), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            xDialog.dismiss();
                        }
                    }).show();
                }
                //modify by alh@xdja.com to fix bug: 1411 2016-07-14 end (rummager:self)
            }else {
                if (topActivity != null) {
                    final XDialog xDialog = new XDialog(topActivity);
                    xDialog.setCanceledOnTouchOutside(false);
                    xDialog.setCancelable(false);
                    xDialog.setTitle(R.string.prompt).setMessage(ActomaApplication.getInstance().getString(R.string.device_not_activate, ((CipException) e).getErrorCode(), CipManager.getInstance().getCardId())).setPositiveButton(ActomaApplication.getInstance().getString(com.xdja.comm.R.string.security_password_layout_confim), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            xDialog.dismiss();
                        }
                    }).show();
                }
            }
            LogUtil.getUtils().e("CipException : " + e);
            //modify by alh@xdja.com to fix bug: 1248 and 427 2016-07-07 start (rummager : fanjiandong)
            return;
        } else if (e instanceof NetException) {
            String errorCode =  ((NetException) e).getErrorCode();
            if ("502".equals(errorCode)) {
                XToast.show(ActomaApplication.getInstance(), R.string.gateway_error);
                LogUtil.getUtils().e("NetException : " + e);
                return;
            }else if (SafeAuthInterceptor.INT_500.equals(errorCode)){
                //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3966 . review by wangchao1. Start
                XToast.show(ActomaApplication.getInstance(), R.string.server_error);
                LogUtil.getUtils().e("NetException : " + e);
                //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3966 . review by wangchao1. End
                return;
            }
            LogUtil.getUtils().e("NetException : " + e);
        }
        super.onError(e);
    }


}
