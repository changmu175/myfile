package com.xdja.imsdk.db.helper;

import android.content.ContentValues;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.BodyType;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.builder.DeletedMsgBuilder;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.builder.HdThumbFileBuilder;
import com.xdja.imsdk.db.builder.MsgEntryBuilder;
import com.xdja.imsdk.db.builder.RawFileBuilder;
import com.xdja.imsdk.db.builder.SessionEntryBuilder;
import com.xdja.imsdk.db.builder.SyncIdBuilder;
import com.xdja.imsdk.manager.ImMsgManager;

import java.util.List;
import java.util.Set;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：数据库操作参数生成器                <br>
 * 创建时间：2016/11/28 19:07                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OptHelper {
    private static OptHelper optHelper;

    public static OptHelper getIns(){
        synchronized(OptHelper.class) {
            if(optHelper == null){
                optHelper =  Factory.getInstance();
            }
        }
        return optHelper;
    }

    private static class Factory {
        static OptHelper getInstance() {
            return new OptHelper();
        }
    }

    /***************************************
     * ********** 查询语句生成 ************* *
     ***************************************/

    /**
     * [session_entry REMIND] 查询会话提醒数量语句参数
     * @param tag 空时为查询所有
     * @return QueryArgs
     */
    public String getRQuery(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return SessionEntryBuilder.queryR(tag);
        } else {
            return SessionEntryBuilder.queryRSum();
        }
    }

    /**
     * [msg_entry _id] 查询tag中最后一条消息
     * @param tag tag
     * @return String
     */
    public String getMQuery(String tag) {
        return MsgEntryBuilder.queryMax(tag);
    }

    /**
     * [session_entry, msg_entry] 查询tags对应的会话，需要联合查询消息
     * @param tags tags
     * @return String
     */
    public String getSQuery(Set<String> tags) {
        return SessionEntryBuilder.queryST(tags);
    }

    /**
     * [session_entry] 查询消息id对应的会话，不需要联合查询消息
     * @param id id
     * @return QueryArgs
     */
    public String getSMIQuery(long id) {
        return SessionEntryBuilder.querySI(id);
    }

    /**
     * [session_entry, msg_entry] 查询tag对应的会话，需要联合查询消息
     * @param tag tag
     * @return String
     */
    public String getSMQuery(String tag) {
        return SessionEntryBuilder.querySM(tag);
    }

    /**
     * [session_entry] 查询tag对应的会话，不需要联合查询消息
     * @param tag tag
     * @return String
     */
    public String getSQuery(String tag) {
        return SessionEntryBuilder.queryS(tag);
    }

    /**
     * [session_entry SESSION_FLAG] 查询所有会话的tags
     * @return String
     */
    public String getTQuery() {
        return SessionEntryBuilder.queryT();
    }

    /**
     * [msg_entry, file_msg] 查询指定 size 消息
     * @param tag tag
     * @param begin begin
     * @param size size
     * @return String
     */
    public String getMQuery(String tag, long begin, int size) {
        return MsgEntryBuilder.queryShow(tag, begin, size);
    }

    /**
     * [msg_entry, file_msg, hd_file, raw_file] 查询指定 size 高清缩略图
     * @param tag tag
     * @param begin begin
     * @param size size
     * @return String
     */
    public String getIQuery(String tag, long begin, int size) {
        return MsgEntryBuilder.queryImage(tag, begin, size);
    }

    /**
     * [msg_entry, file_msg, raw_file] 查询文件
     * @param tag tag
     * @return String
     */
    public String getFQuery(String tag) {
        return MsgEntryBuilder.queryFile(tag);
    }

    /**
     * [msg_entry, file_msg, raw_file] 查询文件
     * @return String
     */
    public String getFQuery() {
        return MsgEntryBuilder.queryFile();
    }

    /**
     * [msg_entry, file_msg, hd_file, raw_file]查询指定消息id的消息，包括所有的关联表
     * @param id id
     * @return String
     */
    public String getAMQuery(long id) {
        return MsgEntryBuilder.queryAMI(id);
    }

    /**
     * [msg_entry] 查询指定消息id的消息
     * @param id id
     * @return String
     */
    public String getMIQuery(long id) {
        return MsgEntryBuilder.queryMI(id);
    }

    /**
     * [msg_entry] 查询已阅读状态闪信的消息
     * @param account account
     * @param tag tag
     * @return String
     */
    public String getARBQuery(String account, String tag) {
        if (TextUtils.isEmpty(tag)) {
            return MsgEntryBuilder.queryARM(account);
        } else {
            StringBuilder builder = new StringBuilder(" WHERE ");
            builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SESSION_FLAG);
            builder.append(" = \'").append(tag).append("\'");

            return MsgEntryBuilder.queryRM(account, builder.toString());
        }
    }

    /**
     * [msg_entry] 查询会话tags中已阅读状态闪信的消息
     * @param account account
     * @param tags tags
     * @return String
     */
    public String getARBQuery(String account, List<String> tags) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SESSION_FLAG);
        SqlBuilder.appendColumn(builder, tags);

        return MsgEntryBuilder.queryRM(account, builder.toString());
    }

    /**
     * [msg_entry] 查询消息id中已阅读状态闪信的消息
     * @param account account
     * @param ids ids
     * @return String
     */
    public String getMRBQuery(String account, List<Long> ids) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.ID);
        SqlBuilder.appendColumn(builder, ids);

        return MsgEntryBuilder.queryRM(account, builder.toString());
    }

    /**
     * [msg_entry] 查询正在发送状态消息的id
     * @param account account
     * @param tag tag
     * @return String
     */
    public String getIngQuery(String account, String tag) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SESSION_FLAG);
        builder.append(" = \'").append(tag).append("\'");
        return MsgEntryBuilder.queryMIng(account, builder.toString());
    }

    /**
     * [msg_entry] 查询正在发送状态消息的id
     * @param account account
     * @param tags tags
     * @return String
     */
    public String getIngQuery(String account, List<String> tags) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SESSION_FLAG);
        SqlBuilder.appendColumn(builder, tags);
        return MsgEntryBuilder.queryMIng(account, builder.toString());
    }

    /**
     * [msg_entry fst] 查询所有发送失败消息的创建时间
     * @return String
     */
    public String getFailQuery(String account) {
        return MsgEntryBuilder.queryFail(account);
    }

    /**
     * [msg_entry] 查询相同fst已保存消息[CREATE_TIME, SENDER, RECEIVER, TYPE, STATE]
     * @param fsts fsts
     * @return String
     */
    public String getDupQuery(List<Long> fsts) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.CREATE_TIME);
        SqlBuilder.appendColumn(builder, fsts);
        return MsgEntryBuilder.querySameFst(builder.toString());
    }

    /**
     * [msg_entry server id] 查询已保存的消息的server ids
     * @param serverIds serverIds
     * @return String
     */
    public String getSaveQuery(List<Long> serverIds) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SERVER_ID);
        SqlBuilder.appendColumn(builder, serverIds);
        return MsgEntryBuilder.querySaved(builder.toString());
    }

    /**
     * [msg_entry] 查询消息server id和state的map
     * @param serverIds server ids
     * @return String
     */
    public String getStates(List<Long> serverIds) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SERVER_ID);
        SqlBuilder.appendColumn(builder, serverIds);
        return MsgEntryBuilder.queryState(builder.toString());
    }

    /**
     * [msg_entry] 查询指定server id的消息
     * @param serverIds serverIds
     * @return String
     */
    public String getMSQuery(List<Long> serverIds) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SERVER_ID);
        SqlBuilder.appendColumn(builder, serverIds);
        return MsgEntryBuilder.queryMS(builder.toString());
    }

    /**
     * [msg_entry, file_msg] 查询指定消息的show信息
     * @param id id
     * @return String
     */
    public String getFQuery(long id, FileType type) {
        return MsgEntryBuilder.queryFile(id, type);
    }

    /**
     * [deleted_msg] 查询删除的消息server ids
     * @param serverIds serverIds
     * @return String
     */
    public String getDelQuery(List<Long> serverIds) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        builder.append(DeletedMsgBuilder.TABLE_NAME).append(".").append(DeletedMsgBuilder.SERVER_ID);
        SqlBuilder.appendColumn(builder, serverIds);
        return DeletedMsgBuilder.queryIds(builder.toString());
    }



    /***************************************
     * ********** UPDATE 参数生成 ************* *
     ***************************************/
    /**
     * 生成新消息数量更新语句 updateSession
     * @param tag tag
     * @param remind remind
     * @return UpdateArgs
     */
    public UpdateArgs getRUpdate(String tag, int remind) {
        UpdateArgs args = new UpdateArgs( SessionEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(SessionEntryBuilder.REMINDED, String.valueOf(remind));

        String whereClause[] = new String[] { SessionEntryBuilder.SESSION_FLAG };

        String whereArgs[] = new String[] { tag };

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 生成会话最后一条消息id更新语句
     * @param tag tag
     * @param lastId lastId
     * @return UpdateArgs
     */
    public UpdateArgs getLUpdate(String tag, long lastId, long time) {
        UpdateArgs args = new UpdateArgs(SessionEntryBuilder.TABLE_NAME);

        ContentValues values = new ContentValues();
        values.put(SessionEntryBuilder.LAST_MSG, String.valueOf(lastId));
        values.put(SessionEntryBuilder.LAST_TIME, String.valueOf(time));
        String whereClause[] = new String[] { SessionEntryBuilder.SESSION_FLAG };
        String whereArgs[] = new String[] { tag };

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 生成闪信更新参数
     * @param type type
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getBombsUpdate(BodyType type, long id) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);

        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, State.BOMB);

        switch (type) {
            case TEXT:
                values.put(MsgEntryBuilder.CONTENT, "");
                break;
            case VOICE:
                values.put(MsgEntryBuilder.CONTENT, String.valueOf(ImSdkFileConstant.FILE_VOICE));
                break;
            case IMAGE:
                values.put(MsgEntryBuilder.CONTENT, String.valueOf(ImSdkFileConstant.FILE_IMAGE));
                break;
            case VIDEO:
                values.put(MsgEntryBuilder.CONTENT, String.valueOf(ImSdkFileConstant.FILE_VIDEO));
                break;
            case NORMAL:
                values.put(MsgEntryBuilder.CONTENT, String.valueOf(ImSdkFileConstant.FILE_NORMAL));
                break;
            case UNKNOWN:
                values.put(MsgEntryBuilder.CONTENT, String.valueOf(ImSdkFileConstant.FILE_UNKNOWN));
                break;
        }

        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {String.valueOf(id)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新发送成功的消息的 server id, state, arrive time, attr(?)
     * @param id id
     * @param serverId serverId
     * @return UpdateArgs
     */
    public UpdateArgs getMSUpdate(String id, String serverId) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, State.SENT);
        values.put(MsgEntryBuilder.SERVER_ID, serverId);
        values.put(MsgEntryBuilder.SENT_TIME, ImMsgManager.getInstance().getCurrentM());
        // attr // TODO: 2016/12/21 liming attr 的更新

        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {id};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新发送成功的消息的 state
     * @param id id
     * @param code code
     * @return UpdateArgs
     */
    public UpdateArgs getMFUpdate(String id, int code) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        if (code == Constant.SERVER_FORBID) {
            values.put(MsgEntryBuilder.STATE, String.valueOf(State.NON_FRIENDS));
        } else {
            values.put(MsgEntryBuilder.STATE, String.valueOf(State.SENT_FAIL));
        }

        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {id};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新“发送中”状态的消息状态为“发送失败”
     * @return UpdateArgs
     */
    public UpdateArgs getAAUpdate(int aState, int fail) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, fail);
        String whereClause[] = new String[] { MsgEntryBuilder.STATE };
        String[] whereArgs = new String[] {String.valueOf(aState)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新文件表中的[loading ==> fail]
     * @param aState aState
     * @param fail fail
     * @return UpdateArgs
     */
    public UpdateArgs getFAUpdate(int aState, int fail) {
        UpdateArgs args = new UpdateArgs(FileMsgBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(FileMsgBuilder.FILE_STATE, fail);
        String whereClause[] = new String[] { FileMsgBuilder.FILE_STATE };
        String[] whereArgs = new String[] {String.valueOf(aState)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新高清表中的[loading ==> fail]
     * @param aState aState
     * @param fail fail
     * @return UpdateArgs
     */
    public UpdateArgs getHAUpdate(int aState, int fail) {
        UpdateArgs args = new UpdateArgs(HdThumbFileBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(HdThumbFileBuilder.HD_STATE, fail);
        String whereClause[] = new String[] { HdThumbFileBuilder.HD_STATE };
        String[] whereArgs = new String[] {String.valueOf(aState)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新源文件表中的[loading ==> fail]
     * @param aState aState
     * @param fail fail
     * @return UpdateArgs
     */
    public UpdateArgs getRAUpdate(int aState, int fail) {
        UpdateArgs args = new UpdateArgs(RawFileBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(RawFileBuilder.RAW_STATE, fail);
        String whereClause[] = new String[] { RawFileBuilder.RAW_STATE };
        String[] whereArgs = new String[] {String.valueOf(aState)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新消息状态改变
     * @param id id
     * @param state state
     * @return UpdateArgs
     */
    public UpdateArgs getMCUpdate(long id, int state) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, state);
        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {String.valueOf(id)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 文件上传失败，更新消息状态
     * @param id id
     * @param state state
     * @return UpdateArgs
     */
    public UpdateArgs getMSUpdate(long id, int state) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, state);
        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {String.valueOf(id)};

        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 文件加密后的更新参数
     * @param type type
     * @param path path
     * @param encryptSize encryptSize
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getEFUpdate(FileType type, String path, long encryptSize, int state, long id) {
        UpdateArgs args = new UpdateArgs();
        ContentValues values = new ContentValues();
        String whereClause[] = new String[]{};
        String whereArgs[] = new String[]{String.valueOf(id)};
        switch (type) {
            case IS_SHOW:
                args.setName(FileMsgBuilder.TABLE_NAME);
                values.put(FileMsgBuilder.ENCRYPT_PATH, path);
                values.put(FileMsgBuilder.ENCRYPT_SIZE, encryptSize);
                values.put(FileMsgBuilder.FILE_STATE, state);
                whereClause = new String[]{FileMsgBuilder.MSG_ID};
                break;
            case IS_HD:
                args.setName(HdThumbFileBuilder.TABLE_NAME);
                values.put(HdThumbFileBuilder.HD_ENCRYPT_PATH, path);
                values.put(HdThumbFileBuilder.HD_ENCRYPT_SIZE, encryptSize);
                values.put(HdThumbFileBuilder.HD_STATE, state);
                whereClause = new String[]{HdThumbFileBuilder.HD_MSG_ID};
                break;
            case IS_RAW:
                args.setName(RawFileBuilder.TABLE_NAME);
                values.put(RawFileBuilder.RAW_ENCRYPT_PATH, path);
                values.put(RawFileBuilder.RAW_ENCRYPT_SIZE, encryptSize);
                values.put(RawFileBuilder.RAW_STATE, state);
                whereClause = new String[]{RawFileBuilder.RAW_MSG_ID};
                break;
        }

        args.setValues(values);
        args.setWhereArgs(whereArgs);
        args.setWhereClause(whereClause);
        return args;
    }

    /**
     * 文件开始上传的更新参数
     * @param type type
     * @param fid fid
     * @param state state
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getFFUpdate(FileType type, String fid, int state, long id) {

        UpdateArgs args = new UpdateArgs();
        ContentValues values = new ContentValues();
        String whereClause[] = new String[]{};
        String whereArgs[] = new String[]{String.valueOf(id)};
        switch (type) {
            case IS_SHOW:
                args.setName(FileMsgBuilder.TABLE_NAME);
                values.put(FileMsgBuilder.FID, fid);
                values.put(FileMsgBuilder.FILE_STATE, state);
                whereClause = new String[]{FileMsgBuilder.MSG_ID};
                break;
            case IS_HD:
                args.setName(HdThumbFileBuilder.TABLE_NAME);
                values.put(HdThumbFileBuilder.HD_FID, fid);
                values.put(HdThumbFileBuilder.HD_STATE, state);
                whereClause = new String[]{HdThumbFileBuilder.HD_MSG_ID};
                break;
            case IS_RAW:
                args.setName(RawFileBuilder.TABLE_NAME);
                values.put(RawFileBuilder.RAW_FID, fid);
                values.put(RawFileBuilder.RAW_STATE, state);
                whereClause = new String[]{RawFileBuilder.RAW_MSG_ID};
                break;
        }

        args.setValues(values);
        args.setWhereArgs(whereArgs);
        args.setWhereClause(whereClause);
        return args;
    }

    /**
     * 文件传输过程中的更新参数
     * @param type type
     * @param size size
     * @param state state
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getFTUpdate(FileType type, long size, int state, long id) {

        UpdateArgs args = new UpdateArgs();
        ContentValues values = new ContentValues();
        String whereClause[] = new String[]{};
        String whereArgs[] = new String[]{String.valueOf(id)};
        switch (type) {
            case IS_SHOW:
                args.setName(FileMsgBuilder.TABLE_NAME);
                if (size >= 0) {
                    values.put(FileMsgBuilder.TRANSLATE_SIZE, size);
                }
                values.put(FileMsgBuilder.FILE_STATE, state);
                whereClause = new String[]{FileMsgBuilder.MSG_ID};
                break;
            case IS_HD:
                args.setName(HdThumbFileBuilder.TABLE_NAME);
                if (size >= 0) {
                    values.put(HdThumbFileBuilder.HD_TRANSLATE_SIZE, size);
                }
                values.put(HdThumbFileBuilder.HD_STATE, state);
                whereClause = new String[]{HdThumbFileBuilder.HD_MSG_ID};
                break;
            case IS_RAW:
                args.setName(RawFileBuilder.TABLE_NAME);
                if (size >= 0) {
                    values.put(RawFileBuilder.RAW_TRANSLATE_SIZE, size);
                }
                values.put(RawFileBuilder.RAW_STATE, state);
                whereClause = new String[]{RawFileBuilder.RAW_MSG_ID};
                break;
        }

        args.setValues(values);
        args.setWhereArgs(whereArgs);
        args.setWhereClause(whereClause);
        return args;
    }

    /**
     * 文件解密成功后的更新参数
     * @param type type
     * @param size size
     * @param state state
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getDFUpdate(FileType type, String path, long size, int state, long id) {
        UpdateArgs args = new UpdateArgs();
        ContentValues values = new ContentValues();
        String whereClause[] = new String[]{};
        String whereArgs[] = new String[]{String.valueOf(id)};
        switch (type) {
            case IS_SHOW:
                args.setName(FileMsgBuilder.TABLE_NAME);
                values.put(FileMsgBuilder.FILE_PATH, path);
                values.put(FileMsgBuilder.FILE_SIZE, size);
                values.put(FileMsgBuilder.FILE_STATE, state);
                whereClause = new String[]{FileMsgBuilder.MSG_ID};
                break;
            case IS_HD:
                args.setName(HdThumbFileBuilder.TABLE_NAME);
                values.put(HdThumbFileBuilder.HD_FILE_PATH, path);
                values.put(HdThumbFileBuilder.HD_FILE_SIZE, size);
                values.put(HdThumbFileBuilder.HD_STATE, state);
                whereClause = new String[]{HdThumbFileBuilder.HD_MSG_ID};
                break;
            case IS_RAW:
                args.setName(RawFileBuilder.TABLE_NAME);
                values.put(RawFileBuilder.RAW_FILE_PATH, path);
                values.put(RawFileBuilder.RAW_FILE_SIZE, size);
                values.put(RawFileBuilder.RAW_STATE, state);
                whereClause = new String[]{RawFileBuilder.RAW_MSG_ID};
                break;
        }

        args.setValues(values);
        args.setWhereArgs(whereArgs);
        args.setWhereClause(whereClause);
        return args;
    }

    /**
     * 更新消息状态
     * @param serverId serverId
     * @param state state
     * @return getSUpdate
     */
    public UpdateArgs getSUpdate(long serverId, int state) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, state);
        String[] whereArgs = new String[] {String.valueOf(serverId), String.valueOf(state)};

        args.setValues(values);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新消息状态
     * @param id id
     * @param state state
     * @return getSUpdate
     */
    public UpdateArgs getEUpdate(long id, int state, String content) {
        UpdateArgs args = new UpdateArgs(MsgEntryBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(MsgEntryBuilder.STATE, state);
        values.put(MsgEntryBuilder.CONTENT, content);
        String whereClause[] = new String[] { MsgEntryBuilder.ID };
        String[] whereArgs = new String[] {String.valueOf(id)};
        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }

    /**
     * 更新同步消息id
     * @param type type
     * @param id id
     * @return UpdateArgs
     */
    public UpdateArgs getSyncUpdate(String type, long id) {
        UpdateArgs args = new UpdateArgs(SyncIdBuilder.TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(SyncIdBuilder.ID_VALUE, id);

        String whereClause[] = new String[] { SyncIdBuilder.ID_TYPE };
        String[] whereArgs = new String[] {type};
        args.setValues(values);
        args.setWhereClause(whereClause);
        args.setWhereArgs(whereArgs);
        return args;
    }


    /***************************************
     * ********** DELETE 参数生成 ************* *
     ***************************************/

    /**
     * 指定会话删除参数 deleteSession
     * @param tags tags
     * @return DelArgs
     */
    public DelArgs getSDel(List<String> tags) {
        DelArgs args = new DelArgs(OptType.DEL_TYPE_1);
        args.setTags(tags);
        return args;
    }

    /**
     * 所有会话删除参数 deleteAllSession
     * @return DelArgs
     */
    public DelArgs getSADel() {
        return new DelArgs(OptType.DEL_TYPE_2);
    }

    /**
     * 删除指定消息参数 deleteMessage
     * @param ids ids
     * @return DelArgs
     */
    public DelArgs getMDel(List<Long> ids) {
        DelArgs args = new DelArgs(OptType.DEL_TYPE_3);
        args.setIds(ids);
        return args;
    }

    /**
     * 会话中所有消息删除参数 deleteSessionAllMessage
     * @param tag tag
     * @return DelArgs
     */
    public DelArgs getMSDel(String tag) {
        DelArgs args = new DelArgs(OptType.DEL_TYPE_4);
        args.setTag(tag);
        return args;
    }

    /**
     * 本地状态消息删除参数
     * @param id id
     * @return DelArgs
     */
    public DelArgs getLSDel(String id) {
        DelArgs args = new DelArgs(OptType.DEL_TYPE_5);
        args.setTag(id);
        return args;
    }
}
