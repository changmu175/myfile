package com.xdja.imsdk.security;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：加解密结果                          <br>
 * 创建时间：2016/12/1 15:12                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SecurityResult {
    /**
     * 加解密成功
     */
    public static final int SECURITY_SUCCESS = 0;

    /**
     * 加解密失败
     */
    public static final int SECURITY_FAIL = 1;

    /**
     * 返回原始文本
     */
    public static final int SECURITY_NON = 2;

    /**
     * 加密或解密后的结果
     */
    private String result;

    /**
     * 加解密结果
     * 0：成功
     * 其他：失败
     */
    private int code;


    public SecurityResult() {
    }

    public SecurityResult(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean success() {
        return this.code == SECURITY_SUCCESS;
    }

    @Override
    public String toString() {
        return "SecurityResult{" +
                "result='" + result + '\'' +
                ", code=" + code +
                '}';
    }
}
