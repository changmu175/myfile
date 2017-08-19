package com.xdja.domain_mainframe.di;

import android.graphics.Bitmap;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.dependence.annotations.PerLife;
import com.xdja.domain_mainframe.LauncherGetUserInfoUsecase;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.domain_mainframe.usecase.CkmsReleaseUseCase;
import com.xdja.domain_mainframe.usecase.DetectUseCase;
import com.xdja.domain_mainframe.usecase.QueryAccountAtLocalUseCase;
import com.xdja.domain_mainframe.usecase.account.AccountPwdLoginUseCase;
import com.xdja.domain_mainframe.usecase.account.BindMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.CkmsCreateUseCase;
import com.xdja.domain_mainframe.usecase.account.CustomAccountUseCase;
import com.xdja.domain_mainframe.usecase.account.DataMigrationAddPwdUseCase;
import com.xdja.domain_mainframe.usecase.account.DataMigrationUseCase;
import com.xdja.domain_mainframe.usecase.account.ForceBindMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.MobileLoginUseCase;
import com.xdja.domain_mainframe.usecase.account.ModifyAccountUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainBindMobileAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainLoginAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainResetAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.account.ReObtainAccountUseCase;
import com.xdja.domain_mainframe.usecase.account.RegistAccountUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.CheckFriendMobilesUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.CheckMobileUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.CkmsForceAddDeviceUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.ObtainAuthInfoUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.ObtainDeviceAuthrizeAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.ReObtaionAuthInfoUseCase;
import com.xdja.domain_mainframe.usecase.pwd.AuthFriendPhoneUseCase;
import com.xdja.domain_mainframe.usecase.pwd.CheckRestPwdAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.pwd.RestPwdByAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.pwd.RestPwdByFriendMobilesUseCase;
import com.xdja.domain_mainframe.usecase.thirdencrypt.OpenThirdTransferUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryServerConfigsUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryStrategysUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.UpdateStrategysUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.UploadImgUseCase;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.frame.domain.usecase.Ext6Interactor;

import java.util.List;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@Module
public class PreUseCaseModule {

    /**
     * 提供帐号注册用例（1.1.1.1.1.  	注册账号）
     *
     * @param useCase fill方法参数依次为：昵称、密码、头像文件Id、头像缩略图Id
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.ACCOUNT_REGIST)
    Ext4Interactor<String, String, String, String, MultiResult<String>>
    provideRegistAccountUseCase(RegistAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 提供重新获取帐号的用例（1.1.1.1.13.	 获取新账号）
     *
     * @param useCase fill方法参数依次为：旧帐号、内部验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.ACCOUNT_REOBTAIN)
    Ext2Interactor<String, String, MultiResult<Object>> provideReObtainAccountUseCase(ReObtainAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 提供自定义帐号的用例（1.1.1.1.16.	自定义账号(认证内部验证码)）
     *
     * @param useCase fill方法参数依次为：原帐号，内部验证码，自定义的新账号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.ACCOUNT_CUSTOM)
    Ext3Interactor<String, String, String, Void> provideCustomAccountUseCase(CustomAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 提供用户生成新帐号后，确认新帐号的用例（1.1.1.1.14.	更换账号）
     *
     * @param useCase fill方法依次为：旧帐号，新帐号，内部验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.ACCOUNT_MODIFY)
    Ext3Interactor<String, String, String, Void> provideModifyAccountUseCase(ModifyAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 提供获取绑定手机号所需的验证码的用例（1.1.1.1.4.	获取注册绑定手机号验证码）
     *
     * @param useCase fill方法参数依次为：帐号、手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.BINDMODBILE_AUTHCODE_OBTAIN)
    Ext2Interactor<String, String, String> provideObtainBindMobileAuthCodeUseCase(ObtainBindMobileAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 提供绑定手机号到账号的用例（1.1.1.1.8.	绑定手机号）
     *
     * @param useCase fill方法参数依次为：帐号、短信验证码、内部验证码、手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.BINDMODBILE)
    Ext4Interactor<String, String, String, String, MultiResult<String>> provideBindMobileUseCase(BindMobileUseCase useCase) {
        return useCase;
    }

    /**
     * 提供确认绑定手机号到账号的用例（1.1.1.1.9.	强制绑定手机号）
     *
     * @param useCase fill方法参数依次为：帐号、内部验证码、手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.FORCE_BINDMODBILE)
    Ext3Interactor<String, String, String, Void> provideForceBindMobileUseCase(ForceBindMobileUseCase useCase) {
        return useCase;
    }


    /**
     * 提供用户名和密码登录的用例（1.1.1.1.2.	账号密码登录）
     *
     * @param useCase fill方法参数依次为：帐号、密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.ACCOUNT_PWD_LOGIN)
    Ext2Interactor<String, String, MultiResult<Object>> provideAccountPwdLoginUseCase(AccountPwdLoginUseCase useCase) {
        return useCase;
    }

    /**
     * 判断是否新老账号
     *
     *
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.DATA_MIGRATION)
    Ext0Interactor<DataMigrationAccountBean> provideDataMigrationUseCase(DataMigrationUseCase useCase) {
        return useCase;
    }

    /**
     * 完成数据迁移
     *
     *
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.DATA_MIGRATION_FINISH)
    Ext3Interactor<String,String,String, MultiResult<Object>> provideDataMigrationAddPwdUseCase(DataMigrationAddPwdUseCase useCase) {
        return useCase;
    }

    /**
     * 提供获取登录密码验证码的用例（1.1.1.1.6.	获取登录验证码）
     *
     * @param useCase fill方法参数为手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.LOGIN_AUTHCODE_OBTAIN)
    Ext1Interactor<String, String> provideObtainLoginAuthCodeUseCase(ObtainLoginAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 提供重置密码验证码的用例
     * @param useCase fill方法参数为手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.RESET_AUTHCODE_OBTAIN)
    Ext1Interactor<String, String> provideObtainResetAuthCodeUseCase(ObtainResetAuthCodeUseCase useCase){
        return useCase;
    }

    /**
     * 提供手机号验证码登录的用例（1.1.1.1.3.	手机验证码登录）
     *
     * @param useCase fill方法参数依次为：手机号、短信验证码、内部验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.LOGIN_MOBILE)
    Ext3Interactor<String, String, String, MultiResult<Object>> provideMobileLoginUseCase(MobileLoginUseCase useCase) {
        return useCase;
    }

    /**
     * 获取服务配置用例
     *
     * @param useCase fill方法的参数依次为：无
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_SERVER_CONFIGS)
    Ext0Interactor<Map<String,String>> provideQueryServerConfigsUseCase(QueryServerConfigsUseCase useCase) {
        return useCase;
    }

    /**
     * bitmap压缩成原图和缩略图的jpg文件并上传至文件服务器用例
     *
     * @param useCase fill方法的参数依次为：bitmap
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.UPLOAD_IMG)
    Ext1Interactor<Bitmap, Map<String, String>> provideUploadImgUseCase(UploadImgUseCase useCase) {
        return useCase;
    }

    /*
    * ************************设备绑定相关****************************************************/



