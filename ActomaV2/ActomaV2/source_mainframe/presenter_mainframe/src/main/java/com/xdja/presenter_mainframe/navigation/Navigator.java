package com.xdja.presenter_mainframe.navigation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.uitl.QRUtil;
import com.xdja.comm.zxing.scan.CaptureActivity;
import com.xdja.contact.presenter.activity.ChooseContactPresenter;
import com.xdja.contact.presenter.activity.FriendSearchPresenter;
import com.xdja.contact.presenter.activity.LocalSearchPresenter;
import com.xdja.contactcommon.SearchFriendControler;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.chooseImg.SetHeadPortraitPresenter;
import com.xdja.presenter_mainframe.presenter.activity.LauncherPresenter;
import com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter;
import com.xdja.presenter_mainframe.presenter.activity.NewEncryptPresenter;
import com.xdja.presenter_mainframe.presenter.activity.ProductIntroductionAnimPresenter;
import com.xdja.presenter_mainframe.presenter.activity.WebViewPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.EmpowerDeviceLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.LoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.MessageVerifyLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyFriendPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.ChooseAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.SetPreAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.WriteRegistrationInfoPresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordPresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordVerifyFriendPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordVerifyPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPwdInputNewPasswordPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AboutActomaPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AccountSafePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AuthAccountLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AuthDeviceLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.BindPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.ChoiceLanguagePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.DeviceManagerPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.ModifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenSafeLockPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SetAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SetNicknamePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingActivityPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingInputNewPasswordPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingSafeLockPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.UserDetailPresenter;
import com.xdja.presenter_mainframe.util.TextUtil;

/**
 * Created by ldy on 16/4/25.
 * 用于在导航app中的各个activity
 */
public class Navigator {

    /**
     * 账户
     */
    public final static String ACCOUNT = "accountOrAlia";
    //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    /**
     * 数字账户
     */
    public final static String DIGITAL_ACCOUNT = "digitalAccount";
    //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.

    /**
     * 内部验证码
     */
    public final static String INNER_AUTH_CODE = "innerAuthCode";

    /**
     * 昵称
     */
    public final static String NICK_NAME = "nickName";
    /**
     * 手机号
     */
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PASSWORD = "password";
    public static final String VERIFY_CODE = "verifyCode";
    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3949 . review by wangchao1. Start
    /*[S]add by tangsha@20160711 for ckms*/
    public static final String CKMS_VERIFY_CODE = "ckmsVerifyCode";
    /*[E]add by tangsha@20160711 for ckms*/
    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3949 . review by wangchao1. End
    /**
     * 返回键类型
     */
    public static final String TOOLBAR_TYPE = "toolbartype";

    /**
     * 是否是从退出界面进入
     */
    public static final String EXIT = "exit";
    public static final int REQUEST_INTRODUCTION = 11;
    public static final int SCAN_TAG = 100;

    public static void navigateToProductIntroductionAnim(boolean isSettingWelcome) {
        Intent intent = generateIntent(ProductIntroductionAnimPresenter.class);
        intent.putExtra(ProductIntroductionAnimPresenter.IS_SETTING_WELCOME, isSettingWelcome);
        startActivityForResult(intent, REQUEST_INTRODUCTION);
    }


    public static void navigateToMainFrame() {
        Intent intent = generateIntent(MainFramePresenter.class);
        Activity activity = provideTopActivity();
        if (activity != null) {
            activity.startActivity(intent);
        }
    }

    public static void navigateToLauncher() {
        startActivity(generateIntent(LauncherPresenter.class));
    }

    /**
     * 应用异常，退出登录并跳转到启动界面，需要pop之前所有的界面
     */
    public static void navigateToLauncherWithExit() {
        Intent intent = generateIntent(LauncherPresenter.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXIT, true);
        startActivity(intent);
    }

