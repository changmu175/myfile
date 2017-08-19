package com.xdja.imsdk.exception;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk异常类                      <br>
 * 创建时间：2016/11/21 20:12                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ImSdkException extends Exception {
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    public ImSdkException(int errorCode) {
        super();// TODO: 2016/11/28  
    }

    public ImSdkException(String detailMessage) {
        super(detailMessage);
    }

    public ImSdkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
