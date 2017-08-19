package com.xdja.imsdk.http.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  网络请求模块相关配置信息                        <br>
 * 创建时间：2016/11/27 下午5:45                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImRequestConfig {

    /**
     * 消息标识，目前用于唯一标识某一请求
     */
    private String msgId;

    /**
     * 网络请求模块使用，标识是否需要启动重发机制
     * true 启动重发机制，主要用于发送文本消息
     * false 不启动重发机制，主要用户消息状态的获取，登录模块等其他业务类请求.
     */
    private boolean needRetry;

    /**
     * 配置选项，即在发送request时，需要添加的一些额外的配置信息.比如在Header中添加自定义字段，以键值对的形式出现。
     */
    private Map<String, String> options = new HashMap<String, String>();

    public ImRequestConfig(){

    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isNeedRetry() {
        return needRetry;
    }

    public void setNeedRetry(boolean needRetry) {
        this.needRetry = needRetry;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setOptions(String key, String value){
        this.options.put(key, value);
    }

    @Override
    public String toString() {
        return "ImRequestConfig{" +
                "msgId='" + msgId + '\'' +
                ", needRetry=" + needRetry +
                ", options=" + options +
                '}';
    }
}