    /************************************
     * start***登录流程
     ************************************/
    /**
     * 跳转到登录界面,如果android版本大于或等于5.0,则执行动画后跳转,否则直接跳转
     */
    public static void navigateToLogin() {
        Intent intent = generateIntent(LoginPresenter.class);
        Activity activity = provideTopActivity();
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * 通过结束登录界面上的界面来回到登录界面
     */
    public static void navigateToLoginByFinish() {
        ActivityStack activityStack = ActivityStack.getInstanse();
        if (activityStack.getAllActivities().contains(activityStack.getActivityByClass(LoginPresenter.class))){
            activityStack.popActivitiesUntil(LoginPresenter.class, true);
        }else {
            navigateToLoginWithExit();
        }
    }

    /**
     * 通过结束登录界面上的界面来回到登录界面
     */
    public static void navigateToMessageVerifyLoginByFinish() {
        ActivityStack.getInstanse().popActivitiesUntil(MessageVerifyLoginPresenter.class, true);
    }

    //[s]modify by xnn for bug 9664 review by tangsha
    public static void navigateToLoginWithExit() {
        navigateToLoginWithExit(null);
    }

    public static void navigateToLoginWithExit(Context context) {
        Intent intent = generateIntent(LoginPresenter.class);
        intent.putExtra(EXIT, true);
        startActivity(context,intent);
    }
    //[e]modify by xnn for bug 9664 review by tangsha
    /**
     * 授权id
     */
    public final static String AUTHORIZE_ID = "authorizeId";

    /**
     * CKMS 使用
     * @param account
     * @param authorizeId
     * @param innerAuthCode
     * @param mobile
     * @param password
     * @param verifyCode
     * @param ckmsAddReq
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    public static void navigateToEmpowerDeviceLogin(String account, String digitalAccount, String authorizeId, String innerAuthCode,
                                                    String mobile, String password, String verifyCode, String ckmsAddReq) {
        Intent intent = generateIntent(EmpowerDeviceLoginPresenter.class);
        intent.putExtra(ACCOUNT, account);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(DIGITAL_ACCOUNT, digitalAccount);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(PASSWORD, password);
        intent.putExtra(AUTHORIZE_ID, authorizeId);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(PHONE_NUMBER, mobile);
        intent.putExtra(VERIFY_CODE, verifyCode);
        /*[S]add by tangsha for ckms authorize*/
        intent.putExtra(CKMS_VERIFY_CODE, ckmsAddReq);
        /*[E]add by tangsha for ckms authorize*/
        startActivity(intent);
    }


    /**
     * 跳转到验证登录activity
     *
     * @param account       账户
     * @param innerAuthCode 内部验证码
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    public static void navigateToVerifyLogin(String account, String digitalAccount, String verifyCode, String innerAuthCode, String mobile, String password) {
        Intent intent = generateIntent(VerifyLoginPresenter.class);
        intent.putExtra(ACCOUNT, account);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(DIGITAL_ACCOUNT, digitalAccount);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(VERIFY_CODE, verifyCode);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(PHONE_NUMBER, mobile);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    }


    /**
     * 跳转到验证手机号activity
     *
     * @param account       账户
     * @param innerAuthCode 内部验证码
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    public static void navigateToVerifyPhoneNumber(String account, String digitalAccount, String verifyCode, String innerAuthCode, String mobile, String password) {
        Intent intent = generateIntent(VerifyPhoneNumberPresenter.class);
        intent.putExtra(ACCOUNT, account);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(DIGITAL_ACCOUNT, digitalAccount);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(VERIFY_CODE, verifyCode);
        intent.putExtra(PHONE_NUMBER, mobile);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    }


    /**
     * 跳转到验证好友手机号activity
     *
     * @param account       账户
     * @param innerAuthCode 内部验证码
     */
    //[S]modify by xienana for bug 3673 @20160906 [reviewed by tangasha]
    @SuppressWarnings("MethodWithTooManyParameters")
    public static void navigateToVerifyFriendPhone(String account, String digitalAccount, String verifyCode, String innerAuthCode, String mobile, String password) {
        Intent intent = generateIntent(VerifyFriendPhonePresenter.class);
        intent.putExtra(ACCOUNT, account);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(DIGITAL_ACCOUNT, digitalAccount);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        intent.putExtra(VERIFY_CODE,verifyCode);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(PHONE_NUMBER, mobile);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    } //[E]modify by xienana for bug 3673 @20160906 [reviewed by tangasha]

