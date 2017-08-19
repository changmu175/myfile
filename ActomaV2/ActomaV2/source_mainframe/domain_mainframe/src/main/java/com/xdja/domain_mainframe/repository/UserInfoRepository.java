package com.xdja.domain_mainframe.repository;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.ImgCompressResult;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.repository.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by ldy on 16/4/19.
 */
public interface UserInfoRepository extends Repository {

    /**
     * 修改昵称
     *
     * @param nickName 新昵称
     */
    Observable<Map<String, String>> modifyNickName(@NonNull String nickName);

    /**
     * 修改头像
     *
     * @param avatarId    头像id
     * @param thumbnailId 缩略图id
     */
    Observable<Void> modifyAvatar(@NonNull String avatarId,
                                  @NonNull String thumbnailId);

    /**
     * 修改密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    Observable<Void> modifyPasswd(@NonNull String passwd);

    /**
     * 检测账号密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    Observable<Void> authPasswd(@NonNull String passwd);

    /**
     * 查询设备账号授信的设备列表
     *
     * @return <p>"cardNo":"78636a7982734923kj49873", //设备卡号</p>
     * <p>"deviceName":"ace", //设备名称</p>
     * <p>"bindTime":14221653215//绑定时间</p>
     */
    Observable<List<Map<String, String>>> queryDevices();

    /**
     * 修改授信设备名称
     *
     * @param cardNo     设备卡号
     * @param deviceName 设备名称
     */
    Observable<Void> modifyDeviceName(@NonNull String cardNo,
                                      @NonNull String deviceName);

    /**
     * 解除授信设备与账号的关系
     *
     * @param cardNo 设备卡号
     */
    Observable<Void> relieveDevice(@NonNull String cardNo);

    /**
     * 客户端退出
     */
    Observable<Void> logout();

    Observable<Void> diskStoreLogout();

    /**
     * 根据查询条件精确查询账户详情
     *
     * @param accountOrMobile 查询条件：账号或手机号
     */
    Observable<MultiResult<String>> queryAccountInfo(@NonNull String accountOrMobile);

    /**
     * <p>分批次获取与发起账户更新请求的账号相关（是好友、在相同的群组、在同一个集团）的有变更的账户信息，以下条件代表更新完成：</p>
     * <p>(1)更新到的accounts列表为空</p>
     * <p>(2)更新到的数量小于batchSize</p>
     *
     * @param lastUpdateId 账户信息最后更新标识，首次更新由客户端置为0
     * @param batchSize    本批次更新的数量，默认为10
     */
    Observable<MultiResult<String>> queryIncrementAccounts(@NonNull int lastUpdateId,
                                                           @NonNull int batchSize);

    /**
     * 批量查询用户信息
     *
     * @param accounts 账号信息列表
     */
    Observable<MultiResult<String>> queryBatchAccount(@NonNull List<String> accounts);


    /**
     * 多设备情况下，账号下一个设备登录后要通知其他类型在线设备，其他在线设备收到上线消息后，向后台拉取上线通知消息
     */
    Observable<Map<String, String>> queryOnlineNotice();

    /**
     * 多设备情况下，账号下同类型设备登录会相互挤下线，被挤下线的设备会收到强制下线通知，收到通知后向后台拉取下线通知消息
     */
    Observable<Map<String, String>> queryForceLogoutNotice();

    /**
     * 当用户解绑设备时，如果被解绑设备在线，会收到设备解绑通知，收到通知后将调用该接口获取显现提示内容
     *
     * @return
     */
    Observable<Map<String, String>> queryUnBindDeviceNotice();

    Observable<Account> getCurrentAccountInfo();


    interface PreUserInfoRepository {
        /**
         * 向fastdfs上传图像文件
         *
         * @param imgFile 图像文件
         * @return fastdfs存储的图像地址
         */
        Observable<String> uploadImg(@NonNull File imgFile);

        /**
         * 压缩bitmap为jpg文件
         */
        Observable<ImgCompressResult> compressBitmap2jpg(@NonNull Bitmap bitmap);


        /**
         * 获取安通+相关后台服务地址信息
         */
        Observable<Map<String, String>> queryServerConfigs();

        /**
         * 获取安通+相关后台服务地址信息并保存到本地
         *
         * @return 获取结果和保存结果
         */
        Observable<Boolean> queryServerConfigsAndSave();

        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         */
   /*     Observable<Integer> updateStrategys(@NonNull String version,
                                            @NonNull String cardNo,
                                            int lastStrategyId,
                                            int batchSize);*/

        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param model          ”ACE”,手机型号
         * @param manufacturer  ”信大捷安”,厂商信息
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         *
         */
        @SuppressWarnings("MethodWithTooManyParameters")
        Observable<Integer> queryStrategyByMobile(@NonNull String version,
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
         * 获取缓存的Ticket
         *
         * @return 获取到的结果
         */
        Observable<String> queryTicketAtLocal();

        /**
         * 查询本地账户信息
         *
         * @return 查询到的信息
         */
        Observable<Account> queryAccountAtLocal();
    }
}
