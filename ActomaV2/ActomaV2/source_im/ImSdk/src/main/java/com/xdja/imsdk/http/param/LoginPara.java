package com.xdja.imsdk.http.param;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  登录IM服务器请求参数                           <br>
 * 创建时间：2016/11/27 下午5:04                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class LoginPara {
    /**
     * 云平台标识
     */
    private String ticket;

    /**
     * 用户帐号
     */
    private String user;

    /**
     * 设备号
     */
    private String device;

    public LoginPara(String ticket, String user, String device) {
        super();
        this.ticket = ticket;
        this.user = user;
        this.device = device;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