    /**
     * 跳转到短信验证登录界面
     */
    public static void navigateToMessageVerifyLogin() {
        startActivity(generateIntent(MessageVerifyLoginPresenter.class));
    }

    /**********************
     * end***登录流程
     ***********************************/


    /**********************
     * start***注册流程
     ************************************/

    /**
     * 跳转到填写注册信息界面
     */
    public static void navigateToWriteRegistrationInfo() {
        startActivity(generateIntent(WriteRegistrationInfoPresenter.class));
    }

    public static void navigateToWebView(String url, String title){
        Intent intent = generateIntent(WebViewPresenter.class);

        if (!TextUtils.isEmpty(url)) {
            intent.putExtra(WebViewPresenter.WEBURL, url);
            if (!TextUtils.isEmpty(title)) {
                intent.putExtra(WebViewPresenter.TITLE, title);
            }
            startActivity(intent);
        }
    }

    /**
     * 跳转到选取账号界面
     *
     * @param account       账户
     * @param innerAuthCode 内部验证码
     * @param password
     */
    public static void navigateToChooseAccount(String account, String innerAuthCode, String password) {
        Intent intent = generateIntent(ChooseAccountPresenter.class);
        intent.putExtra(ACCOUNT, account);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    }

    /**
     * 跳转到注册时验证手机号界面
     *
     * @param account       账户
     * @param innerAuthCode 内部验证码
     * @param password
     */
    public static void navigateToRegisterVerifyPhoneNumber(String account, String innerAuthCode, String password ,boolean isNoBackType) {
        Intent intent = generateIntent(RegisterVerifyPhoneNumberPresenter.class);
        intent.putExtra(ACCOUNT, account);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        intent.putExtra(PASSWORD, password);
        intent.putExtra(TOOLBAR_TYPE, isNoBackType);
        startActivity(intent);
    }

    /**
     * 跳转到选取图片界面
     */
    public static void navigateToSetHeadPortrait() {
        startActivity(generateIntent(SetHeadPortraitPresenter.class));
    }
    /**********************
     * end***注册流程
     ***********************************/


    /**********************
     * start***忘记密码流程
     ************************************/
    /**
     * 跳转到重新设置密码界面
     */
    public static void navigateToResetPassword() {
        startActivity(generateIntent(ResetPasswordPresenter.class));
    }

    /**
     * 跳转到重新设置密码流程中的验证手机号界面
     */
    public static void navigateToResetPasswordVerifyPhone() {
        startActivity(generateIntent(ResetPasswordVerifyPhonePresenter.class));
    }

    /**
     * 跳转到重新设置密码流程中的验证好友手机号界面
     */
    public static void navigateToResetPasswordVerifyFriendPhone() {
        startActivity(generateIntent(ResetPasswordVerifyFriendPhonePresenter.class));
    }

    public static final int RESET_PASSWORD_TYPE_AUTH_CODE = 0;
    public static final int RESET_PASSWORD_TYPE_FRIEND_PHONE = 1;
    public static final int RESET_PASSWORD_TYPE_ERROR = -1;
    public static final String RESET_PASSWD_TYPE = "resetPasswdType";

    public static final String MOBILE = "mobile";
    public static final String MOBILES = "mobiles";

    /**
     * 跳转到重新设置密码流程中的重设密码界面(验证自己的手机号)
     *
     * @param mobile        自己的手机号
     * @param innerAuthCode 内部验证码
     */
    public static void navigateToResetPwdInputNewPasswordByMyPhone(String mobile, String innerAuthCode) {
        Intent intent = generateIntent(ResetPwdInputNewPasswordPresenter.class);
        intent.putExtra(RESET_PASSWD_TYPE, RESET_PASSWORD_TYPE_AUTH_CODE);
        intent.putExtra(MOBILE, mobile);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        startActivity(intent);
    }

