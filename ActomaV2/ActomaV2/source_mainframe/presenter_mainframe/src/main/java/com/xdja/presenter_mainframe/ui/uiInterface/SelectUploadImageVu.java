package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.bean.SelectUploadImageBean;
import com.xdja.presenter_mainframe.cmd.SelectUploadImageCommand;
import com.xdja.presenter_mainframe.presenter.adapter.SelectUploadImageAdapter;

import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public interface SelectUploadImageVu extends ActivityVu<SelectUploadImageCommand> {
    /**
     * 初始化选择图片列表
     *
     * @param list    数据集合
     * @param adapter 适配器
     */
    void initGridView(List<SelectUploadImageBean> list, SelectUploadImageAdapter adapter);

    /**
     * 是否显示确定按钮
     *
     * @param isShow 是否显示
     */
    void isShowBtn(boolean isShow);
}
