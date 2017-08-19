package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk             <br>
 * 类描述  ：ImSdk消息和会话行为 <br>
 * 创建时间：2016/11/16 17:43  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public enum ChangeAction {
    ACT_ADD(1, "新增"),
    ACT_DEL(2, "删除"),
    ACT_RF(3, "刷新"),
    ACT_SC(4, "消息状态变化");

    private String desp;
    private int action;

    private ChangeAction(int act, String dsp) {
        action = act;
        desp = dsp;
    }

    /**
     * 获取当前枚举值
     * @return action
     */
    public int getAction() {
        return action;
    }

    /**
     * 获取当前枚举状态
     * @return String
     */
    public String getDesp() {
        return desp;
    }
}
