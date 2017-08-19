package com.xdja.domain_mainframe.di;

import android.content.Context;
import android.graphics.Bitmap;

import com.xdja.comm.data.SettingBean;
import com.xdja.dependence.annotations.PerLife;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.CheckUpdateUseCase;
import com.xdja.domain_mainframe.usecase.DownloadUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainBindAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainModifyAuthCodeUseCase;
import com.xdja.domain_mainframe.usecase.account.ObtainModifyMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.RefreshTicketUserCase;
import com.xdja.domain_mainframe.usecase.account.TicketBindMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.TicketCustomAccountUseCase;
import com.xdja.domain_mainframe.usecase.account.TicketForceBindMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.UnBindMobileUseCase;
import com.xdja.domain_mainframe.usecase.account.UnbindMobileDiskUseCase;
import com.xdja.domain_mainframe.usecase.dev.ModifyDeviceNameUseCase;
import com.xdja.domain_mainframe.usecase.dev.QueryDevicesUseCase;
import com.xdja.domain_mainframe.usecase.dev.RelieveDeviceUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.AuthDeviceUseCase;
import com.xdja.domain_mainframe.usecase.deviceauth.GetAuthInfo;
import com.xdja.domain_mainframe.usecase.settings.GetNewsRemindSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.GetNoDistrubSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.GetReceiverModeSettingUseCase;
import com.xdja.domain_mainframe.usecase.safeLock.GetSafeLockSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.SaveNewsRemindSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.SaveNoDistrubSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.SaveReceiverModeSettingUseCase;
import com.xdja.domain_mainframe.usecase.safeLock.SaveSafeLockSettingUseCase;
import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackImageUseCase;
import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;
import com.xdja.domain_mainframe.usecase.thirdencrypt.HookUpdateUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.AuthPasswdUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.GetCurrentAccountInfoUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.LogoutUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.ModifyAvatarUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.ModifyNikeNameUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.ModifyPasswdUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryAccountInfoUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryBatchAccountUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryIncrementAccountsUseCase;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.domain.usecase.Ext5Interactor;

import java.util.List;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:登录后---退出前才能执行的用例注入提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/4</p>
 * <p>Time:16:17</p>
 */
