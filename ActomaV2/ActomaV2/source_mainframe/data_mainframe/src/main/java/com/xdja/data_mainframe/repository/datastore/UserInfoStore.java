package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.encrypt.NewStrategyResponseBean;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.domain_mainframe.model.MultiResult;

import java.io.File;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by ldy on 16/4/20.
 */
public interface UserInfoStore {
    /**
     * 修改昵称
     *
     * @param nickName 新昵称
     * @return Http响应
     */
    Observable<Response<Map<String, String>>> modifyNickName(@NonNull String nickName);

    /**
     * 修改头像
     *
     * @param avatarId    头像id
     * @param thumbnailId 缩略图id
     */
    Observable<Response<Void>> modifyAvatar(@NonNull String avatarId,
                                            @NonNull String thumbnailId);

    /**
     * 修改密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    Observable<Response<Void>> modifyPasswd(@NonNull String passwd);

    /**
     * 检测账号密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    Observable<Response<Void>> authPasswd(@NonNull String passwd);

    /**
     * 查询设备账号授信的设备列表
     *
     * @return <p>"cardNo":"78636a7982734923kj49873", //设备卡号</p>
     * <p>"deviceName":"ace", //设备名称</p>
     * <p>"bindTime":14221653215//绑定时间</p>
     */
    Observable<Response<List<Map<String, String>>>> queryDevices();

    /**
     * 修改授信设备名称
     *
     * @param cardNo     设备卡号
     * @param deviceName 设备名称
     */
    Observable<Response<Void>> modifyDeviceName(@NonNull String cardNo,
                                                @NonNull String deviceName);

    /**
     * 解除授信设备与账号的关系
     *
     * @param cardNo 设备卡号
     */
    Observable<Response<Void>> relieveDevice(@NonNull String cardNo);

    /**
     * 客户端退出
     */
    Observable<Response<Void>> logout();

    /**
     * 根据查询条件精确查询账户详情
     *
     * @param accountOrMobile 查询条件：账号或手机号
     */
    Observable<Response<MultiResult<String>>> queryAccountInfo(@NonNull String accountOrMobile);

    /**
     * <p>分批次获取与发起账户更新请求的账号相关（是好友、在相同的群组、在同一个集团）的有变更的账户信息，以下条件代表更新完成：</p>
     * <p>(1)更新到的accounts列表为空</p>
     * <p>(2)更新到的数量小于batchSize</p>
     *
     * @param lastUpdateId 账户信息最后更新标识，首次更新由客户端置为0
     * @param batchSize    本批次更新的数量，默认为10
     */
    Observable<Response<MultiResult<String>>> queryIncrementAccounts(@NonNull int lastUpdateId,
                                                                     @NonNull int batchSize);

    /**
     * 批量查询用户信息
     *
     * @param accounts 账号信息列表
     */
    Observable<Response<MultiResult<String>>> queryBatchAccount(@NonNull List<String> accounts);


    /**
     * 多设备情况下，账号下一个设备登录后要通知其他类型在线设备，其他在线设备收到上线消息后，向后台拉取上线通知消息
     */
    Observable<Response<Map<String, String>>> queryOnlineNotice();

    /**
     * 多设备情况下，账号下同类型设备登录会相互挤下线，被挤下线的设备会收到强制下线通知，收到通知后向后台拉取下线通知消息
     */
    Observable<Response<Map<String, String>>> queryForceLogoutNotice(@NonNull String account,
                                                                     @NonNull String clientType);

    /**
     * 当用户解绑设备时，如果被解绑设备在线，会收到设备解绑通知，收到通知后将调用该接口获取显现提示内容
     *
     * @return
     */
    Observable<Response<Map<String, String>>> queryUnBindDeviceNotice(@NonNull String account);


    interface PreUserInfoStore {
        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         */
    /*    Observable<Response<NewStrategyResponseBean>> updateStrategys(@NonNull String version,
                                                                      @NonNull String cardNo,
                                                                      int lastStrategyId,
                                                                      int batchSize);*/
        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param model         ”ACE”,手机型号
         * @param manufacturer  ”信大捷安”,厂商信息
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         *
         */
        @SuppressWarnings("MethodWithTooManyParameters")
        Observable<Response<NewStrategyResponseBean>> queryStrategyByMobile(@NonNull String version,
                                                                            @NonNull String cardNo,
                                                                            @NonNull String model,
                                                                            @NonNull String manufacturer,
                                                                            int lastStrategyId,
                                                                            int batchSize);

        /**
         * 查询所有第三方加密策略
         * @return 加密策略集合
         */
        Observable<List<EncryptAppBean>> queryStrategys();

        /**
         * 向fastdfs上传图像文件
         *
         * @param imgFile 图像文件
         * @return fastdfs存储的图像地址
         */
        Observable<Response<Map<String, String>>> uploadImg(@NonNull File imgFile);


        /**
         * 从数据库获取账户信息
         */
        Observable<AccountTable> getCurrentAccountTable();

        /**
         * 根据账号获取账号表
         *
         * @param account 帐号
         */
        Observable<AccountTable> getAccountTable(@NonNull String account);

        /**
         * 创建或更新当前账户信息
         *
         * @param account 当前账户
         * @param saveCompanyToPreference 是否执行保存companyCode到sharePreference
         */
        Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account,boolean saveCompanyToPreference);

        /**
         * 创建或更新当前账户信息
         * @param account 当前账户
         */
        Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account);

        /**
         * 更新当前账户的账号信息
         *
         * @param account 帐号
         */
        Observable<Void> updateCurrentAccountTableAccount(String account);


        /**
         * 更新当前账户的自定义账号信息
         *
         * @param alias 自定义账号
         */
        Observable<Void> updateCurrentAccountTableAlias(String alias);
        /**
         * 更新当前账户的手机号
         *
         * @param mobiles
         * @return
         */
        Observable<Void> updateCurrentAccountTableMobile(List<String> mobiles);

        /**
         * 下线当前账号，将数据库中当前账号是否登录值设为false
         */
        Observable<Void> logoutCurrentAccountTable();

        Observable<Void> registerAccountTableChangeListener();

        /**
         * 更新当前账户的昵称
         *
         * @param nickName       昵称
         * @param nickNamePy     昵称简拼
         * @param nickNamePinyin 昵称拼音
         */
        Observable<Void> updateCurrentAccountTableNickName(String nickName, String nickNamePy, String nickNamePinyin);

        /**
         * 更新当前账号的头像id
         *
         * @param avatarId
         * @param thumbnailId
         * @return
         */
        Observable<Void> updateCurrentAccountTableAvatarId(String avatarId, String thumbnailId);

        /**
         * 获取安通+相关后台服务地址信息
         */
        Observable<Response<Map<String, String>>> queryServerConfigs();

        void changeAccountBaseUrl(@NonNull String url);

        /**
         * 保存配置信息到本地
         *
         * @param config 相关配置
         * @return 保存结果
         */
        Observable<Boolean> saveConfig(@Nullable Map<String, String> config);

        /**
         * 保存登陆成功后的缓存信息
         *
         * @param account 本次登陆账号
         * @param ticket  ticket
         * @param chipId  芯片Id
         * @return 保存结果
         */
        Observable<Boolean> saveLoginCache(@NonNull String account, @NonNull String ticket, @NonNull long ticketCreateTime, @NonNull long ticketVaildExpireTime, @NonNull String chipId);

        /**
         * 退出登录时清除ticket信息
         * @return
         */
        Observable<Boolean> clearTicket();
    }

}
