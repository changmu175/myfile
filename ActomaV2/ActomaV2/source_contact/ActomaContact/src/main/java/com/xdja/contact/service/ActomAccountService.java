package com.xdja.contact.service;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.dao.ActomAccountDao;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/7/25.
 *
 * 2016-01-14 重构
 */
public class ActomAccountService  {

    private ActomAccountDao dao;

    public ActomAccountService(){
        this.dao = new ActomAccountDao();
    }

    /**
     * 根据账号查询账户信息
     * @param account
     * @return
     */
    public ActomaAccount queryByAccount(String account){
        ActomaAccount actomaAccount = null;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            actomaAccount = dao.query(account);
            dao.closeDataBase();
        }
        return actomaAccount;
    }

    /**
     * 根据账号查询对应的账户信息
     * @param accounts
     * @return 返回数据
     */
    public Map<String,ActomaAccount> queryAccountMap(List<String> accounts){
        Map<String,ActomaAccount> accountMap = new HashMap<>();
        int length = accounts.size();
        String[]  accountArray = accounts.toArray(new String[length]);
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            accountMap = dao.findMapByAccounts(accountArray);
            dao.closeDataBase();
        }
        return accountMap;
    }

    /**
     * 获取对应的账户信息
     * @param accounts
     * @return
     */
    public List<ActomaAccount> findAccountsByIds(List<String> accounts){
        List<ActomaAccount> result = new ArrayList<>();
        int length = accounts.size();
        String[]  accountArray = accounts.toArray(new String[length]);
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            result = dao.findAccountsByIds(accountArray);
            dao.closeDataBase();
        }
        return result;
    }



    /**
     * @param responseAccounts 调用方进行拦截验证数据大小
     * @return true:保存成功 false : 保存失败
     */
    public boolean batchSaveAccountsAssociateWithAvatar(List<ResponseActomaAccount> responseAccounts){
        //是否是首次登陆或者清楚数据登陆
        //boolean firstLogin = PreferencesServer.getWrapper(context).gPrefBooleanValue(IS_FIRST_USE, true);
        boolean result = false;
        synchronized (dao.helper){
            try {
                dao.getWriteDataBase();
                dao.beginTransaction();
                Map<String,ActomaAccount> map = new HashMap<String,ActomaAccount>();
                String[] accounts = new String[responseAccounts.size()];
                for(int i = 0 ; i < accounts.length; i++){
                    accounts[i] = responseAccounts.get(i).getAccount();
                }
                if(ObjectUtil.arrayIsEmpty(accounts))return result;
                List<ActomaAccount> actomaAccounts = dao.findAccountsByIds(accounts);
                if(!ObjectUtil.collectionIsEmpty(actomaAccounts)) {
                    for (ActomaAccount actomaAccount : actomaAccounts) {
                        map.put(actomaAccount.getAccount(), actomaAccount);
                    }
                    if(ObjectUtil.mapIsEmpty(map)){
                        for (ResponseActomaAccount contact : responseAccounts) {
                            result = dao.insert(contact) >= 0;
                            result = dao.insertAvatar(contact.getAvatarBean()) >= 0;
                            if(!result){
                                break;
                            }
                        }
                    }else{
                        for (ResponseActomaAccount contact : responseAccounts) {
                            ActomaAccount actomaAccount = map.get(contact.getAccount());
                            if(ObjectUtil.objectIsEmpty(actomaAccount)){
                                result = dao.insert(contact) >= 0;
                                result = dao.insertAvatar(contact.getAvatarBean()) >= 0;
                                if(!result){
                                    break;
                                }
                            }else{
                                result = dao.update(contact) >= 1;
                                result = dao.updateAvatar(contact.getAvatarBean()) >= 1;
                                if(!result){
                                    break;
                                }
                            }
                        }
                    }
                }else{
                    for (ResponseActomaAccount contact : responseAccounts) {
                        result = dao.insert(contact) >= 0;
                        result = dao.insertAvatar(contact.getAvatarBean()) >= 0;
                        if(!result){
                            break;
                        }
                    }
                }
                if (result) dao.setTransactionSuccess();
            } catch (Exception e) {
                result = false;
            } finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }




    /**
     *
     * @return
     */
    public boolean saveOrUpdate(ActomaAccount actomaAccount){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            ActomaAccount account = dao.query(actomaAccount.getAccount());
            if(ObjectUtil.objectIsEmpty(account)){
                result = dao.insert(actomaAccount) >= 0;
            }else{
                result = dao.update(actomaAccount) > 0 ;
            }
            dao.closeDataBase();
        }
        return result;
    }


    public CommonDetailDto queryCommonDetailByAccount(String account){
        CommonDetailDto detailDto;
        //[S] modify by lixiaolong on 20160824. fix bug 3303. review by WangChao1.
        synchronized (dao.helper) {
        //[E] modify by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
            dao.getReadableDataBase();
            detailDto = dao.queryCommonDetailByAccount(account);
            dao.closeDataBase();
        }
        return detailDto ;
    }

}
