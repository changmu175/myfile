package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.NewEncryptCommand;

import java.util.List;

/**
 *
 * Created by geyao on 2015/11/23.
 */
public interface NewEncryptVu extends ActivityVu<NewEncryptCommand> {
    /**
     * 初始化第三方加密列表数据
     *
     * @param list                 支持的应用列表数据
     */
    void initView(List<EncryptAppBean> list);

    /**
     * 设置列表适配器
     *
     * @param list 数据
     */
    void setListAdapter(List<EncryptAppBean> list);
}