@Module
public class PostUseCaseModule {
    /**
     * 提供刷新Ticket用例
     *
     * @param useCase fill方法参数为：老ticket
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.REFRESH_TICKET)
    Ext1Interactor<String, Map<String,Object>> provideRefreshTicketUserCase(RefreshTicketUserCase useCase) {
        return useCase;
    }

    /**
     * 提供修改昵称的用例
     *
     * @param useCase fill方法参数为：昵称
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_NICKNAME)
    Ext1Interactor<String, Map<String, String>> provideModifyNickNameUseCase(ModifyNikeNameUseCase useCase) {
        return useCase;
    }

    /**
     * 提供绑定手机号到账号（Ticket验证）的用例（1.1.1.1.10.	绑定手机号—验证ticket）
     *
     * @param useCase fill方法的参数依次为：短信验证码、手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.TICKE_BIND_MOBILE)
    Ext2Interactor<String, String, Void> provideTickeBindMobileUseCase(TicketBindMobileUseCase useCase) {
        return useCase;
    }

    /**
     * 提供强制绑定手机号到账号（Ticket验证）的用例（1.1.1.1.11.	强制绑定手机号—验证ticket）
     *
     * @param useCase fill方法的参数为手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.TICKE_FORCE_BIND_MOBILE)
    Ext1Interactor<String, Void> provideTickeForceBindMobileUseCase(TicketForceBindMobileUseCase useCase) {
        return useCase;
    }

    /**
     * 提供绑定验证码获取的用例（Ticket）（1.1.1.1.7.	获取绑定验证码）
     *
     * @param useCase fill方法参数为手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.BIND_AUTHOCODE_OBTAIN)
    Ext1Interactor<String, Void> provideObtainBindAuthCodeUseCase(ObtainBindAuthCodeUseCase useCase) {
        return useCase;
    }

    /**
     * 提供更换手机号验证码获取的用例（Ticket）
     * @return fill方法参数为手机号
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_AUTHOCODE_OBTAIN)
    Ext1Interactor<String, Void> provideObtainChangeAuthCodeUseCase(ObtainModifyAuthCodeUseCase useCase){
        return useCase;
    }

    /**
     * 提供更换手机号的用例（Ticket）
     * @param useCase fill方法的参数为手机号、验证码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_MOBILE)
    Ext2Interactor<String,String,Void> provideObtainModifyMobileUseCase(ObtainModifyMobileUseCase useCase){
        return useCase;
    }

    /**
     * 提供自定义帐号（Ticket验证）的用例（1.1.1.1.15.	自定义账号）
     *
     * @param useCase fill方法的参数为自定义的帐号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.TICKE_CUSTOM_ACCOUNT)
    Ext1Interactor<String, Void> provideTickeCustomAccountUseCase(TicketCustomAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 检测账号密码用例
     *
     * @param useCase fill方法的参数依次为：密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.AUTH_PASSWD)
    Ext1Interactor<String, Void> provideAuthPasswdUseCase(AuthPasswdUseCase useCase) {
        return useCase;
    }

    /**
     * 客户端退出用例
     *
     * @param useCase fill方法的参数依次为：无
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.LOGOUT)
    Ext0Interactor<Void> provideLogoutUseCase(LogoutUseCase useCase) {
        return useCase;
    }

    /**
     * 修改头像用例
     *
     * @param useCase fill方法的参数依次为：头像Id,头像缩略图Id
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_AVATAR)
    Ext1Interactor<Bitmap, Void> provideModifyAvatarUseCase(ModifyAvatarUseCase useCase) {
        return useCase;
    }

    /**
     * 修改授信设备名称用例
     *
     * @param useCase fill方法的参数依次为：设备卡号,设备名称
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_DEVICE_NAME)
    Ext2Interactor<String, String, Void> provideModifyDeviceNameUseCase(ModifyDeviceNameUseCase useCase) {
        return useCase;
    }

    /**
     * 修改昵称用例
     *
     * @param useCase fill方法的参数依次为：新昵称
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_NIKE_NAME)
    Ext1Interactor<String, Map<String, String>> provideModifyNikeNameUseCase(ModifyNikeNameUseCase useCase) {
        return useCase;
    }

    /**
     * 修改密码用例
     *
     * @param useCase fill方法的参数依次为：新密码
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.MODIFY_PASSWD)
    Ext1Interactor<String, Void> provideModifyPasswdUseCase(ModifyPasswdUseCase useCase) {
        return useCase;
    }

    /**
     * 根据查询条件精确查询账户详情用例
     *
     * @param useCase fill方法的参数依次为：账号或手机号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_ACCOUNT_INFO)
    Ext1Interactor<String, MultiResult<String>> provideQueryAccountInfoUseCase(QueryAccountInfoUseCase useCase) {
        return useCase;
    }

    /**
     * 批量查询用户信息用例
     *
     * @param useCase fill方法的参数依次为：账号信息列表
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_BATCH_ACCOUNT)
    Ext1Interactor<List<String>, MultiResult<String>> provideQueryBatchAccountUseCase(QueryBatchAccountUseCase useCase) {
        return useCase;
    }

    /**
     * 查询设备账号授信的设备列表用例
     *
     * @param useCase fill方法的参数依次为：无
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_DEVICES)
    Ext0Interactor<List<Map<String,String>>> provideQueryDevicesUseCase(QueryDevicesUseCase useCase) {
        return useCase;
    }

    /**
     * 获取用户相关账号信息用例
     *
     * @param useCase fill方法的参数依次为：lastUpdateId,batchSize
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.QUERY_INCREMENT_ACCOUNTS)
    Ext2Interactor<Integer, Integer, MultiResult<String>> provideQueryIncrementAccountsUseCase(QueryIncrementAccountsUseCase useCase) {
        return useCase;
    }

    /**
     * 解除授信设备与账号的关系用例
     *
     * @param useCase fill方法的参数依次为：设备卡号
     * @return
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.RELIEVE_DEVICE)
    Ext3Interactor<String,String,String,Boolean> provideRelieveDeviceUseCase(RelieveDeviceUseCase useCase) {
        return useCase;
    }

    /**
     * 为设备授权
     *
     * @param useCase
     * @return fill参数方法为授权ID,cardNo
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.AUTH_DEVICE)
	/*[S]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    Ext5Interactor<String,String, String, String,String,Void> provideAuthDeviceUseCase(AuthDeviceUseCase useCase) {
	/*[E]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
        return useCase;
    }
    /**
     * 获取授信信息
     *
     * @param useCase
     * @return fill参数方法为授权ID
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.GET_AUTH_INFO)
	/*[S]modify by xienana@2016/08/31 to fix bug 2202 [review by] tangsha*/
    Ext2Interactor<String,String,Map<String,Object>> provideGetAuthInfo(GetAuthInfo useCase) {
	/*[E]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Ext0Interactor<Account> provideGetCurrentAccountInfoUseCase(GetCurrentAccountInfoUseCase useCase){
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.UNBIND_MOBILE)
    Ext1Interactor<String,Void> provideUnBindMobileUseCase(UnBindMobileUseCase useCase){
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.UNBIND_DISK_MOBILE)
    Ext1Interactor<String,Void> provideUnbindMobileDiskUseCase(UnbindMobileDiskUseCase useCase){
        return useCase;
    }

    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.DOWNLOAD_FROM_APPSTORE)
    Ext2Interactor<String, Long, Object> provideDownloadUseCase(DownloadUseCase useCase){
        return useCase;
    }

    /**
     * HookService更新用例
     */
    @Provides
    @PerLife
    @InteractorSpe(DomainConfig.HOOK_UPDATE)
    Ext1Interactor<Context, Boolean> provideHookUpdateUseCase(HookUpdateUseCase useCase) {
        return useCase;
    }

    /**
     * 查询在本地存储新消息提示配置
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.GET_NEWREMIND_SETTINGS)
    Ext1Interactor<Context,SettingBean[]> provideGetNewsRemindSettingUseCase(GetNewsRemindSettingUseCase useCase) {
        return useCase;
    }
    /**
     * 保存新消息提示配置
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.SET_NEWREMIND_SETTINGS)
    Ext2Interactor<Context, SettingBean[], Boolean[]> provideSaveNewsRemindSettingUseCase(SaveNewsRemindSettingUseCase useCase) {
        return useCase;
    }


    /**
     * 查询在本地存储听筒模式配置
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.GET_RECEIVERMODE_SETTINGS)
    Ext1Interactor<Context,SettingBean[]> provideGetReceiverModeSettingUseCase(GetReceiverModeSettingUseCase useCase) {
        return useCase;
    }
    /**
     * 保存新消息提示配置
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.SET_RECEIVERMODE_SETTINGS)
    Ext2Interactor<Context, SettingBean[], Boolean[]> provideSaveReceiverModeSettingUseCase(SaveReceiverModeSettingUseCase useCase) {
        return useCase;
    }

    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.GET_NODISTRUB_SETTINGS)
    Ext1Interactor<Context,GetNoDistrubSettingUseCase.NoDistrubBean> provideGetNoDistrubSettingUseCase(GetNoDistrubSettingUseCase useCase) {
        return useCase;
    }

    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.SET_NODISTRUB_SETTINGS)
    Ext2Interactor<Context,GetNoDistrubSettingUseCase.NoDistrubBean,Boolean> provideSaveNoDistrubSettingUseCase(SaveNoDistrubSettingUseCase useCase){
        return useCase;
    }

    /**
     * 获取安全锁状态信息
     * @param useCase
     * @return
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.GET_SAFELOCK_SETTINGS)
    Ext1Interactor<Context,SettingBean[]> provideGetSafeLockSettingUseCase(GetSafeLockSettingUseCase useCase) {
        return useCase;
    }

    /**
     * 保存安全锁设置状态
     * @param useCase
     * @return
     */
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.SET_SAFELOCK_SETTINGS)
    Ext2Interactor<Context, SettingBean[], Boolean[]> provideSaveSafeLockSettingUseCase(SaveSafeLockSettingUseCase useCase) {
        return useCase;
    }


    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.CHECK_NEW_VERSION)
    Ext1Interactor<Context, Boolean> provideCheckUpdateUseCase(CheckUpdateUseCase useCase) {
        return useCase;
    }

    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.FEED_BACK)
    Ext2Interactor<Context, UploadFeedBackUseCase.FeedBackRequestBean, UploadFeedBackUseCase.UploadFeedBackResponeBean> provideUploadFeedBackUseCase(UploadFeedBackUseCase useCase){
        return useCase;
    }

    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.FEED_BACK_IMAGE)
    Ext2Interactor<Context, List<String> , String> provideUploadFeedBackImageUseCase(UploadFeedBackImageUseCase useCase){
        return useCase;
    }


   //应用锁多设备同步功能增加。
   /* *//**
     * 保存手势密码
     * @param useCase
     * @return
     *//*
    @PerLife
    @Provides
    @InteractorSpe(DomainConfig.SAVE_GESTURE_CLOUD)
    Ext2Interactor<Context,Map<String,String>,String> provideSaveGesturePwdUseCase(SaveGesturePwdUseCase useCase) {
        return useCase;
    }*/
    //应用锁多设备同步功能增加。
}
