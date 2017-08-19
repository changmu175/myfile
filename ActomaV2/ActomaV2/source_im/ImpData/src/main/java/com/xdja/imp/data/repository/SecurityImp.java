package com.xdja.imp.data.repository;

import android.text.TextUtils;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.repository.SecurityRepository;
import com.xdja.imp_data.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


/**
 * <p>Summary:加解密模块实现</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/6</p>
 * <p>Time:9:49</p>
 * Modify history description:
 * 1)Task for 2632, modify for share and forward function by ycm at 20161103.
 * 2)BUG for 5618, modify for share and forward function by ycm at 20161103.
 * 3)修复转发分享加密时无法生成高清缩略图的问题 by ycm at 20161110.
 */
public class SecurityImp implements SecurityRepository {
    public static final String SECURITY_CODE = "security_code";
    public static final String SECURITY_RESULT = "security_result";
    /**
     * 加解密成功
     */
    public static final int SECURITY_SUCCESS = 0;

    /**
     * 加解密失败
     */
    public static final int SECURITY_FAIL = 1;

    /**
     * 没有加解密，原始文本返回，文本为明文，文件无.dat后缀
     */
    public static final int NO_DECRYPT = 2;


    /**
     * 用户信息缓存实体
     */
    private UserCache userCache;

    /**
     * 加密失败的缓存(groupId, fail time)
     */
    private static Map<String, Long> failCache = new HashMap<>();

    /**
     * SGroup缓存(groupId, boolean)
     */
    private static Map<String, Boolean> sGCache = new HashMap<>();


    @Inject
    public SecurityImp(UserCache userCache){
        this.userCache = userCache;
    }


    /**
     * 文本加密
     *
     * @param source  加密的文本
     * @param to      聊天对象账号
     * @param isGroup 是否群组
     * @return 加密后的文本
     */
    @Override
    public Map<String,Object> encryptText(String source, String to, boolean isGroup) {
        String groupId = getGroupId(to, isGroup);

        if (!CkmsGpEnDecryptManager.getCkmsIsOpen()) {
            return getResult(NO_DECRYPT, source);
        }

        // 24 小时内只进行一次加密，否则使用软加密
        if (!isADay(groupId)) {
            return encryptTextSoft(source,groupId);
        }

        String account = userCache.get().getAccount();
        Map<String, Object> result = CkmsGpEnDecryptManager.
                encryptData(account, groupId, source);

        if (result == null) {
            return getResult(SECURITY_FAIL, ActomaController.getApp().getString(R.string.im_encrpto_fail));
        }

        int retCode = (int) result.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG);

