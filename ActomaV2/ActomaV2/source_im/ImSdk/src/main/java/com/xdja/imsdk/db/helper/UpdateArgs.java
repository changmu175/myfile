package com.xdja.imsdk.db.helper;

import android.content.ContentValues;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/30 16:07                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class UpdateArgs {
    private ContentValues values;
    private String[] whereClause;
    private String[] whereArgs;
    private String name;

    public UpdateArgs() {
    }

    public UpdateArgs(String name) {
        this.name = name;
    }

    public ContentValues getValues() {
        return values;
    }

    public void setValues(ContentValues values) {
        this.values = values;
    }

    public String[] getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String[] whereClause) {
        this.whereClause = whereClause;
    }

    public String[] getWhereArgs() {
        return whereArgs;
    }

    public void setWhereArgs(String[] whereArgs) {
        this.whereArgs = whereArgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
