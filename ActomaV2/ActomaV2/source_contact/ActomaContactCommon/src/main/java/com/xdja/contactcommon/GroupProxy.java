package com.xdja.contactcommon;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.bean.dto.ContactNameDto;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.http.response.group.ResponseCkmsOpSign;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contactcommon.dto.ContactDto;

import java.util.List;

/**
 * Created by XDJA_XA on 2015/8/13.
 *
 */
public class GroupProxy {

    private static final String TAG = "ActomaContact GroupProxy ";
    /**
     * 查询群组信息
     * @param context 上下文
     * @param groupId 群组ID
     * @return 如没有查到，返回null；查询到返回ContactDto对象
     */
    public static ContactDto getGroupInfo(Context context, String groupId) {
        ContactDto contactDto = new ContactDto();
        Group groupInfo = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(!ObjectUtil.objectIsEmpty(groupInfo)){
            String groupName = groupInfo.getGroupName();
            if(!ObjectUtil.stringIsEmpty(groupName)){
                contactDto.setName(groupName);
            }
            contactDto.setAvatarUrl(groupInfo.getThumbnail());
        }
        return contactDto;
    }

    /**
     * 查询群组成员信息
     * @param context 上下文
     * @param groupId 群组ID
     * @param account 查询的群组成员账号
     * @return 返回ContactDto对象,返回数据不为空
     */
    public static ContactDto getGroupMemberInfo(Context context, String groupId, String account) {
        ContactDto contactDto = new ContactDto();
        List<ContactNameDto> contactNameDtos = new GroupExternalService(ActomaController.getApp()).queryContactDto(groupId,account);
        if(!ObjectUtil.collectionIsEmpty(contactNameDtos)){
            ContactNameDto contactNameDto = contactNameDtos.get(0);
            contactDto.setName(contactNameDto.getDisplayName());
            contactDto.setAvatarUrl(contactNameDto.getAvatarUrl());
            contactDto.setThumbnailUrl(contactNameDto.getThumbnailUrl());
            contactDto.setAccount(contactNameDto.getAccount());
        }else{
            contactDto.setAccount(account);
            contactDto.setName(account);
            contactDto.setAvatarUrl("");
            contactDto.setThumbnailUrl("");
        }
        return contactDto;
    }

    public static void addGroupListener(Context context, IContactEvent listener) {
        FireEventUtils.addGroupListener(listener);
    }

    public static void removeGroupListener(Context context, IContactEvent listener) {
        FireEventUtils.removeGroupListener(listener);
    }



