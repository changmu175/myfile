package com.xdja.presenter_mainframe.chooseImg;


import android.graphics.Bitmap;

import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by geyao on 2015/7/7.
 */
public interface CutImageCommand extends Command {
    //[S]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
//    /**
//     * 裁剪图片
//     *
//     * @param imageView 图片
//     * @param x         x坐标
//     * @param y         y坐标
//     * @param width     宽度
//     * @param height    高度
//     */
//    void cutImage(ImageView imageView, int x, int y, int width, int height);
    void cutImage(Bitmap bmp);
    //[E]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
}
