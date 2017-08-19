package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by XDJA_XA on 2015/7/23.
 * 更新群内昵称请求体
 */
public class UpdateNicknameBody extends RequestBody {
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
