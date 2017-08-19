package com.xdja.imp.ui.vu;

import android.support.v4.view.PagerAdapter;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.presenter.command.IPicturePreviewCommand;

import java.util.List;

/**
 * Created by xdjaxa on 2016/6/16.
 */
public interface IPicturePreviewVu extends ActivityVu<IPicturePreviewCommand> {

    /**
     * 初始化列表
     * @param adapter 适配器
     */
    void initViewPager(PagerAdapter adapter);

    /**
     * 设置数据
     * @param dataSource
     */
    void setDataSource(List<LocalPictureInfo> dataSource);

    /**
     * 设置当前选中项
     * @param item
     */
    void setCurrentItem(int item);

    /**
     * 刷新title指示器内容
     */
    void refreshTitleIndicator();

    /**
     * 刷新图片选择个数指示器
     */
    void refreshSelectPictureIndicator();

    /**
     * 刷新当前位置的图片信息
     * @param position
     */
    void refreshLocalPictureInfo(int position);

    /**获取已经被选择的图片数量*/
    int getSelectedCount();

    //add by ycm 2016/9/5
    /**重置按钮的状态*/
    void resetSendStatus();
}