        if (retCode == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            failCache.remove(groupId);
            sGCache.remove(groupId);

            String success = (String) result.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            return getResult(SECURITY_SUCCESS, success);
        } else {
            //CKMS 加密失败，记入cache
            failCache.put(groupId, System.currentTimeMillis());

            if (isGroup) {
                return encryptTextSoft(source,groupId);
            }

            if (null != sGCache.get(groupId)) {
                return encryptTextSoft(source,groupId);
            }

            if (retCode == CkmsGpEnDecryptManager.ENTITY_NOT_IN_GROUP && createSGroup(to, groupId, false)) {
                failCache.remove(groupId);
                sGCache.put(groupId, true);

                return encryptText(source, to, false);
            } else {
                return encryptTextSoft(source,groupId);
            }
        }
    }

    /**
     * 文件加密
     *
     * @param source  加密的文件路径
     * @param dest    加密后的文件路径
     * @param to      接收方
     * @param isGroup 是否群组
     * @return 加密后的文件路径
     */
    @Override
    public Map<String,Object> encryptAsync(String source, String dest, String to, boolean isGroup){
        if (TextUtils.isEmpty(source)) {
            return getResult(NO_DECRYPT, source);
        }

        String groupId = getGroupId(to, isGroup);

        if (!CkmsGpEnDecryptManager.getCkmsIsOpen()) {
            return getResult(NO_DECRYPT, source);
        }

        dest = getEncryptPath(source, dest);

        if (!isADay(groupId)) {
            //使用文件软加密
            return encryptFileSoft(source, dest, groupId);
        }


        String account = userCache.get().getAccount();
        int code = CkmsGpEnDecryptManager.encrptyFile(account, groupId, source, dest);

        if (code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            //加密成功，不记入缓冲
            failCache.remove(groupId);
            sGCache.remove(groupId);

            return getResult(SECURITY_SUCCESS, dest);
        }


        //CKMS 加密失败，记入 cache
        failCache.put(groupId, System.currentTimeMillis());

        if (isGroup) {
            //使用文件软加密
            return encryptFileSoft(source, dest, groupId);
        }


        if (null != sGCache.get(groupId)) {
            //使用文件软加密
            return encryptFileSoft(source, dest, groupId);
        }

        if (code == CkmsGpEnDecryptManager.ENTITY_NOT_IN_GROUP && createSGroup(to, groupId, false)) {
            failCache.remove(groupId);
            sGCache.put(groupId, true);

            return encryptAsync(source, dest, to, false);
        } else {
            //使用文件软加密
            return encryptFileSoft(source, dest, groupId);
        }
    }

    /**
     * 文本解密
     *
     * @param source  解密前的文本
     * @param to      接收方
     * @param isGroup 是否群组
     * @return 解密后的文本
     */
    @Override
    public Map<String,Object> decryptText(String source, long msgId, String from, String to, boolean isGroup) {
        String decrypt = userCache.getCacheText(msgId);
        if (!TextUtils.isEmpty(decrypt)) {
            return getResult(SECURITY_SUCCESS, decrypt);
        }

        if (TextUtils.isEmpty(source)) {
            return getResult(SECURITY_FAIL, source);
        }

        if (!CkmsGpEnDecryptManager.getCkmsIsOpen()) {
            userCache.putCacheText(msgId, source);
            return getResult(NO_DECRYPT, source);
        }

        String decryptText = source;

        String account = userCache.get().getAccount();
        Map<String,Object> result =  CkmsGpEnDecryptManager.decryptData(account, source);

        if (result == null) {
            LogUtil.getUtils().d("CKMS decrypt fail...");
            return getResult(SECURITY_FAIL, decryptText);
        }

        int code = (int) result.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG);

        if(code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
            decryptText = (String) result.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            userCache.putCacheText(msgId, decryptText);

            return getResult(SECURITY_SUCCESS, decryptText);
        }


        if (code == CkmsGpEnDecryptManager.CKMS_PARAMETER_WRONG) {
            //明文解密失败，展示明文
            userCache.putCacheText(msgId, decryptText);
            LogUtil.getUtils().e("CKMS decrypt fail " + code);

            return getResult(NO_DECRYPT, decryptText);
        }

        //CKMS调用失败，需要进行软解密
        String groupId = getGroupId(from, to, isGroup);

        return decryptTextSoft(source, msgId, groupId);
    }

    /**
     * 文件解密
     *
     * @param source  解密前的文件路径
     * @param dest    解密后的文件路径
     * @param to      接收方
     * @param isGroup 是否群组
     * @return 解密后的文件路径
     */
    @Override
    public Map<String,Object> decryptAsync(String source, String dest, String to, boolean isGroup) {
        if(TextUtils.isEmpty(source)) {
            LogUtil.getUtils().d("CKMS decrypt file not exist!!!");
            return getResult(NO_DECRYPT, source);
        }


        File file = new File(source);
        if(!file.exists()) {
            LogUtil.getUtils().e("CKMS decrypt file not exist!!!");
            return getResult(SECURITY_FAIL, source);
        }

        if(!source.contains(ConstDef.FILE_ENCRPTY_SUFFIX)){
            return getResult(NO_DECRYPT, source);
        }

        String account = userCache.get().getAccount();
        dest = getDecryptPath(source, dest);

        int code = CkmsGpEnDecryptManager.decFile(account, source, dest);

        if(code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
            return getResult(SECURITY_SUCCESS, dest);
        }

        String groupId = getGroupId(to, isGroup);
        return decryptFileSoft(source, dest, groupId);
    }

    /**
     * 数据软加密，使用自定义加密策略
     * @param source source
     * @param groupId groupId
     * @return Map
     */
    private Map<String,Object> encryptTextSoft(String source,String groupId) {
        Map<String, Object> result = CkmsGpEnDecryptManager.encryptDataSoft(groupId, source);

        int code = (int) result.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG);
        if (code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            String encryptContent = (String) result.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            return getResult(SECURITY_SUCCESS, encryptContent);
        }

        //本次操作失败，不能继续进行业务 // TODO: 2016/12/24 liming 返回结果是否应该为字符串
        return getResult(SECURITY_FAIL, ActomaController.getApp().getString(R.string.im_encrpto_data_fail));
    }

    /**
     * 单个文件软加密
     * @param source source
     * @param groupId groupId
     * @return Map
     */
    private Map<String,Object> encryptFileSoft(String source, String dest, String groupId) {
        int code;
        code = CkmsGpEnDecryptManager.encryptFileSoft(groupId, source, dest);

        if (code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            return getResult(SECURITY_SUCCESS, dest);
        } else {
            //文件加密失败
            LogUtil.getUtils().d("CKMS file encrypt fail");

            return getResult(SECURITY_FAIL, ActomaController.getApp().getString(R.string.im_file_encrpto_fail));
        }
    }

    /**
     * 文本软解密
     * @param source source
     * @param msgId msgId
     * @param groupId groupId
     * @return Map
     */
    private Map<String,Object> decryptTextSoft(String source, long msgId, String groupId) {
        Map<String, Object> softResult = CkmsGpEnDecryptManager.decryptDataSoft(groupId, source);

        if (softResult == null) {
            LogUtil.getUtils().e("CKMS decrypt fail...");

            return getResult(SECURITY_FAIL, source);
        }

        int code = (int) softResult.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG);

        String decryptText = source;
        if (code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            decryptText = (String) softResult.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            userCache.putCacheText(msgId, decryptText);

            return getResult(SECURITY_SUCCESS, decryptText);
        }

        LogUtil.getUtils().e("CKMS decrypt fail " + code);
        //modify by zya 20170324
