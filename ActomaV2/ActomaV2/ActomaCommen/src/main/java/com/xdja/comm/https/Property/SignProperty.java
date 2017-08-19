package com.xdja.comm.https.Property;

/**
 * Created by gbc on 2016/10/21.
 */
/**
 * 签名和加解密的算法配置
 */
public enum SignProperty {
    /**
     * sm3withsm2
     */
    SM3WITHSM2("SM3WITHSM2"),

    /**
     * SHA1WITHRSA
     */
    SHA1WITHRSA("SHA1WITHRSA");

    private String type;

    public String getType() {
        return type;
    }

    SignProperty(String type) {// modified by ycm for lint
        this.type = type;
    }
}
