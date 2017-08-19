package com.xdja.imsdk.model.internal.extra;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：语音文件扩展信息                    <br>
 *          对应file_msg表中的extra_info       <br>
 * 创建时间：2016/12/16 17:34                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class VoiceExtraInfo {
    /**
     * 录音时长
     */
    private int duration;

    public VoiceExtraInfo(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
