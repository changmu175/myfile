package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 16:39                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class Condition {
    long sync;
    long roaming;

    public Condition(long sync, long roam) {
        this.sync = sync;
        this.roaming = roam;
    }
    /**
     * @return the sync
     */
    public long getSync() {
        return sync;
    }
    /**
     * @param sync the sync to set
     */
    public void setSync(long sync) {
        this.sync = sync;
    }
    /**
     * @return the roaming
     */
    public long getRoaming() {
        return roaming;
    }
    /**
     * @param roaming the roaming to set
     */
    public void setRoaming(long roaming) {
        this.roaming = roaming;
    }

    public String toString() {
        return "Sync:"+sync+"  roma:"+roaming;
    }
}