    /**
     * 通过验证好友手机号授权设备
     *
     * @param useCase fill方法参数依次为：帐号、内部验证码、好友手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CHECK_FRIEND_MOBILES)
    Ext3Interactor<String, String, List<String>, MultiResult<Object>> provideCheckFriendMobilesUseCase(CheckFriendMobilesUseCase useCase) {
        return useCase;
    }

    /**
     * 通过验证手机验证码授权设备
     *
     * @param useCase fill方法参数依次为：帐号、手机号、短信验证码、内部验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CHECK_MOBILE)
    Ext4Interactor<String, String, String, String, Void> provideCheckMobileUseCase(CheckMobileUseCase useCase) {
        return useCase;
    }

    /**
     * 获取设备授信所需要的信息
     *
     * @param useCase fill方法参数为授权ID
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.OBTAION_AUTH_INFO)
    Ext1Interactor<String, Map<String, String>> provideObtainAuthInfoUseCase(ObtainAuthInfoUseCase useCase) {
        return useCase;
    }

    /**
     * 第三方加密应用策略更新用例
     *
     * @param useCase fill方法的参数依次为：version:协议版本号,cardNo:设备芯片卡号,lastStrategyId:最后策略更新ID，第一次为0,batchSize:批量条数
     * @return
     */
/*    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.UPDATE_STRATEGYS)
    Ext4Interactor<String, String, Integer, Integer, Integer> provideUpdateStrategysUseCase(UpdateStrategysUseCase useCase) {
        return useCase;
    }*/

