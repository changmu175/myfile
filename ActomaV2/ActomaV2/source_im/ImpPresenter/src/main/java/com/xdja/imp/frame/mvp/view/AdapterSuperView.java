package com.xdja.imp.frame.mvp.view;

import android.support.annotation.NonNull;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.SuperView;
import com.xdja.dependence.uitls.LogUtil;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.frame.mvp.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:11:56</p>
 */
public abstract class AdapterSuperView<T extends Command,D>
                                            extends SuperView<T> implements AdapterVu<T,D> {
    protected D dataSource;

    @Override
    public void bindDataSource(int position,@NonNull D dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onViewCreated() {
        LogUtil.getUtils().i("----------onViewCreated---------");
    }

    @Override
    public void onViewReused() {
        LogUtil.getUtils().i("----------onViewReused---------");
    }
}
