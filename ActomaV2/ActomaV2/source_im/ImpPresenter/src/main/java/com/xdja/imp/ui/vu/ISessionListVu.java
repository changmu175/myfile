package com.xdja.imp.ui.vu;

import android.app.Activity;
import android.content.Intent;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.presenter.command.SessionListCommand;
import com.xdja.imp.presenter.adapter.ChooseIMSessionAdapterPresenter;
import com.xdja.imp.presenter.adapter.SearchResultAdapter;
import com.xdja.imp.widget.SharePopWindow;

import java.util.List;
import java.util.Map;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，分享界面会话选择列表视图接口
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface ISessionListVu extends ActivityVu<SessionListCommand> {
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
     *
     * 加载自己的图像，此图像不显示，为了进入会话详情界面快速加载图像
     */
    void loadSelfImage();

    /**
     * 弹出待选框
     *
     * @param talkListBean 附加参数
     */
    void sharePopuOptionWindow(List<TalkListBean> talkListBean,
                               SharePopWindow.PopWindowEvent<TalkListBean> event,
                               Map<String, String> contactInfo, Intent intent);

    void handOutSharePopuOptionWindow(List<TalkListBean> talkListBeans,
                               SharePopWindow.PopWindowEvent<TalkListBean> event,
                               Map<String, List<String>> contactInfo, Intent intent);
     void showSelectPopWindow(Activity activity);

    void dismissPopuDialog();

    /**
     * 设置ChooseIMSessionAdapterPresenter
     * @param chooseIMSessionAdapterPresenter
     */
    void setChooseIMSessionAdapter(ChooseIMSessionAdapterPresenter chooseIMSessionAdapterPresenter);

    /**
     * 设置SearchResultAdapter
     * @param searchResultAdapterAdapter
     */
    void setLocalSearchAdapter(SearchResultAdapter searchResultAdapterAdapter);

    void setType(boolean isFile);// modified by ycm 2016/12/22:[文件转发或分享]

    boolean getType();// modified by ycm 2016/12/29:[文件转发或分享]
}
