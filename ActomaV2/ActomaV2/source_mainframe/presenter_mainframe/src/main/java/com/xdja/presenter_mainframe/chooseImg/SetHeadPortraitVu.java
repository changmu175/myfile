package com.xdja.presenter_mainframe.chooseImg;


import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by geyao on 2015/7/7.
 */
public interface SetHeadPortraitVu extends ActivityVu<SetHeadPortraitCommand> {
    /**
     * 设置gridview适配器
     *
     * @param adapter 设置头像列表适配器
     */
    void setGridAdapter(SetHeadPortraitAdapter adapter);
}
