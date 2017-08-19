package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by XDJA_XA on 2015/7/22.
 * @see GetGroupInfoResponse
 *
 */
public class ResponseGroup implements Serializable {
    private String groupId;   //群组ID
    private String groupName; //群组名称
    private String avatarId; //原文件头像地址
    private String avatarHash;//原文件头像摘要
    private String thumbnailId; //群头像缩略图地址
    private String thumbnailHash;//群头像缩略图摘要
    private String owner; //群主账号
    private String groupNamePy;
    private String groupNamePinyin;
    private String createTime;
    private String status;//状态 1：新增加、2：修改；3 删除
    private String updateSerial;//更新序列
    private String ksgId;
    private String encryptKsg;
    private long kuepId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public String getAvatarHash() {
        return avatarHash;
    }

    public void setAvatarHash(String avatarHash) {
        this.avatarHash = avatarHash;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public String getThumbnailHash() {
        return thumbnailHash;
    }

    public void setThumbnailHash(String thumbnailHash) {
        this.thumbnailHash = thumbnailHash;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroupNamePy() {
        return groupNamePy;
    }

    public void setGroupNamePy(String groupNamePy) {
        this.groupNamePy = groupNamePy;
    }

    public String getGroupNamePinyin() {
        return groupNamePinyin;
    }

    public void setGroupNamePinyin(String groupNamePinyin) {
        this.groupNamePinyin = groupNamePinyin;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getKsgId() {
        return ksgId;
    }

    public void setKsgId(String ksgId) {
        this.ksgId = ksgId;
    }

    public String getEncryptKsg() {
        return encryptKsg;
    }

    public void setEncryptKsg(String encryptKsg) {
        this.encryptKsg = encryptKsg;
    }

    public long getKuepId() {
        return kuepId;
    }

    public void setKuepId(long kuepId) {
        this.kuepId = kuepId;
    }
}
