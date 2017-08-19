package com.xdja.imsdk.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkFileConstant;

import java.io.File;
import java.io.IOException;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk数据库上下文                  <br>
 * 创建时间：2016/11/25 19:55                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ImSdkContext extends ContextWrapper {
    private Context context;
    private String account;

    public ImSdkContext(Context context, String account) {
        super(context);
        this.context = context;
        this.account = account;
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象
     * @param name 数据库名称
     * @return 数据库路径
     */
    @Override
    public File getDatabasePath(String name) {
        File dir;
        File file;

        if (context == null || TextUtils.isEmpty(name)) {
            return null;
        }
        String dirBuilder = context.getFilesDir().getParent() +
                File.separator +
                ImSdkFileConstant.PARENT_FILE_PATH +
                File.separator +
                account;

        dir = new File(dirBuilder);
        file = new File(dir, name);

        boolean mkDir = false;
        if (!dir.exists()) {
            mkDir = dir.mkdirs();
        }

        if (mkDir && !file.exists()) {
            try {
                if (file.createNewFile()) {
                    return file;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result;
        if (context == null || TextUtils.isEmpty(name)) {
            return null;
        }
        result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result;
        if (context == null || TextUtils.isEmpty(name)) {
            return null;
        }
        result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }
}
