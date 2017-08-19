package com.xdja.contact.http;

import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.configuration.ContactConfigParams;

/**
 * Created by wanghao on 2016/5/16.
 */
public class ConfigurationHttpServiceHelper {

    /**
     * 获取联系人配置信息
     * @return
     */
    public static HttpsRequstResult getContactConfiguration() throws FriendHttpException {
        try {
            return new HttpRequestWrap().synchronizedRequest(new ContactConfigParams());
        }catch (Exception e){
            LogUtil.getUtils().i("wh---------获取联系人配置");
            throw new FriendHttpException();
        }
    }
}
