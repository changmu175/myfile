package com.xdja.imsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMSecurityCallback;

import java.util.HashMap;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：初始化参数        <br>
 * 创建时间：2016/11/16 15:13  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class InitParam implements Parcelable {

    /**
     * 帐号
     */
    private String account;

    /**
     * 安全卡id
     */
    private String tfcardId;

    /**
     * 有效票据
     */
    private String ticket;

    /**
     * ImSdk对外的全局回调
     */
    private CallbackFunction callback;

    /**
     * ImSdk加解密回调
     */
    private IMSecurityCallback securityCallback;

    /**
     * ImSdk的功能属性配置，初始化时配置，不配置时使用默认配置
     */
    private HashMap properties = new HashMap<>();

    /**
     * 设备类型
     */
    private int dType;

    /**
     * 构造方法
     */
    public InitParam() {

    }

    public static final Creator<InitParam> CREATOR = new Creator<InitParam>() {
        @Override
        public InitParam createFromParcel(Parcel in) {
            InitParam initParam = new InitParam();
            initParam.account = in.readString();
            initParam.tfcardId = in.readString();
            initParam.ticket = in.readString();
            initParam.properties = in.readHashMap(HashMap.class.getClassLoader());
            initParam.dType = in.readInt();
            return initParam;
        }

        @Override
        public InitParam[] newArray(int size) {
            return new InitParam[size];
        }
    };

    /**
     * 获取帐号信息
     * @return {@link #account}
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置帐号信息
     * @param account 账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取安全卡id
     * @return {@link #tfcardId}
     */
    public String getTfcardId() {
        return tfcardId;
    }

    /**
     * 设置安全卡id
     * @param tfcardId 安全卡id
     */
    public void setTfcardId(String tfcardId) {
        this.tfcardId = tfcardId;
    }

    /**
     * 获取ticket
     * @return {@link #ticket}
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * 设置ticket
     * @param ticket ticket值
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * 获取回调接口实例
     * @return {@link #callback}
     * @see CallbackFunction
     */
    public CallbackFunction getCallback() {
        return callback;
    }

    /**
     * 设置回调接口实例
     * @param callback 回调接口实例
     * @see CallbackFunction
     */
    public void setCallback(CallbackFunction callback) {
        this.callback = callback;
    }

    /**
     * 加解密回调
     * @return IMSecurityCallback
     */
    public IMSecurityCallback getSecurityCallback() {
        return securityCallback;
    }

    /**
     * 加解密回调
     * @param securityCallback securityCallback
     */
    public void setSecurityCallback(IMSecurityCallback securityCallback) {
        this.securityCallback = securityCallback;
    }

    /**
     * 获取设备类型
     * @return {@link #dType}
     * @see com.xdja.imsdk.constant.DeviceType
     */
    public int getdType() {
        return dType;
    }

    /**
     * 设置设备类型
     * @param dType 设备类型值
     * @see com.xdja.imsdk.constant.DeviceType
     */
    public void setdType(int dType) {
        this.dType = dType;
    }

    /**
     * 获取属性配置项
     * @return {@link #properties}
     */
    public HashMap<String, String> getProperties() {
        return properties;
    }

    /**
     * 设置属性配置项
     * @param properties 配置项表
     */
    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.account);
        dest.writeString(this.tfcardId);
        dest.writeString(this.ticket);
        dest.writeMap(properties);
        dest.writeInt(this.dType);
    }

    @Override
    public String toString() {
        return "InitParam{" +
                "account='" + account +
                "', tfcardId='" + tfcardId +
                "', ticket='" + ticket  +
                "', callback=" + callback +
                ", properties=" + properties +
                ", dType=" + dType +
                '}' + '\'';
    }
}
