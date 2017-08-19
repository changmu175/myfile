package com.xdja.imsdk.model.internal.extra;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：视频文件扩展信息                    <br>
 *          对应file_msg表中的extra_info       <br>
 * 创建时间：2016/12/16 17:43                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class VideoExtraInfo {
    /**
     * 录像时长
     */
    private int duration;

    /**
     * 录像大小
     */
    private long size;

    public VideoExtraInfo(int duration, long size) {
        this.duration = duration;
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
