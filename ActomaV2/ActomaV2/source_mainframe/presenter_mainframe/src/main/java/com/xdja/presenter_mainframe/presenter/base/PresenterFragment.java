package com.xdja.presenter_mainframe.presenter.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.NetworkException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.frame.domain.usecase.Interactor;
import com.xdja.frame.main.ExceptionHandler;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterFragment;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.di.components.DaggerFragmentComponent;
import com.xdja.presenter_mainframe.di.components.FragmentComponent;
import com.xdja.presenter_mainframe.di.components.post.FragmentPostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.PostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.di.components.pre.AppComponent;
import com.xdja.presenter_mainframe.di.components.pre.FragmentPreUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;
import com.xdja.presenter_mainframe.navigation.Navigator;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Summary:通用Fragment相关的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:30</p>
 */
public abstract class PresenterFragment<P extends Command, V extends FragmentVu>
        extends BasePresenterFragment<P, V> implements ExceptionHandler {

    public AppComponent getAppComponent() {
        return ((ActomaApplication) this.getActivity().getApplication()).getAppComponent();
    }

    public UserComponent getUserComponent() {
        return ((ActomaApplication) this.getActivity().getApplication()).getUserComponent();
    }

    private FragmentComponent fragmentComponent;

    public FragmentComponent getFragmentComponent() {
        return fragmentComponent;
    }

    private FragmentPreUseCaseComponent fragmentPreUseCaseComponent;

    @NonNull
    public FragmentPreUseCaseComponent getFragmentPreUseCaseComponent() {
        return fragmentPreUseCaseComponent;
    }

    private FragmentPostUseCaseComponent fragmentPostUseCaseComponent;

    @Nullable
    public FragmentPostUseCaseComponent getFragmentPostUseCaseComponent() {
        return this.fragmentPostUseCaseComponent;
    }

    private List<Interactor> interactors;

    /**
     * 将业务用例加入相关的队列中
     */
    public <T extends Interactor> T addInteractor2Queue(T interactor) {
        if (interactors == null) {
            interactors = new ArrayList<>();
        }
        interactors.add(interactor);
        return interactor;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        InjectOption.Options annotation = getClass().getAnnotation(InjectOption.Options.class);
        if (annotation == null || annotation.value() == InjectOption.OPTION_ACTIVITY) {
            this.fragmentComponent = DaggerFragmentComponent
                    .builder()
                    .fragmentModule(new FragmentModule(this))
                    .subFragmentModule(new SubFragmentModule(this))
                    .build();
        } else if (annotation.value() == InjectOption.OPTION_PRECACHEDUSER) {
            PreUseCaseComponent preUseCaseComponent = getAppComponent().plus(new PreUseCaseModule());
            this.fragmentPreUseCaseComponent = preUseCaseComponent.plus(
                    new FragmentModule(this), new SubFragmentModule(this)
            );
        } else if (annotation.value() == InjectOption.OPTION_POSTCACHEDUSER) {
            UserComponent userComponent = getUserComponent();
            if (userComponent != null) {
                PostUseCaseComponent postUseCaseComponent = userComponent.plus(
                        new PostUseCaseModule(), new PreUseCaseModule()
                );

                this.fragmentPostUseCaseComponent
                        = postUseCaseComponent.plus(
                        new FragmentModule(this), new SubFragmentModule(this)
                );
            } else {
                // TODO: 2016/6/7 此处存在问题，待处理。具体问题描述：在ACE手机上，登陆成功后进入主页面，
                // TODO:通过进程清理工具清理后台的进程（此时进程已经被杀死），再次打开时，加载的是主页面而
                // TODO:不是launcher页面。初步分析，是进程没有杀干净造成的，目前只在ACE手机上出现。初步
                // TODO:处理方案为：在此处做出判断，若无用户信息（UserComponent为空），跳转到launcher页面。

                LogUtil.getUtils().w("未查询到用户信息注入提供对象");
                getActivity().finish();
                Navigator.navigateToLauncher();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.fragmentComponent = null;
        this.fragmentPreUseCaseComponent = null;
        this.fragmentPostUseCaseComponent = null;

        if (this.interactors != null && !this.interactors.isEmpty()) {
            for (Interactor interactor : interactors) {
                interactor.unSubscribe();
            }
        }
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        return true;
    }

    @Override
    public void defaultOkException(@Nullable String code, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        XToast.showBottomWrongToast(getContext(), userMsg);
    }

    @Override
    public boolean handlerThrowable(@Nullable Throwable throwable, @Nullable String mark) {
        return true;
    }

    @Override
    public void defaultThrowable(@Nullable Throwable throwable, @Nullable String mark) {
        String errorMsg = "";
        if (throwable instanceof OkException) {
            if (throwable instanceof NetworkException) {
                int id = ((NetworkException) throwable).getResId();
                if (id != -1) {
                    errorMsg = getResources().getString(id);
                }
                if(TextUtils.isEmpty(errorMsg) == false) {
                    XToast.showBottomWrongToast(getContext(), errorMsg);
                }
            }
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = throwable.getMessage();
                LogUtil.getUtils().e("persenterActivity defaultThrowable "+errorMsg);
            }
        }else{
            //对于未定义的异常的处理形式
            if (throwable!=null) {

                    if (!TextUtils.isEmpty(throwable.getMessage())) {
                        errorMsg = throwable.getMessage();
                    } else {
                        errorMsg = getString(R.string.error_and_not_prompt);
                    }
                LogUtil.getUtils().e("persenterActivity defaultThrowable "+errorMsg);
               // XToast.showBottomWrongToast(getContext(), errorMsg);
            }
//            XToast.showBottomWrongToast(getContext(), "服务器繁忙，请重试！");
        }
    }
}
