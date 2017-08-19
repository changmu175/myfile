package com.xdja.imp.data.utils;

import android.text.TextUtils;

import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2017/1/10.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MentionUtils {


    public static boolean isMention(List<TalkMessageBean> messageBeens) {
        boolean isMention = false;
        for (TalkMessageBean talkMessageBean : messageBeens) {
            if (talkMessageBean.isGroupMsg() && search(talkMessageBean.getContent(), ConstDef.PRONAME)) {
                isMention = true;
                break;
            }
        }
        return isMention;
    }

    public static boolean isMention(TalkMessageBean messageBeen) {
        boolean isMention = false;
            if (messageBeen.isGroupMsg() && search(messageBeen.getContent(), ConstDef.PRONAME)) {
                isMention = true;
            }
        return isMention;
    }

    public static boolean isMention(boolean isGroup, String  messageBeen) {
        boolean isMention = false;
        if (isGroup && search(messageBeen, ConstDef.PRONAME)) {
            isMention = true;
        } else {

        }
        return isMention;
    }
    /**
     * 先查找第一个空格的位置p，然后截取字符串s1，查找字符串s1中的最后一个@，
     *
     * @param world
     */
    public static boolean search(String world, String id) {
        int p1 = -1;// 空格左边最近的@的位置
        String temp = null;
        String account = null;
        String bank = "=";//空格的位置
        if (TextUtils.isEmpty(world) || TextUtils.isEmpty(id)) {
            return false;
        }
        int p2 = world.indexOf(bank);
        if (p2 != -1) {
            temp = world.substring(0, p2);
            p1 = temp.lastIndexOf("@");
            if (p1 != -1) {//@和空格匹配
                account = world.substring(p1 + 1, p2);
                if (id.equals(account)) {
                    return true;
                } else {
                    return search(world.substring(p2+1, world.length()), id);
                }
            } else {//只有空格，没有@，向后重新匹配
                return search(world.substring(p2+1, world.length()), id);
            }
        } else {
            return false;
        }
    }
}
