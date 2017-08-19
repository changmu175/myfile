package com.xdja.imp.ui.vu;

import android.widget.ExpandableListView;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IHistoryFileListCommand;

/**
 * 项目名称：Blade
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/8 17:07
 * 修改人：xdjaxa
 * 修改时间：2016/12/8 17:07
 * 修改备注：
 */
public interface IHistoryFileListVu extends ActivityVu<IHistoryFileListCommand> {

    ExpandableListView getListView();

    /**
     * 显示空白页面
     * @param isShowEmpty
     */
    void showEmpty(boolean isShowEmpty);

    /**
     * 刷新顶部文件选择个数
     * @param num
     */
    void refreshSelectHint(int num);

    /**
     * 选择模式刷新
     * @return
     */
    boolean refreshUI();
}
