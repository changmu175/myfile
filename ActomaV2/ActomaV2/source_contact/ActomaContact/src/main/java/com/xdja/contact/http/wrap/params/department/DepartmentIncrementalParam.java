package com.xdja.contact.http.wrap.params.department;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.util.ContactUtils;

/**
 * Created by yangpeng on 2015/7/29.
 * 集团部门增量更新请求体
 */
public class DepartmentIncrementalParam extends AbstractHttpParams {
    /**
     * 部门更新
     * @param deptLastUpdateId
     * @param deptSubUpdateId
     * @param batchSize
     */
    public DepartmentIncrementalParam(int deptLastUpdateId, int deptSubUpdateId, int batchSize) {
        super(ContactUtils.getCompanyCode(),String.valueOf(deptLastUpdateId), String.valueOf(deptSubUpdateId), String.valueOf(batchSize));
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/contact/depts?deptLastUpdateId=%1$s&deptSubUpdateId=%2$s&batchSize=%3$s",  args[1], args[2],args[3]);//add by lwl
        //return String.format("/v1/contact/%1$s/depts?deptLastUpdateId=%1$s&deptSubUpdateId=%2$s&batchSize=%3$s", args[0], args[1], args[2],args[3]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
