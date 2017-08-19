package com.xdja.imsdk.db.wrapper;

import android.text.TextUtils;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.RawFileDb;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/26 17:19                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class MessageWrapper {
    private MsgEntryDb msgEntryDb;
    private FileMsgDb fileMsgDb;
    private HdThumbFileDb hdThumbFileDb;
    private RawFileDb rawFileDb;

    public MessageWrapper() {
    }

    public MessageWrapper(MsgEntryDb msgEntryDb) {
        this.msgEntryDb = msgEntryDb;
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

    public HdThumbFileDb getHdThumbFileDb() {
        return hdThumbFileDb;
    }

    public void setHdThumbFileDb(HdThumbFileDb hdThumbFileDb) {
        this.hdThumbFileDb = hdThumbFileDb;
    }

    public RawFileDb getRawFileDb() {
        return rawFileDb;
    }

    public void setRawFileDb(RawFileDb rawFileDb) {
        this.rawFileDb = rawFileDb;
    }

    public boolean isText() {
        if (msgEntryDb != null) {
            return msgEntryDb.isText();
        } else {
            return false;
        }
    }

    public boolean isFile() {
        if (msgEntryDb != null) {
            return msgEntryDb.isFile();
        } else {
            return false;
        }
    }

    public boolean isWeb() {
        if (msgEntryDb != null) {
            return msgEntryDb.isWeb();
        } else {
            return false;
        }
    }

    public boolean isExistFile() {
        if (fileMsgDb == null) {
            return false;
        }
        return !TextUtils.isEmpty(fileMsgDb.getFile_path());
    }
}
