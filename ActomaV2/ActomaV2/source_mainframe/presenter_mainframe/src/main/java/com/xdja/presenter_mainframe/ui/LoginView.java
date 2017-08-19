package com.xdja.presenter_mainframe.ui;

import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.LoginCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuLogin;
import com.xdja.presenter_mainframe.util.DensityUtil;
import com.xdja.presenter_mainframe.widget.inputView.IconInputView;

import butterknife.Bind;
import butterknife.OnClick;

import static com.xdja.presenter_mainframe.R.id.btn_input_clear;


/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
@ContentView(R.layout.activity_login)
public class LoginView extends ActivitySuperView<LoginCommand> implements VuLogin {

    /**
     * 移动云朵的偏移距离,单位:dp
     */
    private static final int CLOUD_OFFSET = 860;
    /**
     * 云朵动画移动一次耗时
     */
    private static final long CLOUD_MOVE_DURATION = 3000;

    @Bind(R.id.login_root)
    View pageRoot;
    @Bind(R.id.iv_login_cloud)
    ImageView ivLoginCloud;
    @Bind(R.id.btn_login_login)
    Button btnLoginLogin;
    @Bind(R.id.pctv_login_message_login)
    TextView pctvLoginMessageLogin;
    @Bind(R.id.pctv_login_forget_password)
    TextView pctvLoginForgetPassword;
    @Bind(R.id.iv_login_face)
    CircleImageView ivLoginFace;
    @Bind(R.id.iiv_login_account)
    EditText iivLoginAccount;
    @Bind(R.id.iiv_login_password)
    EditText iivLoginPassword;
    @Bind(R.id.btn_account_clear)
    Button btn_account_clear;
    @Bind(R.id.btn_pwd_clear)
    Button btn_pwd_clear;
    @Bind(R.id.assist_view)
    CheckBox assist_view;

    private TranslateAnimation translateAnimation;
    private static final int ET_MAX_SIZE = 20;


    public static final float TRANSLATIONY = -500;
    public static final float TRANSLATIONY_ACCOUNT = -400;
    public static final float SCALEX= 0.5f;
    public static final float SCALEY = 0.5f;
    public static final float ALPHA = 0.1f;
    public static final long DURATION = 300L;
    public static final long START_DELAY = 300L;
    public static final long START_DELAY_LOGIN = 500L;
    public static final long START_DELAY_MESSAGE_LOGIN = 800L;

    private Handler myHandler = new Handler();
    private Runnable mLoadingRunnable = new Runnable() {

        @Override
        public void run() {
            initAnimation();
        }
    };

    /**
     * 账号框是否已输入
     */
    protected boolean isInputAccount = false;
    /**
     * 密码框是否已输入
     */
    protected boolean isInputPassword = false;

    /**
     * 账号框是否有焦点
     */
    protected boolean isFocusAccount = false;
    /**
     * 密码框是否已输入
     */
    protected boolean isFocusPassword = false;

