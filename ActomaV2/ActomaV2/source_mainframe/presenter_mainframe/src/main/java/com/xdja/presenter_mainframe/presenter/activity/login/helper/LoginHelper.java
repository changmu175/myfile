package com.xdja.presenter_mainframe.presenter.activity.login.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.account.CkmsCreateUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.CkmsForceAddDeviceUseCase;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.widget.XDialog;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.WakeLockManager;
import com.xdja.presenter_mainframe.di.components.post.PostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.DataMigrationAccountPresenter;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.uiInterface.VuLoginResult;
import com.xdja.report.reportClientMessage;
import com.xdja.safekeyjar.util.StringResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import dagger.Lazy;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ldy on 16/4/26.
 * 登录相关的presenter需要使用的帮助类
 */
public class LoginHelper {
    /**
     * 成功
     */
    public final static int RESULT_STATUS_SUCCESS = 0;
    /**
     * 尝试登录次数达到上线
     */
    public final static int RESULT_STATUS_MAX_LOGIN = 1;
    /**
     * 非受信设备
     */
    public final static int RESULT_STATUS_NO_AUTH = 2;
    /**
     * 无任何受信设备
     */
    public final static int RESULT_STATUS_CAN_NOT_AUTH = 3;

    /**
     * 需要进行账号迁移
     */
    public final static int RESULT_STATUS_ACCOUNT_MIGRATION = 4;


    public final static String TICKET = "ticket";
    public static final String COUNT = "count";

    private static final String TAG = "anTongCkmsLoginHelper";

    /**
     * 服务器查询安全锁状态为空
     */
    public static final String SAFE_LOCK = "551.0";

    public static final String SAFE_LOCK_SINGLE = "201.0";


    /**
     * 登录用例执行成功(onNext)后的通用操作，
     *
     * @param objectMultiResult 需要往objectMultiResult.getInfo()中需要：
     *                          <p>如果是使用的账号/手机号，密码登录要放入{@link Navigator#ACCOUNT},{@link Navigator#PASSWORD}<p/>
     *                          <p>如果是使用的手机号，验证码登录要放入{@link Navigator#MOBILE},{@link Navigator#INNER_AUTH_CODE},{@link Navigator#VERIFY_CODE}<p/>
     */
    public static void loginResultHandler(Activity context,
                                          MultiResult<Object> objectMultiResult,
                                          MultiResult<Object> ckmsMultiResult,
                                          VuLoginResult vuLoginResult) {
        Map<String, Object> info = objectMultiResult.getInfo();
        switch (objectMultiResult.getResultStatus()) {
            case RESULT_STATUS_SUCCESS:
                //not exist login Ok but ckms need auth,so here success represent anTong+ and ckms both ok
                ((ActomaApplication) context.getApplicationContext())
                        .createUserComponent(new Account((Map<String, Object>) info.get(Account.USER_INFO)), (String) info.get(TICKET));
                Navigator.navigateToMainFrame();
                //上报登录到上报服务器
                //add by gbc. 2016-12-01. report login count. begin
                loginCountReport();
                //add by gbc. 2016-12-01. report login count. end
                break;
            case RESULT_STATUS_MAX_LOGIN:
                Double aDouble = (Double) objectMultiResult.getInfo().get(LoginHelper.COUNT);
                vuLoginResult.maxLoginCount(aDouble.intValue());
                break;
            case RESULT_STATUS_NO_AUTH:
                //check whether ckms need to auth
                String ckmsAddReq = "";
                if(ckmsMultiResult != null && ckmsMultiResult.getResultStatus() == CkmsCreateUseCase.CREATE_NEED_AUTH){
                    ckmsAddReq = (String)ckmsMultiResult.getInfo().get(CkmsCreateUseCase.ADD_REQID);
                    Log.d(TAG,"RESULT_STATUS_NO_AUTH ckmsAddReq "+ckmsAddReq);
                }
                Navigator.navigateToEmpowerDeviceLogin(
                        (String) info.get(Navigator.ACCOUNT),
                        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
                        (String) info.get("account"),
                        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
                        (String) info.get(Navigator.AUTHORIZE_ID),
                        (String) info.get(Navigator.INNER_AUTH_CODE),
                        TextUtils.isEmpty((String) info.get(Navigator
                                .MOBILE)) ? (String) info.get(Navigator.PHONE_NUMBER) : (String) info.get(Navigator
                                .MOBILE),
                        (String) info.get(Navigator.PASSWORD),
                        (String) info.get(Navigator.VERIFY_CODE),
                        ckmsAddReq);

                break;
            case RESULT_STATUS_CAN_NOT_AUTH:
                //ckms need force add
                Navigator.navigateToVerifyLogin(
                        (String) info.get(Navigator.ACCOUNT),
                        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
                        (String) info.get("account"),
                        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
                        (String) info.get(Navigator.VERIFY_CODE),
                        (String) info.get(Navigator.INNER_AUTH_CODE),
                        TextUtils.isEmpty((String) info.get(Navigator
                                .MOBILE)) ? (String) info.get(Navigator.PHONE_NUMBER) : (String) info.get(Navigator
                                .MOBILE),
                        (String) info.get(Navigator.PASSWORD));
                break;
            case RESULT_STATUS_ACCOUNT_MIGRATION:
                Intent intent = new Intent(context, DataMigrationAccountPresenter.class);
                intent.putExtra(Account.ACCOUNT, (String) info.get(Account.ACCOUNT));
                context.startActivity(intent);
                ActivityStack.getInstanse().pop2TopActivity(true);
                context.finish();
                break;
        }
    }

