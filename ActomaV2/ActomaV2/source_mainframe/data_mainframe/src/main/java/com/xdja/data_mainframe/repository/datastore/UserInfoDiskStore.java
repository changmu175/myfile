package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.encrypt.NewStrategyResponseBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.bean.MailTable;
import com.xdja.data_mainframe.db.bean.MobileTable;
import com.xdja.data_mainframe.db.dao.AccountDao;
import com.xdja.data_mainframe.db.encrypt.EncryptHelper;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.entities.AccountEntityDataMapper;
import com.xdja.dependence.rx.ExtFunc1;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.persistent.PreferencesUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ldy on 16/4/20.
 */
@SuppressWarnings({"ReturnOfNull"})
public class UserInfoDiskStore implements UserInfoStore, UserInfoStore.PreUserInfoStore {

    public static final String KEY_SERVERCONFIG = "serverConfig";
    public static final String KEY_PRE_TICKET = CacheModule.KEY_PRE_TICKET;
    public static final String KEY_PRE_CHIPID = CacheModule.KEY_PRE_CHIPID;
    public static final String KEY_PRE_ACCOUNT_IN_PRE_LOGIN = CacheModule.KEY_PRE_ACCOUNT_IN_PRE_LOGIN;
    public static final String KEY_LOGIN_STATE = CacheModule.KEY_LOGIN_STATE;


    // TODO: 2016/6/4 这个静态变量的作用是？
    public static final String KEY_PRE_ACCOUNT_IN_AFTER_LOGIN = CacheModule.KEY_PRE_ACCOUNT_IN_AFTER_LOGIN;

    private final PreferencesUtil preferencesUtil;
    private final AccountDao accountDao;
    private final AccountEntityDataMapper accountEntityDataMapper;

    private final Gson gson;

    @Inject
    public UserInfoDiskStore(PreferencesUtil preferencesUtil,
                             AccountDao accountDao,
                             AccountEntityDataMapper accountEntityDataMapper,
                             Gson gson) {
        this.preferencesUtil = preferencesUtil;
        this.accountDao = accountDao;
        this.accountEntityDataMapper = accountEntityDataMapper;
        this.gson = gson;
    }

    /**
     * 修改昵称
     *
     * @param nickName 新昵称
     * @return Http响应
     */
    @Override
    public Observable<Response<Map<String, String>>> modifyNickName(@NonNull String nickName) {
        return null;
    }

    @Override
    public Observable<Response<Void>> modifyAvatar(@NonNull String avatarId, @NonNull String thumbnailId) {
        return null;
    }

    /**
     * 修改密码通过原密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    @Override
    public Observable<Response<Void>> modifyPasswd(@NonNull String passwd) {
        return null;
    }

    /**
     * 检测账号密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    @Override
    public Observable<Response<Void>> authPasswd(@NonNull String passwd) {
        return null;
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
        return null;
    }

    /**
     * 修改授信设备名称
     *
     * @param cardNo     设备卡号
     * @param deviceName 设备名称
     */
    @Override
    public Observable<Response<Void>> modifyDeviceName(@NonNull String cardNo, @NonNull String deviceName) {
        return null;
    }

    /**
     * 解除授信设备与账号的关系
     *
     * @param cardNo 设备卡号
     */
    @Override
    public Observable<Response<Void>> relieveDevice(@NonNull String cardNo) {
        return null;
    }

    /**
     * 客户端退出
     */
    @Override
    public Observable<Response<Void>> logout() {
        return null;
    }

    /**
     * 根据查询条件精确查询账户详情
     *
     * @param accountOrMobile 查询条件：账号或手机号
     */
    @Override
    public Observable<Response<MultiResult<String>>> queryAccountInfo(@NonNull String accountOrMobile) {
        return null;
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
        return null;
    }

    /**
     * 批量查询用户信息
     *
     * @param accounts 账号信息列表
     */
    @Override
    public Observable<Response<MultiResult<String>>> queryBatchAccount(@NonNull List<String> accounts) {
        return null;
    }


    /**
     * 为安通+客户端提供第三方应用加密策略的增加量更新
     *
     * @param version        协议版本号
     * @param cardNo         设备芯片卡号
     * @param model          ”ACE”,手机型号
     * @param manufacturer   ”信大捷安”,厂商信息
     * @param lastStrategyId 最后策略更新ID，第一次为0
     * @param batchSize      批量条数
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    @Override
    public Observable<Response<NewStrategyResponseBean>> queryStrategyByMobile(@NonNull String version, @NonNull String cardNo, @NonNull String model, @NonNull String manufacturer, int lastStrategyId, int batchSize) {
        return null;
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
     * 获取安通+相关后台服务地址信息
     */
    @Override
    public Observable<Response<Map<String, String>>> queryServerConfigs() {
        String configs = preferencesUtil.gPrefStringValue(KEY_SERVERCONFIG);
        LogUtil.getUtils().d("从本地查询到服务配置信息：" + configs);
        if (TextUtils.isEmpty(configs)) {
            return Observable.just(null);
        }
        Map<String, String> stringStringMap = (Map<String, String>) gson.fromJson(configs, Map.class);
        return Observable.just(Response.success(stringStringMap));
    }

