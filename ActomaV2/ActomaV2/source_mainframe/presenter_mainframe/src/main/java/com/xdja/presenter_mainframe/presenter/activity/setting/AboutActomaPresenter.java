package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.DeviceUtil;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AboutActomaCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.WebViewPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewAboutActoma;
import com.xdja.presenter_mainframe.ui.uiInterface.AboutActomaVu;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;
import webrelay.ConfigurationServer;

/**
 * Created by geyao on 2015/7/7.
 * 关于安通+
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class AboutActomaPresenter extends PresenterActivity<AboutActomaCommand, AboutActomaVu> implements AboutActomaCommand {
    /**
     * 域名
     */
    private String HOST;

    @Inject
    @InteractorSpe(DomainConfig.CHECK_NEW_VERSION)
    Lazy<Ext1Interactor<Context, Boolean>> checkUpdateUseCase;

    @Inject
    BusProvider busProvider;

    @Override
    protected Class<? extends AboutActomaVu> getVuClass() {
        return ViewAboutActoma.class;
    }

    @Override
    protected AboutActomaCommand getCommand() {
        return this;
    }

    /**
     * 打开欢迎页
     */
    @Override
    public void openWelcomPage() {
        Navigator.navigateToProductIntroductionAnim(true);
    }


    /**
     * 打开功能介绍
     */
    @Override
    public void openIntroduce() {
        String title = getString(R.string.aboutactoma_functionintroduction);
        //[s]modify by xienana for multi languange change local web @20161115[review by tangsha]
        //私有化部署版本，应用介绍无第三方加密。 gbc 2017-02-20
        if (CustInfo.isCustom()) {
            String uri = "file:///android_asset/introduce-2.html";
            String result_uri = UniversalUtil.changeLanLocalWebUrl(this, uri);
            Navigator.navigateToWebView(result_uri, title);
        } else {
            if (UniversalUtil.isXposed()) {
                String uri_xpose = "file:///android_asset/introduce.html";
                String result_uri = UniversalUtil.changeLanLocalWebUrl(this, uri_xpose);
                Navigator.navigateToWebView(result_uri, title);
            } else {
                String uri = "file:///android_asset/introduce-1.html";
                String result_uri = UniversalUtil.changeLanLocalWebUrl(this, uri);
                Navigator.navigateToWebView(result_uri, title);
            }
        }
        //[e]modify by xienana for multi languange change local web @20161115[review by tangsha]
    }

    /**
     * 打开常见问题
     */
    @Override
    public void openProblem() {
        String problem_url = ConfigurationServer.getAssetsConfig(this , "config.properties").read("problems3", "", String.class);
        Intent intent = new Intent(this, WebViewPresenter.class);
        intent.putExtra(WebViewPresenter.TITLE, getString(R.string.aboutactoma_commonproblems));
        //[s]modify by xienana for multi language for server web change
        String url = HOST + "/" + problem_url;
        String res_url = UniversalUtil.changeLanServerWebUrl(this,url);
        intent.putExtra(WebViewPresenter.WEBURL, res_url);
        //[e]modify by xienana for multi language for server web change
        startActivity(intent);
    }

    /**
     * 打开版本更新
     */
    @Override
    public void openUpdate() {
        Intent intent = new Intent(this, UpdateTransparentPresenter.class);
        intent.putExtra(UpdateTransparentPresenter.ISSHOWWITHOUTNEWDIALOG, true);
        startActivity(intent);
    }


    /**
     * 打开条款和隐私政策
     */
    @Override
    public void openTermPolicy() {
//        Intent intent = new Intent(this, WebViewPresenter.class);
//        intent.putExtra(WebViewPresenter.TITLE, getString(R.string.title_activity_view_terms_policy));
//        intent.putExtra(WebViewPresenter.WEBURL,
//                HOST + ConfigurationServer.getAssetsConfig(this).read("law", "", String.class));
//        startActivity(intent);
        String title = getString(R.string.title_activity_view_terms_policy);
        /*[S]modify by tangsha @20161012 for multi language*/
        //[s]modify by xienana for multi languange change local web @20161115[review by tangsha]
        String uri = "file:///android_asset/law.html";
        String result_uri = UniversalUtil.changeLanLocalWebUrl(this,uri);
        //[e]modify by xienana for multi languange change local web @20161115[review by tangsha]
        /*[E]modify by tangsha @20161012 for multi language*/
        Navigator.navigateToWebView(result_uri, title);
    }

    /**
     * 退出
     */
    @Override
    public void exit() {
        final CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(getString(R.string.sure_you_want_to_logout))
                .setMessage(getString(R.string.logout_content_prompt))
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.text_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!UniversalUtil.isXposed()) {
                            //haveXposedlogout();
                            logout();
                        }
                    }
                }).show();

    }

    /**
     * 账户登出(含有xposed框架)
     */
    /*private void haveXposedlogout() {
        getVu().showCommonProgressDialog("正在退出中...");
        //清空加密所需数据
//            String account = StateParams.getStateParams().getEncryptAccount();
//            String pkgName = StateParams.getStateParams().getPkgName();
        ClearActomaEncryptParamsUseCase useCase = new ClearActomaEncryptParamsUseCase(
                ActomaApp.getActomaApp().getApplicationContext());
        useCase.execute(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                logout();
            }

            @Override
            public void onError(Throwable e) {
                getVu().dismissCommonProgressDialog();
                XToast.show(AboutActomaPresenter.this, "清除加密数据失败");
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }*/

    /**
     * 账户登出(不含有xposed框架)
     */
    private void logout() {
        getVu().showCommonProgressDialog(getString(R.string.exiting));
        /*try {

            //各个插件退出
            PluginsControlCase.PluinsLogout(this);

            StringResult stringResult = TFCardManager.getCardId();
            if (stringResult == null || stringResult.getErrorCode() != 0) {
                getVu().dismissCommonProgressDialog();

                XToast.show(AboutActomaPresenter.this, "获取卡设备失败 (" + stringResult.getErrorCode() + ")");
                return;
            }
            String cardId = stringResult.getResult();

            LogoutUseCase useCase = new LogoutUseCase(cardId);
            useCase.execute(new ActomaUseCase.ActomaSub() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onNext(Object o) {
                    getVu().dismissCommonProgressDialog();
                    Function.simpleLogout(AboutActomaPresenter.this);
                    ActomaApp.getActomaApp().getAccountInfo().setTicket("");
                    ActivityContoller.getInstanse().exit();
                    System.exit(0);
                }

                @Override
                public void onError(Throwable e) {
                    getVu().dismissCommonProgressDialog();

                    //处理错误信息
                    if (e instanceof ATErrorHandler.AtHttpResponseException) {
                        ATErrorHandler.AtHttpResponseException exception
                                = ((ATErrorHandler.AtHttpResponseException) e);
                        if (exception != null && exception.getErrorCode() == ATErrorHandler.UNKNOWN_ERROR_CODE) {
                            if (exception.getMessage().equals("ticket_resuorce_disaccord")) {
                                LogUtil.getUtils().i("==用户凭证与客户端标识不一致==");
                            }
                        }
                    }

                    Function.simpleLogout(AboutActomaPresenter.this);
                    ActomaApp.getActomaApp().getAccountInfo().setTicket("");
                    ActivityContoller.getInstanse().exit();
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            getVu().dismissCommonProgressDialog();

            XToast.show(AboutActomaPresenter.this, "账户退出时异常");
            LogUtil.getUtils().i("退出获取carid失败======》" + e.getMessage());
        } finally {
            //Function.simpleLogout(AboutActomaPresenter.this);
        }*/
    }


    /**
     * View初始化之后
     *
     * @param savedInstanceState
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }
        String currentVersion = DeviceUtil.getClientVersion(this);
        getVu().setTitle(getString(R.string.title_mainFrame) + currentVersion);

        busProvider.register(this);

        //进入到界面后根据存的信息刷新界面
        checkUpdateNew();

        HOST = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl");

        //私有化部署版本，隐藏“帮助与反馈”菜单
        if (CustInfo.isCustom()) {
            //隐藏菜单
            getVu().updateHelpFeedBack(false);
        }
    }

    /**
     * 检测是否有新版本
     */
    private void checkUpdateNew() {
        checkUpdateUseCase.get().fill(this).execute(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//                    boolean isShowNewView =
//                            SharePreferceUtil.getPreferceUtil(AboutActomaPresenter.this).getIsShowNewView();
//                    if (isShowNewView) {
//                        getVu().freshUpdateNew(aBoolean);
//                    }
                    if (aBoolean != null) {
                        getVu().freshUpdateNew(aBoolean);
                    }
                    // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }

    /**
     * View初始化之前
     *
     * @param savedInstanceState
     */
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }


    /**
     * 发送广播
     */
    @Override
    public void sendUpdateEvent() {
        if (busProvider != null) {
            FreshUpdateNewEvent event = new FreshUpdateNewEvent();
            event.setIsHaveUpdate(true);
            busProvider.post(event);
        }
    }

    @Subscribe
    public void freshUpdate(FreshUpdateNewEvent event) {
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//        boolean isShowNewView =
//                SharePreferceUtil.getPreferceUtil(AboutActomaPresenter.this).getIsShowNewView();
//        if (isShowNewView) {
//            getVu().freshUpdateNew(event.isHaveUpdate());
//        }
        if (event != null) {
            getVu().freshUpdateNew(event.isHaveUpdate());
        }
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    }

//    /**
//     * 根据是否有更新内容刷新界面事件
//     */
//    public static class FreshUpdateNewEvent {
//        public boolean isHaveUpdate() {
//            return isHaveUpdate;
//        }
//
//        public void setIsHaveUpdate(boolean isHaveUpdate) {
//            this.isHaveUpdate = isHaveUpdate;
//        }
//
//        private boolean isHaveUpdate;
//    }

}
