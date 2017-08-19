package com.xdja.imsdk.http.result;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午5:02                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class LoginResult {
    /**
     * 登录设备是否发生改变，0:未改变 1:改变
     */
    private int chg;

    /**
     * 登录时间 
     */
    private long sst;// TODO: 2016/11/27 liming optimize 待确认

    public int getChg() {
        return chg;
    }

    public void setChg(int chg) {
        this.chg = chg;
    }

    public long getSst() {
        return sst;
    }

    public void setSst(long sst) {
        this.sst = sst;
    }
}
