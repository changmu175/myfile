package com.xdja.imp.ui.vu;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IChatDetailCommand;

/**
 * Created by wanghao on 2015/11/23.
 */
public interface IChatDetailVu extends ActivityVu<IChatDetailCommand> {

    /**
     * 初始化列表
     *
     * @param adapter 适配器
     */
    void initListView(BaseAdapter adapter);

    /**
     * 获取展示的ListView
     * @return
     */
    ListView getDisplayList();

    /**
     * 停止界面刷新
     */
    void stopRefresh();

    /**
     * 设置当前选中项<br>
     * 建议使用 setListSelection2Last
     * @param selection
     */
    void setListSelection(int selection);
    //[S]add by lixiaolong on 20160902. fix bug 3158. review by gbc.
    void setDownRefreshSelection(int selection);
    //[E]add by lixiaolong on 20160902. fix bug 3158. review by gbc.
    /**
     * 跳转最后一项显示
     */
    void setListSelection2Last();

    /**
     * 重置底部view的状态
     */
    void restoreActionState();

    /**
     * 获取输入框内容
     * @return  输入框内容
     */
    String getInputString();

    /**
     * 弹出系统权限的Dialog
     */
    void showPermissionDialog();
    
    /**
     * 给输入框补充草稿
     * @param message
     */
    void setMessageText(String message);

    /**
     * add by zya@xdja.com,20161018
     * 重置闪信更多标志位
     */
    void restoreInputAction();

    /**
     * 听筒模式关闭
     * @param isOpen true 打开听筒模式 false 关闭听筒模式
     */
    void onReceiverModeChanged(boolean isOpen);
}
