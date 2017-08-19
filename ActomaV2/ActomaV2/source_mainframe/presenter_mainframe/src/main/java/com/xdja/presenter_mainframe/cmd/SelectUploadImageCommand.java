package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.presenter_mainframe.bean.SelectUploadImageBean;
import com.xdja.presenter_mainframe.presenter.adapter.SelectUploadImageAdapter;

import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public interface SelectUploadImageCommand extends Command {
    /**
     * 点击列表item
     *
     * @param list     数据集合
     * @param position item所对应的下标
     * @param adapter  适配器
     */
    void clickItem(List<SelectUploadImageBean> list, int position, SelectUploadImageAdapter adapter);

    /**
     * 点击确认按钮
     */
    void clickBtn();
}
