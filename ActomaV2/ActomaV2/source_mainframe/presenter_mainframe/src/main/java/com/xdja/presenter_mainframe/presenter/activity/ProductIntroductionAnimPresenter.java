package com.xdja.presenter_mainframe.presenter.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ProductIntroduceCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewProductIntroduce;
import com.xdja.presenter_mainframe.ui.uiInterface.ProductIntroduceVu;
import com.xdja.presenter_mainframe.util.TextUtil;

import javax.inject.Inject;

/**
 * <p>Summary:产品介绍动画</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/30</p>
 * <p>Time:19:09</p>
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ProductIntroductionAnimPresenter extends PresenterActivity<ProductIntroduceCommand, ProductIntroduceVu> implements ProductIntroduceCommand {
    public static final String IS_FINISH_INTRODUCE = "isFinishIntroduce";
    //是否从设置进入的欢迎页
    public final static String IS_SETTING_WELCOME = "isSettingWelcome";
    //是否是第一次或者清空数据进入安通+，安全锁的设置
    private final static String IS_SAFE_LOCK = "isSafeLock";
    private boolean mIsClose = false;

    @Inject
    PreferencesUtil preferencesUtil;

    @Override
    protected Class<? extends ProductIntroduceVu> getVuClass() {
        return ViewProductIntroduce.class;
    }

    @Override
    protected ProductIntroduceCommand getCommand() {
        return this;
    }

    @SuppressLint("NewApi")
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        //设置导航栏透明(防止背景因导航栏出现拉伸压缩失真)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        getActivityPreUseCaseComponent().inject(this);
        getVu().isHideWelcomeBtn(false);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-12 add. fix demand 566 . review by wangchao1. Start
        boolean isSettingWelcome = getIntent().getBooleanExtra(ProductIntroductionAnimPresenter.IS_SETTING_WELCOME,
                false);
        if (!isSettingWelcome) {
            showPermissionDialog();
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-12 add. fix demand 566 . review by wangchao1. End
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-20 add. fix demand 3142 . review by wangchao1. Start
    private void showPermissionDialog() {
        final CustomDialog customDialog = new CustomDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout , null);
        TextView tips = (TextView)view.findViewById(R.id.tips_textView);
        tips.setText(TextUtil.getActomaText(ProductIntroductionAnimPresenter.this,TextUtil.ActomaImage.IMAGE_VERSION_BIG,0,0,0, getResources().getString(R.string.permission_message)));
        Button ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVu().setHideFlippoint(false);
                customDialog.dismiss();
            }
        });
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVu().setHideFlippoint(false);
                customDialog.dismiss();// 关闭进度对话框
                mIsClose = true;
                ActivityStack.getInstanse().exitApp();
                finish();
            }
        });
        customDialog.setCancelable(false);
        customDialog.setView(view).show();
        getVu().setHideFlippoint(true);
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-20 add. fix demand 3142 . review by wangchao1. End

    @Override
    public void finishIntroduce() {
        if (!mIsClose) {
            preferencesUtil.setPreferenceBooleanValue(IS_FINISH_INTRODUCE, true);
            //设置第一次使用撤回功能的标识
            //preferencesUtil.setPreferenceBooleanValue(ConstDef.HAS_USE_RECALL_MSG,false);
            //第一次使用安全锁，以及清空数据进入，保存安全锁状态
            preferencesUtil.setPreferenceBooleanValue(IS_SAFE_LOCK,true);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().onDestroy();
    }

    //[s]modify by xienana for bug 6039 @20161118 review by tangsha
    @Override
    public void initSafePin() {}
    //[e]modify by xienana for bug 6039 @20161118 review by tangsha

    //[s]add by ysp@xdja.com
    @Override
    public void detectUninstallSafekey() {
    }
    //[e]add by ysp@xdja.com

    @Override
    public void detectSafeKey() {
    }
}
