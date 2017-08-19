package com.xdja.presenter_mainframe.chooseImg;


import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by geyao on 2015/7/7.
 */
public interface CutImageVu extends ActivityVu<CutImageCommand> {
    /**
     * 显示上级传递的图片
     *
     * @param url 图片地址
     */
    void setImage(String url);
    //void freeSourceBitmap();
}
