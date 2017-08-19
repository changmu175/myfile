package com.xdja.imp.ui.vu;

import android.widget.BaseAdapter;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IPictureSelectCommand;

/**
 * Created by xdjaxa on 2016/6/16.
 */
public interface IPictureSelectVu extends ActivityVu<IPictureSelectCommand> {


    /**
     * 初始化列表
     * @param adapter 适配器
     */
    void initListView(BaseAdapter adapter);

    /**
     * 刷新发送按钮上面已选图片指示器信息
     * @param selectCnt 已选图片个数
     */
    void refreshSelectPictureIndicator(int selectCnt);

    /*
    * 本地图片加载完成
    */
    void localPicLoadFinish(boolean isSelectEmpty);

    /**
     * 显示无图片时的界面
     */
    void showEmptyImage();

    /**
     * 隐藏预览与发送按钮
     */
    void hidePreAndSendBtn();

    /**
     * 重置发送状态的相关信息（主要用在发送失败后，需要进行重发，重置一些状态信息）
     */
    void resetSendStatus();
}
