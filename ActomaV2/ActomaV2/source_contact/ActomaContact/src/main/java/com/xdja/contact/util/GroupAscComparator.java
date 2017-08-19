package com.xdja.contact.util;

import com.xdja.contact.bean.Group;

import java.util.Comparator;

/**
 * Created by wanghao on 2016/3/1.
 * 群组列表显示群名称按照当前排序规则
 */
public class GroupAscComparator implements Comparator<Group> {

    @Override
    public int compare(Group left, Group right) {
        int result = left.getComparatorColumn().compareTo(right.getComparatorColumn());
        return result;
    }
}
