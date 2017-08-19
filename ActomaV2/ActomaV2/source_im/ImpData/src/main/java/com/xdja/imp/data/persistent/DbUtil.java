package com.xdja.imp.data.persistent;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.imp.data.cache.UserEntity;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.di.annotation.UserScope;
import com.xdja.xutils.DbUtils;

import javax.inject.Inject;

/**
 * <p>Summary:数据库操作工具</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.persistent</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:13:39</p>
 */
@UserScope
public class DbUtil {
    /**
     * xutils操作句柄
     */
    private DbUtils dbUtils;
    /**
     * 数据库名称
     */
    private final String DBNAME = "mxdb.db";
    /**
     * 数据库路径
     */
    //private String DBDIR = ；//"/data/data/com.xdja.actoma/XdjaIm/";




    private String databasePath;

    @Inject
    public DbUtil(@Scoped(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        final int DBVERSION = 0;// modified by ycm for lint 2017/02/16
        String DBDIR;
        UserEntity userEntity = new UserEntity();
        String account = userEntity.getAccount();
        if (TextUtils.isEmpty(account)) {
            AccountBean accountBean = AccountServer.getAccount();

            if (accountBean != null) {// modified by ycm for lint 2017/02/16
                account = accountBean.getAccount();
            }
        }

        DBDIR = context.getFilesDir().getAbsolutePath();
        databasePath = DBDIR + "/" + account;
        dbUtils = DbUtils.create(context, databasePath, DBNAME, DBVERSION, new DbUtils
                .DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils dbUtils, int i, int i1) {

            }
        });
    }

    /**
     * 获取XutilsDB的操作句柄
     * @return
     */
    public DbUtils get(){
        return dbUtils;
    }

    public void closeDbutil() {
        dbUtils.close(databasePath, DBNAME);
    }
}