    /**
     * 获取群组群主账号
     * @param context  上下文
     * @param groupId  群组ID
     * @return  群主账号 返回值: ""; 群组账号
     *  @see GroupUtils#isGroupOwner(Context context, String groupId, String account)
     */
    @Deprecated
    public static String getGroupOwner(Context context, String groupId) {
        Group localGroup = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(ObjectUtil.objectIsEmpty(localGroup) || ObjectUtil.objectIsEmpty(localGroup.getGroupOwner())){
            return "";
        }
        return localGroup.getGroupOwner();
    }
    /**
     * 退出或解散群组
     * @param context  上下文
     * @param groupId  群组Id
     * @param account  当前账号
     * @param callback 回调
     * @return  true，调用成功；false，调用失败
     */
    public static void exitFromGroup(final Context context, final String groupId, final String account,
                                        final ContactModuleProxy.ContactModuleResult callback) {
        if(ObjectUtil.objectIsEmpty(groupId)){
            callback.result(false);return;
        }
        final Group group = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(ObjectUtil.objectIsEmpty(group)) {
            callback.result(false);return;
        }
        GroupHttpServiceHelper.quitOrDismissGroup(group, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
                if (!ObjectUtil.objectIsEmpty(callback)) {
                    callback.result(false);
                }
            }

            @Override
            public void onSuccess(String body) {
                if (!ObjectUtil.objectIsEmpty(group)) {
                    String currentAccount = ContactUtils.getCurrentAccount();
                    boolean result = GroupInternalService.getInstance().saveOrUpdate(group);
                    GroupMember groupMember = GroupInternalService.getInstance().queryMember(groupId, currentAccount);
                    if (!ObjectUtil.objectIsEmpty(groupMember)) {
                        groupMember.setIsDeleted(GroupConvert.DELETED);
                        boolean updateGroupMember = GroupInternalService.getInstance().updateGroupMember(groupMember);
                        if (result && updateGroupMember) {
                            String groupOwner = group.getGroupOwner();
                            if (groupOwner.equals(currentAccount)) {
                                FireEventUtils.fireGroupQuit(group.getGroupId(), account, IContactEvent.DISMISS);
                            } else {
                                FireEventUtils.fireGroupQuit(group.getGroupId(), account, IContactEvent.QUIT);
                                /*start:add by wal@xdja.com for ckms add group child 2016/08/02*/
                                //[S]remove by tangsha@1104 for quite not do ckms action
                                /*if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
                                    final List<String> accountList= new ArrayList<String>();
                                    accountList.add(account);
                                    new AsyncTask<Void,Void,Void>(){

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            GroupHttpServiceHelper.getQuitOrDestroyGroupOpSign(new CkmsGroupHttpCallback(groupId,accountList,CkmsGpEnDecryptManager.QUIT_GROUP),groupId, CkmsGpEnDecryptManager.QUIT_GROUP);
                                            return null;
                                        }
                                    }.execute();
                                }*/
                                //[E]remove by tangsha@1104 for quite not do ckms action
                                /*end:add by wal@xdja.com for ckms add group child 2016/08/02*/
                            }
                            if (!ObjectUtil.objectIsEmpty(callback)) {
                                callback.result(true);
                            }
                        } else {
                            if (!ObjectUtil.objectIsEmpty(callback)) {
                                callback.result(false);
                            }
                        }
                    } else {
                        if (!ObjectUtil.objectIsEmpty(callback)) {
                            callback.result(false);
                        }
                    }
                }
            }

            @Override
            public void onErr() {
                //Start:add by wal@xdja.com for 4141
                if (!ObjectUtil.objectIsEmpty(callback)) {
                    callback.result(false);
                }
                //end:add by wal@xdja.com for 4141
            }
        });
    }
    /*start:add by wal@xdja.com for ckms create group 2016/08/02*/
    private static class CkmsGroupHttpCallback implements IModuleHttpCallBack{
        private String groupID;
        private List<String> accountList;
        private String opSignType;
        private CkmsGroupHttpCallback(String groupID,List<String> accountList,String opSignType){
            this.groupID=groupID;
            this.accountList=accountList;
            this.opSignType=opSignType;
        }
        @Override
        public void onFail(HttpErrorBean httpErrorBean) {
            if(!ObjectUtil.objectIsEmpty(httpErrorBean)) {
                LogUtil.getUtils().e(TAG+"CkmsGroupHttpCallback onFail Ckms create group httpErrorBean.getMessage:"+httpErrorBean.getMessage());
            }else{
                LogUtil.getUtils().e(TAG+"CkmsGroupHttpCallback onFail Ckms create group exception");
            }
        }

        @Override
        public void onSuccess(String s) {
            ResponseCkmsOpSign response = JSON.parseObject(s, ResponseCkmsOpSign.class);
            final String opSign = response.getSignedOpCode();
            LogUtil.getUtils().d(TAG+"CkmsGroupHttpCallback onSuccess opSign "+opSign);
            if (CkmsGpEnDecryptManager.QUIT_GROUP.equals(opSignType)){
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        String currentAccount = ContactUtils.getCurrentAccount();
                        CkmsGpEnDecryptManager.removeEntities(currentAccount,accountList,groupID,opSign);
                        LogUtil.getUtils().e(TAG+"Ckms 自己退出，SGroup移除该账号");
                        return null;
                    }
                }.execute();
            }
        }

        @Override
        public void onErr() {

        }
    }
    /*end:add by wal@xdja.com for ckms create group 2016/08/02*/
}
