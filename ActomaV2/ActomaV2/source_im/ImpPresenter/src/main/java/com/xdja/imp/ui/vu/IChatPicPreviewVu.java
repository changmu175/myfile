package com.xdja.imp.ui.vu;

import android.support.v4.view.PagerAdapter;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IChatPicPreviewCommand;

/**
 * Created by guorong on 2016/7/6.
 */
public interface IChatPicPreviewVu extends ActivityVu<IChatPicPreviewCommand> {
    /**
     * 初始化列表
     * @param adapter 适配器
     */
    void initViewPager(PagerAdapter adapter);

    /**
     * 设置当前选中项
     * @param item
     */
    void setCurrentItem(int item);
}
