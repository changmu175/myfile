package com.xdja.contact.http.request.friend;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by wanghao on 2015/7/23.
 * 设置备注 需要传递的参数
 */
public class UpdateRemarkBody extends RequestBody {

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