    /**
     * 多设备情况下，账号下一个设备登录后要通知其他类型在线设备，其他在线设备收到上线消息后，向后台拉取上线通知消息
     */
    @Override
    public Observable<Response<Map<String, String>>> queryOnlineNotice() {
        return null;
    }

    @Override
    public Observable<Response<Map<String, String>>> queryForceLogoutNotice(@NonNull String account, @NonNull String clientType) {
        return null;
    }

    @Override
    public Observable<Response<Map<String, String>>> queryUnBindDeviceNotice(@NonNull String account) {
        return null;
    }

    /**
     * 向fastdfs上传图像文件
     *
     * @param imgFile 图像文件
     * @return fastdfs存储的图像地址
     */
    @Override
    public Observable<Response<Map<String, String>>> uploadImg(@NonNull File imgFile) {
        return null;
    }

    @Override
    public Observable<Boolean> saveConfig(@Nullable final Map<String, String> config) {
        if (config == null || config.isEmpty()) {
            return Observable.just(Boolean.FALSE);
        }

        Set<String> keySet = config.keySet();

        return Observable.just(config)
                .doOnNext(
                        new Action1<Map<String, String>>() {
                            @Override
                            public void call(Map<String, String> stringMap) {
                                preferencesUtil.setPreferenceStringValue(KEY_SERVERCONFIG, gson.toJson(config));
                            }
                        }
                )
                .map(
                        new Func1<Map<String, String>, Set<String>>() {
                            @Override
                            public Set<String> call(Map<String, String> stringMap) {
                                return stringMap.keySet();
                            }
                        }
                )
                .flatMap(
                        new Func1<Set<String>, Observable<String>>() {
                            @Override
                            public Observable<String> call(Set<String> strings) {
                                return Observable.from(strings);
                            }
                        }
                )
                .map(
                        new ExtFunc1<String, Boolean, Map<String, String>>(config) {
                            @Override
                            public Boolean call(String key) {

                                return !TextUtils.isEmpty(key) && preferencesUtil.setPreferenceStringValue(key, config.get(key));

                            }
                        }
                )
                .toList()
                .map(new Func1<List<Boolean>, Boolean>() {
                    @Override
                    public Boolean call(List<Boolean> booleen) {
                        return !booleen.contains(Boolean.FALSE);
                    }
                });
    }

    /**
     * 从数据库获取账户信息
     */
    @Override
    public Observable<AccountTable> getCurrentAccountTable() {
        return accountDao.findAll().map(new Func1<List<AccountTable>, AccountTable>() {
            @Override
            public AccountTable call(List<AccountTable> accountTables) {
                for (AccountTable accountTable : accountTables) {
                    if (accountTable.isOnLine())
                        return accountTable;
                }
                return null;
            }
        });
    }

    @Override
    public void changeAccountBaseUrl(@NonNull String url) {

    }

    /**
     * 根据账号获取账号表
     *
     * @param account 帐号
     */
    @Override
    public Observable<AccountTable> getAccountTable(@NonNull final String account) {
        return accountDao.find(account).map(new Func1<List<AccountTable>, AccountTable>() {
            @Override
            public AccountTable call(List<AccountTable> accountTables) {
                if (accountTables == null || accountTables.isEmpty()) {
                    return null;
                } else {
                    //只用了一个主键查询,所以只会有一个结果
                    return accountTables.get(0);
                }
            }
        });
    }

