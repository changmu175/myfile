package com.xdja.contact.http;

import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.request.friend.AddFriendBody;
import com.xdja.contact.http.request.friend.UpdateRemarkBody;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.account.BatchAccessAccountParam;
import com.xdja.contact.http.wrap.params.friend.AcceptFriendRequestParam;
import com.xdja.contact.http.wrap.params.friend.AddFriendParam;
import com.xdja.contact.http.wrap.params.friend.DeleteFriendParam;
import com.xdja.contact.http.wrap.params.friend.FriendIncrementalParam;
import com.xdja.contact.http.wrap.params.friend.FriendRequestIncrementalParam;
import com.xdja.contact.http.wrap.params.friend.UpdateRemarkParam;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.service.FriendService;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.Collection;

/**
 * Created by wanghao on 2016/4/20.
 * 好友模块与服务端对接帮助对象
 * 集成当前好友功能内所有与服务对接的接口
 */
public class FriendHttpServiceHelper {

    /**
     * 增量好友列表
     *
     * 注意当前逻辑内需要判断本地好友是否大于0
     * A 和 B 首次成为好友关系 B:主动添加方,B有且只有一个好友关系 ，A此时执行删除B终端一直删除不了A好友关系
     * @return
     */
    public static HttpsRequstResult incrementalFriend() throws FriendHttpException {
        String updateSerial;
        int count = new FriendService().countFriends();
        if(count<=0){
            updateSerial = "-1";
        }else{
            FriendService service = new FriendService();
            updateSerial = service.queryMaxUpdateSerial();
        }
        try {
            return new HttpRequestWrap().synchronizedRequest(new FriendIncrementalParam(updateSerial));
        }catch (Exception e){
            LogUtil.getUtils().e("Actoma contact Friend Http Service Helper :incrementalFriend");
            throw new FriendHttpException();
        }
    }


    /**
     * 接受好友请求
     * @param httpCallBack
     * @param param
     * @throws FriendHttpException
     */
    public static void acceptFriend(IModuleHttpCallBack httpCallBack,String param) throws FriendHttpException {
        AcceptFriendRequestParam acceptFriendRequestParam = new AcceptFriendRequestParam(httpCallBack,param);
        try {
            new HttpRequestWrap().request(acceptFriendRequestParam);
        }catch (Exception e){
            throw new FriendHttpException();
        }
        LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : acceptFriend "+param);
    }


    /**
     * 删除好友
     * @param param
     * @param httpCallBack
     */
    public static void deleteFriend(IModuleHttpCallBack httpCallBack,String param) throws FriendHttpException{
        try {
            new HttpRequestWrap().request(new DeleteFriendParam(httpCallBack, param));
        }catch (Exception e){
            throw new FriendHttpException();
        }
        LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : deleteFriend");
    }

    /**
     * 添加好友
     * @param httpCallBack
     * @param verifyInfo
     * @param param
     */
    public static void addFriend(IModuleHttpCallBack httpCallBack,String verifyInfo,String param) throws FriendHttpException {
        AddFriendBody addFriendBody = new AddFriendBody();
        addFriendBody.setVerification(verifyInfo);
        AddFriendParam httpBodyParams = new AddFriendParam(addFriendBody,httpCallBack,param);
        try {
            new HttpRequestWrap().request(httpBodyParams);
        }catch (Exception e){
            throw new FriendHttpException();
        }
        LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : addFriend");
    }


    /**
     * 更新好友备注
     * @param callBack
     */
    public static void saveRemark(IModuleHttpCallBack callBack,String friendAccount,String remark) throws FriendHttpException {
        UpdateRemarkBody remarkFriend = new UpdateRemarkBody();
        remarkFriend.setRemark(remark);
        try {
            new HttpRequestWrap().request(new UpdateRemarkParam(remarkFriend, callBack, friendAccount));
        }catch (Exception e){
            throw new FriendHttpException();
        }
        LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : saveRemark");
    }

    /**
     * 增量获取好友请求数据
     * @return
     * @throws FriendHttpException
     */
    public static HttpsRequstResult incrementalRequest() throws FriendHttpException {
        FriendRequestService service = new FriendRequestService();
        String updateSerial = service.queryMaxUpdateSerial();
        try{
            return new HttpRequestWrap().synchronizedRequest(new FriendRequestIncrementalParam(updateSerial));
        }catch (Exception e){
            LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : incrementalRequest");
            throw new FriendHttpException();
        }
    }

    /**
     * 批量下载账户信息
     * @param accountSet
     * @return
     */
    public static HttpsRequstResult bulkDownloadAccounts(Collection<String> accountSet) throws FriendHttpException {
        if(ObjectUtil.collectionIsEmpty(accountSet))return null;
        /*BatchAccessAccountBody request = new BatchAccessAccountBody();
        request.setAccounts(accountSet);*/
        BatchAccessAccountParam requestParams = new BatchAccessAccountParam(accountSet);
        try {
            return new HttpRequestWrap().synchronizedRequest(requestParams);
        }catch (Exception e){
            LogUtil.getUtils().e("Actoma contact Friend Http Service Helper : bulkDownloadAccounts");
            throw new FriendHttpException();
        }
    }


}
