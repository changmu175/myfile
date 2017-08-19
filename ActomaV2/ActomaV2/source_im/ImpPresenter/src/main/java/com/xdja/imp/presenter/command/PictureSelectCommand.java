package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.LocalPictureInfo;
import java.util.Map;

/**
 * Created by xdjaxa on 2016/6/20.
 */
public interface PictureSelectCommand extends Command {

    /**
     * 添加数据
     * @param pictureInfos
     */
    void setData(Map<String ,LocalPictureInfo> pictureInfos);

    /**
     * 图片选择监听器
     */
    void notifySelectedChanged();


    /**
     * 开始预览图片
     * @param index 当前开始预览图片索引
     */
    void startToPreviewPicture(int index);

    /**
     * 获取当前已经选择的图片数
     * */
    int getSelectedCount();

    /**
     * 当前图片是否被选择
     * */
    boolean isPicSelected(LocalPictureInfo info);

    void startToChatDetailPreview(String name);

    void setLoadingProgress(boolean bLoading);
}