    /**
     * 第三方加密应用策略更新用例
     *
     * @param useCase fill方法的参数依次为：version:协议版本号,cardNo:设备芯片卡号, model:手机型号，anufacturer:厂商信息 lastStrategyId:最后策略更新ID，第一次为0,batchSize:批量条数
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_STRATEGYBYMOBILE)
    Ext6Interactor<String, String,String, String, Integer, Integer, Integer> provideUpdateStrategysUseCase(UpdateStrategysUseCase useCase) {
        return useCase;
    }

    /**
     * 查询第三方加密应用策略
     *
     * @param useCase fill方法的参数依次为：无
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_STRATEGYS)
    Ext0Interactor<List<EncryptAppBean>> provideQueryStrategysUseCase(QueryStrategysUseCase useCase) {
        return useCase;
    }

    /**
     * 获取授信设备需要的验证码
     *
     * @param useCase fill方法参数依次为：帐号、手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.OBTAION_DEVICE_AUTHRIZE_AUTHCODE)
    Ext2Interactor<String, String, Map<String, String>> provideObtainDeviceAuthrizeAuthCodeUseCase(
            ObtainDeviceAuthrizeAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 重新获取认证所需的信息
     *
     * @param useCase fill方法的参数依次为：帐号、内部验证码和授权码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.REOBTATION_AUTHINF)
    Ext3Interactor<String, String, String, Map<String, String>> provideReObtaionAuthInfoUseCase(
            ReObtaionAuthInfoUseCase useCase) {
        return useCase;
    }

    /*
    * ************************密码管理相关****************************************************/

    /**
     * 验证重置密码的短信验证码
     *
     * @param useCase fill方法的参数依次为：手机号、短信验证码和内部验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CHECK_RESTPWD_AUTHCODE)
    Ext3Interactor<String, String, String, Map<String, String>> provideCheckRestPwdAuthCodeUseCase(
            CheckRestPwdAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 通过验证码重置密码
     *
     * @param useCase fill方法的参数依次为：手机号、内部验证码和密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.RESTPWD_BYAUTHCODE)
    Ext3Interactor<String, String, String, Void> provideRestPwdByAuthCodeUseCase(
            RestPwdByAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 通过验证码重置密码
     *
     * @param useCase fill方法的参数依次为：手机号、内部验证码和密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.AUTH_FRIEND_PHONE)
    Ext2Interactor<String, List<String>, MultiResult<Object>> provideAuthFriendPhoneUseCase(
            AuthFriendPhoneUseCase useCase) {
        return useCase;
    }

    /**
     * 通过好友手机号重置密码
     *
     * @param useCase fill方法的参数依次为：帐号、内部验证码和密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.RESTPWD_BYFRIENDMOBILES)
    Ext3Interactor<String, String, String, Void> provideRestPwdByFriendMobilesUseCase(
            RestPwdByFriendMobilesUseCase useCase) {
        return useCase;
    }

    /**
     * 初始化检测流程
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.DETECT_INIT)
    Ext0Interactor<MultiResult<Object>> provideDetectUseCase(DetectUseCase useCase) {
        return useCase;
    }

    //[S]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.
    /**
     * 初始化检测流程,获取用户配置信息
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.USER_INFO_INIT)
    Ext0Interactor<MultiResult<Object>> provideLauncherGetUserInfoUseCase(LauncherGetUserInfoUsecase useCase) {
        return useCase;
    }
    //[E]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.

    /*[S]add by tangsha@0705 for ckms init*/
    /**
     * Ckms初始化流程
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CKMS_INIT)
    Ext1Interactor<Boolean,MultiResult<Object>> provideCkmsInitUseCase(CkmsInitUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CKMS_CREATE_SEC)
    Ext1Interactor<String,MultiResult<Object>> provideCkmsCreateUseCase(CkmsCreateUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CKMS_FORCE_ADD_DEV)
    Ext1Interactor<String,MultiResult<Object>> provideCkmsForceAddUseCase(CkmsForceAddDeviceUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.OPEN_THIRD_ENCRYPT_TRANSFER)
    Ext4Interactor<String,String,String,String,Boolean> provideOpenThirdEncryptUseCase(OpenThirdTransferUseCase useCase) {
        return useCase;
    }

    /**
     * Ckms释放流程
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.CKMS_RELEASE)
    Ext0Interactor<Integer> provideCkmsReleaseUseCase(CkmsReleaseUseCase useCase) {
        return useCase;
    }
    /*[E]add by tangsha@0705 for ckms init*/

    /**
     * 查询在本地存储的上次登录账号
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_ACCOUNT_AT_LOCAL)
    Ext0Interactor<Account> provideQueryAccountAtLocalUseCase(QueryAccountAtLocalUseCase useCase) {
        return useCase;
    }

    //安全锁同步功能，查询密码和状态
  /*  *//**
     * 查询服务器上的安全锁相关信息
     * @param useCase
     * @return
     *//*
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.GET_SAFELOCK_CLOUD_SETTINGS)
    Ext1Interactor<String,MultiResult<Object>> provideGetSafeLockCloudUseCase(GetSafeLockCloudUseCase useCase) {
        return useCase;
    }*/
    //安全锁同步功能，查询密码和状态
}
