package com.xdja.contact.http;

import android.util.Log;

import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.contact.exception.http.DepartmentException;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.department.GetCompanyCodeParam;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by xienana on 2016/12/14.
 */
public class DepartmentHttpServiceHelper {
    /**
     * 获取集团通讯录companyCode信息
     *  */
    public static HttpsRequstResult getComapnyCode(String account) throws DepartmentException {
        try {
            return new HttpRequestWrap().synchronizedRequest(new GetCompanyCodeParam(account));
        }catch (Exception e){
            LogUtil.getUtils().i("DepartmentHttpServiceHelper getcompanycode error"+"Exception="+e.toString());
            throw new DepartmentException();
        }
    }
}
