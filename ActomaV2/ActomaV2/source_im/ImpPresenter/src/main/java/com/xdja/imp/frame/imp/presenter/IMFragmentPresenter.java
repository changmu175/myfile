package com.xdja.imp.frame.imp.presenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterFragment;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.imp.IMAccountLifeCycle;
import com.xdja.imp.data.error.OkException;
import com.xdja.imp.data.error.OkHandler;
import com.xdja.imp.di.component.DaggerUseCaseComponent;
import com.xdja.imp.di.component.UseCaseComponent;
import com.xdja.imp.di.component.UserComponent;
import com.xdja.imp.domain.interactor.def.RemindNewMessage;
import com.xdja.imp.handler.OkHandlerImp;
import com.xdja.imp.util.MsgDisplay;
import com.xdja.imp.util.NotificationUtil;

/**
 * Created by jing on 2015/11/13.
 */
public abstract class IMFragmentPresenter<P extends Command, V extends FragmentVu>
        extends BasePresenterFragment<P, V> {

    protected UseCaseComponent useCaseComponent;

    protected RemindNewMessage remindNewMessage;

    protected OkHandler<OkException> okHandler;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        if (null == IMAccountLifeCycle.imAccountLifeCycle || null == IMAccountLifeCycle.imAccountLifeCycle.getComponent()) {
            //容错处理，回退到登陆界面
            LogUtil.getUtils().d("未查询到用户信息注入提供对象");
            Intent intent = Intent.makeMainActivity(
                    new ComponentName(getContext().getPackageName(), "com.xdja.presenter_mainframe.presenter.activity.LauncherPresenter"));//modify by xnn for inter version @20170224
            intent.putExtra("exit", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else {
            UserComponent userComponent =
                    IMAccountLifeCycle.imAccountLifeCycle.getComponent();
            this.useCaseComponent = DaggerUseCaseComponent.builder()
                    .userComponent(userComponent)
                    .build();
            MsgDisplay msgDisplay = this.useCaseComponent.msgDisplay();
            this.okHandler = new OkHandlerImp<>(msgDisplay);
        }
        
    }
    @Override
    public void onResume() {
        super.onResume();
        NotificationUtil.getInstance(getActivity().getApplicationContext()).clearPNNotification();
    }
}
