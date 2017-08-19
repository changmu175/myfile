package com.xdja.contact.bean.enumerate;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.R;

/**
 * Created by wanghao on 2015/7/8.
 */
public enum FriendHistoryState {

    WAIT_ACCEPT(1, ActomaController.getApp().getString(R.string.contact_friend_state_wait)),//modify by wal@xdja.com for string 等待验证

    ALREADY_FRIEND(2,ActomaController.getApp().getString(R.string.contact_friend_state_add)),//modify by wal@xdja.com for string 已添加

    ACCEPT(4,ActomaController.getApp().getString(R.string.contact_friend_state_accepte));//modify by wal@xdja.com for string 接受

    private int key;

    private String description;

    FriendHistoryState(int key,String description){
        this.key = key;
        this.description = description;
    }

    public int getKey(){
        return key;
    }


    public static FriendHistoryState getState(int key){

        FriendHistoryState messageType = null;

        for(FriendHistoryState type : FriendHistoryState.values()){

            if(type.getKey() == key){

                messageType = type;

                break;
            }
        }
        return messageType;
    }

}
