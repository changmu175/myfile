package com.xdja.data_mainframe.rest;

import com.xdja.frame.data.net.ServiceGenerator;

/**
 * <p>Summary:Rest服务适配器</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:14:24</p>
 */
public class ApiFactory {

    public static AccountRestApi getAccountRestApi(ServiceGenerator serviceGenerator) {
        return serviceGenerator.createService(AccountRestApi.class);
    }

    public static AccountRestApi getAccountRestApi(ServiceGenerator serviceGenerator, String baseUrl) {
        return serviceGenerator.createService(AccountRestApi.class, baseUrl);
    }

    public static UserInfoRestApi getUserInfoRestApi(ServiceGenerator serviceGenerator) {
        return serviceGenerator.createService(UserInfoRestApi.class);
    }

    public static UserInfoRestApi getUserInfoRestApi(ServiceGenerator serviceGenerator, String baseUrl) {
        return serviceGenerator.createService(UserInfoRestApi.class, baseUrl);
    }


    public static DeviceAuthRestApi getDeviceAuthRestApi(ServiceGenerator serviceGenerator) {
        return serviceGenerator.createService(DeviceAuthRestApi.class);
    }

    public static DeviceAuthRestApi getDeviceAuthRestApi(ServiceGenerator serviceGenerator, String baseUrl) {
        return serviceGenerator.createService(DeviceAuthRestApi.class, baseUrl);
    }

    public static PwdRestApi getPwdRestApi(ServiceGenerator serviceGenerator) {
        return serviceGenerator.createService(PwdRestApi.class);
    }

    public static PwdRestApi getPwdRestApi(ServiceGenerator serviceGenerator, String baseUrl) {
        return serviceGenerator.createService(PwdRestApi.class, baseUrl);
    }
    /*[S]add by tangsha@20160705 for ckms*/
    public static CkmsApi getCkmsApi(ServiceGenerator serviceGenerator) {
        return serviceGenerator.createService(CkmsApi.class);
    }
    /*[E]add by tangsha@20160705 for ckms*/

    public static DownloadApi getDownloadApi(ServiceGenerator serviceGenerator, String baseUrl) {
        return serviceGenerator.createService(DownloadApi.class, baseUrl);
    }

}
