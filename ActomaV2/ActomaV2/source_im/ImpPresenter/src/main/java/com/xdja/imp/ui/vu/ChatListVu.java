package com.xdja.imp.ui.vu;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.presenter.command.ChatListCommand;
import com.xdja.imp.widget.ChatListPopupWindow;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.ui.vu</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:14:00</p>
 */
public interface ChatListVu extends FragmentVu<ChatListCommand> {
    /**
     * 弹出进度提示框
     */
    void showProgressDialog(String msgStr);

    /**
     * 取消弹框
     */
    void dismissDialog();

    /**
     * 初始化列表
     *
     * @param adapter 适配器
     */
    void initListView(BaseAdapter adapter);

    /**
     * 弹出待选框
     *
     * @param talkListBean 附加参数
     */
    void popuOptionWindow(TalkListBean talkListBean,
                          ChatListPopupWindow.PopupWindowEvent<TalkListBean> event);

    void dismissPopuDialog();
    /**
     * 获取展示的ListView
     * @return
     */
    ListView getDisplayList();

    /**
     *
     * 加载自己的图像，此图像不显示，为了进入会话详情界面快速加载图像
     */
    void loadSelfImage();

    void changeViewSate(int state);
}
