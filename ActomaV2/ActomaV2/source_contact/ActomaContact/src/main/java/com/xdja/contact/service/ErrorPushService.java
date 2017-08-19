package com.xdja.contact.service;

import android.content.Context;

import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.dao.ErrorPushDao;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by wanghao on 2015/7/15.
 * 推送错误数据业务操作
 */
public class ErrorPushService {

    private ErrorPushDao dao;

    public ErrorPushService(){}

    public ErrorPushService(Context context){
        this.dao = new ErrorPushDao();
    }

    /**
     * 查询所有推送异常的数据
     * @return
     */
    public List<ErrorPush> queryErrorPush(){
        List<ErrorPush> errorPushs = null;
        synchronized (dao.helper){
            dao.getReadableDataBase();
            errorPushs = dao.queryAll();
            dao.closeDataBase();
        }
        return  errorPushs;
    }

    /**
     * 保存错误数据
     * @param push
     * @return
     */
    public boolean insert(ErrorPush push){
        boolean result = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            result = dao.insert(push) > 0 ? true : false;
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * 删除是否成功
     * @param push
     * @return boolean true : 成功 ; false : 失败
     */
    public boolean delete(ErrorPush push){
        boolean result = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            ErrorPush errorPush = dao.query(push.getTransId());
            if(ObjectUtil.objectIsEmpty(errorPush)) {
                result = true;
            }else {
                result = dao.delete(push) > 0;
            }
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * 根据推送业务类型返回推送异常数据信息
     * @param transId
     * @return
     */
    public ErrorPush queryErrorPush(String transId){
        ErrorPush errorPush;
        synchronized (dao.helper){
            dao.getReadableDataBase();
            errorPush = dao.query(transId);
            dao.closeDataBase();
        }
        return errorPush;
    }

    /**
     * 更新错误推送数据
     * @param errorPush
     * @return
     */
    public boolean updateErrorPush(ErrorPush errorPush){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            result = dao.update(errorPush) >= 1 ? true : false;
            dao.closeDataBase();
        }
        return result;
    }
    /**
     * 更新或者保存
     * @return
     */
    public boolean saveOrUpdate(ErrorPush errorPush){
        boolean result = false;
        synchronized (dao.helper) {
                dao.getWriteDataBase();
                ErrorPush push = dao.query(errorPush.getTransId());
                if (ObjectUtil.objectIsEmpty(push)) {
                    result = dao.insert(errorPush) >= 0;
                } else {
                    result = dao.update(errorPush) > 0;
                }
                dao.closeDataBase();
            }
        return result;
    }

}
