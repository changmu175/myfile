package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.google.gson.internal.LinkedHashTreeMap;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by XDJA_XA on 2015/7/29.
 *
 * modify wanghao 2016-02-26
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161222.
 */
public abstract class BaseSelectAdapter extends BaseAdapter {

    protected Context context;

    protected ISelectCallBack selectedCallBack;

    protected static Map<String,String> EXISTE_ACCOUNT_MAP = new LinkedHashTreeMap<>();//按照选择的顺序

    protected static Map<String,Object> SELECTED_ACCOUNT_MAP = new LinkedHashTreeMap<>();


    public BaseSelectAdapter(Context context,ISelectCallBack callBack,List<String> existedMemberAccounts){
        this.context = context;
        this.selectedCallBack = callBack;
        if(!ObjectUtil.collectionIsEmpty(existedMemberAccounts)){
            for(String account : existedMemberAccounts){
                EXISTE_ACCOUNT_MAP.put(account, account);
            }
        }
    }

    protected int calculateSelected(){
        return SELECTED_ACCOUNT_MAP.size();
    }

    protected int calculateAccounts(){
        return EXISTE_ACCOUNT_MAP.size() + SELECTED_ACCOUNT_MAP.size();
    }

    // Task 2632 [Begin]
    public String shareMark = null;
    public void setShareMark(String mark) {
        this.shareMark = mark;
    }

    public String getShareMark() {
        return shareMark;
    }
    // modified by ycm 2016/12/22:[文件转发或分享]设置或者获取checkbox的状态[start]
    private boolean isDisplay = false;

    public void setCheckBoxStatus(boolean noDisplay) {
        this.isDisplay = noDisplay;
    }

    public boolean getCheckBoxStatus() {
        return !this.isDisplay;
    }
    // modified by ycm 2016/12/22:[文件转发或分享]设置或者获取checkbox的状态[end]
    // Task 2632 [End]

    //移除已经选择的账号
    protected void removeSelectedAccount(String account){
        SELECTED_ACCOUNT_MAP.remove(account);
        selectedCallBack.selectedCallback(SELECTED_ACCOUNT_MAP);
    }

    //保存已经选择的账号
    protected void putSelectedAccount(String account,Object object){
        SELECTED_ACCOUNT_MAP.put(account, object);
        selectedCallBack.selectedCallback(SELECTED_ACCOUNT_MAP);
    }

    //获取已经选中的对象
    protected Object getSelected(String account){
        return SELECTED_ACCOUNT_MAP.get(account);
    }

    public static void clear(){
        EXISTE_ACCOUNT_MAP.clear();
        SELECTED_ACCOUNT_MAP.clear();
    }

}
