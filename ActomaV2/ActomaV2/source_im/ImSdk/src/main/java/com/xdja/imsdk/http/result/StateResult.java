package com.xdja.imsdk.http.result;

import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 19:50                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class StateResult {
    public int code;
    public long requestid;
    public long sst;
    public List<FailStateResult> statmsg;
    public long flagid;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getRequestid() {
        return requestid;
    }

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }

    public long getSst() {
        return sst;
    }

    public void setSst(long sst) {
        this.sst = sst;
    }

    public List<FailStateResult> getStatmsg() {
        return statmsg;
    }

    public void setStatmsg(List<FailStateResult> statmsg) {
        this.statmsg = statmsg;
    }

    public long getFlagid() {
        return flagid;
    }

    public void setFlagid(long flagid) {
        this.flagid = flagid;
    }
}
