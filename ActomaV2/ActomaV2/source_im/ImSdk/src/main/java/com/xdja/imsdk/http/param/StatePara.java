package com.xdja.imsdk.http.param;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 17:01                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class StatePara {
    private String user;
    private String ticket;
    private String flagid;
    private String fi;
    private String fst;
    private String i;
    private String lc;
    private String rst;
    private String sst;
    private String content;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    public String getFi() {
        return fi;
    }

    public void setFi(String fi) {
        this.fi = fi;
    }

    public String getFst() {
        return fst;
    }

    public void setFst(String fst) {
        this.fst = fst;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getLc() {
        return lc;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public String getRst() {
        return rst;
    }

    public void setRst(String rst) {
        this.rst = rst;
    }

    public String getSst() {
        return sst;
    }

    public void setSst(String sst) {
        this.sst = sst;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SendImStateMsgParams{" +
                "user='" + user + '\'' +
                ", ticket='" + ticket + '\'' +
                ", flagid='" + flagid + '\'' +
                ", fi='" + fi + '\'' +
                ", fst='" + fst + '\'' +
                ", i='" + i + '\'' +
                ", lc='" + lc + '\'' +
                ", rst='" + rst + '\'' +
                ", sst='" + sst + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
