package com.xdja.presenter_mainframe.ui.uiInterface;


import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.ProductIntroduceCommand;

/**
 * Created by chenbing on 2015/7/29.
 */
public interface ProductIntroduceVu extends ActivityVu<ProductIntroduceCommand> {

    /**
     * 是否隐藏介绍动画上的 立即体验 按钮
     *
     * @param isHide
     */
    void isHideWelcomeBtn(boolean isHide);

    void setHideFlippoint(boolean  isHide);

    /**
     * 隐藏进度动画
     */
    void hideLoading();

//    /**
//     * 显示导航栏
//     *
//     * @param isShow
//     */
//    void showToolBar(boolean isShow);
}