    /**
     * 跳转到重新设置密码流程中的重设密码界面(验证好友手机号)
     *
     * @param account       安通账号
     * @param innerAuthCode 内部验证码
     */
    public static void navigateToResetPwdInputNewPasswordByFriendPhone(String account, String innerAuthCode) {
        Intent intent = generateIntent(ResetPwdInputNewPasswordPresenter.class);
        intent.putExtra(RESET_PASSWD_TYPE, RESET_PASSWORD_TYPE_FRIEND_PHONE);
        intent.putExtra(ACCOUNT, account);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        startActivity(intent);
    }


    /**********************
     * end***忘记密码流程
     ***********************************/


    /**********************
     * start***设置相关
     ***********************************/
    public static void navigateToSettingActivity() {
        startActivity(generateIntent(SettingActivityPresenter.class));
    }

    public static void navigateToAccountSafe() {
        startActivity(generateIntent(AccountSafePresenter.class));
    }

    public static void navigateToDeviceManager() {
        startActivity(generateIntent(DeviceManagerPresenter.class));
    }

    public static void navigateToUserDetail() {
        startActivity(generateIntent(UserDetailPresenter.class));
    }

    public static void navigateToBindPhoneNumber() {
        startActivity(generateIntent(BindPhoneNumberPresenter.class));
    }

    public static void navigateToBindPhoneNumber(String type, String typeValue) {
        Intent intent = generateIntent(BindPhoneNumberPresenter.class);
        intent.putExtra(type, typeValue);
        startActivity(intent);
    }

    public static void navigateToModifyPhoneNumber(String phoneNumber) {
        Intent intent = generateIntent(ModifyPhoneNumberPresenter.class);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }

    public static void navigateToAuthDeviceLogin() {
        startActivity(generateIntent(AuthDeviceLoginPresenter.class));
    }

    public static void navigateToSetAccount() {
        startActivity(generateIntent(SetAccountPresenter.class));
    }

    public static final int REQUEST_SET_ACCOUNT = 60;

    public static void navigateToSetAccount(int requestCode) {
        startActivityForResult(generateIntent(SetAccountPresenter.class), requestCode);
    }

    public static void navigateToSetPreAccount(String account, String innerAuthCode, int requestCode) {
        Intent intent = generateIntent(SetPreAccountPresenter.class);
        intent.putExtra(ACCOUNT, account);
        intent.putExtra(INNER_AUTH_CODE, innerAuthCode);
        startActivityForResult(intent, requestCode);
    }

    public static void navigateToSetNickNameWithName(String oldNickName){
        Intent intent = generateIntent(SetNicknamePresenter.class);
        intent.putExtra(NICK_NAME, oldNickName);
        startActivity(intent);
    }

    public static void navigateToSetNickname() {
        startActivity(generateIntent(SetNicknamePresenter.class));
    }

    public static void navigateToSettingInputNewPassword() {
        startActivity(generateIntent(SettingInputNewPasswordPresenter.class));
    }

