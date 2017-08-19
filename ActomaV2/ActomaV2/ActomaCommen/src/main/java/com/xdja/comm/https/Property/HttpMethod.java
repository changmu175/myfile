package com.xdja.comm.https.Property;

/**
 * Created by gbc on 2016/10/21.
 */
/**
 * 网络请求方式
 */
public enum HttpMethod {
    /**
     * put
     */
    PUT("PUT"),

    /**
     * get
     */
    GET("GET"),

    /**
     * post
     */
    POST("POST"),

    /**
     * delete
     */
    DELETE("DELETE");

    private String type;

    public String getType() {
        return type;
    }
    HttpMethod(String type) {// modified by ycm for lint
        this.type = type;
    }
}
