package webrelay.voice;

import com.csipsimple.api.MediaState;

/**
 * Created by admin on 16/3/26.
 */
public interface IVoiceMediaOperate {
    /**
     * 打开扬声器
     * @param on
     */
    void setSpeakerphoneOn(boolean on);


    /**
     * 静音模式
     * @param on
     */
    void setMicrophoneMute(boolean on);


    /**
     * 蓝牙耳机
     * @param on
     */
    void setBluetoothOn(boolean on);

    /**
     * 线控耳机是否插入
     * @param on
     */
    void setHeadsetOn(boolean on);

    /**
     * 获取当前媒体状态
     * @return
     */
     MediaState getMediaState();

}
