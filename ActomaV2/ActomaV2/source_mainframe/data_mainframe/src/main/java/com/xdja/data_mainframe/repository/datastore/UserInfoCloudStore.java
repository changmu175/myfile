package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.encrypt.NewStrategyResponseBean;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.encrypt.EncryptHelper;
import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.data_mainframe.rest.UserInfoRestApi;
import com.xdja.data_mainframe.util.Util;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.net.ServiceGenerator;
import com.xdja.frame.data.persistent.PreferencesUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ldy on 16/4/20.
 */
public class UserInfoCloudStore extends CloudStore implements UserInfoStore {

    public static final String FILEID = "fileid";

    private ServiceGenerator serviceGeneratorWithNoTicket;

    @SuppressWarnings("UnusedParameters")
    @Inject
    public UserInfoCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
                              ServiceGenerator serviceGenerator,
                              @ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF)
                              ServiceGenerator serviceGeneratorWithNoTicket,
                              Map<String, Provider<String>> stringMap) {
        super(serviceGenerator);
        this.serviceGeneratorWithNoTicket = serviceGeneratorWithNoTicket;
    }

    /**
     * 修改昵称
     *
     * @param nickName 新昵称
     * @return Http响应
     */
    @Override
    public Observable<Response<Map<String, String>>> modifyNickName(@NonNull String nickName) {
        Map<String, String> body = new HashMap<>();
        body.put("nickName", nickName);

        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).modifyNickName(body);
    }

    @Override
    public Observable<Response<Void>> modifyAvatar(@NonNull String avatarId, @NonNull String thumbnailId) {
        Map<String, String> body = new HashMap<>();
        body.put("avatarId", avatarId);
        body.put("thumbnailId", thumbnailId);
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).modifyAvatar(body);
    }

    @Override
    public Observable<Response<Void>> modifyPasswd(@NonNull String passwd) {
        Map<String, String> body = new HashMap<>();
        body.put("passwd", passwd);
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).modifyPasswd(body);
    }

    /**
     * 检测账号密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    @Override
    public Observable<Response<Void>> authPasswd(@NonNull String passwd) {
        Map<String, String> body = new HashMap<>();
        body.put("passwd", passwd);
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).authPasswd(body);
    }

    /**
     * 查询设备账号授信的设备列表
     *
     * @return <p>"cardNo":"78636a7982734923kj49873", //设备卡号</p>
     * <p>"deviceName":"ace", //设备名称</p>
     * <p>"bindTime":14221653215//绑定时间</p>
     */
    @Override
    public Observable<Response<List<Map<String, String>>>> queryDevices() {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryDevices();
    }

    /**
     * 修改授信设备名称
     *
     * @param cardNo     设备卡号
     * @param deviceName 设备名称
     */
    @Override
    public Observable<Response<Void>> modifyDeviceName(@NonNull String cardNo, @NonNull String deviceName) {
        Map<String, String> body = new HashMap<>();
        body.put("cardNo", cardNo);
        body.put("deviceName", deviceName);
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).modifyDeviceName(body);
    }

    /**
     * 解除授信设备与账号的关系
     *
     * @param cardNo 设备卡号
     */
    @Override
    public Observable<Response<Void>> relieveDevice(@NonNull String cardNo) {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).relieveDevice(cardNo);
    }

    /**
     * 客户端退出
     */
    @Override
    public Observable<Response<Void>> logout() {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).logout();
    }

    /**
     * 根据查询条件精确查询账户详情
     *
     * @param accountOrMobile 查询条件：账号或手机号
     */
    @Override
    public Observable<Response<MultiResult<String>>> queryAccountInfo(@NonNull String accountOrMobile) {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryAccountInfo(accountOrMobile);
    }

    /**
     * <p>分批次获取与发起账户更新请求的账号相关（是好友、在相同的群组、在同一个集团）的有变更的账户信息，以下条件代表更新完成：</p>
     * <p>(1)更新到的accounts列表为空</p>
     * <p>(2)更新到的数量小于batchSize</p>
     *
     * @param lastUpdateId 账户信息最后更新标识，首次更新由客户端置为0
     * @param batchSize    本批次更新的数量，默认为10
     */
    @Override
    public Observable<Response<MultiResult<String>>> queryIncrementAccounts(@NonNull int lastUpdateId, @NonNull int batchSize) {
        Map<String, String> body = new HashMap<>();
        body.put("lastUpdateId", String.valueOf(lastUpdateId));
        body.put("batchSize", String.valueOf(batchSize));
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryIncrementAccounts(body);
    }

    /**
     * 批量查询用户信息
     *
     * @param accounts 账号信息列表
     */
    @Override
    public Observable<Response<MultiResult<String>>> queryBatchAccount(@NonNull List<String> accounts) {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryBatchAccount(accounts);
    }


    /**
     * 多设备情况下，账号下一个设备登录后要通知其他类型在线设备，其他在线设备收到上线消息后，向后台拉取上线通知消息
     */
    @Override
    public Observable<Response<Map<String, String>>> queryOnlineNotice() {
        return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryOnlineNotice();
    }

    /**
     * 多设备情况下，账号下同类型设备登录会相互挤下线，被挤下线的设备会收到强制下线通知，收到通知后向后台拉取下线通知消息
     */
    @Override
    public Observable<Response<Map<String, String>>> queryForceLogoutNotice(@NonNull String account,
                                                                            @NonNull String clientType) {
        return ApiFactory.getUserInfoRestApi(this.serviceGeneratorWithNoTicket)
                .queryForceLogoutNotice(account, clientType);
    }

    /**
     * 当用户解绑设备时，如果被解绑设备在线，会收到设备解绑通知，收到通知后将调用该接口获取显现提示内容
     *
     * @return
     */
    @Override
    public Observable<Response<Map<String, String>>> queryUnBindDeviceNotice(@NonNull String account) {
        return ApiFactory.getUserInfoRestApi(this.serviceGeneratorWithNoTicket).queryUnBindDeviceNotice(account);
    }


    public static class uploadImgReponse {
        private String fileid;

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }
    }

    @SuppressWarnings("ReturnOfNull")
    public static class PreUserInfoCloudStore extends CloudStore implements UserInfoStore.PreUserInfoStore {

        private final ServiceGenerator httpServiceGenerator;

        private final PreferencesUtil util;

        @Inject
        public PreUserInfoCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF)
                                     ServiceGenerator serviceGenerator,
                                     @ConnSecuritySpe(DiConfig.CONN_HTTP)
                                     ServiceGenerator httpServiceGenerator,
                                     PreferencesUtil util) {
            super(serviceGenerator);
            this.httpServiceGenerator = httpServiceGenerator;
            this.util = util;
        }

        @Override
        public Observable<Boolean> saveConfig(Map<String, String> config) {
            return null;
        }

        /**
         * 从数据库获取账户信息
         */
        @Override
        public Observable<AccountTable> getCurrentAccountTable() {
            return null;
        }

        /**
         * 根据账号获取账号表
         *
         * @param account 帐号
         */
        @Override
        public Observable<AccountTable> getAccountTable(@NonNull String account) {
            return null;
        }

        /**
         * 创建或更新当前账户信息
         *
         * @param account 帐号
         * @param saveCompanyToPreference 是否保存companyCode到sharePreference
         */
        @Override
        public Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account,boolean saveCompanyToPreference) {
            return null;
        }

        /**
         * 创建或更新当前账户信息
         *
         * @param account 帐号
         */
        @Override
        public Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account) {
            return null;
        }

        /**
         * 更新当前账户的账号信息
         *
         * @param account 帐号
         */
        @Override
        public Observable<Void> updateCurrentAccountTableAccount(String account) {
            return null;
        }

        @Override
        public Observable<Void> updateCurrentAccountTableAlias(String alias) {
            return null;
        }

        /**
         * 更新当前账户的手机号
         *
         * @param mobiles
         * @return
         */
        @Override
        public Observable<Void> updateCurrentAccountTableMobile(List<String> mobiles) {
            return null;
        }

        /**
         * 下线当前账号，将数据库中当前账号是否登录值设为false
         */
        @Override
        public Observable<Void> logoutCurrentAccountTable() {
            return null;
        }

        @Override
        public Observable<Void> registerAccountTableChangeListener() {
            return null;
        }

        /**
         * 更新当前账户的昵称
         *
         * @param nickName       昵称
         * @param nickNamePy     昵称简拼
         * @param nickNamePinyin 昵称拼音
         */
        @Override
        public Observable<Void> updateCurrentAccountTableNickName(String nickName, String nickNamePy, String nickNamePinyin) {
            return null;
        }

        /**
         * 更新当前账号的头像id
         *
         * @param avatarId
         * @param thumbnailId
         * @return
         */
        @Override
        public Observable<Void> updateCurrentAccountTableAvatarId(String avatarId, String thumbnailId) {
            return null;
        }

        /**
         * 获取安通+相关后台服务地址信息
         */
        @Override
        public Observable<Response<Map<String, String>>> queryServerConfigs() {
            return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryServerConfigs();
        }

        @Override
        public void changeAccountBaseUrl(@NonNull String url) {
            serviceGenerator.resetService(Util.generateFullAccountUrl(url));
        }

        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         */
       /* @Override
        public Observable<Response<NewStrategyResponseBean>> updateStrategys(@NonNull String version,
                                                                              @NonNull String cardNo,
                                                                              int lastStrategyId,
                                                                              int batchSize) {
            Map<String, String> body = new HashMap<>();
            body.put("version", version);
            body.put("cardNo", cardNo);
            body.put("lastStrategyId", String.valueOf(lastStrategyId));
            body.put("batchSize", String.valueOf(batchSize));
            return ApiFactory.getUserInfoRestApi(this.serviceGenerator).updateStrategys(body);
        }*/

        @SuppressWarnings("MethodWithTooManyParameters")
        @Override
        public Observable<Response<NewStrategyResponseBean>> queryStrategyByMobile(@NonNull String version,
                                                                              @NonNull String cardNo,
                                                                              @NonNull String model,
                                                                              @NonNull String manufacturer,
                                                                              int lastStrategyId,
                                                                              int batchSize) {
            Map<String, String> body = new HashMap<>();
            body.put("version", version);
            body.put("cardNo", cardNo);
            body.put("model", String.valueOf(model));
            body.put("manufacturer", String.valueOf(manufacturer));
            body.put("lastStrategyId", String.valueOf(lastStrategyId));
            body.put("batchSize", String.valueOf(batchSize));
            return ApiFactory.getUserInfoRestApi(this.serviceGenerator).queryStrategyByMobile(body);

        }

        /**
         * 查询所有第三方加密策略
         * @return 加密策略集合
         */
        @Override
        public Observable<List<EncryptAppBean>> queryStrategys() {
            return Observable.just(EncryptHelper.queryEncryptApps());
        }

        /**
         * 向fastdfs上传图像文件
         *
         * @param imgFile 图像文件
         * @return fastdfs存储的图像地址
         */
        @Override
        public Observable<Response<Map<String, String>>> uploadImg(@NonNull File imgFile) {
            String url = this.util.gPrefStringValue("fastDfs");
            if (url.contains("upload")) {
                int upload = url.indexOf("upload");
                url = url.substring(0, upload);
            }

            final String fUrl = url;
            final UserInfoRestApi service = ApiFactory.getUserInfoRestApi(this.httpServiceGenerator, fUrl);
            RequestBody fileBody = null;
            if (imgFile != null) {
                fileBody = RequestBody.create(null, imgFile);
            }
            return service.uploadFile(fileBody)
                    .map(new Func1<Response<Map<String, String>>, Response<Map<String, String>>>() {
                        @Override
                        public Response<Map<String, String>> call(Response<Map<String, String>> mapResponse) {
                            Map<String, String> body = mapResponse.body();
                            if (body != null) {
                                String fileid = body.get(FILEID);
                                if (fileid != null) {
                                    body.put(FILEID, fUrl + "download/" + fileid);
                                }
                            }
                            return mapResponse;
                        }
                    });
        }

        @Override
        public Observable<Boolean> saveLoginCache(@NonNull String account, @NonNull String ticket, @NonNull long ticketCreateTime, @NonNull long ticketVaildExpireTime, @NonNull String chipId) {
            return null;
        }

        @Override
        public Observable<Boolean> clearTicket() {
            return null;
        }
    }


}
