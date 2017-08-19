package com.xdja.contact.util;

import com.xdja.contact.bean.Friend;

import java.util.Comparator;

public class FriendAscComparator implements Comparator<Friend> {

    /**这里完全信任 getComparatorColumn();{优先级依次是: 备注; 昵称 ; 帐号(账号一定存在所以这里我们不需要判空);}
     * @param o1
     * @param o2
     * @return
     */
	@Override
    public int compare(Friend o1, Friend o2) {
        int result = o1.getComparatorColumn().compareTo(o2.getComparatorColumn());
        return result;
    }

}