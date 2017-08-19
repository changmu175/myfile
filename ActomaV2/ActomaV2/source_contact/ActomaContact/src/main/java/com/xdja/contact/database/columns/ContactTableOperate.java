package com.xdja.contact.database.columns;

import com.xdja.contact.database.columns.error.TableErrorDepartment;
import com.xdja.contact.database.columns.error.TableErrorPush;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/9.
 * 联系人模块建立表结构 统一出口，方便主框架调用
 */
public class ContactTableOperate {

    public static List<String> buildSqls(){
        List<String> sqls = new ArrayList<String>();
        sqls.add(new TableAccountAvatar().buildTable());
        sqls.add(new TableActomaAccount().buildTable());
        sqls.add(new TableDepartment().buildTable());
        sqls.add(new TableDepartmentMember().buildTable());
        sqls.add(new TableEncryptRecord().buildTable());
        sqls.add(new TableExtended().buildTable());
        sqls.add(new TableFriend().buildTable());
        sqls.add(new TableFriendHistory().buildTable());
        sqls.add(new TableGroup().buildTable());
        sqls.add(new TableGroupMember().buildTable());
        sqls.add(new TableRequestInfo().buildTable());
        sqls.add(new TableErrorDepartment().buildTable());
        sqls.add(new TableErrorPush().buildTable());
        return sqls;
    }
}
