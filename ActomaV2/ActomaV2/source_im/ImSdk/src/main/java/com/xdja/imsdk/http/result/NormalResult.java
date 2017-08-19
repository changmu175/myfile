package com.xdja.imsdk.http.result;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 19:49                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class NormalResult {
    private String flag;// 固定1
    private String msgid;// 返回生成的消息ID
    private String flagid;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagId(String flagid) {
        this.flagid = flagid;
    }
}
