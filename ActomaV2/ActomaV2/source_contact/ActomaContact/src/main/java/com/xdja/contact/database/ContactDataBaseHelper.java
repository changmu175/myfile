package com.xdja.contact.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.database.columns.ContactTableOperate;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * 联系人模块 数据库操作对象
 */
public class ContactDataBaseHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME = "_actom_contact.db";

    static int DATABASE_VERSION = 1;

    private static ContactDataBaseHelper instance;

    protected ContactDataBaseHelper(String databaseName){
        super(ActomaController.getApp(), databaseName, null, DATABASE_VERSION);
    }

    public synchronized static ContactDataBaseHelper getInstance(){
        String account = ContactUtils.getCurrentAccount();
        int startIndex = DATABASE_NAME.indexOf("_");
        String prefix = DATABASE_NAME.substring(0,startIndex);
        if(!ObjectUtil.stringIsEmpty(account) && account.equals(prefix)){
            LogUtil.getUtils().i("用户帐号一致--不再重新创建helper对象, account="+account);
            if(instance == null){
                instance = new ContactDataBaseHelper(DATABASE_NAME);
            }
        }else{
            if(ObjectUtil.stringIsEmpty(account)){
                LogUtil.getUtils().e("Actoma contact ContactDataBaseHelper getInstance:创建数据库，但是当前登录用户为空");
                throw new NullPointerException("创建数据库，但是当前登录用户为空");
            }else{
                LogUtil.getUtils().i("用户切换了帐号--重新创建数据库helper对象, account="+account+", prefix="+prefix);
                DATABASE_NAME = account+DATABASE_NAME.substring(startIndex,DATABASE_NAME.length());
                instance = new ContactDataBaseHelper(DATABASE_NAME);
            }
        }
        return instance;
    }

    /*public ContactDataBaseHelper(){
        super(ActomaController.getApp(), ContactUtils.getCurrentAccount()+DATABASE_NAME, null, DATABASE_VERSION);
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.getUtils().i("helper-----创建表对象");
        createTables(db);
    }

    private void createTables(SQLiteDatabase db){

        List<String> array = ContactTableOperate.buildSqls();
        for(String sql : array){
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //切换账号的时候调用删除数据库
    public void deleteDatabase(){
        ActomaController.getApp().deleteDatabase(DATABASE_NAME);
    }
}
