package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.FeedBackCommand;
import com.xdja.presenter_mainframe.presenter.adapter.UploadImageAdapter;

import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public interface VuFeedBack extends ActivityVu<FeedBackCommand> {
    /**
     * 初始化问题截图gridview
     *
     * @param adapter 适配器
     * @param list    列表数据集合
     */
    void initGridView(UploadImageAdapter adapter, List<String> list);

    /**
     * 是否显示提交成功页面
     *
     * @param isShow 是否显示
     */
    void isShowSubmitSuccessLayout(boolean isShow);

    /**
     * 显示等待框
     *
     * @param msg 显示信息
     */
    void showDialog(String msg);

    /**
     * 关闭等待框
     */
    void closeDialog();
}
