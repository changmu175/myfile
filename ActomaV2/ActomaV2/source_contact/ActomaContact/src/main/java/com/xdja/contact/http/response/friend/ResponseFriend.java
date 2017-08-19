package com.xdja.contact.http.response.friend;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.http.response.BaseResponse;

/**
 * Created by wanghao on 2015/7/23.
 *
 * 解析返回的好友关系数据
 */
public class ResponseFriend extends BaseResponse {

    private String account;
    /**
     * 更新标示
     */
    private String updateSerial;
    /**
     * 增量状态: -1 已经删除，0-正常，1-修改
     */
    private String state;
    /**
     * 备注
     */
    private String remark;
    /**
     * 备注简拼
     */
    private String remarkPy;
    /**
     * 备注全拼
     */
    private String remarkPinyin;

    //是否是好友发起方，0-是，1-否
    private String initiative;


    public Friend convert2Friend(){
        Friend friend = new Friend();
        friend.setAccount(getAccount());
        friend.setUpdateSerial(getUpdateSerial());
        friend.setState(getState());
        //Start:add by wal@xdja.com for 4887
        if(getState().equals("-1")) {
            friend.setIsShow("0");
            friend.setRemark("");
            friend.setRemarkPy("");
            friend.setRemarkPinyin("");
        } else {
            friend.setIsShow("1");
            friend.setRemark(getRemark());
            friend.setRemarkPy(getRemarkPy());
            friend.setRemarkPinyin(getRemarkPinyin());
        }
        friend.setType("1");
//        friend.setRemark(getRemark());
//        friend.setRemarkPy(getRemarkPy());
//        friend.setRemarkPinyin(getRemarkPinyin());
        //End:add by wal@xdja.com for 4887
        friend.setInitiative(getInitiative());
        return friend;
    }





    public String getInitiative() {
        return initiative;
    }

    public void setInitiative(String initiative) {
        this.initiative = initiative;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkPy() {
        return remarkPy;
    }

    public void setRemarkPy(String remarkPy) {
        this.remarkPy = remarkPy;
    }

    public String getRemarkPinyin() {
        return remarkPinyin;
    }

    public void setRemarkPinyin(String remarkPinyin) {
        this.remarkPinyin = remarkPinyin;
    }
}
