package com.xdja.imsdk.http.bean;

import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.constant.internal.State;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  收发消息请求内容结构                            <br>
 * 创建时间：2016/11/27 下午4:53                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class MsgBean {
    /**
     * 消息服务器id，IM服务器返回
     */
    private long i;

    /**
     * 消息发送方的身份标识, 例如AT+账号
     */
    private String f;

    /**
     * 消息发送方的芯片卡id
     */
    private String fi;

    /**
     * 接收消息的用户标识
     */
    private String to;

    /**
     * 消息类型
     * @see MsgType
     */
    private int t;

    /**
     * 消息内容
     * 文本消息为消息的具体内容；
     * 文件消息为文件的描述信息，例如文件的获取地址等信息。
     */
    private String c;

    /**
     * 消息状态
     * @see com.xdja.imsdk.constant.MsgState
     */
    private int stat;

    /**
     * 消息发出来的时间
     */
    private long fst;

    /**
     * 服务器接收到消息的时间
     */
    private long sst;

    /**
     * 消息到达接收方的时间
     */
    private long rst;

    /**
     * 消息生存期
     * 闪信时，表示消息收到后lc时间之后要销毁
     */
    private int lc;

    public MsgBean() {
    }

    public MsgBean(long i) {
        this.i = i;
    }

    public MsgBean(long i, String f, String fi, String to,
                   int t, String c, int stat,long fst,
                   long sst, long rst, int lc) {
        this.i = i;
        this.f = f;
        this.fi = fi;
        this.to = to;
        this.t = t;
        this.c = c;
        this.stat = stat;
        this.fst = fst;
        this.sst = sst;
        this.rst = rst;
        this.lc = lc;
    }

    public long getI() {
        return i;
    }

    public void setI(long i) {
        this.i = i;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getFi() {
        return fi;
    }

    public void setFi(String fi) {
        this.fi = fi;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public long getFst() {
        return fst;
    }

    public void setFst(long fst) {
        this.fst = fst;
    }

    public long getSst() {
        return sst;
    }

    public void setSst(long sst) {
        this.sst = sst;
    }

    public long getRst() {
        return rst;
    }

    public void setRst(long rst) {
        this.rst = rst;
    }

    public int getLc() {
        return lc;
    }

    public void setLc(int lc) {
        this.lc = lc;
    }

    /**
     * 判断是否是文本消息
     * @return boolean <br>
     *         true: 是文本类型消息<br>
     *         false: 不是文本类型消息
     */
    public boolean isTextMsg() {
        if ((t & MsgType.MSG_TYPE_TEXT) == MsgType.MSG_TYPE_TEXT) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是文件消息
     * @return boolean <br>
     *         true: 是文件类型消息<br>
     *         false: 不是文件类型消息
     */
    public boolean isFileMsg() {
        if ((t & MsgType.MSG_TYPE_FILE) == MsgType.MSG_TYPE_FILE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是网页消息
     * @return boolean <br>
     *         true: 是网页类型消息<br>
     *         false: 不是网页类型消息
     */
    public boolean isWebMsg() {
        if ((t & MsgType.MSG_TYPE_WEB) == MsgType.MSG_TYPE_WEB ) {
            return true;
        }
        return false;
    }

    /**
     * 消息是否是群组消息
     * @return boolean <br>
     *         true: 是群组类型消息<br>
     *         false: 不是群组类型消息
     */
    public boolean isGroupMsg() {
        if ((t & MsgType.MSG_TYPE_GROUP) == MsgType.MSG_TYPE_GROUP) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是闪信
     * @return boolean <br>
     *         true: 是闪信<br>
     *         false: 不是闪信
     */
    public boolean isBombMsg() {
        if ((t & MsgType.MSG_TYPE_BOMB) == MsgType.MSG_TYPE_BOMB) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是状态消息
     * @return boolean <br>
     *         true: 是状态类型消息<br>
     *         false: 不是状态类型消息
     */
    public boolean isStateMsg() {
        if ((t & MsgType.MSG_TYPE_STATE) == MsgType.MSG_TYPE_STATE) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isReadMsg() {
        return stat == State.READ;
    }

    public boolean removeDuplication(MsgBean msgBean) {
        if (this == msgBean) return true;
        if (msgBean == null || getClass() != msgBean.getClass()) return false;

        if (t != msgBean.t) return false;
        if (fst != msgBean.fst) return false;
        if (lc != msgBean.lc) return false;
        if (!f.equals(msgBean.f)) return false;
        if (!fi.equals(msgBean.fi)) return false;
        if (!to.equals(msgBean.to)) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MsgBean msgBean = (MsgBean) o;

        if (i != msgBean.i) return false;
        if (t != msgBean.t) return false;
        if (stat != msgBean.stat) return false;
        if (fst != msgBean.fst) return false;
        if (sst != msgBean.sst) return false;
        if (rst != msgBean.rst) return false;
        if (lc != msgBean.lc) return false;
        if (f != null ? !f.equals(msgBean.f) : msgBean.f != null) return false;
        if (fi != null ? !fi.equals(msgBean.fi) : msgBean.fi != null) return false;
        if (to != null ? !to.equals(msgBean.to) : msgBean.to != null) return false;
        return c != null ? c.equals(msgBean.c) : msgBean.c == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (i ^ (i >>> 32));
        result = 31 * result + (f != null ? f.hashCode() : 0);
        result = 31 * result + (fi != null ? fi.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + t;
        result = 31 * result + (c != null ? c.hashCode() : 0);
        result = 31 * result + stat;
        result = 31 * result + (int) (fst ^ (fst >>> 32));
        result = 31 * result + (int) (sst ^ (sst >>> 32));
        result = 31 * result + (int) (rst ^ (rst >>> 32));
        result = 31 * result + lc;
        return result;
    }

    /**
     * 消息转换为字符串
     * @return String
     */
    public String toString() {
        StringBuffer msgBuffer = new StringBuffer();
        msgBuffer.append("MsgBean: ")
                .append("[i = ").append(i)
                .append(", f = ").append(f)
                .append(", fi = ").append(fi)
                .append(", to = ").append(to)
                .append(", c = ").append(c)
                .append(", t = ").append(t)
                .append(", stat = ").append(stat)
                .append(", fst = ").append(fst)
                .append(", sst = ").append(sst)
                .append(", rst = ").append(rst)
                .append(", lc = ").append(lc).append("]").append("\n");
        return msgBuffer.toString();
    }
}
