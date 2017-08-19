package com.xdja.comm.data;

/**
 * Created by geyao on 2015/7/17.
 * 系统设置bean
 */
public class SettingBean {
    /*================相关系统设置变量定义===================*/
    /**
     * 新消息通知
     */
    public final static String NEWSREMIND = "newsRemind";
    /**
     * 新消息通知-声音
     */
    public final static String NEWSREMIND_RING = "newsRemindByRing";
    /**
     * 新消息通知-振动
     */
    public final static String NEWSREMIND_SHAKE = "newsRemindByShake";

    /**
  * 听筒模式
  */
    public final static String RECEIVER_MODE = "receiverMode";

    /**
     * 勿扰模式
     */
    public final static String NODISTRUB = "noDistrub";

    /**
     * 安全锁通知
     *
     */
    public final static String SAFE_LOCK = "safeLock";

    /**
     * 锁屏锁定
     */
    public final static String LOCK_SCREEN = "lockScreen";

    /**
     * 后台运行锁定
     */
    public final static String LOCK_BACKGROUND = "backgorundLock";
    /**
     * 加密对象的安通账号
     */
    public static String ACCOUNT = "encryptAccount";
    /**
     * 第三方应用加密服务
     */
    public static String SEVER = "otherAppEncryptSever";
    /**
     * 启动时安全口令验证
     */
    public static String VERTIFY = "otherAppPasswordVertify";
    /**
     * 快速开启第三方应用
     */
    public static String THIRDAPP = "quickOtherThirdApp";
    /**
     * 版本号
     */
    public static String VERSION = "version";

    /**
     * 最后策略更新ID，第一次为0
     */
    public static String LAST_STRATEGY_ID = "lastStrategyId";
    /*================相关系统设置变量定义===================*/
    /**
     * 设置key
     */
    private String key;
    /**
     * 对应key的value值
     */
    private String value;

    /**
     * 预留扩展字段
     */
    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;

    /**
     * 预留扩展字段
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    @Override
    public String toString() {
        return "SettingBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", column1='" + column1 + '\'' +
                ", column2='" + column2 + '\'' +
                ", column3='" + column3 + '\'' +
                ", column4='" + column4 + '\'' +
                ", column5='" + column5 + '\'' +
                '}';
    }
}
