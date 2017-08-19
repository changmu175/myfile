package com.xdja.contact.http.request.account;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by tangsha on 2016/7/15.
 */
public class GetCkmsOpSignBody extends RequestBody {
    public String opCode;


    public void setOpCode(String code){
        opCode = code;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