    public static void maxLoginDialog(Context context, int maxLoginCount) {
        final XDialog xDialog = new XDialog(context);
        xDialog.setTitle(ActomaController.getApp().getString(R.string.login_count_limit_title))
                .setMessage(ActomaController.getApp().getString(R.string.login_count_limit_content , maxLoginCount))
                .setPositiveButton(ActomaController.getApp().getString(R.string.roger), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 用于账号密码自动登录（即记住用户输入过的数据在别的界面登录），出现错误时自动返回登录界面
     *
     * @param activity               调用的activity
     * @param accountPwdLoginUseCase 账号密码登录用例
     * @param account                帐号
     * @param password               密码
     */
    public static void accountPwdAutoLogin(
            final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
            Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase,
            final String account, final String password) {
        activity.executeInteractorNoRepeat(accountPwdLoginUseCase.get().fill(account, password), new LoadingDialogSubscriber<MultiResult<Object>>(activity, activity) {
            @Override
            public void onNext(MultiResult<Object> objectMultiResult) {
                super.onNext(objectMultiResult);
                objectMultiResult.getInfo().put(Navigator.ACCOUNT, account);
                objectMultiResult.getInfo().put(Navigator.PASSWORD, password);
                LoginHelper.loginResultHandler(activity, objectMultiResult, null, activity.getVu());
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().e(e);
                //modify by alh@xdja.com to fix bug: 1822 2016-07-22 start (rummager : self)
                if (ActomaApplication.getInstance() != null && e != null && !TextUtils.isEmpty(e.getMessage())) {
                    XToast.show(ActomaApplication.getInstance(), e.getMessage());
                }
                //modify by alh@xdja.com to fix bug: 1822 2016-07-22 end (rummager : self)
                Navigator.navigateToLoginByFinish();
            }
        }.registerLoadingMsg(ActomaController.getApp().getString(R.string.login)));
    }

    /**
     * 用于手机号，验证码自动登录（即记住用户输入过的数据在别的界面登录），出现错误时自动返回短信验证登录界面
     *
     * @param activity           调用界面
     * @param mobileLoginUseCase 登录用例
     * @param phoneNumber        电话号码
     * @param verifyCode         验证码
     * @param innerAuthCode      内部验证码
     */
    public static void MobileVerifyAutoLogin(
            final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
            Lazy<Ext3Interactor<String, String, String, MultiResult<Object>>> mobileLoginUseCase,
            final String phoneNumber, final String verifyCode, final String innerAuthCode) {
        activity.executeInteractorNoRepeat(mobileLoginUseCase.get().fill(phoneNumber, verifyCode, innerAuthCode),
                new LoadingDialogSubscriber<MultiResult<Object>>(activity, activity) {
                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        objectMultiResult.getInfo().put(Navigator.PHONE_NUMBER, phoneNumber);
                        objectMultiResult.getInfo().put(Navigator.VERIFY_CODE, verifyCode);
                        objectMultiResult.getInfo().put(Navigator.INNER_AUTH_CODE, innerAuthCode);
                        loginResultHandler(activity, objectMultiResult, null, activity.getVu());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                        Navigator.navigateToMessageVerifyLoginByFinish();
                    }
                }.registerLoadingMsg(ActomaController.getApp().getString(R.string.login)));
    }
    public static void execCkmsCreateUseCase(
            final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
            Lazy<Ext1Interactor<String,MultiResult<Object>>> ckmsCreateUseCase,
            final Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase,
            final MultiResult<Object> logInMultiResult,
            final LogoutHelper logoutHelper, int progressMode){
         /*[S]add by xienana for bug 2339 @2016/08/11 [review by] tangsha*/
            if (logInMultiResult.getResultStatus() == LoginHelper.RESULT_STATUS_MAX_LOGIN) {
                LoginHelper.loginResultHandler(activity, logInMultiResult, null, activity.getVu());
                return;
            }
        /*[E]add by xienana for bug 2339 @2016/08/11 [review by] tangsha*/
            String digitalAccout ="";
            if(logInMultiResult.getResultStatus() == LoginHelper.RESULT_STATUS_SUCCESS) {
                digitalAccout = (String)((Map<String, Object>)logInMultiResult.getInfo().get(Account.USER_INFO)).get("account");
            } else {
                digitalAccout = (String)logInMultiResult.getInfo().get("account");
            }
            final String ckmsDigitalAccount = digitalAccout;
            LogUtil.getUtils().i(TAG+"execCkmsCreateUseCase digitalAccout "+digitalAccout);
            activity.executeInteractorNoRepeat(ckmsCreateUseCase.get().fill(digitalAccout)
                    ,new LoadingDialogSubscriber<MultiResult<Object>>(activity,activity , progressMode){
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            ckmsFailProcess(activity,logInMultiResult,logoutHelper);
                            LogUtil.getUtils().e(TAG+"exec createCkmsEntitySecUseCase onError "+e);
                        }

                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        int status = objectMultiResult.getResultStatus();
                        int loginStatus = logInMultiResult.getResultStatus();
                        LogUtil.getUtils().e(TAG+"createCkmsEntitySecUseCase onNext ckmsStatus "+status
                          +" loginStatus "+loginStatus);
                        //if ckms create fail, not to login
                        if(status == CkmsCreateUseCase.CREATE_FAIL) {
                            ckmsFailProcess(activity,logInMultiResult,logoutHelper);
                        }else if(status == CkmsCreateUseCase.CREATE_NEED_AUTH && loginStatus == LoginHelper.RESULT_STATUS_SUCCESS){
                            setDismissDialogWhenCompleleted(false);
                            execCkmsForceAddUseCase(activity,ckmsForceAddUseCase,logoutHelper,logInMultiResult,ckmsDigitalAccount);
                        } else{
                            //[S]add by licong for safeLock
                            /*if (logInMultiResult.getResultStatus() == LoginHelper.RESULT_STATUS_SUCCESS) {
                                exeGetSafelockSettingsUseCase(activity,getSafeLockSettings,ckmsDigitalAccount,logoutHelper,logInMultiResult);
                            }else{
                                LoginHelper.loginResultHandler(activity, logInMultiResult, objectMultiResult, activity.getVu());
                            }*/
                            //[E] add by licong for safeLock

                            LoginHelper.loginResultHandler(activity, logInMultiResult, objectMultiResult, activity.getVu());
                        }
                    }
                }.registerLoadingMsg(ActomaController.getApp().getString(R.string.load))
        );
    }
    /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
    public static void execCkmsCreateUseCase(
            final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
            Lazy<Ext1Interactor<String,MultiResult<Object>>> ckmsCreateUseCase,
            final Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase,
            final MultiResult<Object> logInMultiResult,
            final LogoutHelper logoutHelper){
        execCkmsCreateUseCase(activity , ckmsCreateUseCase , ckmsForceAddUseCase , logInMultiResult , logoutHelper , 0);
    }

    /*[S]tangsha@xdja.com @2016-08-12. if.*/
    private static void execCkmsForceAddUseCase(
            final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
            Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase,
            final LogoutHelper logoutHelper,
            final MultiResult<Object> logInMultiResult,
            final String digitalAccout){
        activity.executeInteractorNoRepeat(ckmsForceAddUseCase.get().fill(digitalAccout)
                ,new LoadingDialogSubscriber<MultiResult<Object>>(activity,activity){
                    @SuppressWarnings("EmptyMethod")
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ckmsFailProcess(activity,logInMultiResult,logoutHelper);
                    }

                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        if(result.getResultStatus() == CkmsForceAddDeviceUseCase.FORCE_ADD_DEV_OK){
                            //[S] add by licong for safeLock
                            //exeGetSafelockSettingsUseCase(activity,getSafeLockSettings,digitalAccout,logoutHelper,logInMultiResult);
                            //[E] add by licong for safeLock
                            loginResultHandler(activity, logInMultiResult, result, activity.getVu());
                        }else{
                            ckmsFailProcess(activity,logInMultiResult,logoutHelper);
                        }
                    }
                }.registerLoadingMsg(ActomaController.getApp().getString(R.string.load))
        );
    }
    /*[E]tangsha@xdja.com @2016-08-04. for account not unify.review by self.*/

    private static void ckmsFailProcess(final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
                                        final MultiResult<Object> logInMultiResult,
                                        LogoutHelper logoutHelper){
        if(logInMultiResult.getResultStatus() == RESULT_STATUS_SUCCESS) {
            execLogoutUseCase(activity,logInMultiResult,logoutHelper);
        }else{
            // getVu().showToast("");
        }
    }

   //[S]licong@xdja.com @2016-12-16. for safeLock.
   //网络保存数据，在登录的时候添加到服务器上
   /* private static void exeGetSafelockSettingsUseCase(final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
                                                      Lazy<Ext1Interactor<String,MultiResult<Object>>> getSafeLockSettings,final String account,
                                                      final LogoutHelper logoutHelper, final MultiResult<Object> logInMultiResult) {

        activity.executeInteractorNoRepeat(getSafeLockSettings.get().fill(account)
                ,new LoadingDialogSubscriber<MultiResult<Object>>(activity,activity){
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.getUtils().e("safelock     ----- ", e);
                        failProcess(activity,logInMultiResult,logoutHelper);
                    }

                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        if (objectMultiResult.getInfo() != null) {
                            Map<String,Object> map = objectMultiResult.getInfo();
                            if (TextUtils.isEmpty((String)map.get("pwd"))) {
                                if (!map.get("code").equals(SAFE_LOCK) && !map.get("code").equals(SAFE_LOCK_SINGLE)) {
                                    failProcess(activity,logInMultiResult,logoutHelper);
                                } else {
                                    LoginHelper.loginResultHandler(activity, logInMultiResult, objectMultiResult, activity.getVu());
                                }
                            } else {
                                String pwd = (String)map.get("pwd");
                                if (!TextUtils.isEmpty(pwd)) {
                                    LockPatternUtils lockPatternUtils = new LockPatternUtils(activity,account);
                                    lockPatternUtils.obtainPasswordToSaveLocal(pwd);
                                }
                                LoginHelper.loginResultHandler(activity, logInMultiResult, objectMultiResult, activity.getVu());
                            }
                        } else {
                            failProcess(activity,logInMultiResult,logoutHelper);
                        }
                    }
                }.registerLoadingMsg(ActomaController.getApp().getString(R.string.load))
        );
    }*/
   //[E]licong@xdja.com @2016-12-16. for safeLock.

    private static void execLogoutUseCase(final PresenterActivity<? extends Command, ? extends VuLoginResult<? extends Command>> activity,
                                   final MultiResult<Object> logInMultiResult,
                                   LogoutHelper logoutHelper){
        ((ActomaApplication) activity.getApplicationContext())
                .createUserComponent(new Account((Map<String, Object>) logInMultiResult.getInfo().get(Account.USER_INFO)),
                        (String) logInMultiResult.getInfo().get(TICKET), true);
        logoutHelper.logout(null);
    }
    /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. End*/

    //add by gbc. 2016-12-01. report login count. begin
    /**
     *上报登录动作到服务器，统计日登录次数
     */
    public static void loginCountReport() {
        String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("ticket");
        StringResult tfCardIdResult = TFCardManager.getCardId();
        String cardId = "";
        if (tfCardIdResult != null && tfCardIdResult.getErrorCode() == 0) {
            cardId = tfCardIdResult.getResult();//cardId
        }
        WakeLockManager.getInstance().acquire();
        reportClientMessage.reportClientMessage_loginCount(ticket,
                ContactUtils.getCurrentAccount(),
                cardId,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.getUtils().e(e);
                        WakeLockManager.getInstance().release();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        WakeLockManager.getInstance().release();
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body().string();
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(body);
                                if (null != jsonObject &&jsonObject.has("error")) {
                                    LogUtil.getUtils().e("report login count has error!");
                                    return;
                                }
                                LogUtil.getUtils().e("report login count success!");
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                LogUtil.getUtils().e("report login count JSON parse have error!", e);
                            }
                        }
                    }
                });
    }
    //add by gbc. 2016-12-01. report login count. begin

    private static final int ONE_DAY_TIME = 60 * 60 * 24;
    public synchronized static void startBrushTicket() {
        LogUtil.getUtils().e("startBrushTicket");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue(Account.TICKET);
                    LogUtil.getUtils().e("startBrushTicket ticket : " + (ticket != null ? ticket.substring(0 , 4) : "NULL"));
                    if (TextUtils.isEmpty(ticket)) {
                        return;
                    }
                    long start = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefLongValue(Account.TICKET_CREATE_TIME);
                    long end = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefLongValue(Account.TICKET_VAILD_EXPIRE_TIME);
                    long curr = System.currentTimeMillis();
                    LogUtil.getUtils().e("curr : " + curr + " , start : " + start + " , end :" + end);
                    if (curr < start || curr > end || start > end) {
                        return;
                    }
                    int intervalDay = (int) ((end - start) / (ONE_DAY_TIME * 1000));
                    int currentday = (int) ((curr - start) / (ONE_DAY_TIME * 1000));
                    int rate = currentday * 100 / intervalDay;
                    LogUtil.getUtils().e("currentday : " + currentday + " , rate : " + rate);
                    if (rate <= 50) {
                        return;
                    }

                    int odds = (int) (Math.random() * 100);
                    int seed = (rate - 50) / 10 * 25 + 25;
                    LogUtil.getUtils().e("odds : " + odds  + " , seed : " + seed);
                    if (odds < seed){
                        //开始刷新Ticket
                        onUpdateTicket();
                        return;
                    }
                } catch (Exception e) {
                    LogUtil.getUtils().e("startBrushTicket  Exception e : " + (e != null ? e.getMessage() : "NULL"));
                    return;
                }
            }
        }).start();
    }

    private static void onUpdateTicket() {
        UserComponent userComponent = ((ActomaApplication) ActomaApplication.getInstance()).getUserComponent();
        String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue(Account.TICKET);
        LogUtil.getUtils().e("onUpdateTicket userComponent : " + userComponent + " , ticket : " + (ticket != null ? ticket.substring(0 , 4) : "NULL"));
        if (userComponent == null || TextUtils.isEmpty(ticket)) {
            return;
        }
        WakeLockManager.getInstance().acquire();
        PostUseCaseComponent postUseCaseComponent = userComponent.plus(
                new PostUseCaseModule(), new PreUseCaseModule());
        postUseCaseComponent.refreshTicket()
                .fill(ticket)
                .execute(new PerSubscriber<Map<String,Object>>(null) {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e("refreshTicket error : " + e);
                        WakeLockManager.getInstance().release();
                    }

                    @Override
                    public void onNext(Map<String,Object> info) {
                        WakeLockManager.getInstance().release();
//                        if (info == null) return;
//                        LogUtil.getUtils().e("refreshTicket : " + info);
//                        if (info == null) return;
//                        PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceStringValue(UserInfoDiskStore.KEY_PRE_TICKET, (String) info.get("newTicket"));
//                        PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceLongValue(Account.TICKET_CREATE_TIME, (info.get("createTime") instanceof
//                                Double) ? ((Double) info.get("createTime")).longValue() : ((long) info.get
//                                ("createTime")));
//                        PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceLongValue(Account.TICKET_VAILD_EXPIRE_TIME, (info.get("vaildExpireTime")
//                                instanceof Double) ? ((Double) info.get("vaildExpireTime")).longValue() : ((long)
//                                info.get("vaildExpireTime")));
//                        PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceStringValue(Account.TICKET, (String) info.get("newTicket"));
                    }
                });
    }

}
