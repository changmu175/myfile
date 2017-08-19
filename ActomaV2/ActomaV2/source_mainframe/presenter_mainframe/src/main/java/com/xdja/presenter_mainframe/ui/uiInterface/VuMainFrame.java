package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.comm.event.TabTipsEvent;
import com.xdja.contact.view.arclayout.ArcLayoutAnimation;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.MainFrameCommand;
import com.xdja.presenter_mainframe.widget.TabFragmentAdapter;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.ui.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:17:04</p>
 */
public interface VuMainFrame extends ActivityVu<MainFrameCommand> {
    /**
     * 设置ViewPager的适配器
     *
     * @param adapter 适配器
     */
    void setFragmentAdapter(TabFragmentAdapter adapter);


    /**
     * 更新消息Tab上的红点显示内容
     *
     * @param text 要显示的内容
     */
    void updateMsgTabTips(CharSequence text);

    /**
     * 清空消息Tab上的红点
     */
    void clearMsgTabTips();

    /**
     * 更新通话Tab上的红点显示内容
     *
     * @param text 要显示的内容
     */
    void updatePhoneTabTips(CharSequence text);

    /**
     * 清空通话Tab上的红点
     */
    void clearPhoneTabTips();

    /**
     * 更新联系人Tab上的红点显示内容
     *
     * @param text 要显示的内容
     */
    void updateContactTabTips(CharSequence text);

    /**
     * 清空联系人Tab上的红点
     */
    void clearContactTabTips();

    /**
     * 更新更多Tab上的红点显示内容
     *
     * @param text 要显示的内容
     */
    void updateMoreTabTips(CharSequence text);

    /**
     * 清空更多Tab上的红点
     */
    void clearMoreTabTips();


    /**
     * 设置显示的Fragment
     *
     * @param index 要显示的Fragment索引
     */
    void setSelectedFragment(@TabTipsEvent.POINT_DEF int index);

    /**
     * 根据是否有更新刷新界面
     *
     * @param isHaveNew
     */
    void freshUpdateNew(boolean isHaveNew);
    /**
     * 设置扇形菜单动画
     *
     * @return
     */
    ArcLayoutAnimation getArcLayoutAnimation();


    /**
     * 只获取这个扇形菜单界面
     *
     * @return
     */
    ArcLayoutAnimation getArcView();

    /**
     * 获取扇形菜单是否展开
     */
    boolean getCirCleMenuIsOpen( );

    /**
     * 关闭扇形菜单
     */
    void closeCirCleMenu();

    /**
     * 打开三方通道错误
     */
    //tangsha@xdja.com 2016-08-09 add. for open third transfer. review by self. start
    void showOpenEncryptErrorDialog(String message);
    //tangsha@xdja.com 2016-08-09 add. for open third transfer. review by self. End

}
