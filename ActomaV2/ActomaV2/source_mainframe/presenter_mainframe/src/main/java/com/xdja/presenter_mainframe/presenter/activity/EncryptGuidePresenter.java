package com.xdja.presenter_mainframe.presenter.activity;

import android.os.Bundle;

import com.xdja.presenter_mainframe.cmd.EncryptGuideCommand;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewEncryptGuide;
import com.xdja.presenter_mainframe.ui.uiInterface.EncryptGuideVu;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;


/**
 * 第三方加密服务引导页
 */
public class EncryptGuidePresenter extends PresenterActivity<EncryptGuideCommand, EncryptGuideVu> implements EncryptGuideCommand {
    @Override
    protected Class<? extends EncryptGuideVu> getVuClass() {
        return ViewEncryptGuide.class;
    }

    @Override
    protected EncryptGuideCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        setResult(RESULT_OK);
        //修改状态
        SharePreferceUtil.getPreferceUtil(this).setIsFirstOpenSever(false);
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().onDestroy();
    }
}
