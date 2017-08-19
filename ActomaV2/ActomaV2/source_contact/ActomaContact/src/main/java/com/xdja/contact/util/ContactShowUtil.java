package com.xdja.contact.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**联系人界面显示工具类
 * Created by yangpeng on 2015/12/21.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ContactShowUtil {

    /**
     * 设置匹配到的关键字变成红色
     * @param nameOrNumber
     * @return
     */
    public static Spanned getSpanned(String nameOrNumber,String searchKey,Context context){
        if (TextUtils.isEmpty(nameOrNumber)) {
            return Html.fromHtml(nameOrNumber);
        }
        int index = nameOrNumber.indexOf(searchKey);
        if (index == -1) {
            return Html.fromHtml(nameOrNumber);
        }
        int keyLength = searchKey.length();
        String start = nameOrNumber.substring(0, index);
        String middle = nameOrNumber.substring(index, index + keyLength);
        String end = nameOrNumber.substring(index + keyLength, nameOrNumber.length());
        StringBuffer sb = new StringBuffer();
        sb.append(TextUtils.htmlEncode(start));
        sb.append("<font color = ");
        sb.append(context.getResources().getColor(R.color.high_light_color));
        sb.append(">");
        sb.append(TextUtils.htmlEncode(middle));
        sb.append("</font>");
        sb.append(TextUtils.htmlEncode(end));
        return Html.fromHtml(sb.toString());
    }

    /**
     * 将排序好的好友列表，按照首字母分类，并增加字母序号条目
     * @param data
     * @return
     */
    public static List<Friend> comparatorDataSource(List<Friend> data) {
        List<Friend> showData = new ArrayList<Friend>();
        String preCode = null;
        Friend anTongFriend = null;
        int anTongTeameIndex = -1;
        for (Friend contact : data) {
            if (contact != null) {
                String name = AlphaUtils.getAlpha(contact.getComparatorColumn());
                if (TextUtils.isEmpty(preCode)) {
                    preCode = name;
                    if (name.equals(AlphaUtils.JING)) {
                        Friend contactBeanTemp = new Friend();
                        contactBeanTemp.setViewType(Friend.ALPHA);
                        contactBeanTemp.setIndexChar(AlphaUtils.JING);
                        showData.add(contactBeanTemp);
                    } else {
                        Friend contactBeanTemp = new Friend();
                        contactBeanTemp.setViewType(Friend.ALPHA);
                        contactBeanTemp.setIndexChar(name);
                        showData.add(contactBeanTemp);
                    }

                } else if (!(preCode.equals(name))) {
                    if(preCode.equals("A")){
                        anTongTeameIndex = showData.size();
                    }
                    preCode = name;
                    Friend contactBeanTemp = new Friend();
                    contactBeanTemp.setViewType(Friend.ALPHA);
                    contactBeanTemp.setIndexChar(name);
                    showData.add(contactBeanTemp);
                }
                contact.setIndexChar(name);
                contact.setViewType(Friend.CONTACT_ITEM);
				/*[S]tangsha add@20161202 for 6552*/
                if(contact.getAccount().compareTo(Friend.ANTONG_TEAM_ACCOUNT) == 0){
                    anTongFriend = contact;
                }else {
                    showData.add(contact);
                }
            }
        }
        if(anTongFriend != null) {
            if (anTongTeameIndex == -1) {
                showData.add(anTongFriend);
            }else{
                showData.add(anTongTeameIndex,anTongFriend);
            }
        }else{
            LogUtil.getUtils().e("ContactShowUtil comparatorDataSource anTongFriend is null!!!");
        }
        /*[E]tangsha add@20161202 for 6552*/
        return showData;
    }

    /**
     * 按照昵称，将好友分为两类（特殊、普通），并排序
     * @param data
     * @return
     */
    public static List<Friend> dataSeparate(List<Friend> data) {
        List<Friend> specfiNumric = new ArrayList<>();
        List<Friend> normalArray = new ArrayList<>();
        for (Friend bean : data) {
            String key = bean.getComparatorColumn();
            char c = key.trim().substring(0, 1).charAt(0);
            Pattern pattern = Pattern.compile("^[A-Za-z]+$");
            if (!pattern.matcher(c + "").matches()) {
                specfiNumric.add(bean);
            } else {
                normalArray.add(bean);
            }
        }
        Collections.sort(specfiNumric, new FriendAscComparator());
        Collections.sort(normalArray, new FriendAscComparator());
        List<Friend> beans = new ArrayList<Friend>();
        beans.addAll(normalArray);
        beans.addAll(specfiNumric);
        return beans;
    }

    // Task 2632 [Begin]
    /**
     * 按照昵称，将好友分为两类（特殊、普通），并排序
     * @param data
     * @return
     */
    public static List<Group> groupDataSeparate(List<Group> data) {
        List<Group> specfiNumric = new ArrayList<>();
        List<Group> normalArray = new ArrayList<>();
        for (Group bean : data) {
            String key = bean.getComparatorColumn();
            if (!TextUtils.isEmpty(key)) {
                char c = key.trim().substring(0, 1).charAt(0);
                Pattern pattern = Pattern.compile("^[A-Za-z]+$");
                if (!pattern.matcher(c + "").matches()) {
                    specfiNumric.add(bean);
                } else {
                    normalArray.add(bean);
                }
            } else {
                specfiNumric.add(bean);
            }
        }
        Collections.sort(specfiNumric, new GroupAscComparator());
        Collections.sort(normalArray, new GroupAscComparator());
        List<Group> beans = new ArrayList<Group>();
        beans.addAll(normalArray);
        beans.addAll(specfiNumric);
        return beans;
    }

    /**
     * 将排序好的好友列表，按照首字母分类，并增加字母序号条目
     * @param data
     * @return
     */
    public static List<Group> GroupComparatorDataSource(List<Group> data) {
        List<Group> showData = new ArrayList<Group>();
        String preCode = null;
        for (Group contact : data) {
            if (contact != null) {
                String name = AlphaUtils.getAlpha(contact.getComparatorColumn());
                if (TextUtils.isEmpty(preCode)) {
                    preCode = name;
                    if (name.equals(AlphaUtils.JING)) {
                        Group contactBeanTemp = new Group();
                        contactBeanTemp.setViewType(Friend.ALPHA);
                        contactBeanTemp.setIndexChar(AlphaUtils.JING);
                    } else {
                        Group contactBeanTemp = new Group();
                        contactBeanTemp.setViewType(Group.ALPHA);
                        contactBeanTemp.setIndexChar(name);
                    }

                } else if (!(preCode.equals(name))) {
                    preCode = name;
                    Group contactBeanTemp = new Group();
                    contactBeanTemp.setViewType(Group.ALPHA);
                    contactBeanTemp.setIndexChar(name);
                }
                contact.setIndexChar(name);
                showData.add(contact);
            }
        }
        return showData;
    }

   static class GroupAscComparator implements Comparator<Group> {

        /**这里完全信任 getComparatorColumn();{优先级依次是: 备注; 昵称 ; 帐号(账号一定存在所以这里我们不需要判空);}
         * @param o1
         * @param o2
         * @return
         */
        @Override
        public int compare(Group o1, Group o2) {
            int result = o1.getComparatorColumn().compareTo(o2.getComparatorColumn());
            return result;
        }

    }
    // Task 2632 [End]
}
