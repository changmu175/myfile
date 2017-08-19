package com.xdja.contact.service;

import android.content.Context;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.dao.EncryptRecordDao;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/8/12.
 */
public class EncryptRecordService {

    private EncryptRecordDao encryptRecordDao;

    public EncryptRecordService(Context context) {
        encryptRecordDao = new EncryptRecordDao();
    }

    /**
     * 关闭通道
     * @return
     */
    public boolean closeSafeTransfer(){
        boolean result = false;
        synchronized (encryptRecordDao.helper){
            encryptRecordDao.getWriteDataBase();
            EncryptRecord record = encryptRecordDao.lastSelectedRecord();
            if(!ObjectUtil.objectIsEmpty(record)){
                result = encryptRecordDao.updateCloseTransferByAccount(record.getAccount())>0;
            }else{
                result = true;
            }
            encryptRecordDao.closeDataBase();
        }
        return result;
    }


    /**
     * 查询最近一次选中的人记录
     * @return
     */
    public EncryptRecord lastSelectedRecord(){
        EncryptRecord encryptRecord;
        synchronized (encryptRecordDao.helper){
            encryptRecordDao.getReadableDataBase();
            encryptRecord = encryptRecordDao.lastSelectedRecord();
            encryptRecordDao.closeDataBase();
        }
        return encryptRecord;
    }


    /**
     * ui 选中人,这里我们要做这么多动作判断是否选中的是否是当前人
     * @param account
     * @return
     */
    public boolean selectionFriendUpdateState(String account){
        LogUtil.getUtils().e("Actoma contact EncryptRecordService,selectionFriendUpdateState,开启加密通道  account:"+account);
        boolean result = false;
        if(ObjectUtil.stringIsEmpty(account))return result;
        synchronized (encryptRecordDao.helper){
            encryptRecordDao.getWriteDataBase();
            encryptRecordDao.beginTransaction();
            try {
                EncryptRecord historyRecord = encryptRecordDao.query(account);
                if (ObjectUtil.objectIsEmpty(historyRecord)) {
                    LogUtil.getUtils().e("Actoma contact EncryptRecordService,selectionFriendUpdateState,帐号account:" + account + "---不存在加密记录");
                    EncryptRecord selectedRecord = encryptRecordDao.lastSelectedRecord();
                    if (ObjectUtil.objectIsEmpty(selectedRecord)) {//不存在加密通道
                        LogUtil.getUtils().e("Actoma contact EncryptRecordService,数据库---不存在----已经加密的历史通道");
                        result = encryptRecordDao.insertOpenTransferByAccount(account) > -1;
                    } else {
                        LogUtil.getUtils().e("Actoma contact EncryptRecordService,数据库--- 存在---已经加密的历史通道");
                        int countRowEffect = encryptRecordDao.updateCloseTransferByAccount(selectedRecord.getAccount());
                        if (countRowEffect > 0) {
                            LogUtil.getUtils().e("Actoma contact EncryptRecordService,数据库---关闭----加密的历史通道成功");
                            result = encryptRecordDao.insertOpenTransferByAccount(account) > -1;
                        } else {
                            LogUtil.getUtils().e("Actoma contact EncryptRecordService,数据库---关闭----加密的历史通道失败");
                        }
                    }
                } else {
                    LogUtil.getUtils().i("Actoma contact EncryptRecordService,帐号account:" + account + "--存在---加密记录");
                    EncryptRecord selectedRecord = encryptRecordDao.lastSelectedRecord();
                    if (ObjectUtil.objectIsEmpty(selectedRecord)) {//不存在加密通道
                        result = encryptRecordDao.updateOpenTransferByAccount(account) > 0;
                    } else {
                        if (selectedRecord.getAccount().equals(account)) {
                            result = encryptRecordDao.updateOpenTransferByAccount(account) > 0;
                        } else {
                            int countRowEffect = encryptRecordDao.updateCloseTransferByAccount(selectedRecord.getAccount());
                            if (countRowEffect > 0) {
                                LogUtil.getUtils().i("Actoma contact EncryptRecordService,数据库---关闭----加密的历史通道成功");
                                result = encryptRecordDao.updateOpenTransferByAccount(account) > -1;
                            } else {
                                LogUtil.getUtils().i("Actoma contact EncryptRecordService,数据库---关闭----加密的历史通道失败");
                            }
                        }
                    }
                }
                if (result) {
                    encryptRecordDao.setTransactionSuccess();
                }
            }catch (Exception e){

            }finally {
                encryptRecordDao.endTransaction();
                encryptRecordDao.closeDataBase();
            }
        }
        return result;
    }


}
