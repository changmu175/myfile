package com.xdja.presenter_mainframe.ui;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.QuickOpenAppBean;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.uitl.StateParams;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.contact.view.arclayout.ArcLayout;
import com.xdja.contact.view.arclayout.ArcLayoutAnimation;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.MainFrameCommand;
import com.xdja.presenter_mainframe.enc3rd.utils.StrategysUtils;
import com.xdja.presenter_mainframe.enc3rd.utils.ThirdEncAppProperty;
import com.xdja.presenter_mainframe.ui.uiInterface.VuMainFrame;
import com.xdja.presenter_mainframe.widget.TabFragmentAdapter;
import com.xdja.presenter_mainframe.widget.TabView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.ui.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:17:53</p>
 */
@ContentView(R.layout.activity_mainframe)
public class ViewMainFrame extends ActivityView<MainFrameCommand> implements VuMainFrame {
    private final int POSITIN_MSG = 0;
    private final int POSITIN_PHONE = 1;
    private final int POSITIN_CONTACT = 2;
    private final int POSITIN_MROE = 3;

    @Bind(R.id.tabview)
    TabView tabview;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.arc_layout)
    ArcLayout arcLayout;
    @Bind(R.id.root_layout)
    FrameLayout rootLayout;

    private ArcLayoutAnimation arcLayoutAnimation;
    /**
     * 是否有更新提示
     */
    private boolean isHaveUpdate = false;

    @Override
    public void setFragmentAdapter(TabFragmentAdapter adapter) {
        viewpager.setAdapter(adapter);
        tabview.setViewPager(viewpager);
    }

    @Override
    public void updateMsgTabTips(CharSequence text) {
        tabview.showMsgItem(POSITIN_MSG, text);
    }

    @Override
    public void clearMsgTabTips() {
        tabview.clearMsgItem(POSITIN_MSG);
    }

    @Override
    public void updatePhoneTabTips(CharSequence text) {
        tabview.showMsgItem(POSITIN_PHONE, text);
    }

    @Override
    public void clearPhoneTabTips() {
        tabview.clearMsgItem(POSITIN_PHONE);
    }

    @Override
    public void updateContactTabTips(CharSequence text) {
        tabview.showMsgItem(POSITIN_CONTACT, text);
    }

    @Override
    public void clearContactTabTips() {
        tabview.clearMsgItem(POSITIN_CONTACT);
    }

    @Override
    public void updateMoreTabTips(CharSequence text) {
        tabview.showMsgItem(POSITIN_MROE, text);
    }

    @Override
    public void clearMoreTabTips() {
        tabview.clearMsgItem(POSITIN_MROE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isHaveUpdate) {
            getActivity().getMenuInflater().inflate(R.menu.menu_mainframe_update, menu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.menu_mainframe, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setSelectedFragment(@TabTipsEvent.POINT_DEF int index) {

        if (index == viewpager.getCurrentItem()) {
            return;
        }
        viewpager.setCurrentItem(index, false);
        tabview.setSelectedTab(index);
    }

    @Override
    public ArcLayoutAnimation getArcLayoutAnimation() {
        if (arcLayoutAnimation == null) {
            arcLayoutAnimation = new ArcLayoutAnimation(rootLayout, arcLayout);
        }
        ArrayList<View> views = new ArrayList<>();
        initMenuData(arcLayoutAnimation, views);
        if (views.isEmpty()) {
            XToast.show(ActomaApplication.getInstance().getApplicationContext(),
                    getStringRes(R.string.no_encrypt_menu_can_show));
        } else {
            arcLayoutAnimation.addViews(views);
        }
        return arcLayoutAnimation;
    }

    @Override
    public ArcLayoutAnimation getArcView() {
        return arcLayoutAnimation;
    }

    @Override
    public boolean getCirCleMenuIsOpen() {
        return arcLayoutAnimation != null && arcLayoutAnimation.isShown();
    }

    @Override
    public void closeCirCleMenu() {
        if (arcLayoutAnimation != null) {
            arcLayoutAnimation.onFabClick(false);
        }
    }
  /**
     * 根据是否有更新刷新界面
     *
     * @param isHaveNew
     */
    @Override
    public void freshUpdateNew(boolean isHaveNew) {
        isHaveUpdate = isHaveNew;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                getCommand().search();
                break;
            //添加好友
            case R.id.action_adduser:
                getCommand().addUser();
                break;
            //发起群聊
            case R.id.action_chat:
                getCommand().createGroup();
                break;
            //扫一扫
            case R.id.action_scan:
                getCommand().scan();
                break;
            case R.id.action_setting:
                //点击后小红点消失
                freshUpdateNew(false);
                getCommand().setting();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_DEFAULT;
    }

    /**
     * 初始化菜单数据
     *
     * @param animation
     * @param views
     */
    private String TAG = "CkmsViewMainFrame";
    private void initMenuData(final ArcLayoutAnimation animation, final ArrayList<View> views) {
        //[S] fix bug 6088 by licong
        //点击盾牌时,更新第三方策略
        if (UniversalUtil.isXposed()) {
            StrategysUtils.updateStrategys(getActivity());
        }
        //[E] fix bug 6088 by licong

        List<QuickOpenAppBean> qoaBean = StateParams.getStateParams().getQuickOpenAppBeanList();
        if (qoaBean == null || qoaBean.size() <= 0) {
            StrategysUtils.queryQuickOpenApps(getContext(), new StrategysUtils.QueryQuickOpenAppsCallback() {
                @Override
                public void queryQuickOpenApps(List<QuickOpenAppBean> apps) {
                    if (apps != null && !apps.isEmpty()) {
                        createMenuData(apps, animation, views);
                    }
                }
            });
        } else {
            createMenuData(qoaBean, animation, views);
        }
    }


    private void createMenuData(List<QuickOpenAppBean> data, final ArcLayoutAnimation animation, ArrayList<View> views){
       if (data != null && !data.isEmpty()) {
           for (final QuickOpenAppBean bean : data) {
               if (bean != null) {
                   if (bean.getType() == QuickOpenAppBean.TYPT_SHOW) {
                       final String pkgName = bean.getPackageName();
                       View view = LayoutInflater.from(getContext()).inflate(R.layout.circle_menu_btn,
                               arcLayout, false);
                       ImageView image = (ImageView) view.findViewById(R.id.circle_menu_image);
                       int res = R.drawable.img_btn_encryption_serve;
                       if (pkgName.equals("com.tencent.mm")) {//匹配微信
                           res = R.drawable.app_circle_wechat;
                       } else if (pkgName.equals("com.tencent.mobileqq")) {//匹配QQ
                           res = R.drawable.app_circle_qq;
                       } else if (pkgName.equals("com.alibaba.android.rimet")) {//匹配钉钉
                           res = R.drawable.app_circle_dingding;
                       } else if (pkgName.equals("com.immomo.momo")) {//匹配陌陌
                           res = R.drawable.app_circle_momo;
                       }

                       //modify by thz 2016-6-21 适配第三方手机
//                        else if (pkgName.equals("com.android.mms")) {//匹配原生短信
//                            res = R.drawable.app_circle_message;
//                        }

                       else if (ThirdEncAppProperty.mmsHash.containsKey(pkgName)) {//匹配go短信
                           res = R.drawable.app_circle_message;
                       }
                       else if (pkgName.equals("com.jb.gosms")) {//匹配go短信
                           res = R.drawable.app_circle_go;
                       } else if (pkgName.equals("com.hellotext.hello")) {//匹配hello短信
                           res = R.drawable.app_circle_hello;
                       } else if (pkgName.equals("com.snda.youni")) {//匹配youni短信
                           res = R.drawable.app_circle_youni;
                       } else if (pkgName.equals("com.tencent.pb")) {//匹配微信通讯录
                           res = R.drawable.app_circle_wechat_phone;
                       } else if (pkgName.equals("cn.com.fetion")) {//匹配飞信
                           res = R.drawable.app_circle_feixin;
                       }
                       image.setImageResource(res);
                       image.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(final View v) {
                               //设置hook加解密所需的数据
                               final String account = StateParams.getStateParams().getEncryptAccount();
                               StateParams.getStateParams().setPkgName(pkgName);
                               //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. Start
                               StateParams.getStateParams().setAppName(bean.getAppName());
                               //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. Start
                               Log.d(TAG,"image.OnClickListener account "+account+" pkgName "+ pkgName);
                               if(IEncryptUtils.isOrNotSupportThrEncrypt(getContext(),pkgName)){
                                   getCommand().openThirdTransfer();
                               }else{
                                   //弹出对话框提示用户加密通道打开失败
                                   showOpenEncryptErrorDialogTips(animation,pkgName);
                               }

                               //关闭扇形菜单
                               /* animation.onFabClick(false);
                                //弹出错误提示框
                                showOpenEncryptErrorDialog(v.getContext());*/
//
//                                ClickArcItemUseCase useCase = new ClickArcItemUseCase(
//                                        getContext(), account, pkgName, bean.getAppName());
//                                useCase.execute(new ActomaUseCase.ActomaSub<Object>() {
//                                    @Override
//                                    public void onCompleted() {
//                                        //关闭扇形菜单
//                                        animation.onFabClick(false);
//                                    }
//
//                                    @Override
//                                    public void onNext(Object o) {
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        //关闭扇形菜单
//                                        animation.onFabClick(false);
//                                        //弹出错误提示框
//                                        showOpenEncryptErrorDialog(v.getContext());
//                                    }
//                                });
                           }
                       });
                       views.add(view);
                   }
               }
           }
       }
        //如果上述操作完成后集合仍为空 则证明没有可供展示的应用 那么久不添加盾牌的View
        if (!views.isEmpty()) {
            ImageView image = new ImageView(getContext());
            image.setImageResource(R.drawable.at_normal);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭扇形菜单
                    animation.onFabClick(false);
                }
            });
            views.add(0, image);
        }
    }

    /**
     * 显示打开加密服务错的对话框
     *
     *
     */
    private String SERVER_NO_DEVICE_ERROR = "设备不存在";
    @Override
    public void showOpenEncryptErrorDialog(String message) {
        final CustomDialog dialog = new CustomDialog(getContext());
        String showMessage = getStringRes(R.string.open_encrypt_error_message);
        if(TextUtils.isEmpty(message) == false){
            if(message.endsWith(CkmsException.CODE_EXIST_NOT_AUTH_DEVICE)) {
                showMessage = getStringRes(R.string.third_encrypt_version_low);
            }else if(message.compareTo(SERVER_NO_DEVICE_ERROR) == 0){
                showMessage = getStringRes(R.string.third_encrypt_push_error);
            }
        }
        dialog.setTitle(getStringRes(R.string.open_encrypt_sever_error_title))
                .setMessage(showMessage)
                .setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 弹出错误的对话框，提示用户策略查询失败
     *
     */
    public void showOpenEncryptErrorDialogTips(final ArcLayoutAnimation animation , String pkgName) {
        final CustomDialog dialog = new CustomDialog(getContext());
        dialog.setTitle(getStringRes(R.string.open_encrypt_sever_error_title))
                .setMessage(getStringRes(R.string.open_encrypt_error_message_2) + IEncryptUtils.getAppName(pkgName)
                        + getStringRes(R.string.open_encrypt_error_message_3))
                .setPositiveButton(getStringRes(R.string.text_ok_2).toString(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //关闭扇形菜单
                        animation.onFabClick(false);
                    }
                }).setCancelable(false).show();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_mainFrame);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

}