    /**
     * 创建或更新当前账户信息(以账户为唯一标识)
     * @param saveCompanyToPreference  是否执行保存companyCode到sharePreference操作
     */
    @Override
    public Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account,boolean saveCompanyToPreference) {
        if(saveCompanyToPreference){
            CommonUtils.setCompanyCode(account.getAccount(),account.getCompanyCode());
        }
        return createOrUpdateCurrentAccountInfo(account);
    }

    @Override
    public Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account) {
        // TODO: 2016/5/19 用于适配原来的数据存储方案
        AccountBean accountBean = new AccountBean();
        accountBean.setAccount(account.getAccount());
        accountBean.setAlias(account.getAlias());
        accountBean.setAvatar(account.getAvatarId());
        accountBean.setAvatarDownloadUrl("");
        accountBean.setCompanyCode(account.getCompanyCode());
        accountBean.setDeviceName("");

        RealmList<MailTable> mails = account.getMails();
        if (mails != null) {
            MailTable first = mails.first();
            if (first != null) {
                accountBean.setMail(first.getMail());
            }
        }

        RealmList<MobileTable> mobiles = account.getMobiles();
        if (mobiles != null) {
            MobileTable first = mobiles.first();
            if (first != null) {
                accountBean.setMobile(first.getMobile());
            }
        }

        accountBean.setNickname(account.getNickName());
        accountBean.setNicknamePinyin(account.getNickNamePinyin());
        accountBean.setNicknamePy(account.getNickNamePy());
        accountBean.setThumbnail(account.getThumbnailId());
        accountBean.setThumbnailDownloadUrl("");

        AccountServer.saveAccount(accountBean);
        return accountDao.createOrUpdate(account);
    }

    /**
     * 更新当前账户的账号信息
     *
     * @param account 帐号
     */
    @Override
    public Observable<Void> updateCurrentAccountTableAccount(final String account) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setAccount(account);
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    @Override
    public Observable<Void> updateCurrentAccountTableAlias(final String alias) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setAlias(alias);
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    /**
     * 更新当前账户的手机号
     *
     * @param mobiles
     * @return
     */
    @Override
    public Observable<Void> updateCurrentAccountTableMobile(final List<String> mobiles) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setMobiles(accountEntityDataMapper.getEntityMobiles(mobiles));
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    /**
     * 下线当前账号，将数据库中当前账号是否登录值设为false
     */
    @Override
    public Observable<Void> logoutCurrentAccountTable() {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        if (accountTable != null) {
                            accountTable.setOnLine(false);
                            return createOrUpdateCurrentAccountInfo(accountTable);
                        }
                        return null;
                    }
                });
    }

    @Override
    public Observable<Void> registerAccountTableChangeListener() {
        return accountDao.registerChangeListener();
    }

    /**
     * 更新当前账户的昵称
     *
     * @param nickName       昵称
     * @param nickNamePy     昵称简拼
     * @param nickNamePinyin 昵称拼音
     */
    @Override
    public Observable<Void> updateCurrentAccountTableNickName(final String nickName, final String nickNamePy, final String nickNamePinyin) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setNickName(nickName);
                        accountTable.setNickNamePinyin(nickNamePinyin);
                        accountTable.setNickNamePy(nickNamePy);
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    /**
     * 更新当前账号的头像id
     *
     * @param avatarId
     * @param thumbnailId
     * @return
     */
    @Override
    public Observable<Void> updateCurrentAccountTableAvatarId(final String avatarId, final String thumbnailId) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setAvatarId(avatarId);
                        accountTable.setThumbnailId(thumbnailId);
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    @Override
    public Observable<Boolean> saveLoginCache(@NonNull String account, @NonNull String ticket, @NonNull long ticketCreateTime, @NonNull long ticketVaildExpireTime, @NonNull String chipId) {
        String lastAccount = preferencesUtil.gPrefStringValue(KEY_PRE_ACCOUNT_IN_PRE_LOGIN);
        if (!TextUtils.isEmpty(lastAccount))
            preferencesUtil.setPreferenceStringValue(KEY_PRE_ACCOUNT_IN_AFTER_LOGIN, lastAccount);
        boolean result1 = preferencesUtil.setPreferenceStringValue(KEY_PRE_TICKET, ticket);
        boolean result2 = preferencesUtil.setPreferenceStringValue(KEY_PRE_CHIPID, chipId);
        boolean result3 = preferencesUtil.setPreferenceStringValue(KEY_PRE_ACCOUNT_IN_PRE_LOGIN, account);
        boolean result4 = preferencesUtil.setPreferenceIntValue(KEY_LOGIN_STATE, CacheModule.LOGIN_STATE_POS);
        boolean result5 = preferencesUtil.setPreferenceLongValue(Account.TICKET_CREATE_TIME, ticketCreateTime);
        boolean result6 = preferencesUtil.setPreferenceLongValue(Account.TICKET_VAILD_EXPIRE_TIME, ticketVaildExpireTime);

        //TODO:用于适配原来的Ticket存储
        preferencesUtil.setPreferenceStringValue("ticket", ticket);


        return Observable.just(result1 && result2 && result3 && result4 && result5 && result6);
    }

    @Override
    public Observable<Boolean> clearTicket() {
        boolean result = preferencesUtil.setPreferenceStringValue(KEY_PRE_TICKET, "");
        return Observable.just(result);
    }


    String getProperty(Method method) {
        String name = method.getName().substring(3);
        String firstChar = name.substring(0, 1);
        String lowerFirstChar = firstChar.toLowerCase();
        return lowerFirstChar + name.substring(1);
    }

    String getBooleanProperty(Method method) {
        String name = method.getName().substring(3);
        return "is" + name;
    }
}
