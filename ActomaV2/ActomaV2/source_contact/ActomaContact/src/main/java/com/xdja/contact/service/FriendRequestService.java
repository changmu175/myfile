package com.xdja.contact.service;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.dao.FriendHistoryRequestDao;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/7/21.
 */
public class FriendRequestService {

    private FriendHistoryRequestDao dao;

    public FriendRequestService(){
        this.dao = new FriendHistoryRequestDao();
    }

    /**
     * 查询最大的 更新序列
     * @return
     */
    public String queryMaxUpdateSerial(){
        String result = "0";
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            result = dao.queryMaxUpdateSerial();
            dao.closeDataBase();
        }
        return ObjectUtil.stringIsEmpty(result) ? "0" : result ;
    }

    /**
     * 查询本地请求历史
     * @return List<FriendRequestHistory> 返回请求历史记录不会为空但是数据大小可能为0
     */
    public List<FriendRequestHistory> queryFriendRequestHistories(){
        List<FriendRequestHistory> dataSource;
        synchronized (dao.helper){
            dao.getReadableDataBase();
            dataSource = dao.queryAll();
            dao.closeDataBase();
        }
        //
        if(ObjectUtil.collectionIsEmpty(dataSource)){
            return dataSource;
        } else {
            //
            Map<String, FriendRequestHistory> historyMap = getUnrelatedHistory(dataSource);
            if(ObjectUtil.mapIsEmpty(historyMap)){
                return dataSource;
            }else {
                //
                List<Friend> localFriends = new FriendService().queryFriends();
                if(ObjectUtil.collectionIsEmpty(localFriends)){
                    return dataSource;
                }else{
                    List<String> historyAccounts = new ArrayList<>();
                    for(Friend friend : localFriends){
                        FriendRequestHistory history = historyMap.get(friend.getAccount());
                        if(!ObjectUtil.objectIsEmpty(history)){
                            history.setRequestState(FriendHistoryState.ALREADY_FRIEND);
                            historyAccounts.add(history.getShowAccount());
                        }
                    }
                    if(ObjectUtil.collectionIsEmpty(historyAccounts)){
                        return dataSource;
                    }else{
                        boolean result = updateHistories(historyAccounts,FriendHistoryState.ALREADY_FRIEND);
                        if(!result){
                            synchronized (dao.helper){
                                dao.getReadableDataBase();
                                dataSource = dao.queryAll();
                                dao.closeDataBase();
                            }
                        }
                    }
                }
            }
        }
        return dataSource;
    }

    /**
     * 返回不是好友关系的历史记录
     * @param dataSource
     * @return
     */
    private Map<String,FriendRequestHistory> getUnrelatedHistory(List<FriendRequestHistory> dataSource){
        Map<String,FriendRequestHistory> historyMap = new HashMap<>();
        for(FriendRequestHistory history : dataSource){
            if(history.getRequestState() != FriendHistoryState.ALREADY_FRIEND) {
                historyMap.put(history.getShowAccount(), history);
            }
        }
        return historyMap;
    }


    /**
     * 更新历史记录未读状态--->已读状态
     * @return
     */
    public boolean updateIsRead(){
        boolean result = false ;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            result = dao.updateIsRead();
            dao.closeDataBase();
        }
        return result;
    }


    /**
     * 更新历史请求信息状态
     * @param requestHistory
     * @return
     */
    public boolean update(FriendRequestHistory requestHistory){
        int result = 0;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            result = dao.update(requestHistory);
            dao.closeDataBase();
        }
        return result > 0;
    }

    /**
     * 更新历史请求信息状态
     * @param reqAccount
     * * @param recAccount
     * @return
     */
    public FriendRequestHistory query(String reqAccount,String recAccount){
        FriendRequestHistory friendRequestHistory;
        synchronized (dao.helper){
            dao.getReadableDataBase();
            friendRequestHistory= dao.query(reqAccount,recAccount);
            dao.closeDataBase();
        }
        return friendRequestHistory;
    }

    /**
     * 修正制定账号的历史请求状态
     * @param accounts
     * @param historyState
     * @return
     */
    public boolean updateHistories(List<String> accounts, FriendHistoryState historyState){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            try{
                dao.beginTransaction();
                String status = String.valueOf(historyState.getKey());
                result = dao.updateRequestHistoryState(accounts,status);
                if(result){
                    dao.setTransactionSuccess();
                }
            }catch (Exception e){
                LogUtil.getUtils().e("Actoma contact FriendRequestService,updateHistories error");
            }finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }




    /********以下是未重构的代码*******************************************************************************/
    /**
     * 批量保存请求消息
     * @param dataSource
     * @return
     */
    public boolean batchSaveOrUpdate(List<FriendRequestHistory> dataSource) {
        if(ObjectUtil.collectionIsEmpty(dataSource))return true;
        boolean result = true;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            try {
                dao.beginTransaction();
                List<FriendRequestHistory> requestHistories = dao.queryAll();
                if(ObjectUtil.collectionIsEmpty(requestHistories)){
                    for(FriendRequestHistory history : dataSource){
                        if(ObjectUtil.stringIsEmpty(history.getShowAccount())){
                            result = false;
                            break;
                        }else{
                            result = dao.insert(history) >= 0;
                            if(!result){
                                break;
                            }
                        }
                    }
                }else{
                    //modify by lwl start showaccount recaccount reqaccount
                    ArrayList<FriendRequestHistory> requestHistoryMap=new ArrayList<>();
                    ArrayList<String> requestAccount=new ArrayList<>();
                    ArrayList<String> receAccount=new ArrayList<>();
                    for(FriendRequestHistory history : requestHistories){
                        if(ObjectUtil.stringIsEmpty(history.getShowAccount())){
                            result = false;
                            requestHistoryMap.clear();
                            break;
                        }else {
                            receAccount.add(history.getRecAccount());
                            requestAccount.add(history.getReqAccount());
                            requestHistoryMap.add(history);
                        }
                    }
                    if(!ObjectUtil.objectIsEmpty(requestHistoryMap)){
                        for (FriendRequestHistory history : dataSource) {
                            boolean isUpdate=false;
                            for (int i = 0; i <requestAccount.size() ; i++) {
                                String req=requestAccount.get(i);
                                String rec=receAccount.get(i);
                                if(!ObjectUtil.objectIsEmpty(req)&&!ObjectUtil.objectIsEmpty(rec)
                                        &&req.equals(history.getReqAccount())&&rec.equals(history.getRecAccount())){
                                    history.setIsRead(FriendRequestHistory.UNREAD);
                                    result = dao.update(history) > 0;
                                    isUpdate=true;
                                    break;
                                }
                            }
                            if(!isUpdate){
                                history.setIsRead(FriendRequestHistory.UNREAD);
                                result = dao.insert(history)  >= 0;
                            }

                            //if(!result){
                             //   break;
                           // }
                        }
                        //modify by lwl end showaccount recaccount reqaccount
                    }
                }
                if (result) dao.setTransactionSuccess();
            }catch (Exception e){
                result = false;
            }finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }


    /**
     * 更新或者保存请求记录
     * @param history
     * @return
     */
    public boolean saveOrUpdate(FriendRequestHistory history){
        boolean result = false;
        FriendRequestHistory requestHistory = null;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            requestHistory = dao.query(history);
            if(ObjectUtil.objectIsEmpty(requestHistory)){
                result = dao.insert(history)  >= 0;
            }else{
                result = dao.update(history) > 0;
            }
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * 统计新消息条数
     * @return
     */
    public int countNewFriend(){
        int result = 0;
        synchronized (dao.helper){
            dao.getReadableDataBase();
            result = dao.countNewFriend();
            dao.closeDataBase();
        }
        return result;
    }



}
