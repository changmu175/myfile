package com.xdja.imp.ui.vu;

import android.support.v4.view.PagerAdapter;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * <p>Author: leiliangliang </p>
 * <p>Date: 2016/11/29 10:46</p>
 * <p>Package: com.xdja.imp.ui.vu</p>
 * <p>Description: 文件浏览View定义接口</p>
 */
public interface IFileExplorerVu<P extends Command> extends ActivityVu<P> {

    /**
     * 加载进度是否可见设置
     *
     * @param visibility
     */
    void setProgressBarVisibility(int visibility);

    /**
     * 设置ViewPager适配器
     *
     * @param adapter viewpager适配器
     */
    void setFragmentAdapter(PagerAdapter adapter);

    /**
     * 设置ViewPager当前选择item
     *
     * @param item 选择item
     */
    void setViewPagerCurrentItem(int item);

    /**
     * 设置当前已选文件大小
     * @param size 文件大小
     */
    void setCurrentSelectedFileSize(long size);

    /**
     * 设置当前已选文件个数
     *
     * @param selectCount
     */
    void setCurrentSelectedFileCount(int selectCount);
}
