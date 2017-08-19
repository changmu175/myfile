package com.xdja.data_mainframe.repository;

import com.google.gson.Gson;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.frame.data.repository.RepositoryImp;

import java.util.Set;

import javax.inject.Named;

/**
 * <p>Summary:可以解析服务端响应的仓库类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:21:33</p>
 */
public class ExtRepositoryImp<T> extends RepositoryImp<T> {

    protected Set<Integer> errorStatus;

    protected Gson gson;

    public ExtRepositoryImp(@StoreSpe(DiConfig.TYPE_DISK) T diskStore,
                            @StoreSpe(DiConfig.TYPE_CLOUD) T cloudStore,
                            @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                            Gson gson) {
        super(diskStore, cloudStore);
        this.errorStatus = errorStatus;
        this.gson = gson;
    }
}
