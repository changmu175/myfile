package com.xdja.presenter_mainframe.cmd;


import android.view.View;
import android.widget.AdapterView;

import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by geyao on 2015/7/7.
 */
public interface SetHeadPortraitCommand extends Command {

    /**
     * 设置gridview item点击事件
     * @param parent
     * @param view      列表点击的item的视图
     * @param position  列表点击的item的下标
     * @param id
     */
    void setGridItemClick(AdapterView<?> parent, View view, int position, long id);
}
