package com.xdja.contact.convert;

import com.xdja.contact.bean.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xnn on 2017/3/14.
 */
public class DepartmentConvert {

    public static List<String> convertDepartMemberWorkId(List<Member> members){
        List<String> departMemberWorkIdList = new ArrayList<>();
        for(Member member :members){
            departMemberWorkIdList.add(member.getWorkId());
        }
        return departMemberWorkIdList;
    }

    public static List<String> convertDepartMemberAccount(List<Member> members){
        List<String> departMemberAccountList = new ArrayList<>();
        for(Member member :members){
            departMemberAccountList.add(member.getAccount());
        }
        return departMemberAccountList;
    }

}