    /**
     * 根据安全锁的状态  判断该跳转的界面
     * @param isFirstSafeLock
     */
    public static void navigateToSettingInputSafeLock(boolean isFirstSafeLock,boolean isForgetPwd) {
        if (isFirstSafeLock || isForgetPwd) {
            startActivity(generateIntent(SettingSafeLockPresenter.class));
        } else {
            startActivity(generateIntent(OpenSafeLockPresenter.class));
        }

    }


    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 1915 . review by wangchao1. Start
    public static void navigateToAuthAccountLogin(String authorizeId) {
        Intent intent = generateIntent(AuthAccountLoginPresenter.class);
        intent.putExtra(AUTHORIZE_ID, authorizeId);
        startActivity(intent);
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 1915 . review by wangchao1. End

    public static void navigateToEncryptServer() {
        startActivity(generateIntent(NewEncryptPresenter.class));
    }

    public static void navigateToAboutActoma() {
        startActivity(generateIntent(AboutActomaPresenter.class));
    }


    public static void navigateToChoiceLanguage(){
        startActivity(generateIntent(ChoiceLanguagePresenter.class));
    }

    /**********************
     * end***设置相关
     ***********************************/


    /************************
     * begin********联系人相关
    **************************************/
    public static void navigateToAddUser() {
        startActivity(generateIntent(FriendSearchPresenter.class));
    }
    public static void navigateToSearchUser() {
        startActivity(generateIntent(LocalSearchPresenter.class));
    }

    public static void navigateToPickPeople() {
        startActivity(generateIntent(ChooseContactPresenter.class));
    }


    /************************
     * end*****联系人相关
     **************************************/





    public static void navigateToCaptureActivity() {
        startActivityForResult(generateIntent(CaptureActivity.class),SCAN_TAG);
    }
    //modify by alh@xdja.com to fix bug: 2190 2016-07-28 start (rummager : anlihuang)
    public static final int SCAN_ERROR_CODE_NONE = -1;
    public static final int SCAN_ERROR_CODE_INVALID = -2;
    public static final int SCAN_ERROR_CODE_NORMAL = 0;
    //modify by alh@xdja.com to fix bug: 2190 2016-07-28 end (rummager : anlihuang)

    //modify by alh@xdja.com to fix bug: 810 2016-07-14 start (rummager : anlihuang)
    public static int handleScanResultEvent(Intent data) {
        if (data == null){
            return SCAN_ERROR_CODE_NONE;
        }
        if (!NetworkUtil.isNetworkConnect(ActomaApplication.getInstance())) {
            XToast.show(ActomaApplication.getInstance(), R.string.netNotWork);
            return SCAN_ERROR_CODE_NORMAL;
        }
        String qrCode = data.getStringExtra(CaptureActivity.QR_RESULT);
        String authId = QRUtil.qrString2AuthorizeId(qrCode);
        if (authId != null && TextUtil.isRuleAuthorizeId(authId)) {
            Navigator.navigateToAuthAccountLogin(authId);
            return SCAN_ERROR_CODE_NORMAL;
        }
        String account = QRUtil.qrString2Account(qrCode);
        if (!TextUtils.isEmpty(account)) {
            //查询是否是好友关系
            SearchFriendControler searchFriendControler = new SearchFriendControler(ActomaApplication.getInstance());
            searchFriendControler.skip(account);
            return SCAN_ERROR_CODE_NORMAL;
        }
        return SCAN_ERROR_CODE_INVALID;
    }
    //modify by alh@xdja.com to fix bug: 810 2016-07-14 end (rummager : anlihuang)
    //[s]modify by xnn for bug 9664 review by tangsha
    private static void startActivity(Intent intent) {
        startActivityForResult(null, intent, -1);
    }

    private static void startActivity(Context context, Intent intent) {
        startActivityForResult(context, intent, -1);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(null, intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void startActivityForResult(Context context, Intent intent, int requestCode) {
        if(context == null) {
            Activity activity = provideTopActivity();
            if (activity != null) {
                activity.startActivityForResult(intent, requestCode);
            }
        }else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    //[e]modify by xnn for bug 9664 review by tangsha

    private static Activity provideTopActivity() {
        Activity activity = ActivityStack.getInstanse().getTopActivity();
        if (activity == null) {
            LogUtil.getUtils().w("自定义栈的栈顶activity为空");
            return null;
        }
        return activity;
    }

    public static void closeInputKeyboard(Activity context) {
        if (context == null || context.getCurrentFocus() == null || context.getCurrentFocus().getWindowToken() == null) return;//add by xnn for 闪屏页进入时收到集团推送，无法正常退出问题 @20170308
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException nullPoint) {
            nullPoint.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static Intent generateIntent(Class<?> cls) {
        //modify by alh@xdja.com to fix bug: 800 2016-07-01 start (rummager : anlihuang)
        Activity topActivity = ActivityStack.getInstanse().getTopActivity();
        if (topActivity != null){
            closeInputKeyboard(topActivity);
            return new Intent(topActivity, cls);
        }
        return new Intent(ActomaApplication.getInstance(), cls);
        //modify by alh@xdja.com to fix bug: 800 2016-07-01 end (rummager : anlihuang)
    }
}
