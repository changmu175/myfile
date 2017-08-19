package com.xdja.frame.presenter.mvp.view;

import android.support.annotation.NonNull;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.Vu;


/**
 * <p>Summary:适配器View通用接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.frame.mvp.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:11:44</p>
 */
public interface AdapterVu<P extends Command,D> extends Vu<P> {
    /**
     * 适配器相关的View被新创建出来
     */
    void onViewCreated();

    /**
     *  适配器相关的View被重用
     */
    void onViewReused();

    /**
     * 绑定数据源到View上
     */
    void  bindDataSource(int position, @NonNull D dataSource);
}
