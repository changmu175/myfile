package com.xdja.data_mainframe.db.encrypt;

/**
 * Created by geyao on 2015/8/31.
 */
public class EncryptFrameDao {
    protected static EncryptDBHelper helper = null;

    public EncryptFrameDao open() {
        if (helper == null) {
            synchronized (EncryptFrameDao.class) {
                if (helper == null) {
                    helper = new EncryptDBHelper();
                }
            }
        }
        return this;
    }

    public synchronized void close() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }
}
