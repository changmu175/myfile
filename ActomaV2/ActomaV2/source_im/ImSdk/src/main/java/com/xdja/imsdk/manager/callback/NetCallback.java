package com.xdja.imsdk.manager.callback;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/12 19:01                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public interface NetCallback {
    /**
     * 和IM SERVER 连接的网络状态变化回调接口
     * @param state 变化状态
     */
    void NetChanged(String state);
}
