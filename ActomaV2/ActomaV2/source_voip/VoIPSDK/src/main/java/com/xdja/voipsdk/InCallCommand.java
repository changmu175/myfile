package com.xdja.voipsdk;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by guoyaxin on 2016/1/7.
 */
public interface InCallCommand extends Command {

    /**
     * 挂断
     */
     void  hangup();

    /**
     * 重播
     */
     void recall();

     /**
      * 发送短信
      */
     void sendIM();

    /**
     * 接听
     */
     void answer();

    /**
     * 静音
     * @param isOpen
     */
    void setMicrophoneMute(boolean isOpen);

    /**
     * 扩音器
     * @param isOpen
     */
    void setSpeakerphoneOn(boolean isOpen);


    /**
     * 蓝牙
     * @param isOpen
     */
    void setBluetoothOn(boolean isOpen);

    /**
     * 显示当前界面
     */
    void setCurrent();

    /**
     * 显示通知
     */
    void showNotify();

    /**
     * 清除通知
     */
    void clearNotify();



}
