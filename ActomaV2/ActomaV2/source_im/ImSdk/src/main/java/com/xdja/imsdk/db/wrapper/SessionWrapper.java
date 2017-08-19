package com.xdja.imsdk.db.wrapper;

import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.SessionEntryDb;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/26 17:19                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SessionWrapper {
    private SessionEntryDb sessionEntryDb;
    private MsgEntryDb msgEntryDb;
    private FileMsgDb fileMsgDb;

    public SessionWrapper() {
    }

    public SessionWrapper(SessionEntryDb sessionEntryDb) {
        this.sessionEntryDb = sessionEntryDb;
    }

    public SessionEntryDb getSessionEntryDb() {
        return sessionEntryDb;
    }

    public void setSessionEntryDb(SessionEntryDb sessionEntryDb) {
        this.sessionEntryDb = sessionEntryDb;
    }

    public MsgEntryDb getMsgEntryDb() {
        return msgEntryDb;
    }

    public void setMsgEntryDb(MsgEntryDb msgEntryDb) {
        this.msgEntryDb = msgEntryDb;
    }

    public FileMsgDb getFileMsgDb() {
        return fileMsgDb;
    }

    public void setFileMsgDb(FileMsgDb fileMsgDb) {
        this.fileMsgDb = fileMsgDb;
    }
}
