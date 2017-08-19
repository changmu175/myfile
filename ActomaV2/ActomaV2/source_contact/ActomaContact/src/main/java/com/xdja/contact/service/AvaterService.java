package com.xdja.contact.service;

import com.xdja.contact.bean.Avatar;
import com.xdja.contact.dao.AvatarDao;
import com.xdja.comm.uitl.ObjectUtil;


/**
 * 头像业务类
 *
 * @author hkb.
 * @since 2015/8/8/0008.
 * 2016 - 01 - 29 wanghao 重构
 */
public class AvaterService {

    private AvatarDao avatarDao;

    public AvaterService() {
        this.avatarDao = new AvatarDao();
    }

    /**
     * 根据账号查询头像
     * @param account 账号外界调用方校验
     * @return 返回图片数据可能为空
     */
    public Avatar queryByAccount(String account) {
        Avatar avatar = null;
        synchronized (avatarDao.helper) {
            avatarDao.getReadableDataBase();
            avatar = avatarDao.query(account);
            avatarDao.closeDataBase();
        }
        return avatar;
    }


    /**
     * 保存或更新
     * @param avatar
     * @return
     */
    public boolean saveOrUpdate(Avatar avatar){
        boolean result = false;
        synchronized (avatarDao.helper) {
            avatarDao.getWriteDataBase();
            Avatar local = avatarDao.query(avatar.getAccount());
            if(ObjectUtil.objectIsEmpty(local)){
                result = avatarDao.insert(avatar) >= 0;
            }else{
                result = avatarDao.update(avatar) > 0 ;
            }
            avatarDao.closeDataBase();
        }
        return result;
    }

    public boolean insert(Avatar avatar,boolean isOpenDataBase){
        boolean result = false;
        if(ObjectUtil.objectIsEmpty(avatar))return result;
        if(ObjectUtil.stringIsEmpty(avatar.getAccount()))return result;
        //start:modify by wal@xdja.com
        synchronized (avatarDao.helper){
            if(isOpenDataBase) {
                avatarDao.getWriteDataBase();
            }
            result = avatarDao.insert(avatar) >= 0;
            if(isOpenDataBase) {
                avatarDao.closeDataBase();
            }
        }
        //end:modify by wal@xdja.com
        return result;
    }

    public boolean update(Avatar avatar,boolean isOpenDataBase){
        boolean result = false;
        if(ObjectUtil.objectIsEmpty(avatar))return result;
        if(ObjectUtil.stringIsEmpty(avatar.getAccount()))return result;
        //start:modify by wal@xdja.com
        synchronized (avatarDao.helper){
            if(isOpenDataBase) {
                avatarDao.getWriteDataBase();
            }
            result = avatarDao.update(avatar) > 0;
            if(isOpenDataBase) {
                avatarDao.closeDataBase();
            }
        }
        return result;
    }

}