    @Override
    public void onCreated() {
        super.onCreated();
//        initAnimation();

        //一种延时加载的方式,在一定程度上避免了动画加载的卡顿
        getActivity().getWindow().getDecorView().post(new Runnable() {
            //    @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });

        final String accountDigits = getStringRes(R.string.account_digits);
        //modify by xnn@xdja.com to fix bug: 1131 2016-07-07 start (rummager : anlihuang)
        iivLoginAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFocusAccount = hasFocus;
                setInputClearVisible();
            }
        });
        iivLoginAccount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        iivLoginAccount.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = accountDigits.toCharArray();
                return numberChars;
            }
        });
        iivLoginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputAccount = iivLoginAccount.getText().toString() != null && !iivLoginAccount.getText().toString().isEmpty();
                setButtonEnabled();
                setInputClearVisible();

                boolean isEmpty = TextUtils.isEmpty(iivLoginAccount.getText().toString());
                getCommand().afterAccountChanged(isEmpty);
                if (isEmpty){
                    if (iivLoginPassword != null){
                        iivLoginPassword.setText(null);
                    }
                }
            }
        });
        final String passwordDigits = getStringRes(R.string.password_digits);
        iivLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFocusPassword = hasFocus;
                setInputClearVisible();
            }
        });
        iivLoginPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        //modify by xnn@xdja.com to fix bug: 1131 2016-07-07 end (rummager : anlihuang)
        iivLoginPassword.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = passwordDigits.toCharArray();
                return numberChars;
            }
        });
        iivLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputPassword = iivLoginPassword.getText() != null && iivLoginPassword.getText().length() > 5;
                setButtonEnabled();
                setInputClearVisible();
            }
        });
        assist_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    iivLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    iivLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                if (iivLoginPassword.getText() != null) {
                    iivLoginPassword.setSelection(iivLoginPassword.getText().length());
                }
            }
        });
        if (assist_view.getVisibility() == View.VISIBLE) {
            iivLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtonEnabled();
    }

    private void setInputClearVisible(){
        btn_account_clear.setVisibility(isInputAccount && isFocusAccount ? View.VISIBLE : View.GONE);
        btn_pwd_clear.setVisibility(iivLoginPassword.getText() != null && iivLoginPassword.getText().length() > 0 && isFocusPassword ? View.VISIBLE : View.GONE);
    }

    private void setButtonEnabled(){
        if (isInputAccount && isInputPassword) {
            btnLoginLogin.setEnabled(true);
        } else {
            btnLoginLogin.setEnabled(false);
        }
    }

    private void initAnimation() {
        translateAnimation = new TranslateAnimation(-DensityUtil.dip2px(getContext(), CLOUD_OFFSET), 0, 0, 0);
        translateAnimation.setDuration(CLOUD_MOVE_DURATION);
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.RESTART);
        translateAnimation.setInterpolator(AnimationUtils.loadInterpolator(getContext(),
                android.R.interpolator.linear));
        ivLoginCloud.startAnimation(translateAnimation);


        iivLoginPassword.setTranslationY(TRANSLATIONY);
        iivLoginPassword.setScaleX(SCALEX);
        iivLoginPassword.setScaleY(SCALEY);
        iivLoginPassword.setAlpha(ALPHA);
        iivLoginPassword.animate()
                .setDuration(DURATION)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(START_DELAY)
                .translationY(0);

        iivLoginAccount.setTranslationY(TRANSLATIONY_ACCOUNT);
        iivLoginAccount.setScaleX(SCALEX);
        iivLoginAccount.setScaleY(SCALEY);
        iivLoginAccount.setAlpha(ALPHA);
        iivLoginAccount.animate()
                .setDuration(DURATION)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(DURATION)
                .translationY(0);

        ivLoginFace.setTranslationY(TRANSLATIONY_ACCOUNT);
        ivLoginFace.setScaleX(SCALEX);
        ivLoginFace.setScaleY(SCALEY);
        ivLoginFace.setAlpha(ALPHA);
        ivLoginFace.animate()
                .setDuration(DURATION)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(START_DELAY_LOGIN)
                .translationY(0);

        btnLoginLogin.setTranslationY(1000);
        btnLoginLogin.setAlpha(ALPHA);
        btnLoginLogin.animate()
                .setDuration(START_DELAY_LOGIN)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(DURATION)
                .translationY(0);

        pctvLoginMessageLogin.setScaleX(0);
        pctvLoginMessageLogin.setScaleY(0);
        pctvLoginMessageLogin.setAlpha(ALPHA);
        pctvLoginMessageLogin.animate()
                .setDuration(DURATION)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(START_DELAY_MESSAGE_LOGIN)
                .translationY(0);
        pctvLoginForgetPassword.setScaleX(0);
        pctvLoginForgetPassword.setScaleY(0);
        pctvLoginForgetPassword.setAlpha(ALPHA);
        pctvLoginForgetPassword.animate()
                .setDuration(DURATION)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setStartDelay(START_DELAY_MESSAGE_LOGIN)
                .translationY(0);



    }

    @OnClick(R.id.pctv_login_message_login)
    void messageLogin() {
        getCommand().messageVerifyLogin();
    }

    @OnClick(R.id.btn_login_login)
    void login() {
        if (TextUtils.isEmpty(iivLoginAccount.getText()) || TextUtils.isEmpty(iivLoginPassword.getText())){
            showToast(getStringRes(R.string.account_or_pwd_cannot_empty));
            return;
        }
        getCommand().login(iivLoginAccount.getText().toString(), iivLoginPassword.getText().toString());
    }

    @OnClick(R.id.btn_account_clear)
    void clearAccount(){
        if (iivLoginAccount != null)iivLoginAccount.setText(null);
    }

    @OnClick(R.id.btn_pwd_clear)
    void clearPwd(){
        if (iivLoginPassword != null)iivLoginPassword.setText(null);
    }

    @OnClick(R.id.pctv_login_forget_password)
    void forgetPassword() {
        final XDialog xDialog = new XDialog(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_more,null);
        view.findViewById(R.id.tv_login_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().register();
                xDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_login_forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().forgetPassword();
                xDialog.dismiss();
            }
        });
        xDialog.setView(view);
        xDialog.setCanceledOnTouchOutside(true);
        xDialog.show();

    }

    @Override
    public void clearPassword() {
        iivLoginPassword.setText("");
    }

    @Override
    public void setAccount(final String account) {
        iivLoginAccount.setText(account);
        iivLoginAccount.post(new Runnable() {
            @Override
            public void run() {
                iivLoginAccount.setSelection(iivLoginAccount.getText().length());
            }
        });
    }

    @Override
    public void setAvatarId(String avatarId) {
        ivLoginFace.loadImage(avatarId,true);
    }

    @Override
    public String getInputAccount() {
        if (iivLoginAccount == null){
            return "";
        }
        return iivLoginAccount.getText().toString();
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(),maxLoginCount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GcMemoryUtil.clearMemory(pageRoot);
    }
}