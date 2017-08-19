package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by tangsha on 2016/7/15.
 */
public class ResponseCkmsOpSign implements Serializable {

    public String signedOpCode;

    public String getSignedOpCode() {
        return signedOpCode;
    }
}
