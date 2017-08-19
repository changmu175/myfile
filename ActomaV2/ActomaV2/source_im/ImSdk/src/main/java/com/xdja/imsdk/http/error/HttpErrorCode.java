package com.xdja.imsdk.http.error;

import com.xdja.imsdk.constant.internal.Constant;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午4:38                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class HttpErrorCode {

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = Constant.CODE_BEGIN + 100;

    /**
     * ticket过期
     */
    public static final int TICKET_EXPIRE = Constant.CODE_BEGIN + 101;

    /**
     * 未知错误
     */
    public static final int UNKNOWN_ERROR = Constant.CODE_BEGIN + 102;
}
