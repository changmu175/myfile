package com.xdja.presenter_mainframe.presenter.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.exeptions.NetworkException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.frame.domain.usecase.Interactor;
import com.xdja.frame.main.ExceptionHandler;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.di.components.ActivityComponent;
import com.xdja.presenter_mainframe.di.components.DaggerActivityComponent;
import com.xdja.presenter_mainframe.di.components.post.ActivityPostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.PostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.di.components.pre.ActivityPreUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.pre.AppComponent;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.SplashPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>Summary:通用Activity相关的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:30</p>
 */
public abstract class PresenterActivity<P extends Command, V extends ActivityVu>
        extends BasePresenterActivity<P, V> implements ExceptionHandler {

    public AppComponent getAppComponent() {
        return ((ActomaApplication) this.getApplication()).getAppComponent();
    }

    public UserComponent getUserComponent() {
        return ((ActomaApplication) this.getApplication()).getUserComponent();
    }

    private ActivityComponent activityComponent;

    @NonNull
    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    private ActivityPreUseCaseComponent activityPreUseCaseComponent;

    @NonNull
    public ActivityPreUseCaseComponent getActivityPreUseCaseComponent() {
        return activityPreUseCaseComponent;
    }

    private ActivityPostUseCaseComponent activityPostUseCaseComponent;

    @Nullable
    public ActivityPostUseCaseComponent getActivityPostUseCaseComponent() {
        return this.activityPostUseCaseComponent;
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

    private Map<Class<? extends Interactor>,LoadingDialogSubscriber> subscriberMap = new HashMap<>();
    /**
     * 不重复的执行用例，即相同的用例如果处于执行状态（未取消订阅），则不执行，只显示loading
     * @param interactor    用例
     * @param useCaseSubscriber 用例对应的执行者
     */
    public <T> void executeInteractorNoRepeat(@NonNull Interactor<T> interactor, @NonNull LoadingDialogSubscriber<T> useCaseSubscriber){
        Class<? extends Interactor> interactorClass = interactor.getClass();
        if (subscriberMap.containsKey(interactorClass)) {
            LoadingDialogSubscriber perSubscriber = subscriberMap.get(interactorClass);
            if (!perSubscriber.isUnsubscribed()) {
                perSubscriber.showLoadingDialog();
                return;
            }else {
                subscriberMap.remove(interactorClass);
            }
        }
        addInteractor2Queue(interactor);
        subscriberMap.put(interactorClass,useCaseSubscriber);
        interactor.execute(useCaseSubscriber);
    }

    public <T> void executeInteractorNoRepeat(@NonNull Interactor<T> interactor, @NonNull PerSubscriber<T> useCaseSubscriber){
        Class<? extends Interactor> interactorClass = interactor.getClass();
        addInteractor2Queue(interactor);
        interactor.execute(useCaseSubscriber);
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        /*[s]modify by tangsha@20161206 for multi language*/
        UniversalUtil.changeLanguageConfig(this);
        /*[E]modify by tangsha@20161206for multi language*/
    }

    private final void startLauncherAndFinishSelf(){
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext()
                .getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Navigator.navigateToLauncherWithExit();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        InjectOption.Options annotation = getClass().getAnnotation(InjectOption.Options.class);
        if (annotation == null || annotation.value() == InjectOption.OPTION_ACTIVITY) {
            if (!((ActomaApplication)ActomaApplication.getInstance()).getCkmsInitOk() && !(this instanceof SplashPresenter)){
                LogUtil.getUtils().e("没有执行CKMS初始化 重新进入Launcher界面 OPTION_ACTIVITY : " + this);
                startLauncherAndFinishSelf();
                return;
            }
            this.activityComponent = DaggerActivityComponent
                    .builder()
                    .activityModule(new ActivityModule(this))
                    .subActivityModule(new SubActivityModule(this))
                    .build();
        } else if (annotation.value() == InjectOption.OPTION_PRECACHEDUSER) {
            if (!((ActomaApplication)ActomaApplication.getInstance()).getCkmsInitOk() && !(this instanceof SplashPresenter)){
                LogUtil.getUtils().e("没有执行CKMS初始化 重新进入Launcher界面 OPTION_PRECACHEDUSER : " + this);
                startLauncherAndFinishSelf();
                return;
            }
            PreUseCaseComponent preUseCaseComponent = getAppComponent().plus(new PreUseCaseModule());
            this.activityPreUseCaseComponent = preUseCaseComponent.plus(
                    new ActivityModule(this), new SubActivityModule(this)
            );
        } else if (annotation.value() == InjectOption.OPTION_POSTCACHEDUSER) {
            UserComponent userComponent = getUserComponent();
            if (userComponent != null) {
                PostUseCaseComponent postUseCaseComponent = userComponent.plus(
                        new PostUseCaseModule(), new PreUseCaseModule()
                );
                this.activityPostUseCaseComponent
                        = postUseCaseComponent.plus(
                        new ActivityModule(this), new SubActivityModule(this)
                );
            } else {
                // TODO: 2016/6/7 此处存在问题，待处理。具体问题描述：在ACE手机上，登陆成功后进入主页面，
                // TODO:通过进程清理工具清理后台的进程（此时进程已经被杀死），再次打开时，加载的是主页面而
                // TODO:不是launcher页面。初步分析，是进程没有杀干净造成的，目前只在ACE手机上出现。初步
                // TODO:处理方案为：在此处做出判断，若无用户信息（UserComponent为空），跳转到launcher页面。
                LogUtil.getUtils().w("未查询到用户信息注入提供对象");
                startLauncherAndFinishSelf();
            }
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.activityComponent = null;
        this.activityPreUseCaseComponent = null;
        this.activityPostUseCaseComponent = null;

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
        if (!TextUtils.isEmpty(userMsg)) {
            XToast.showBottomWrongToast(this, userMsg);
        }
    }

    @Override
    public boolean handlerThrowable(@Nullable Throwable throwable, @Nullable String mark) {
        return true;
    }

    @Override
    public void defaultThrowable(@Nullable Throwable throwable, @Nullable String mark) {
        String errorMsg = "";
        if (throwable instanceof OkException) {
            if(throwable instanceof NetworkException){
                int id = ((NetworkException)throwable).getResId();
                if(id != -1){
                    errorMsg = getResources().getString(id);
                }
                if(TextUtils.isEmpty(errorMsg) == false) {
                    XToast.showBottomWrongToast(this, errorMsg);
                }
            }
            if(TextUtils.isEmpty(errorMsg)){
                errorMsg = throwable.getMessage();
                LogUtil.getUtils().e("persenterActivity defaultThrowable "+errorMsg);
            }
        }
        else{
            //对于未定义的异常的处理形式
            if (throwable!=null){
                if (TextUtils.isEmpty(errorMsg)){
                    if(!TextUtils.isEmpty(throwable.getMessage())) {
                        errorMsg = throwable.getMessage();
                    }else{
                        errorMsg = getString(R.string.error_and_not_prompt);
                    }
                }
            }
            LogUtil.getUtils().e("persenterActivity defaultThrowable "+errorMsg);
           // XToast.showBottomWrongToast(this, errorMsg);
        }
    }
}
