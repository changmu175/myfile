package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk               <br>
 * 类描述  ：接口调用时返回结果码   <br>
 * 创建时间：2016/11/16 15:17    <br>
 * 修改记录：                    <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImSdkResult {
    /**
     *  方法调用成功
     */
    public static final int RESULT_OK = 0;

    /**
     *  方法调用失败，原因是参数未通过校验
     */
    public static final int RESULT_FAIL_PARA = -1;

    /**
     *  方法调用失败，原因是sdk未初始化，sdk服务未启动
     */
    public static final int RESULT_FAIL_SERVICE = -2;

    /**
     *  方法调用失败，原因是返回的结果无效
     */
    public static final int RESULT_FAIL_INVALID = -3;

    /**
     *  方法调用失败，原因是设置配置项禁止了调用
     */
    public static final int RESULT_FAIL_FORBID = -4;

    /**
     *  方法调用失败，原因是数据库操作失败
     */
    public static final int RESULT_FAIL_DATABASE = -5;
}
