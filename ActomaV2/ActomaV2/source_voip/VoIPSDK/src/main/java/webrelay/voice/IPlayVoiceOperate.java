package webrelay.voice;

import android.content.res.AssetFileDescriptor;

/**
 * Created by gouhao on 2016/3/21.
 */
public interface IPlayVoiceOperate {
    /**
     * 播放音频文件
     * @param loopCount 循环次数，-1：循环播放。0：不播放。其它：次数
     * @param delay 延迟播放
     */

    void play(AssetFileDescriptor fileDescriptor, int loopCount, int delay);

    /**
     * 停止播放
     */
    void stopPlay();

    /**
     * 暂停播放
     */
    void pause();

}
