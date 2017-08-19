package com.xdja.imsdk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.HttpApiConstant;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.bean.DuplicateIdDb;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.RawFileDb;
import com.xdja.imsdk.db.bean.SessionEntryDb;
import com.xdja.imsdk.db.bean.SyncIdDb;
import com.xdja.imsdk.db.builder.DeletedMsgBuilder;
import com.xdja.imsdk.db.builder.DuplicateIdBuilder;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.builder.HdThumbFileBuilder;
import com.xdja.imsdk.db.builder.LocalStateMsgBuilder;
import com.xdja.imsdk.db.builder.MsgEntryBuilder;
import com.xdja.imsdk.db.builder.OptionsBuilder;
import com.xdja.imsdk.db.builder.RawFileBuilder;
import com.xdja.imsdk.db.builder.SessionEntryBuilder;
import com.xdja.imsdk.db.builder.SyncIdBuilder;
import com.xdja.imsdk.db.dao.DuplicateIdDao;
import com.xdja.imsdk.db.dao.FileMsgDao;
import com.xdja.imsdk.db.dao.HdThumbFileDao;
import com.xdja.imsdk.db.dao.MsgEntryDao;
import com.xdja.imsdk.db.dao.RawFileDao;
import com.xdja.imsdk.db.dao.SessionEntryDao;
import com.xdja.imsdk.db.dao.SyncIdDao;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.manager.ModelMapper;
import com.xdja.imsdk.model.internal.old.OldFile;
import com.xdja.imsdk.util.JsonUtils;
import com.xdja.imsdk.util.ToolUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 13:48                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ImSdkDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private Context context;
    private String account;

    public ImSdkDatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
        this.context = context;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createMsgEntryTable(sqLiteDatabase);
        createSessionEntryTable(sqLiteDatabase);
        createFileMsgTable(sqLiteDatabase);
        createHdThumbFileTable(sqLiteDatabase);
        createRawFileTable(sqLiteDatabase);
        createLocalStateMsgTable(sqLiteDatabase);
        createDeletedMsgTable(sqLiteDatabase);
        createDuplicateIdTable(sqLiteDatabase);
        createSyncIdTable(sqLiteDatabase);
        createOptionsTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion == 5) {
            upgrade2V6(sqlLiteDatabase);
        }
    }

    private void upgrade2V6(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_TRIGGER_MSG_UPDATE);
        db.execSQL(SQL_DROP_TRIGGER_MSG_DELETE_INSERT);
        db.execSQL(SQL_DROP_TRIGGER_MSG_DELETE);
        db.execSQL(SQL_DROP_TRIGGER_MSG_DELETE_UPDATE);
        db.execSQL(SQL_DROP_TRIGGER_MSG_DELETE_DELETE);
        db.execSQL(SQL_DROP_TRIGGER_SESSION_DELETE);

        db.execSQL(SQL_ALTER_OLD_ID_REFERENCE);
        db.execSQL(SQL_ALTER_OLD_FILE_MSG);
        db.execSQL(SQL_ALTER_OLD_FILE_EXTRA);
        db.execSQL(SQL_ALTER_OLD_MSG_ENTRY);
        db.execSQL(SQL_ALTER_OLD_OPTIONS);
        db.execSQL(SQL_ALTER_OLD_SESSION_ENTRY);

        createDuplicateIdTable(db);
        createSyncIdTable(db);
        createFileMsgTable(db);
        createHdThumbFileTable(db);
        createRawFileTable(db);
        createMsgEntryTable(db);
        createOptionsTable(db);
        createSessionEntryTable(db);

        upgradeId5To6(db);
        upgradeHdRaw5To6(db);
        upgradeFileMsg5To6(db);
        upgradeMsg5To6(db);
        upgradeSession5To6(db);

        db.execSQL(SQL_DROP_OLD_ID);
        db.execSQL(SQL_DROP_FILE_MSG);
        db.execSQL(SQL_DROP_FILE_EXTRA);
        db.execSQL(SQL_DROP_OPTIONS);
        db.execSQL(SQL_DROP_MSG);
        db.execSQL(SQL_DROP_SESSION);
    }

    private void createMsgEntryTable(SQLiteDatabase db) {
        try {
            db.execSQL(MsgEntryBuilder.SQL_CREATE_TABLE_MSG);
            db.execSQL(MsgEntryBuilder.SESSION_UPDATE_ON_INSERT_TRIGGER);
            db.execSQL(MsgEntryBuilder.MSG_INSERT_DELETED_ON_DELETE_TRIGGER);
            db.execSQL(MsgEntryBuilder.FILE_MSG_DELETE_ON_DELETE_TRIGGER);
            db.execSQL(MsgEntryBuilder.FILE_MSG_DELETE_ON_UPDATE_TRIGGER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSessionEntryTable(SQLiteDatabase db) {
        db.execSQL(SessionEntryBuilder.SQL_CREATE_TABLE_SESSION_ENTRY);
        db.execSQL(SessionEntryBuilder.SESSION_DELETE_MSG_ON_DELETE_TRIGGER);
    }

    private void createFileMsgTable(SQLiteDatabase db) {
        db.execSQL(FileMsgBuilder.SQL_CREATE_TABLE_FILE_MSG);
    }

    private void createHdThumbFileTable(SQLiteDatabase db) {
        db.execSQL(HdThumbFileBuilder.SQL_CREATE_TABLE_HD_THUMB);
    }

    private void createRawFileTable(SQLiteDatabase db) {
        db.execSQL(RawFileBuilder.SQL_CREATE_TABLE_RAW_FILE);
    }

    private void createLocalStateMsgTable(SQLiteDatabase db) {
        db.execSQL(LocalStateMsgBuilder.SQL_CREATE_TABLE_LOCAL_STATE_MSG);
    }

    private void createDeletedMsgTable(SQLiteDatabase db) {
        db.execSQL(DeletedMsgBuilder.SQL_CREATE_TABLE_DELETED_MSG);
    }

    private void createDuplicateIdTable(SQLiteDatabase db) {
        db.execSQL(DuplicateIdBuilder.SQL_CREATE_TABLE_DUPLICATE_ID);
    }

    private void createSyncIdTable(SQLiteDatabase db) {
        db.execSQL(SyncIdBuilder.SQL_CREATE_TABLE_SYNC_ID);
    }

    private void createOptionsTable(SQLiteDatabase db) {
        db.execSQL(OptionsBuilder.SQL_CREATE_TABLE_OPTIONS);
    }

    private void upgradeHdRaw5To6(SQLiteDatabase db) {
        List<HdThumbFileDb> hdList = new ArrayList<>();
        List<RawFileDb> rawList = new ArrayList<>();

        Cursor c = null;
        try {
            String query = "SELECT msg_tmp.SENDER, file_extra_tmp.* FROM file_extra_tmp " +
                    "LEFT JOIN msg_tmp ON file_extra_tmp.MSG_ID = msg_tmp._id;";

            c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                return;
            }

            c.moveToPosition(-1);
            while (c.moveToNext()) {
                String hdName = c.getString(c.getColumnIndex("TH_FILE_NAME"));
                if (!TextUtils.isEmpty(hdName)) {
                    HdThumbFileDb hd = new HdThumbFileDb();
                    hd.setHd_msg_id(c.getLong(c.getColumnIndex("MSG_ID")));
                    hd.setHd_file_path(c.getString(c.getColumnIndex("TH_FILE_URL")));
                    hd.setHd_file_name(c.getString(c.getColumnIndex("TH_FILE_NAME")));
                    hd.setHd_file_size(c.getLong(c.getColumnIndex("TH_FILE_SIZE")));
                    hd.setHd_translate_size(c.getLong(c.getColumnIndex("TH_TRANSLATE_SIZE")));
                    hd.setHd_fid(c.getString(c.getColumnIndex("TH_FID")));
                    
                    hd.setHd_encrypt_size(c.getLong(c.getColumnIndex("TH_FILE_SIZE")));// TODO: 2017/1/3 liming
                    hd.setHd_encrypt_path(c.getString(c.getColumnIndex("TH_FILE_URL")));

                    String sender = c.getString(c.getColumnIndex("SENDER"));
                    long size = hd.getHd_file_size();
                    long tSize = hd.getHd_translate_size();

                    if (TextUtils.equals(sender, account)) {
                        // 发送出去的消息
                        if (tSize == 0L) {
                            hd.setHd_state(FileTState.UP_NON);
                        } else if (tSize == size) {
                            hd.setHd_state(FileTState.UP_DONE);
                        } else {
                            hd.setHd_state(FileTState.UP_FAIL);
                        }

                        // 发送出去的图片，需要将文件名和路径的dat去掉
                        hd.setHd_file_path(removeDat(hd.getHd_file_path()));
                        hd.setHd_file_name(removeDat(hd.getHd_file_name()));
                    } else {
                        // 接收到的消息
                        if (tSize == 0L) {
                            hd.setHd_state(FileTState.DOWN_NON);
                        } else if (tSize == size) {
                            hd.setHd_state(FileTState.DOWN_DONE);
                        } else {
                            hd.setHd_state(FileTState.DOWN_FAIL);
                        }
                    }

                    hdList.add(hd);
                }

                String rawName = c.getString(c.getColumnIndex("RAW_FILE_NAME"));
                if (!TextUtils.isEmpty(rawName)) {
                    RawFileDb raw = new RawFileDb();
                    raw.setRaw_msg_id(c.getLong(c.getColumnIndex("MSG_ID")));
                    raw.setRaw_file_path(c.getString(c.getColumnIndex("RAW_FILE_URL")));
                    raw.setRaw_file_name(c.getString(c.getColumnIndex("RAW_FILE_NAME")));
                    raw.setRaw_file_size(c.getLong(c.getColumnIndex("RAW_FILE_SIZE")));
                    raw.setRaw_translate_size(c.getLong(c.getColumnIndex("RAW_TRANSLATE_SIZE")));
                    raw.setRaw_fid(c.getString(c.getColumnIndex("RAW_FID")));
                    
                    raw.setRaw_encrypt_size(c.getLong(c.getColumnIndex("RAW_FILE_SIZE")));// TODO: 2017/1/3 liming
                    raw.setRaw_encrypt_path(c.getString(c.getColumnIndex("RAW_FILE_URL")));

                    String sender = c.getString(c.getColumnIndex("SENDER"));
                    long size = raw.getRaw_file_size();
                    long tSize = raw.getRaw_translate_size();

                    if (TextUtils.equals(sender, account)) {
                        // 发送出去的消息
                        if (tSize == 0L) {
                            raw.setRaw_state(FileTState.UP_NON);
                        } else if (tSize == size) {
                            raw.setRaw_state(FileTState.UP_DONE);
                        } else {
                            raw.setRaw_state(FileTState.UP_FAIL);
                        }

                        // 发送出去的图片，需要将文件名和路径的dat去掉
                        raw.setRaw_file_path(removeDat(raw.getRaw_file_path()));
                        raw.setRaw_file_name(removeDat(raw.getRaw_file_name()));
                    } else {
                        // 接收到的消息
                        if (tSize == 0L) {
                            raw.setRaw_state(FileTState.DOWN_NON);
                        } else if (tSize == size) {
                            raw.setRaw_state(FileTState.DOWN_DONE);
                        } else {
                            raw.setRaw_state(FileTState.DOWN_FAIL);
                        }
                    }

                    rawList.add(raw);
                }
            }

        }  finally {
            if (c != null) {
                c.close();
            }
        }

        if (!hdList.isEmpty()) {
            HdThumbFileDao.getInstance().insertBatchUpgrade(db, hdList);
        }

        if (!rawList.isEmpty()) {
            RawFileDao.getInstance().insertBatchUpgrade(db, rawList);
        }
    }

    private void upgradeFileMsg5To6(SQLiteDatabase db) {
        List<FileMsgDb> files = new ArrayList<>();
        List<HdThumbFileDb> hdList = new ArrayList<>();
        List<RawFileDb> rawList = new ArrayList<>();
        Cursor c = null;
        try {
            String query = "SELECT msg_tmp.SENDER, msg_tmp.RECEIVER, file_msg_tmp.*, " +
                    "file_extra_tmp.RAW_FILE_URL, file_extra_tmp.TH_FILE_URL " +
                    "FROM file_msg_tmp LEFT JOIN msg_tmp ON msg_tmp._id = file_msg_tmp.MSG_ID " +
                    "LEFT JOIN file_extra_tmp ON msg_tmp._id = file_extra_tmp.MSG_ID;";

            c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                return;
            }

            c.moveToPosition(-1);
            while (c.moveToNext()) {
                FileMsgDb file = new FileMsgDb();
                file.setId(c.getLong(c.getColumnIndex("_id")));
                file.setFile_path(c.getString(c.getColumnIndex("FILE_URL")));
                file.setEncrypt_path(c.getString(c.getColumnIndex("FILE_URL")));// TODO: 2017/1/3 liming 
                file.setFile_name(c.getString(c.getColumnIndex("FILE_NAME")));
                file.setFile_size(c.getLong(c.getColumnIndex("FILE_SIZE")));
                file.setEncrypt_size(c.getLong(c.getColumnIndex("FILE_SIZE")));// TODO: 2017/1/3 liming
                file.setTranslate_size(c.getLong(c.getColumnIndex("TRANSLATE_SIZE")));
                file.setSuffix(c.getString(c.getColumnIndex("SUFFIX")));
                file.setFid(c.getString(c.getColumnIndex("FID")));
                file.setMsg_id(c.getLong(c.getColumnIndex("MSG_ID")));
                file.setType(c.getInt(c.getColumnIndex("FILE_TYPE")));

                if (file.isVoice()) {
                    file.setExtra_info(c.getString(c.getColumnIndex("EXTRA_INFO")));
                }

                if (file.isImage()) {
                    String extraInfo = c.getString(c.getColumnIndex("EXTRA_INFO"));
                    String thPath = c.getString(c.getColumnIndex("TH_FILE_URL"));
                    String rawPath = c.getString(c.getColumnIndex("RAW_FILE_URL"));
                    if (!TextUtils.isEmpty(extraInfo)) {
                        if (TextUtils.isEmpty(thPath) && TextUtils.isEmpty(rawPath))  {
                            // 存在未保存的图片缩略图和原图信息
                            MsgEntryDb msg = new MsgEntryDb();
                            msg.setId(file.getMsg_id());
                            msg.setSender(c.getString(c.getColumnIndex("SENDER")));
                            msg.setReceiver(c.getString(c.getColumnIndex("RECEIVER")));
                            HdThumbFileDb hd = ModelMapper.getIns().mapHdExtraInfo(extraInfo, msg);
                            RawFileDb raw = ModelMapper.getIns().mapRawExtraInfo(extraInfo, msg);

                            if (hd != null) {
                                hd.setHd_msg_id(file.getMsg_id());
                                hdList.add(hd);
                            }

                            if (raw != null) {
                                raw.setRaw_msg_id(file.getMsg_id());
                                rawList.add(raw);
                            }
                        }
                    }

                    file.setExtra_info("");
                }

                String sender = c.getString(c.getColumnIndex("SENDER"));
                long size = file.getFile_size();
                long tSize = file.getTranslate_size();

                if (TextUtils.equals(sender, account)) {
                    // 发送出去的消息
                    if (tSize == 0L) {
                        file.setFile_state(FileTState.UP_NON);
                    } else if (tSize == size) {
                        file.setFile_state(FileTState.UP_DONE);
                    } else {
                        file.setFile_state(FileTState.UP_FAIL);
                    }

                    // 发送出去的图片，需要将文件名和路径的dat去掉
                    file.setFile_path(removeDat(file.getFile_path()));
                    file.setFile_name(removeDat(file.getFile_name()));
                } else {
                    // 接收到的消息
                    if (tSize == 0L) {
                        file.setFile_state(FileTState.DOWN_NON);
                    } else if (tSize == size) {
                        file.setFile_state(FileTState.DOWN_DONE);
                    } else {
                        file.setFile_state(FileTState.DOWN_FAIL);
                    }
                }
                files.add(file);
            }

        }  finally {
            if (c != null) {
                c.close();
            }
        }

        if (!hdList.isEmpty()) {
            HdThumbFileDao.getInstance().insertBatchUpgrade(db, hdList);
        }

        if (!rawList.isEmpty()) {
            RawFileDao.getInstance().insertBatchUpgrade(db, rawList);
        }

        if (!files.isEmpty()) {
            FileMsgDao.getInstance().insertBatchUpgrade(db, files);
        }

    }

    private void upgradeMsg5To6(SQLiteDatabase db) {
        List<MessageWrapper> list = new ArrayList<>();
        int count;
        Cursor c = null;
        try {
            String query = "SELECT msg_tmp.*, file_msg_tmp.FILE_TYPE, file_msg_tmp.FILE_URL FROM msg_tmp " +
                    "LEFT JOIN file_msg_tmp ON msg_tmp._id = file_msg_tmp.MSG_ID;";

            c = db.rawQuery(query, null);
            count = c.getCount();
            if (count == 0) {
                return;
            }

            c.moveToPosition(-1);
            while (c.moveToNext()) {
                MessageWrapper wrapper = new MessageWrapper();

                MsgEntryDb msg = new MsgEntryDb();
                msg.setId(c.getLong(c.getColumnIndex("_id")));
                msg.setServer_id(c.getLong(c.getColumnIndex("SERVER_ID")));
                msg.setSender(c.getString(c.getColumnIndex("SENDER")));
                msg.setReceiver(c.getString(c.getColumnIndex("RECEIVER")));
                msg.setCard_id(c.getString(c.getColumnIndex("CARD_ID")));
                msg.setType(c.getInt(c.getColumnIndex("TYPE")));
                msg.setContent(c.getString(c.getColumnIndex("CONTENT")));
                msg.setState(c.getInt(c.getColumnIndex("STATE")));
                msg.setSession_flag(c.getString(c.getColumnIndex("SESSION_FLAG")));
                msg.setLife_time(c.getInt(c.getColumnIndex("LIFE_TIME")));

                int fileType = c.getInt(c.getColumnIndex("FILE_TYPE"));
                String filePath = c.getString(c.getColumnIndex("FILE_URL"));
                long dTime = c.getLong(c.getColumnIndex("DTIME"));
                long sTime = c.getLong(c.getColumnIndex("STIME")) / Constant.TIME_MULTIPLE;
                int failCode = c.getInt(c.getColumnIndex("FAIL_CODE"));
                int isNew = c.getInt(c.getColumnIndex("IS_NEW"));
                int modify = c.getInt(c.getColumnIndex("MODIFY"));// TODO: 2017/1/3 此字段可丢弃

                msg.setCreate_time(dTime);
                msg.setSent_time(sTime);

                // ATTR
                if (isNew == 0) {
                    //本账号发送的消息
                    msg.setAttr(Constant.MSG_SENT_OLD);
                }

                if (isNew == 1) {
                    //本账号接收的新消息
                    msg.setAttr(Constant.MSG_REC_NEW);
                }

                if (isNew == 2) {
                    //本账号接收的旧消息
                    msg.setAttr(Constant.MSG_REC_OLD);
                }

                // 失败错误码
                if (failCode == 703) {
                    msg.setState(State.NON_FRIENDS);
                }

                // SORT TIME
                if (TextUtils.equals(msg.getSender(), account)) {
                    // 发送出去的消息
                    msg.setSort_time(dTime / Constant.TIME_MULTIPLE);

                    // CONTENT, 发送的文件类型消息，file_msg已经存储了，不需要进行解析，可以拿到数据
                    if (msg.isFile()) {
                        if (fileType == ImSdkFileConstant.FILE_VOICE) {
                            msg.setContent(String.valueOf(ImSdkFileConstant.FILE_VOICE));
                        }

                        if (fileType == ImSdkFileConstant.FILE_IMAGE) {
                            msg.setContent(String.valueOf(ImSdkFileConstant.FILE_IMAGE));
                        }
                    }
                } else {
                    // 接收到的消息
                    msg.setSort_time(sTime);

                    // CONTENT
                    if (msg.isFile()) {
                        if (TextUtils.isEmpty(filePath)) {
                            // 接收到的文件类型消息未保存
                            String content = msg.getContent();

                            OldFile oldFile = JsonUtils.mapGson(content, OldFile.class);

                            if (oldFile != null && !TextUtils.isEmpty(oldFile.getFileUrl())) {
                                wrapper = ModelMapper.getIns().mapOldFile(wrapper, msg, oldFile);
                            }

                        } else {
                            // 接收到的文件类型消息已保存
                            if (fileType == ImSdkFileConstant.FILE_VOICE) {
                                msg.setContent(String.valueOf(ImSdkFileConstant.FILE_VOICE));
                            }

                            if (fileType == ImSdkFileConstant.FILE_IMAGE) {
                                msg.setContent(String.valueOf(ImSdkFileConstant.FILE_IMAGE));
                            }
                        }
                    }
                }

                wrapper.setMsgEntryDb(msg);

                list.add(wrapper);
            }

        }  finally {
            if (c != null) {
                c.close();
            }
        }

        if (list.isEmpty()) {
            return;
        }

        MsgEntryDao.getInstance().insertBatchUpgrade(db, list);
    }

    private void upgradeSession5To6(SQLiteDatabase db) {
        List<SessionEntryDb> sessions = new ArrayList<>();

        Cursor c = null;
        try {
            String query = "SELECT session_tmp.* FROM session_tmp;";

            c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                return;
            }

            c.moveToPosition(-1);
            while (c.moveToNext()) {
                SessionEntryDb session = new SessionEntryDb();
                session.setId(c.getLong(c.getColumnIndex("_id")));
                session.setIm_partner(c.getString(c.getColumnIndex("IM_PARTNER")));
                session.setSession_type(c.getInt(c.getColumnIndex("SESSION_TYPE")));
                session.setLast_msg(c.getLong(c.getColumnIndex("LAST_MSG")));
                session.setReminded(c.getInt(c.getColumnIndex("REMINDED")));
                session.setSession_flag(c.getString(c.getColumnIndex("SESSION_FLAG")));

                long time = c.getLong(c.getColumnIndex("DTIME"));

                session.setLast_time(time / Constant.TIME_MULTIPLE);
                session.setStart_time(time / Constant.TIME_MULTIPLE);

                sessions.add(session);
            }

        }  finally {
            if (c != null) {
                c.close();
            }
        }

        if (!sessions.isEmpty()) {
            SessionEntryDao.getInstance().insertBatchUpgrade(db, sessions);
        }
    }

    private void upgradeId5To6(SQLiteDatabase db) {
        List<DuplicateIdDb> dupList = new ArrayList<>();
        List<SyncIdDb> syncList = new ArrayList<>();

        Cursor c = null;
        try {
            String query = "SELECT id_ref_tmp.* FROM id_ref_tmp;";

            c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                return;
            }

            c.moveToPosition(-1);
            while (c.moveToNext()) {
                String idType = c.getString(c.getColumnIndex("ID_TYPE"));
                String idValue = c.getString(c.getColumnIndex("ID_VALUE"));

                if (TextUtils.equals("maxPreviousPullId", idType)) {
                    SyncIdDb max = new SyncIdDb();
                    max.setId_type(HttpApiConstant.MAX);
                    max.setId_value(idValue);
                    syncList.add(max);
                } else if (TextUtils.equals("lastPreviousPullId", idType)) {
                    SyncIdDb last = new SyncIdDb();
                    last.setId_type(HttpApiConstant.LAST);
                    last.setId_value(idValue);
                    syncList.add(last);
                } else if (TextUtils.equals("handledId", idType)) {
                    SyncIdDb process = new SyncIdDb();
                    process.setId_type(HttpApiConstant.PROCESS);
                    process.setId_value(idValue);
                    syncList.add(process);
                } else if (TextUtils.equals("prePullEndState", idType)) {
                    SyncIdDb state = new SyncIdDb();
                    state.setId_type(HttpApiConstant.STATE);
                    state.setId_value(idValue);
                    syncList.add(state);
                } else {
                    DuplicateIdDb dup = new DuplicateIdDb();
                    dup.setSend_time(idType);
                    dup.setServer_id(idValue);
                    dupList.add(dup);
                }
            }

        }  finally {
            if (c != null) {
                c.close();
            }
        }

        if (!dupList.isEmpty()) {
            DuplicateIdDao.getInstance().insertBatchUpgrade(db, dupList);
        }

        if (!syncList.isEmpty()) {
            SyncIdDao.getInstance().insertBatchUpgrade(db, syncList);
        }
    }

    private String removeDat(String name) {
        return ToolUtils.subString(name, Constant.ENCRYPT_SUFFIX_SUB);
    }

    private static final String SQL_ALTER_OLD_ID_REFERENCE =
            "ALTER TABLE id_reference RENAME TO id_ref_tmp";

    private static final String SQL_ALTER_OLD_FILE_MSG =
            "ALTER TABLE file_msg RENAME TO file_msg_tmp";

    private static final String SQL_ALTER_OLD_FILE_EXTRA =
            "ALTER TABLE file_extra_msg RENAME TO file_extra_tmp";

    private static final String SQL_ALTER_OLD_MSG_ENTRY =
            "ALTER TABLE msg_entry RENAME TO msg_tmp";

    private static final String SQL_ALTER_OLD_OPTIONS =
            "ALTER TABLE OPTIONS RENAME TO options_tmp";

    private static final String SQL_ALTER_OLD_SESSION_ENTRY =
            "ALTER TABLE session_entry RENAME TO session_tmp";

    private static final String SQL_DROP_TRIGGER_MSG_UPDATE =
            "DROP TRIGGER IF EXISTS session_update_on_insert_trigger";

    private static final String SQL_DROP_TRIGGER_MSG_DELETE_INSERT =
            "DROP TRIGGER IF EXISTS msg_deleted_on_insert_trigger";

    private static final String SQL_DROP_TRIGGER_MSG_DELETE =
            "DROP TRIGGER IF EXISTS msg_insert_deleted_on_delete_trigger";

    private static final String SQL_DROP_TRIGGER_MSG_DELETE_UPDATE =
            "DROP TRIGGER IF EXISTS file_msg_delete_on_update_trigger";

    private static final String SQL_DROP_TRIGGER_MSG_DELETE_DELETE =
            "DROP TRIGGER IF EXISTS file_msg_delete_on_delete_trigger";

    private static final String SQL_DROP_TRIGGER_SESSION_DELETE =
            "DROP TRIGGER IF EXISTS session_delete_msg_on_delete_trigger";

    private static final String SQL_DROP_OLD_ID = "DROP TABLE id_ref_tmp;";

    private static final String SQL_DROP_FILE_MSG = "DROP TABLE file_msg_tmp;";

    private static final String SQL_DROP_FILE_EXTRA = "DROP TABLE file_extra_tmp;";

    private static final String SQL_DROP_OPTIONS = "DROP TABLE options_tmp;";

    private static final String SQL_DROP_MSG = "DROP TABLE msg_tmp;";

    private static final String SQL_DROP_SESSION = "DROP TABLE session_tmp;";
}
