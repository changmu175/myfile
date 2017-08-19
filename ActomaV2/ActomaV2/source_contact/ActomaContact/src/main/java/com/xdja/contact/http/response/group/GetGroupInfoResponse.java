package com.xdja.contact.http.response.group;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guorong on 2015/9/14.
 *
 * @see ResponseGroup
 */
public class GetGroupInfoResponse {


    private G group;

    private List<M> members;

    public List<M> getMembers() {
        return members;
    }

    public void setMembers(List<M> members) {
        this.members = members;
    }

    public G getGroup() {
        return group;
    }

    public void setGroup(G group) {
        this.group = group;
    }


    public class M implements Serializable {
        private String account;
        private String nickname;
        private String nicknamePy;
        private String nicknamePinyin;
        private String status;
        private String inviteAccount;
        private String createTime;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getNicknamePy() {
            return nicknamePy;
        }

        public void setNicknamePy(String nicknamePy) {
            this.nicknamePy = nicknamePy;
        }

        public String getNicknamePinyin() {
            return nicknamePinyin;
        }

        public void setNicknamePinyin(String nicknamePinyin) {
            this.nicknamePinyin = nicknamePinyin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInviteAccount() {
            return inviteAccount;
        }

        public void setInviteAccount(String inviteAccount) {
            this.inviteAccount = inviteAccount;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }

    public class G implements Serializable {
        private String groupId;
        private String groupName;
        private String avatarUrl;
        private String avatarHash;
        private String thumbnailUrl;
        private String thumbnailHash;
        private String owner;
        private String groupNamePy;
        private String groupNamePinyin;
        private String createTime;
        private String status;
        private String ksgId;
        private String encryptKsg;
        private long kuepId;



        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public long getKuepId() {
            return kuepId;
        }

        public void setKuepId(long kuepId) {
            this.kuepId = kuepId;
        }

        public String getEncryptKsg() {
            return encryptKsg;
        }

        public void setEncryptKsg(String encryptKsg) {
            this.encryptKsg = encryptKsg;
        }

        public String getKsgId() {
            return ksgId;
        }

        public void setKsgId(String ksgId) {
            this.ksgId = ksgId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getGroupNamePy() {
            return groupNamePy;
        }

        public void setGroupNamePy(String groupNamePy) {
            this.groupNamePy = groupNamePy;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getThumbnailHash() {
            return thumbnailHash;
        }

        public void setThumbnailHash(String thumbnailHash) {
            this.thumbnailHash = thumbnailHash;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getAvatarHash() {
            return avatarHash;
        }

        public void setAvatarHash(String avatarHash) {
            this.avatarHash = avatarHash;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

}


