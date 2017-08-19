package com.xdja.frame.data.repository;

import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;

/**
 * <p>Summary:仓库实现类基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:50</p>
 */
public abstract class RepositoryImp<T> {

    protected T diskStore, cloudStore;

    public RepositoryImp(@StoreSpe(DiConfig.TYPE_DISK) T diskStore,
                         @StoreSpe(DiConfig.TYPE_CLOUD) T cloudStore) {
        this.diskStore = diskStore;
        this.cloudStore = cloudStore;
    }
}
