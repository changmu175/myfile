package com.xdja.imsdk.db.helper;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/28 19:06                 <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OptType {
    public static final int DEL_TYPE_1 = 1;                      //删除指定会话
    public static final int DEL_TYPE_2 = 2;                      //删除所有会话

    public static final int DEL_TYPE_3 = 3;                      //删除指定消息
    public static final int DEL_TYPE_4 = 4;                      //删除会话消息

    public static final int DEL_TYPE_5 = 5;                      //删除状态消息

    /**
     * 查询消息中，查询的类型
     */
    public enum MQuery {
        ALL,//[msg_entry, file_msg, hd_file, raw_file]
        NO, //[msg_entry]
        SHOW,//[msg_entry, file_msg]
        RAW//[msg_entry, file_msg, raw_file]
    }

    /**
     * 查询消息中，查询的类型
     */
    public enum SQuery {
        HAVE,//[session_entry, msg_entry]
        NON  // [session_entry]
    }
}
