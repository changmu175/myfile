package com.xdja.contact.http.wrap.params.department;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.util.ContactUtils;

/**
 * 检测集团当前用户集团通讯录是否变更
 * 注意如果用户数据属于多个集团---处理方式
 */
public class DetectDepartIncrementalParam extends AbstractHttpParams {

    /**
     * 检查部门和人员更新
     * @param deptLastUpdateId
     * @param personLastUpdateId
     */
    public DetectDepartIncrementalParam(int deptLastUpdateId, int personLastUpdateId) {
        super(ContactUtils.getCompanyCode(),String.valueOf(deptLastUpdateId), String.valueOf(personLastUpdateId));
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/contact/detector?deptLastUpdateId=%1$s&personLastUpdateId=%2$s", args[1],args[2]);//add by lwl
        //return String.format("/v1/contact/detector/%1$s?deptLastUpdateId=%2$s&personLastUpdateId=%3$s", args[0], args[1],args[2]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
