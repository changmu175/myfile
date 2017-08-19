package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.db.DbHelper;
import com.xdja.imsdk.db.bean.DuplicateIdDb;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.bean.LocalStateMsgDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.RawFileDb;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.builder.HdThumbFileBuilder;
import com.xdja.imsdk.db.builder.MsgEntryBuilder;
import com.xdja.imsdk.db.builder.RawFileBuilder;
import com.xdja.imsdk.db.helper.OptType.MQuery;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                              <br>
 * 创建时间：2016/11/27 下午3:07                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class MsgEntryDao extends AbstractDao<MsgEntryDb> {
    private static MsgEntryDao instance;

    private MsgEntryDao() {
        super();
    }

    public static MsgEntryDao getInstance() {
        if (instance == null) {
            synchronized (MsgEntryDao.class) {
                if (instance == null) {
                    instance = new MsgEntryDao();
                }
            }
        }
        return instance;
    }

    /**
     * 消息保存，可能操作其他表，需开启事务
     * @param wrapper 消息
     * @return 数据库id
     */
    public long insert(MessageWrapper wrapper) {
        long rowId = 0;

        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return rowId;
        }

        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    rowId = insert(db, wrapper);

                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return ImSdkResult.RESULT_FAIL_DATABASE;
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }

        return rowId;
    }

    /**
     * 消息批量保存，可能操作其他表，需开启事务
     * @param db db
     * @param wrappers 消息列表
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<MessageWrapper> wrappers) {
        if (wrappers == null || wrappers.isEmpty()) {
            return;
        }

        synchronized (DB_LOCK) {
            try {
                db.beginTransaction();
                try {
                    for (MessageWrapper wrapper : wrappers) {
                        insert(db, wrapper);
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 开启事务，保存数据
     * @param sessions sessions
     * @param messages messages
     * @param dupIds dupIds
     * @param bombs bombs
     * @param dupes dupes
     */
    public void insertBatch(List<SessionWrapper> sessions, List<MessageWrapper> messages,
                            List<DuplicateIdDb> dupIds, List<LocalStateMsgDb> bombs,
                            List<LocalStateMsgDb> dupes, List<LocalStateMsgDb> recs) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {

                    SessionEntryDao.getInstance().insertBatch(db, sessions);
                    insertBatch(db, messages);

                    DuplicateIdDao.getInstance().insertBatch(db, dupIds);
                    LocalStateMsgDao.getInstance().insertBatch(db, bombs);
                    LocalStateMsgDao.getInstance().insertBatch(db, dupes);
                    LocalStateMsgDao.getInstance().insertBatch(db, recs);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 保存，已开启事务
     * @param db db
     * @param messages messages
     */
    private void insertBatch(SQLiteDatabase db, List<MessageWrapper> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (MessageWrapper message : messages) {
            insert(db, message);
        }
    }

    /**
     * 保存操作
     * @param db db
     * @param wrapper 消息
     * @return id
     */
    private long insert(SQLiteDatabase db, MessageWrapper wrapper) {
        long msgId = insert(db, wrapper.getMsgEntryDb());          // 保存msg_entry

        FileMsgDb fileMsgDb = wrapper.getFileMsgDb();
        if (wrapper.isFile() && fileMsgDb != null) {
            fileMsgDb.setMsg_id(msgId);

            FileMsgDao.getInstance().insert(db, fileMsgDb);        // 保存file_msg

            HdThumbFileDb hdDb = wrapper.getHdThumbFileDb();
            if (hdDb != null) {
                hdDb.setHd_msg_id(msgId);

                HdThumbFileDao.getInstance().insert(db, hdDb);     // 保存hd_thumb_file
            }

            RawFileDb rawDb = wrapper.getRawFileDb();
            if (rawDb != null) {
                rawDb.setRaw_msg_id(msgId);

                RawFileDao.getInstance().insert(db, rawDb);        // 保存raw_file
            }
        } else if (wrapper.isWeb() && fileMsgDb != null) {
            fileMsgDb.setMsg_id(msgId);

            FileMsgDao.getInstance().insert(db, fileMsgDb);
        }

        return msgId;
    }

    /**
     * msg_entry保存
     * @param db db
     * @param msg msg
     * @return id
     */
    private long insert(SQLiteDatabase db, MsgEntryDb msg) {
        return insert(db, msg, MsgEntryBuilder.insertSql());
    }

    /**
     * 删除
     * @param sql 指定的会话标识
     * @return 操作结果
     */
    public int deleteMsg(String sql) {
        return delete(sql);
    }

    /**
     * 更新
     */
    public void updateM(UpdateArgs args) {
        update(args);
    }

    /**
     * 批量更新消息状态
     * @param argsList argsList
     */
    public void updateMBatch(List<UpdateArgs> argsList) {
        updateBatch(MsgEntryBuilder.updateStateSql(), argsList);
    }

    /**
     * 开启事务，多表更新
     * @param message message
     * @param file file
     */
    public void updateMFState(UpdateArgs message, UpdateArgs file) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    update(db, message);
                    update(db, file);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 开启事务，多表更新
     * @param msg msg
     * @param file file
     * @param hd hd
     * @param raw raw
     */
    public void updateEF(UpdateArgs msg, UpdateArgs file, UpdateArgs hd, UpdateArgs raw) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    update(db, msg);
                    update(db, file);
                    update(db, hd);
                    update(db, raw);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 开启事务，更新消息，保存状态消息
     * @param args args
     * @param lState lState
     */
    public void updateAndSave(UpdateArgs args, LocalStateMsgDb lState) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    update(db, args);
                    LocalStateMsgDao.getInstance().insert(db, lState);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 开启事务，批量更新消息状态和内容，保存状态消息
     * @param args args
     * @param lStates lStates
     */
    public void updateAndSaveBatch(List<UpdateArgs> args, List<LocalStateMsgDb> lStates) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    updateBatch(db, MsgEntryBuilder.updateMsgStateSql(), args);
                    LocalStateMsgDao.getInstance().insertBatch(db, lStates);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Logger.getLogger().e("database exception !!!");
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    /**
     * 查询消息列表
     * @param query query
     * @param type type
     * @return List
     */
    public List<MessageWrapper> getMessages(String query, MQuery type) {
        List<MessageWrapper> messages = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                if (type == MQuery.NO) {
                    MsgEntryDb msgEntryDb = readEntry(cursor, 0);
                    MessageWrapper wrapper = new MessageWrapper(msgEntryDb);
                    messages.add(wrapper);
                } else {
                    messages.add(loadMessageCursor(cursor, type));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return messages;
    }

    /**
     * 查询fst重复的消息 [CREATE_TIME, SENDER, RECEIVER, TYPE, STATE]
     * MsgEntryBuilder querySameFst()
     * @param query query
     * @return List
     * @see MsgEntryBuilder
     */
    public List<MessageWrapper> getDup(String query) {
        List<MessageWrapper> messages = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                messages.add(loadDupCursor(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return messages;
    }

    /**
     * 查询消息
     * @param query query
     * @param type type
     * @return MessageWrapper
     */
    public MessageWrapper getMessage(String query, MQuery type) {
        MessageWrapper message = new MessageWrapper();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                if (type == MQuery.NO) {
                    MsgEntryDb msgEntryDb = readEntry(cursor, 0);
                    message.setMsgEntryDb(msgEntryDb);
                } else {
                    message = loadMessageCursor(cursor, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return message;
    }

    /**
     * 查询文件消息信息 MsgEntryBuilder queryFile()
     * @param query query
     * @param type type
     * @return MessageWrapper
     * @see MsgEntryBuilder
     */
    public MessageWrapper getMessage(String query, FileType type) {
        MessageWrapper message = new MessageWrapper();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                message = loadFileCursor(cursor, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return message;
    }

    /**
     * 查询id列表
     * @param query query
     * @return List
     */
    public List<Long> getIds(String query) {
        List<Long> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                result.add(cursor.getLong(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return result;
    }

    /**
     * 指定server id的消息已经被保存的server id 和state
     * @param query query
     * @return Map
     */
    public Map<Long, Integer> getStates(String query) {
        Map<Long, Integer> states = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                states.put(cursor.getLong(0), cursor.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return states;
    }

    /**
     * 查询所有发送失败的fst列表
     * @param query query
     * @return List
     */
    public List<Long> getFsts(String query) {
        List<Long> fsts = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                fsts.add(cursor.getLong(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return fsts;
    }

    /**
     * 读取cursor数据
     * @param cursor cursor
     * @return MessageWrapper
     */
    private MessageWrapper loadMessageCursor(Cursor cursor, MQuery type) {
        MessageWrapper wrapper = new MessageWrapper();

        MsgEntryDb m = new MsgEntryDb();

        m.setId(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.JOIN_ID)));
        m.setServer_id(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SERVER_ID)));
        m.setCard_id(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.CARD_ID)));
        m.setSender(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.SENDER)));
        m.setReceiver(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.RECEIVER)));
        m.setContent(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.CONTENT)));
        m.setState(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.STATE)));
        m.setType(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.TYPE)));
        m.setSession_flag(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.SESSION_FLAG)));
        m.setLife_time(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.LIFE_TIME)));
        m.setAttr(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.ATTR)));
        m.setSort_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SORT_TIME)));
        m.setCreate_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.CREATE_TIME)));
        m.setSent_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SENT_TIME)));

        wrapper.setMsgEntryDb(m);

        if (m.isFile() || m.isWeb()) {
            FileMsgDb f = new FileMsgDb();
            f.setId(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.F_JOIN_ID)));
            f.setFile_path(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FILE_PATH)));
            f.setEncrypt_path(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.ENCRYPT_PATH)));
            f.setFile_name(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FILE_NAME)));
            f.setMsg_id(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.MSG_ID)));
            f.setFile_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.FILE_SIZE)));
            f.setTranslate_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.TRANSLATE_SIZE)));
            f.setEncrypt_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.ENCRYPT_SIZE)));
            f.setFid(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FID)));
            f.setFile_state(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_STATE)));
            f.setType(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_TYPE)));
            f.setSuffix(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.SUFFIX)));
            f.setExtra_info(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.EXTRA_INFO)));

            wrapper.setFileMsgDb(f);

            if (type == MQuery.SHOW) {
                return wrapper;
            }

            if (type == MQuery.RAW || type == MQuery.ALL) {
                long rawId = cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_JOIN_ID));
                if (rawId > 0) {
                    RawFileDb raw = new RawFileDb();
                    raw.setId(rawId);
                    raw.setRaw_file_path(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_PATH)));
                    raw.setRaw_encrypt_path(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_ENCRYPT_PATH)));
                    raw.setRaw_file_name(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_NAME)));
                    raw.setRaw_msg_id(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_MSG_ID)));
                    raw.setRaw_file_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_SIZE)));
                    raw.setRaw_translate_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_TRANSLATE_SIZE)));
                    raw.setRaw_encrypt_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_ENCRYPT_SIZE)));
                    raw.setRaw_fid(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FID)));
                    raw.setRaw_state(cursor.getInt(cursor.getColumnIndex(RawFileBuilder.RAW_STATE)));
                    wrapper.setRawFileDb(raw);

                    if (type == MQuery.RAW) {
                        return wrapper;
                    }
                }
            }

            if (type == MQuery.ALL) {
                long hdId = cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_JOIN_ID));
                if (hdId > 0) {
                    HdThumbFileDb hd = new HdThumbFileDb();
                    hd.setId(hdId);
                    hd.setHd_file_path(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_PATH)));
                    hd.setHd_encrypt_path(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_ENCRYPT_PATH)));
                    hd.setHd_file_name(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_NAME)));
                    hd.setHd_msg_id(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_MSG_ID)));
                    hd.setHd_file_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_SIZE)));
                    hd.setHd_translate_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_TRANSLATE_SIZE)));
                    hd.setHd_encrypt_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_ENCRYPT_SIZE)));
                    hd.setHd_fid(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FID)));
                    hd.setHd_state(cursor.getInt(cursor.getColumnIndex(HdThumbFileBuilder.HD_STATE)));

                    wrapper.setHdThumbFileDb(hd);
                }
            }
        }

        return wrapper;
    }

    /**
     *
     * @param cursor cursor
     * @param type type
     * @return MessageWrapper
     * @see MsgEntryBuilder queryFile
     */
    private MessageWrapper loadFileCursor(Cursor cursor, FileType type) {
        MessageWrapper wrapper = new MessageWrapper();

        MsgEntryDb m = new MsgEntryDb();

        m.setId(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.JOIN_ID)));
        m.setType(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.TYPE)));
        wrapper.setMsgEntryDb(m);

        switch (type) {
            case IS_HD:
                if (m.isFile()) {
                    FileMsgDb f = new FileMsgDb();
                    f.setType(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_TYPE)));

                    wrapper.setFileMsgDb(f);

                    long hdId = cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.ID));
                    if (hdId > 0) {
                        HdThumbFileDb hd = new HdThumbFileDb();
                        hd.setId(hdId);
                        hd.setHd_file_path(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_PATH)));
                        hd.setHd_encrypt_path(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_ENCRYPT_PATH)));
                        hd.setHd_file_name(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_NAME)));
                        hd.setHd_msg_id(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_MSG_ID)));
                        hd.setHd_file_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_FILE_SIZE)));
                        hd.setHd_translate_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_TRANSLATE_SIZE)));
                        hd.setHd_encrypt_size(cursor.getLong(cursor.getColumnIndex(HdThumbFileBuilder.HD_ENCRYPT_SIZE)));
                        hd.setHd_fid(cursor.getString(cursor.getColumnIndex(HdThumbFileBuilder.HD_FID)));
                        hd.setHd_state(cursor.getInt(cursor.getColumnIndex(HdThumbFileBuilder.HD_STATE)));

                        wrapper.setHdThumbFileDb(hd);
                    }
                }
                break;
            case IS_SHOW:
                if (m.isFile() || m.isWeb()) {
                    FileMsgDb f = new FileMsgDb();
                    f.setId(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.ID)));
                    f.setFile_path(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FILE_PATH)));
                    f.setEncrypt_path(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.ENCRYPT_PATH)));
                    f.setFile_name(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FILE_NAME)));
                    f.setMsg_id(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.MSG_ID)));
                    f.setFile_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.FILE_SIZE)));
                    f.setTranslate_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.TRANSLATE_SIZE)));
                    f.setEncrypt_size(cursor.getLong(cursor.getColumnIndex(FileMsgBuilder.ENCRYPT_SIZE)));
                    f.setFid(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FID)));
                    f.setFile_state(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_STATE)));
                    f.setType(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_TYPE)));
                    f.setSuffix(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.SUFFIX)));
                    f.setExtra_info(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.EXTRA_INFO)));
                    f.setFile_state(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_STATE)));

                    wrapper.setFileMsgDb(f);
                }
                break;
            case IS_RAW:
                if (m.isFile()) {
                    FileMsgDb f = new FileMsgDb();
                    f.setType(cursor.getInt(cursor.getColumnIndex(FileMsgBuilder.FILE_TYPE)));

                    wrapper.setFileMsgDb(f);

                    long rawId = cursor.getLong(cursor.getColumnIndex(RawFileBuilder.ID));
                    if (rawId > 0) {
                        RawFileDb raw = new RawFileDb();
                        raw.setId(rawId);
                        raw.setRaw_file_path(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_PATH)));
                        raw.setRaw_encrypt_path(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_ENCRYPT_PATH)));
                        raw.setRaw_file_name(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_NAME)));
                        raw.setRaw_msg_id(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_MSG_ID)));
                        raw.setRaw_file_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_FILE_SIZE)));
                        raw.setRaw_translate_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_TRANSLATE_SIZE)));
                        raw.setRaw_encrypt_size(cursor.getLong(cursor.getColumnIndex(RawFileBuilder.RAW_ENCRYPT_SIZE)));
                        raw.setRaw_fid(cursor.getString(cursor.getColumnIndex(RawFileBuilder.RAW_FID)));
                        raw.setRaw_state(cursor.getInt(cursor.getColumnIndex(RawFileBuilder.RAW_STATE)));
                        wrapper.setRawFileDb(raw);
                    }
                }
                break;
            default:
                break;
        }

        return wrapper;
    }

    /**
     * [CREATE_TIME, SENDER, RECEIVER, TYPE, STATE]
     * @param cursor cursor
     * @return MessageWrapper
     */
    private MessageWrapper loadDupCursor(Cursor cursor) {
        MessageWrapper wrapper = new MessageWrapper();
        MsgEntryDb m = new MsgEntryDb();

        m.setCreate_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.CREATE_TIME)));
        m.setSender(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.SENDER)));
        m.setReceiver(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.RECEIVER)));
        m.setType(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.TYPE)));
        m.setState(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.STATE)));

        wrapper.setMsgEntryDb(m);
        return wrapper;
    }

    @Override
    protected MsgEntryDb readEntry(Cursor cursor, int offset) {
        MsgEntryDb entry = new MsgEntryDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),     // id
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1),     // server_id
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),   // sender
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3),   // receiver
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4),   // card_id
                cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5),      // type
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6),   // content
                cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7),      // state
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8),   // session_flag
                cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9),      // attr
                cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10),    // life_time
                cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11),   // create_time
                cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12),   // sent_time
                cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13)    // arrive_time
        );

        return entry;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, MsgEntryDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long server_id = entity.getServer_id();
        if (server_id != null) {
            stmt.bindLong(2, server_id);
        }

        String sender = entity.getSender();
        if (sender != null) {
            stmt.bindString(3, sender);
        }

        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(4, receiver);
        }

        String card_id = entity.getCard_id();
        if (card_id != null) {
            stmt.bindString(5, card_id);
        }

        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(6, type);
        }

        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(7, content);
        }

        Integer state = entity.getState();
        if (state != null) {
            stmt.bindLong(8, state);
        }

        String session_flag = entity.getSession_flag();
        if (session_flag != null) {
            stmt.bindString(9, session_flag);
        }

        Integer attr = entity.getAttr();
        if (attr != null) {
            stmt.bindLong(10, attr);
        }

        Integer life_time = entity.getLife_time();
        if (life_time != null) {
            stmt.bindLong(11, life_time);
        }

        Long create_time = entity.getCreate_time();
        if (create_time != null) {
            stmt.bindLong(12, create_time);
        }

        Long sent_time = entity.getSent_time();
        if (sent_time != null) {
            stmt.bindLong(13, sent_time);
        }

        Long sort_time = entity.getSort_time();
        if (sort_time != null) {
            stmt.bindLong(14, sort_time);
        }
    }
}
