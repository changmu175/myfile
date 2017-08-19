package com.xdja.contact.http.response.friend;

import com.xdja.contact.http.response.BaseResponse;

/**
 * Created by wanghao on 2015/7/23.
 */
public class ResponseRemarkFriend extends BaseResponse {

    private String remarkPy;

    private String remarkPinyin;




    public String getRemarkPinyin() {
        return remarkPinyin;
    }

    public void setRemarkPinyin(String remarkPinyin) {
        this.remarkPinyin = remarkPinyin;
    }

    public String getRemarkPy() {
        return remarkPy;
    }

    public void setRemarkPy(String remarkPy) {
        this.remarkPy = remarkPy;
    }
}
