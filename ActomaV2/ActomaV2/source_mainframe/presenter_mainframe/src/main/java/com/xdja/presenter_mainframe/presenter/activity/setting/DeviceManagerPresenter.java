package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.util.ContactUtils;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.DeviceInfoBean;
import com.xdja.presenter_mainframe.cmd.DeviceManagerCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.DeviceManagerView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDeviceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class DeviceManagerPresenter extends PresenterActivity<DeviceManagerCommand, VuDeviceManager> implements DeviceManagerCommand {
    public static final String CARD_NO = "cardNo";
    public static final String DEVICE_NAME = "deviceName";
    /*[S]add by tangsha for ckms relieve device by ckms deviceId*/
    public static final String DEVICE_SN = "sn";
    /*[E]add by tangsha for ckms relieve device by ckms deviceId*/
    public static final String BIND_TIME = "bindTime";
    //modify by alh@xdja.com to fix bug: 588 2016-06-24 start(rummager: liuwangle)
    //[s]modify by xienana for multi language to remove static string @20161226
    public final String STATUS_STR_MINE = ActomaController.getApp().getString(R.string.local);
    public static final String STATUS_STR_OUTLINE = "";
    public final String STATUS_STR_ONLINE = ActomaController.getApp().getString(R.string.has_login);
    //[e]modify by xienana for multi language to remove static string @20161226
    //modify by alh@xdja.com to fix bug: 588 2016-06-24 end(rummager: liuwangle)
    public static final String STATUS = "status";
    public static final String STATUS_OUTLINE = "0";
    public static final String STATUS_ONLINE = "1";


    @Inject
    @InteractorSpe(value = DomainConfig.QUERY_DEVICES)
    Lazy<Ext0Interactor<List<Map<String, String>>>> queryDevicesUseCase;
    @Inject
    @InteractorSpe(value = DomainConfig.RELIEVE_DEVICE)
    Lazy<Ext3Interactor<String,String,String,Boolean>> relieveDeviceUseCase;
    @Inject
    @InteractorSpe(value = DomainConfig.MODIFY_DEVICE_NAME)
    Lazy<Ext2Interactor<String, String, Void>> modifyDeviceNameUseCase;

    @Inject
    LogoutHelper logoutHelper;

    @Inject
    Map<String, Provider<String>> stringMap;
    private List<Map<String, String>> deviceInfoMaps;
    private List<DeviceInfoBean> deviceInfoBeen;
    private DeviceInfoBean currentDeviceInfoBean;


    @NonNull
    @Override
    protected Class<? extends VuDeviceManager> getVuClass() {
        return DeviceManagerView.class;
    }

    @NonNull
    @Override
    protected DeviceManagerCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }
        executeInteractorNoRepeat(queryDevicesUseCase.get().fill(),
                new LoadingDialogSubscriber<List<Map<String,String>>>(this,this) {
                    @Override
                    public void onNext(List<Map<String, String>> maps) {
                        super.onNext(maps);
                        deviceInfoMaps = maps;
                        deviceInfoBeen = new ArrayList<>();
                        for (Map<String, String> map : maps) {
                            DeviceInfoBean deviceInfoBean = new DeviceInfoBean(map.get(CARD_NO),
                                                            map.get(DEVICE_NAME),map.get(DEVICE_SN));
                            LogUtil.getUtils().d("cardNo:"+map.get(CARD_NO)+"\nKEY_DEVICEID:"+stringMap.get(CacheModule.KEY_DEVICEID).get());
                            if (map.get(CARD_NO).equals(stringMap.get(CacheModule.KEY_DEVICEID).get())) {
                                deviceInfoBean.setStatus(STATUS_STR_MINE);
                                //本机设备放在第一个
                                deviceInfoBeen.add(0,deviceInfoBean);
                            } else {
                                String status = map.get(STATUS);
                                if (status != null) {
                                    switch (status) {
                                        case STATUS_OUTLINE:
                                            deviceInfoBean.setStatus(STATUS_STR_OUTLINE);
                                            break;
                                        case STATUS_ONLINE:
                                            deviceInfoBean.setStatus(STATUS_STR_ONLINE);
                                    }
                                }
                                deviceInfoBeen.add(deviceInfoBean);
                            }
                        }
                        getVu().setDeviceInfoList(deviceInfoBeen);
                    }
                }.registerLoadingMsg(getString(R.string.load_device_list)));

    }

    @Override
    public void authDeviceLogin() {
        Navigator.navigateToAuthDeviceLogin();
    }

    @Override
    public void deleteDevice(final DeviceInfoBean deviceInfoBean) {
        if (deviceInfoMaps == null) {
            return;
        }
        String cardNo = getDeviceCardNo(deviceInfoBean);
        if (cardNo == null)
            return;
        currentDeviceInfoBean = deviceInfoBean;
        executeInteractorNoRepeat(relieveDeviceUseCase.get().fill(cardNo,
                ContactUtils.getCurrentAccount(),getDeviceCkmsId(deviceInfoBean)),
                new LoadingDialogSubscriber<Boolean>(this,this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        super.onNext(aBoolean);
                        deleteDeviceSuccess(deviceInfoBean);
                    }
                }.registerLoadingMsg(getString(R.string.delete)));
    }

    private void deleteDeviceSuccess(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean.getStatus().equals(STATUS_STR_MINE)){
            logoutHelper.diskLogout();
            logoutHelper.navigateToLoginWithExit();
            return;
        }
        deviceInfoBeen.remove(deviceInfoBean);
        getVu().setDeviceInfoList(deviceInfoBeen);
    }

    @Override
    public void modifyDeviceName(final DeviceInfoBean deviceInfoBean, final String newName) {
        if (deviceInfoMaps == null) {
            return;
        }
        String cardNo = getDeviceCardNo(deviceInfoBean);
        if (cardNo == null)
            return;
        executeInteractorNoRepeat(modifyDeviceNameUseCase.get().fill(cardNo, newName),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        for (DeviceInfoBean device : deviceInfoBeen) {
                            if (device.getCardNo().equals(deviceInfoBean.getCardNo())) {
                                device.setDeviceName(newName);
                            }
                        }
                        getVu().setDeviceInfoList(deviceInfoBeen);
                    }
                }.registerLoadingMsg(getString(R.string.modify)));
    }


    private String getDeviceCardNo(DeviceInfoBean deviceInfoBean) {
        return deviceInfoBean.getCardNo();
    }

    private String getDeviceCkmsId(DeviceInfoBean deviceInfoBean) {
        return deviceInfoBean.getSnNo();
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        if (okCode.equals(ServerException.DEVICE_NOT_REGISTER)) {
            if (currentDeviceInfoBean != null) {
                deleteDeviceSuccess(currentDeviceInfoBean);
            }
        }
        return true;
    }
}
