package com.xdja.data_mainframe.entities.cache;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.entities.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:19:36</p>
 */
public interface UserCache {

    /**
     * 获取当前登录用户的帐号
     *
     * @return 当前登录用户的帐号
     */
    @Nullable
    String getAccount();

    void setAccount(String account);

    @Nullable
    String getTicket();

    void setTicket(String ticket);

    void setNickName(String nickName, String nickNamePinYin, String nickNamePy);

    void setAvatar(String avatarId, String thumbnailId);

    void setMobiles(List<String> mobiles);

    String getAlias();

    void setAlias(String alias);

    String getCompanyCode();

    void setCompanyCode(String companyCode);

    List<String> getMails();

    void setMails(List<String> mails);

    List<String> getCompanies();

    void setCompanies(List<String> companies);

    String getNickName();

    String getNickNamePinyin();

    String getNickNamePy();

    String getAvatarId();

    String getThumbnailId();

    List<String> getMobiles();
}
