package com.xdja.imsdk.db;

import android.content.ContentValues;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.db.bean.DuplicateIdDb;
import com.xdja.imsdk.db.bean.LocalStateMsgDb;
import com.xdja.imsdk.db.bean.OptionsDb;
import com.xdja.imsdk.db.bean.SyncIdDb;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.builder.HdThumbFileBuilder;
import com.xdja.imsdk.db.builder.LocalStateMsgBuilder;
import com.xdja.imsdk.db.builder.MsgEntryBuilder;
import com.xdja.imsdk.db.builder.RawFileBuilder;
import com.xdja.imsdk.db.builder.SessionEntryBuilder;
import com.xdja.imsdk.db.builder.SyncIdBuilder;
import com.xdja.imsdk.db.dao.DeletedMsgDao;
import com.xdja.imsdk.db.dao.FileMsgDao;
import com.xdja.imsdk.db.dao.HdThumbFileDao;
import com.xdja.imsdk.db.dao.LocalStateMsgDao;
import com.xdja.imsdk.db.dao.MsgEntryDao;
import com.xdja.imsdk.db.dao.OptionsDao;
import com.xdja.imsdk.db.dao.RawFileDao;
import com.xdja.imsdk.db.dao.SessionEntryDao;
import com.xdja.imsdk.db.dao.SyncIdDao;
import com.xdja.imsdk.db.helper.DelArgs;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.OptType.MQuery;
import com.xdja.imsdk.db.helper.OptType.SQuery;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.manager.ModelMapper;
import com.xdja.imsdk.model.internal.IMState;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：数据库操作接口                     <br>
 * 创建时间：2016/11/26 17:25                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ImSdkDbUtils {
    /***************************************
     * ************* 增 ****************** *
     ***************************************/
    /**
     * 保存单条消息
     * @param wrapper 消息
     * @return 消息id
     */
    public static long saveMessage(MessageWrapper wrapper) {
        long rawId;
        rawId = MsgEntryDao.getInstance().insert(wrapper);
        return rawId;
    }

    /**
     * 保存单个会话
     * @param wrapper 会话
     * @return 会话id
     */
    public static long saveSession(SessionWrapper wrapper) {
        long rawId;
        rawId = SessionEntryDao.getInstance().insert(wrapper);
        return rawId;
    }

    /**
     * 保存所有配置项
     * @param optionMap 配置项
     */
    public static void saveOptions(Map<String, String> optionMap) {
        List<OptionsDb> dbs = ModelMapper.getIns().mapOption(optionMap);
        OptionsDao.getInstance().insertBatch(dbs);
    }

    /**
     * 保存接收到的消息，开启事务统一处理，需要测试效率 
     * @param sessions sessions
     * @param messages messages
     * @param dupIds dupIds
     * @param bombs bombs
     * @param dupes dupes
     */
    public static void saveReceived(List<SessionWrapper> sessions, List<MessageWrapper> messages,
                                    List<DuplicateIdDb> dupIds, List<IMState> bombs,
                                    List<IMState> dupes, List<IMState> recs) {
        List<LocalStateMsgDb> lBombs = ModelMapper.getIns().mapLStates(bombs);
        List<LocalStateMsgDb> lDupes = ModelMapper.getIns().mapLStates(dupes);
        List<LocalStateMsgDb> lRecs = ModelMapper.getIns().mapLStates(recs);

        MsgEntryDao.getInstance().insertBatch(sessions, messages, dupIds, lBombs, lDupes, lRecs);
    }

    /**
     * 批量保存同步消息id
     * @param syncIds syncIds
     */
    public static void saveSyncIdBatch(List<SyncIdDb> syncIds) {
        if (SyncIdDao.getInstance().getCount() == 0) {
            SyncIdDao.getInstance().insertBatch(syncIds);
        }
    }

    /**
     * 保存失败的状态消息
     * @param local local
     * @return long
     */
    public static long saveLocal(LocalStateMsgDb local) {
        return LocalStateMsgDao.getInstance().insert(local);
    }


    /***************************************
     * ************* 删 ****************** *
     ***************************************/
    /**
     * 删除
     * @param args args
     * @return int
     */
    public static int delete(DelArgs args) {
        int result = 0;
        switch (args.getType()) {
            case OptType.DEL_TYPE_1:
            case OptType.DEL_TYPE_2:
                result = SessionEntryDao.getInstance().deleteS(delArgs2Sql(args));
                break;

            case OptType.DEL_TYPE_3:
            case OptType.DEL_TYPE_4:
                result = MsgEntryDao.getInstance().deleteMsg(delArgs2Sql(args));
                break;

            case OptType.DEL_TYPE_5:
                result = LocalStateMsgDao.getInstance().deleteL(delArgs2Sql(args));
                break;
            default:
                break;
        }

        return result;
    }

    /***************************************
     * ************* 改 ****************** *
     ***************************************/
    /**
     * 更新
     * @param args args
     * @return int
     */
    public static int update(UpdateArgs args) {
        if (args == null) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        ContentValues values = args.getValues();
        String whereClause[] = args.getWhereClause();
        String whereArgs[] = args.getWhereArgs();

        if (values.size() == 0 ||
                whereClause == null ||
                whereArgs == null ||
                whereClause.length != whereArgs.length) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        switch (args.getName()) {
            case SessionEntryBuilder.TABLE_NAME:
                SessionEntryDao.getInstance().updateS(args);
                break;
            case MsgEntryBuilder.TABLE_NAME:
                MsgEntryDao.getInstance().updateM(args);
                break;
            case FileMsgBuilder.TABLE_NAME:
                FileMsgDao.getInstance().updateF(args);
                break;
            case HdThumbFileBuilder.TABLE_NAME:
                HdThumbFileDao.getInstance().updateH(args);
                break;
            case RawFileBuilder.TABLE_NAME:
                RawFileDao.getInstance().updateR(args);
                break;
            case SyncIdBuilder.TABLE_NAME:
                SyncIdDao.getInstance().updateS(args);
                break;
            default:
                break;
        }
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 单表多条记录更新
     * @param args args
     */
    public static void updateBatch(List<UpdateArgs> args) {
        if (args == null || args.isEmpty()) {
            return;
        }

        if (MsgEntryBuilder.TABLE_NAME.equals(args.get(0).getName())) {
            MsgEntryDao.getInstance().updateMBatch(args);
        }

        if (SyncIdBuilder.TABLE_NAME.equals(args.get(0).getName())) {
            SyncIdDao.getInstance().updateSBatch(args);
        }
    }

    /**
     * 开启事务，更新消息状态和文件状态
     * @param file file
     * @param message message
     */
    public static void updateMF(UpdateArgs message, UpdateArgs file) {
        MsgEntryDao.getInstance().updateMFState(message, file);
    }

    public static void updateEF(UpdateArgs msg, UpdateArgs file, UpdateArgs hd, UpdateArgs raw) {
        MsgEntryDao.getInstance().updateEF(msg, file, hd, raw);
    }

    /**
     * 开启事务，更新并保存
     * 1、更新消息状态，保存状态消息
     */
    public static void updateAndSave(UpdateArgs args, IMState state) {
        LocalStateMsgDb lState = ModelMapper.getIns().mapState(state);
        MsgEntryDao.getInstance().updateAndSave(args, lState);
    }

    /**
     * 开启事务，批量更新并保存
     * @param args args
     * @param states states
     */
    public static void updateAndSaveBatch(List<UpdateArgs> args, List<IMState> states) {
        List<LocalStateMsgDb> lStates = ModelMapper.getIns().mapLStates(states);

        MsgEntryDao.getInstance().updateAndSaveBatch(args, lStates);
    }

    /***************************************
     * ************* 查 ****************** *
     ***************************************/
    /**
     * 查询会话列表，需要联合查询消息
     * @param tag tag
     * @param size size
     * @return List
     */
    public static List<SessionWrapper> querySessions(String tag, int size) {
        return SessionEntryDao.getInstance().getSessions(tag, size);
    }

    /**
     * 查询会话列表
     * @param query query
     * @param type type
     * @return List
     */
    public static List<SessionWrapper> querySessions(String query, SQuery type) {
        return SessionEntryDao.getInstance().getSessions(query, type);
    }

    /**
     * 查询会话
     * @param query query
     * @param type type
     * @return SessionWrapper
     */
    public static SessionWrapper querySession(String query, SQuery type) {
        return SessionEntryDao.getInstance().getSession(query, type);
    }

    /**
     * 查询消息列表
     * @param query query
     * @param type type
     * @return List
     */
    public static List<MessageWrapper> queryMessages(String query, MQuery type) {
        return MsgEntryDao.getInstance().getMessages(query, type);
    }

    /**
     * 查询相同fst已保存消息[CREATE_TIME, SENDER, RECEIVER, TYPE, STATE]
     * @param query query
     * @return List
     */
    public static List<MessageWrapper> queryDup(String query) {
        return MsgEntryDao.getInstance().getDup(query);
    }

    /**
     * 查询消息
     * @param query query
     * @param type type
     * @return List
     */
    public static MessageWrapper queryMessage(String query, MQuery type) {
        return MsgEntryDao.getInstance().getMessage(query, type);
    }

    /**
     * 查询消息
     * @param query query
     * @param type type
     * @return List
     */
    public static MessageWrapper queryMessage(String query, FileType type) {
        return MsgEntryDao.getInstance().getMessage(query, type);
    }

    /**
     * 查询会话新消息提醒数量
     * @param query query
     * @return int
     */
    public static int queryRemindCount(String query) {
        return SessionEntryDao.getInstance().getRemind(query);
    }


    /**
     * 查询所有会话的session flag
     * @return List
     */
    public static List<String> querySessions(String query) {
        return SessionEntryDao.getInstance().querySessions(query);
    }

    /**
     * 查询消息id列表
     * 查询指定状态的消息id列表
     * @param query query
     * @return List
     */
    public static List<Long> queryIds(String query) {
        return MsgEntryDao.getInstance().getIds(query);
    }

    /**
     * 查询所有发送失败的消息fst列表
     * @param query query
     * @return List
     */
    public static List<Long> queryFst(String query) {
        return MsgEntryDao.getInstance().getFsts(query);
    }



    /**
     * 查询指定server id的消息已经被保存的server id 和state
     * @param query query
     * @return Map
     */
    public static Map<Long, Integer> queryMStates(String query) {
        return MsgEntryDao.getInstance().getStates(query);
    }

    /**
     * 查询本地所有状态消息
     * @return List
     */
    public static List<LocalStateMsgDb> queryStates() {
        return LocalStateMsgDao.getInstance().getStates();
    }

    /**
     * 查询所有配置项
     * @return Map
     */
    public static Map<String, String> queryOptions() {
        return ModelMapper.getIns().mapOption(OptionsDao.getInstance().getAll());
    }

    /**
     * [sync_id] 查询同步id
     * @param key key
     * @return long
     */
    public static long querySyncId(String key) {
        return SyncIdDao.getInstance().getSyncId(key);
    }

    /**
     * [deleted_msg] 查询删除表中已删除的消息server ids
     * @param query query
     * @return List
     */
    public static List<Long> queryDelIds(String query) {
        return DeletedMsgDao.getInstance().getIds(query);
    }

    /**
     * 生成删除语句
     * @param args args
     * @return sql
     */
    private static String delArgs2Sql(DelArgs args) {
        String sql = "";
        int type = args.getType();
        switch (type) {
            case OptType.DEL_TYPE_1:
                sql = SessionEntryBuilder.delSql(args.getTags());
                break;

            case OptType.DEL_TYPE_2:
                sql = SessionEntryBuilder.delAll();
                break;

            case OptType.DEL_TYPE_3:
                sql = MsgEntryBuilder.delSql(args.getIds());
                break;
            case OptType.DEL_TYPE_4:
                sql = MsgEntryBuilder.delSql(args.getTag());
                break;
            case OptType.DEL_TYPE_5:
                sql = LocalStateMsgBuilder.delSql(args.getTag());
                break;
            default:
                break;
        }
        return sql;
    }
}
