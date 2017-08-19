package com.xdja.comm.https;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;

/**
 * Created by THZ on 2015/5/8.
 * 认证授权的bean
 */
public class AuthBean {

    /**
     * 终端签名证书sn
     */
    public static String SIGNATURE_SN_HEADER_NAME = "x-at-signature-sn";

    /**
     * 终端签名证书dn
     */
    public static String SIGNATURE_DN_HEADER_NAME = "x-at-signature-dn";

    /**
     * 签名方式，使用SM3WithSM2
     */
    public static String SIGNATURE_METHOD_HEADER_NAME = "x-at-signature-method";

    /**
     * 签名算法版本，目前版本是1.0
     */
    public static String SIGNATURE_VERSION_HEADER_NAME = "x-at-signature-version";

    /**
     * API版本号，为日期形式：YYYY-MM-DD，版本对应为2014-05-26
     */
    public static String VERSION_HEADER_NAME = "x-at-version";

    /**
     * 请求的时间戳。
     */
    public static String TIMESTAMP_HEADER_NAME = "x-at-timestamp";

    /**
     * 唯一随机数，用于防止网络重放攻击。用户在不同请求间要使用不同的随机数值
     */
    public static String SIGNATURE_NONCE_HEADER_NAME = "x-at-signature-nonce";

    /**
     * 签名值
     */
    public static String SIGN_AUTHORIZATION = "Authorization";

    /**
     * API版本号
     */
    public String version;
    /**
     * 终端签名证书sn
     */
    public String signSn;

    /**
     * 终端签名证书Dn
     */
    public String signDn;

    /**
     * 签名方式，使用SM3WithSM2
     */
    public String signMethod;

    /**
     * 请求的时间戳。
     */
    public String timeStamp;

    /**
     * 签名算法版本，目前版本是1.0
     */
    public String signVersion;

    /**
     * 唯一随机数，用于防止网络重放攻击。用户在不同请求间要使用不同的随机数值
     */
    public String nonce;

    /**
     * 签名结果
     */
    public String sinResult;

    /**
     * 生成header数组
     * @return ArrayList<Header> 协议头
     */
    public ArrayList<Header> toHeads() {
        ArrayList<Header> headerList = new ArrayList<>();
        Header header;

        //终端签名证书dn
        header = new BasicHeader(SIGNATURE_DN_HEADER_NAME, signDn);
        headerList.add(header);

        //终端签名方法
        header = new BasicHeader(SIGNATURE_METHOD_HEADER_NAME, signMethod);
        headerList.add(header);

        //签名随机数
        header = new BasicHeader(SIGNATURE_NONCE_HEADER_NAME, nonce);
        headerList.add(header);

        //终端签名证书sn
        header = new BasicHeader(SIGNATURE_SN_HEADER_NAME, signSn);
        headerList.add(header);

        //签名版本
        header = new BasicHeader(SIGNATURE_VERSION_HEADER_NAME, signVersion);
        headerList.add(header);
        
        //请求时间
        header = new BasicHeader(TIMESTAMP_HEADER_NAME, timeStamp);
        headerList.add(header);
        
        //版本
        header = new BasicHeader(VERSION_HEADER_NAME, version);
        headerList.add(header);

        return headerList;
    }

    /**
     * 转换authBean
     * @param headers
     * @return
     */
    public AuthBean toAuthBean(Header [] headers) {

        if (headers != null) {

            for (Header header : headers) {// modified by ycm for lint 2017/02/13

                if (header != null) {
                    if (header.getName().equals(SIGNATURE_DN_HEADER_NAME)) {
                        signDn = header.getValue();
                    } else if (header.getName().equals(SIGNATURE_METHOD_HEADER_NAME)) {
                        signMethod = header.getValue();
                    } else if (header.getName().equals(SIGNATURE_NONCE_HEADER_NAME)) {
                        nonce = header.getValue();
                    } else if (header.getName().equals(SIGNATURE_SN_HEADER_NAME)) {
                        signSn = header.getValue();
                    } else if (header.getName().equals(SIGNATURE_VERSION_HEADER_NAME)) {
                        signVersion = header.getValue();
                    } else if (header.getName().equals(TIMESTAMP_HEADER_NAME)) {
                        timeStamp = header.getValue();
                    } else if (header.getName().equals(VERSION_HEADER_NAME)) {
                        version = header.getValue();
                    } else if (header.getName().equals(SIGN_AUTHORIZATION)) {
                        sinResult = header.getValue();
                    }
                }
            }
        }
        return this;
    }
}
