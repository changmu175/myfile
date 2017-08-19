package com.xdja.contact.http.wrap.params.department;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.util.ContactUtils;

/**
 * 增量集团人员请求体
 */
public class DepartMemberIncrementalParam extends AbstractHttpParams {

    /**
     * 人员更新
     * @param personLastUpdateId
     * @param personSubUpdateId
     * @param batchSize
     */
    public DepartMemberIncrementalParam(long personLastUpdateId,
                                        long personSubUpdateId, long batchSize) {
        super(ContactUtils.getCompanyCode(),String.valueOf(personLastUpdateId), String.valueOf(personSubUpdateId), String.valueOf(batchSize));
    }


    @Override
    public HttpMethod getMethod() {
        return  HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/contact/persons?personLastUpdateId=%1$s&personSubUpdateId=%2$s&batchSize=%3$s",  args[1], args[2],args[3]);//add by lwl
       // return String.format("/v1/contact/%1$s/persons?personLastUpdateId=%1$s&personSubUpdateId=%2$s&batchSize=%3$s", args[0], args[1], args[2],args[3]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
