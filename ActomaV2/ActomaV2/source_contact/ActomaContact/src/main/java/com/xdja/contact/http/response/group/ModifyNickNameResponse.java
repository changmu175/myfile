package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by XDJA_XA on 2015/7/23.
 */
public class ModifyNickNameResponse implements Serializable {

    private String nicknamePy;

    private String nicknamePinyin;

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
}