//        if ((code == CkmsGpEnDecryptManager.DECRYPT_DATA_FORMATE_WRONG ||
//                code == CkmsGpEnDecryptManager.CKMS_PARAMETER_WRONG)) {
        if (code == CkmsGpEnDecryptManager.CKMS_PARAMETER_WRONG) {
            //end by zya
            //明文解密失败，展示明文
            userCache.putCacheText(msgId, decryptText);

            return getResult(NO_DECRYPT, decryptText);
        } else {
            return getResult(SECURITY_FAIL, decryptText);//ActomaController.getApp().getString(R.string.im_data_decrpto_fail));
        }
    }

    /**
     * 文件软解密
     * @param source source
     * @param dest dest
     * @param groupId groupId
     * @return Map
     */
    private Map<String,Object> decryptFileSoft(String source, String dest, String groupId) {
        int code = CkmsGpEnDecryptManager.decryptFileSoft(groupId, source, dest);
        if(code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
            return getResult(SECURITY_SUCCESS, dest);
        }

        return getResult(SECURITY_FAIL, source);
    }


    /**
     * 创建SGroup
     * @param to to
     * @param groupId groupId
     * @param isGroup isGroup
     * @return boolean
     */
    private synchronized boolean createSGroup(String to, String groupId, boolean isGroup){
        String currentAccount = ContactUtils.getCurrentAccount();
        int taskType = isGroup? CkmsGpEnDecryptManager.START_GROUP_TALK :
                CkmsGpEnDecryptManager.START_FRIEND_TALK;
        List<String> accountList = new ArrayList<>();

        if (!isGroup) {
            accountList.add(currentAccount);
            accountList.add(to);
            String ckmsGroupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount, to);
            boolean isInGroup = CkmsGpEnDecryptManager.isEntitySGroupsExist(currentAccount, ckmsGroupId);
            return isInGroup || doCkmsOpWithCode(taskType, ckmsGroupId, accountList, CkmsGpEnDecryptManager.CREATE_GROUP, to);// modified by ycm for lint 2017/02/16

        }

        int  inSGroupCode = CkmsGpEnDecryptManager.isEntityInSGroup(currentAccount, groupId);

        if (inSGroupCode == CkmsGpEnDecryptManager.ENTITY_IN_GROUP) {
            List<UserInfo> dataSource = new GroupExternalService().getUserInfosByGroupId(groupId);
            for (UserInfo userInfo : dataSource) {
                accountList.add(userInfo.getAccount());
            }

            CkmsGpEnDecryptManager.filterExistedEntities(accountList, groupId);
            // modified by ycm for lint 2017/02/16
            return ListUtils.isEmpty(accountList) || doCkmsOpWithCode(taskType, groupId, accountList, CkmsGpEnDecryptManager.ADD_ENTITY, to);
        }

        if (inSGroupCode == CkmsGpEnDecryptManager.ENTITY_NOT_IN_GROUP){
            LogUtil.getUtils().i("CKMS cannot add self SGroup");
            return false;
        }

        if(inSGroupCode == CkmsGpEnDecryptManager.GROUP_NOT_EXIST){
            List<UserInfo> dataSource = new GroupExternalService().getUserInfosByGroupId(groupId);

            for(UserInfo userInfo : dataSource){
                accountList.add(userInfo.getAccount());
            }
            return doCkmsOpWithCode(taskType, groupId, accountList, CkmsGpEnDecryptManager.CREATE_GROUP,to);
        }

        if(inSGroupCode == CkmsGpEnDecryptManager.CKMS_EXPIRED_TO_INIT){
            return false;
        } else {
            return false;
        }
    }

    /**
     * 获取ckms操作码
     * @param taskType taskType
     * @param ckmsGroupId ckmsGroupId
     * @param accountList accountList
     * @param OpSignType OpSignType
     * @return boolean
     */
    private boolean doCkmsOpWithCode(int taskType, String ckmsGroupId, List<String> accountList, String OpSignType,String to) {
        String currentAccount = ContactUtils.getCurrentAccount();
        String opSign = GroupHttpServiceHelper.syncGetCkmsGroupOpSign(ckmsGroupId, accountList, OpSignType);

        if (TextUtils.isEmpty(opSign)) {
            return false;
        }

        int createRes = 0;
        if (CkmsGpEnDecryptManager.START_FRIEND_TALK == taskType ||
                CkmsGpEnDecryptManager.START_FRIEND_VOIP == taskType){

            createRes = CkmsGpEnDecryptManager.createSGroupWithFriend(ckmsGroupId, currentAccount, to, opSign);
        } else if(CkmsGpEnDecryptManager.START_GROUP_TALK == taskType){
            int inSGroupCode= CkmsGpEnDecryptManager.isEntityInSGroup(currentAccount, ckmsGroupId);

            if(inSGroupCode == CkmsGpEnDecryptManager.ENTITY_NOT_IN_GROUP){
                LogUtil.getUtils().i("CKMS cannot add self SGroup");
            }else if(inSGroupCode == CkmsGpEnDecryptManager.GROUP_NOT_EXIST){
                createRes =  CkmsGpEnDecryptManager.createSgroup(currentAccount, ckmsGroupId,accountList,opSign);
            }
        }

        switch (createRes) {
            case CkmsGpEnDecryptManager.CREATE_SGROUP_SUCCEED:
                return true;
            case CkmsGpEnDecryptManager.EXIST_NOT_AUTH_DEVICE:
            case CkmsGpEnDecryptManager.CKMS_EXPIRED_TO_INIT:
            default:
                return false;
        }

    }

    /**
     * 获取groupId
     * @param to to
     * @param isGroup isGroup
     * @return groupId
     */
    private String getGroupId(String to, boolean isGroup) {
        String account = userCache.get().getAccount();
        return isGroup ? to : CkmsGpEnDecryptManager.getGroupIdWithFriend(account, to);
    }

    /**
     * 获取groupId
     * @param from from
     * @param to to
     * @param isGroup isGroup
     * @return groupId
     */
    private String getGroupId(String from, String to, boolean isGroup) {
        return isGroup ? to : CkmsGpEnDecryptManager.getGroupIdWithFriend(from, to);
    }

    /**
     * 是否24小时内
     * @param groupId groupId
     * @return boolean
     */
    private boolean isADay(String groupId) {
        return System.currentTimeMillis() - (failCache.get(groupId) == null ?
                0 : failCache.get(groupId)) > 24 * 60 * 60 * 1000;
    }

    /**
     * 获取加密后的文件路径
     * @param path path
     * @return String
     */
    private String getEncryptPath(String path, String dest) {
        if (TextUtils.isEmpty(dest)) {
            return path + ConstDef.FILE_ENCRPTY_SUFFIX;
        } else {
            return dest;
        }
    }

    /**
     * 获取解密后的文件路径
     * @param path path
     * @param dest dest
     * @return String
     */
    private String getDecryptPath(String path, String dest) {
        if (TextUtils.isEmpty(dest)) {
            return path.split(ConstDef.FILE_ENCRPTY_SUFFIX)[0];
        } else {
            return dest;
        }
    }

    /**
     * 加解密结果
     * @param code code
     * @param result result
     * @return Map
     */
    private Map<String,Object> getResult(int code, String result) {
        Map<String, Object> map = new HashMap<>();
        map.put(SECURITY_CODE, code);
        map.put(SECURITY_RESULT, result);
        return map;
    }
}
