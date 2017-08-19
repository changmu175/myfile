package com.xdja.contact.bean.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/10/23.
 */
public class CommonDetailDto implements Parcelable {

    private ActomaAccount actomaAccount;

    private Avatar avatar;

    private Friend friend;

    private Member member;

    private Department department;

    private ResponseActomaAccount serverActomaAccount;

    //本地是否存在
    private boolean isExist;
    //存在集团
    private boolean isMember;
    //存在好友关系
    private boolean isFriend;
    //2016-04-14 添加该字段，方便联系人详情界面点击添加好友获取账号出口，
    private String account;

    private String alias;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getShowName(){
        if(!ObjectUtil.objectIsEmpty(friend) && friend.isShow()){
            if(!ObjectUtil.stringIsEmpty(friend.getRemark())){
                return friend.getRemark();
            }
        }
        if(!ObjectUtil.objectIsEmpty(member)){
            if(!ObjectUtil.stringIsEmpty(member.getName())){
                return member.getName();
            }
        }
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){
            if(!ObjectUtil.stringIsEmpty(actomaAccount.getNickname())){
                return actomaAccount.getNickname();
            } else if(!ObjectUtil.stringIsEmpty(actomaAccount.getAlias())){
                return actomaAccount.getAlias();
            }else{
                return actomaAccount.getAccount();
            }
        }
        return "";
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setServerActomaAccount(ResponseActomaAccount serverActomaAccount) {
        this.serverActomaAccount = serverActomaAccount;
    }

    public ResponseActomaAccount getServerActomaAccount() {
        return serverActomaAccount;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setIsExist(boolean isExist) {
        this.isExist = isExist;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public ActomaAccount getActomaAccount() {
        return actomaAccount;
    }

    public void setActomaAccount(ActomaAccount actomaAccount) {
        this.actomaAccount = actomaAccount;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(alias);
        dest.writeParcelable(actomaAccount,flags);
        dest.writeParcelable(avatar,flags);
        dest.writeParcelable(friend,flags);
        dest.writeParcelable(member,flags);
        dest.writeParcelable(department,flags);
        dest.writeParcelable(serverActomaAccount,flags);
        dest.writeByte(isExist ? (byte) 1 : (byte) 0);
        dest.writeByte(isFriend ? (byte) 1 : (byte) 0);
        dest.writeByte(isMember ? (byte) 1 : (byte) 0);
    }

    public CommonDetailDto(){}

    public CommonDetailDto(Parcel source){

        this.account = source.readString();

        this.alias = source.readString();

        this.actomaAccount = source.readParcelable(ActomaAccount.class.getClassLoader());

        this.avatar = source.readParcelable(Avatar.class.getClassLoader());

        this.friend = source.readParcelable(Friend.class.getClassLoader());

        this.member = source.readParcelable(Member.class.getClassLoader());

        this.department = source.readParcelable(Department.class.getClassLoader());

        this.serverActomaAccount = source.readParcelable(ResponseActomaAccount.class.getClassLoader());

        this.isExist = source.readByte() == 1 ;

        this.isFriend = source.readByte() == 1;

        this.isMember = source.readByte() == 1;
    }

    public static final Creator<CommonDetailDto> CREATOR = new Creator<CommonDetailDto>() {
        public CommonDetailDto createFromParcel(Parcel source) {
            return new CommonDetailDto(source);
        }

        public CommonDetailDto[] newArray(int size) {
            return new CommonDetailDto[size];
        }
    };
}
