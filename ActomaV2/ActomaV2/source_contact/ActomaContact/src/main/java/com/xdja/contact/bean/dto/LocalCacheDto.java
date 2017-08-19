package com.xdja.contact.bean.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/10/22.
 * 本地搜索时组建临时的传输对象，方便搜索结果过滤和展示同时也可以传递给下个界面MainDetailPresenter;
 */
public class LocalCacheDto implements Parcelable {

    private String account;
    /**昵称*/
    private String nickName;

    private String nickNamePy;

    private String nickNamePinYin;
    /**备注*/
    private String remark;

    private String remarkPy;

    private String remarkPinYin;
    /**集团名称*/
    private String name;

    private String namePy;

    private String namePinYin;

    private String phone;

    /**头像*/
    private String thumbnailUrl;

    private Avatar avatar;

    private String workdId;

    //显示
    public static final int FRIEND_ALPHA = 0;
    //好友item
    public static final int FRIEND_ITEM = 1;
    //更多好友
    public static final int FRIEND_MORE = 2;

    public static final int GROUP_ALPHA = 3;
    //群组item
    public static final int GROUP_ITEM = 4;
    //更多群组
    public static final int GROUP_MORE = 5;

    private int viewType;

    //群组名称
    private String groupName;
    //群组头像
    private String groupAvatar;
    //群组id
    private String groupId;

    private String alias;


    public LocalCacheDto(){}


    public LocalCacheDto(Friend friend){
        setAccount(friend.getAccount());
        setAlias(friend.getAlias());
        setRemark(friend.getRemark());
        setRemarkPinYin(friend.getRemarkPinyin());
        setRemarkPy(friend.getRemarkPy());
        ActomaAccount actomaAccount = friend.getActomaAccount();
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){
            setNickName(actomaAccount.getNickname());
            setNickNamePy(actomaAccount.getNicknamePy());
            setNickNamePinYin(actomaAccount.getNicknamePinyin());
        }
        Avatar avatar = friend.getAvatar();
        if(!ObjectUtil.objectIsEmpty(avatar)){
            setAvatar(avatar);
        }
    }

    public LocalCacheDto(Member member){
        setAccount(member.getAccount());
        setAlias(member.getAlias());
        setName(member.getName());
        setNamePy(member.getNamePy());
        setNamePinYin(member.getNameFullPy());
        setPhone(member.getMobile());
        setWorkdId(member.getWorkId());
        ActomaAccount actomaAccount = member.getActomaAccount();
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){
            setNickName(actomaAccount.getNickname());
            setNickNamePy(actomaAccount.getNicknamePy());
            setNickNamePinYin(actomaAccount.getNicknamePinyin());
        }

        Avatar avatar = member.getAvatarInfo();
        if(!ObjectUtil.objectIsEmpty(avatar)){
            setAvatar(avatar);
        }
    }


    public void setMember(Member member){
        setAccount(member.getAccount());
        setAlias(member.getAlias());
        setName(member.getName());
        setNamePy(member.getNamePy());
        setNamePinYin(member.getNameFullPy());
        setPhone(member.getMobile());
        setWorkdId(member.getWorkId());
        ActomaAccount actomaAccount = member.getActomaAccount();
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){
            setNickName(actomaAccount.getNickname());
            setNickNamePy(actomaAccount.getNicknamePy());
            setNickNamePinYin(actomaAccount.getNicknamePinyin());
        }

        Avatar avatar = member.getAvatarInfo();
        if(!ObjectUtil.objectIsEmpty(avatar)){
            setAvatar(avatar);
        }
    }


    public LocalCacheDto(Group group){
        setGroupId(group.getGroupId());
        setGroupName(group.getGroupName());
        setGroupAvatar(group.getAvatar());
        setViewType(GROUP_ITEM);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String showName(){
        if(!ObjectUtil.stringIsEmpty(remark)){
            return remark;
        }
        if(!ObjectUtil.stringIsEmpty(name)){
            return name;
        }
        if(!ObjectUtil.stringIsEmpty(nickName)){
            return nickName;
        }
        if(!ObjectUtil.stringIsEmpty(alias)){
            return alias;
        }
        if(!ObjectUtil.stringIsEmpty(account)){
            return account;
        }else{
            LogUtil.getUtils().e("Actoma contact LocalCacheDto showName:nothing!");
            return "";
        }
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }
    //add by lwl start
    public String getAlias() {
        return alias;
    }
    //add by lwl end

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setWorkdId(String workdId) {
        this.workdId = workdId;
    }

    public String getWorkdId() {
        return workdId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickNamePy() {
        return nickNamePy;
    }

    public void setNickNamePy(String nickNamePy) {
        this.nickNamePy = nickNamePy;
    }

    public void setNickNamePinYin(String nickNamePinYin) {
        this.nickNamePinYin = nickNamePinYin;
    }

    public String getNickNamePinYin() {
        return nickNamePinYin;
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

    public String getRemarkPinYin() {
        return remarkPinYin;
    }

    public void setRemarkPinYin(String remarkPinYin) {
        this.remarkPinYin = remarkPinYin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePy() {
        return namePy;
    }

    public void setNamePy(String namePy) {
        this.namePy = namePy;
    }

    public String getNamePinYin() {
        return namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public LocalCacheDto(Parcel in){
        this.avatar = in.readParcelable(Avatar.class.getClassLoader());
        this.account = in.readString();
        this.alias = in.readString();
        this.remark = in.readString();
        this.remarkPy = in.readString();
        this.remarkPinYin = in.readString();
        this.nickName = in.readString();
        this.nickNamePy= in.readString();
        this.nickNamePinYin = in.readString();
        this.name = in.readString();
        this.namePy= in.readString();
        this.namePinYin= in.readString();
        this.phone = in.readString();
        this.thumbnailUrl = in.readString();


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.avatar, 0);
        dest.writeString(this.account);
        dest.writeString(this.alias);
        dest.writeString(this.remark);
        dest.writeString(this.remarkPy);
        dest.writeString(this.remarkPinYin);

        dest.writeString(this.nickName);
        dest.writeString(this.nickNamePy);
        dest.writeString(this.nickNamePinYin);

        dest.writeString(this.name);
        dest.writeString(this.namePy);
        dest.writeString(this.namePinYin);

        dest.writeString(this.phone);
        dest.writeString(this.thumbnailUrl);
    }

    public static final Creator<LocalCacheDto> CREATOR = new Creator<LocalCacheDto>() {
        public LocalCacheDto createFromParcel(Parcel source) {
            return new LocalCacheDto(source);
        }
        public LocalCacheDto[] newArray(int size) {
            return new LocalCacheDto[size];
        }
    };
}
