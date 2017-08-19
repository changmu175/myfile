package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：                 <br>
 * 创建时间：2016/11/16 15:11  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImSdkConstant {

    /***************************************
     * ******** ImSdk释放时的参数 ********  *
     ***************************************/
    /**
     *  释放ImSdk时的参数，保持继续接收消息
     */
    public static final int RELEASE_KEEP = 0;

    /**
     *  释放ImSdk时的参数，完全退出，不再接收消息
     */
    public static final int RELEASE_QUIT = 1;



    /***************************************
     * ******* ImSdk的会话新消息数量 ******  *
     ***************************************/
    /**
     *  会话新消息数量设为0
     */
    public static final int REMIND_CLEAR = 0;

    /**
     *  会话置为未读，数量为1
     */
    public static final int REMIND_UNREAD = 1;



    /***************************************
     * ********* ImSdk网络状态码 ********  *
     ***************************************/
    /**
     * 网络状态正常
     */
    public static final int IM_NETWORK_CONNECTED = 0;

    /**
     * 网络状态连接不到服务器
     */
    public static final int IM_NETWORK_NO_SERVER = 1;

    /**
     * 网络状态不可用
     */
    public static final int IM_NETWORK_DISABLED = 2;

    /**
     * 网络状态广播ACTION
     */
    public static final String IM_NETWORK_STATE_ACTION = "com.xdja.im.net.state";

    /**
     * 网络状态广播EXTRA
     */
    public static final String IM_NETWORK_STATE = "im.extra.network.state";
}
