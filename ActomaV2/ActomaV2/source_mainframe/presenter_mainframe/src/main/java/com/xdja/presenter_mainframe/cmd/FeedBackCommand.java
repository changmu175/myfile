package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.presenter_mainframe.presenter.adapter.UploadImageAdapter;

import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public interface FeedBackCommand extends Command {
    /**
     * 选择上传图片
     *
     * @param size 当前已选择的图片集合size
     */
    void selectUploadImages(int size);

    /**
     * 显示图片大图
     *
     * @param imagePath 图片地址
     */
    void showBigImage(String imagePath);

    /**
     * 是否删除图片对话框
     *
     * @param adapter  适配器
     * @param list     图片集合
     * @param position 图片所在集合下标
     */
    void isDeleteImage(UploadImageAdapter adapter, List<String> list, int position);

    /**
     * 提交
     *
     * @param opinion 问题和意见
     * @param mobile  联系电话
     */
    void submit(String opinion, String mobile);
}
