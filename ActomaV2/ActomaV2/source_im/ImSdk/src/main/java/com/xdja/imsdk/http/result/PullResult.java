package com.xdja.imsdk.http.result;

import com.xdja.imsdk.http.bean.MsgBean;

import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 19:48                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class PullResult {
    private List<MsgBean> data;
    private int value;

    /**
     * @return the data
     */
    public List<MsgBean> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<MsgBean> data) {
        this.data = data;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
}
