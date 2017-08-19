package com.xdja.imsdk.model.body;

/**
 * 项目名称：ImSdk                       <br>
 * 类描述  ：语音类型消息内容体数据模型      <br>
 * 创建时间：2016/11/21 16:15            <br>
 * 修改记录：                            <br>
 *
 * @author liming@xdja.com             <br>
 * @version V1.1.7                     <br>
 */
public class IMVoiceBody extends IMFileBody {
    /**
     * 语音时长
     */
    private int duration;

    public IMVoiceBody(int type) {
        super(type);
    }

    /**
     * 构造方法
     * @param type {@link #type}
     * @param duration {@link #duration}
     */
    public IMVoiceBody(int type, int duration) {
        super(type);
        this.duration = duration;
    }


    /**
	 * 获取语音时长
	 * @return the duration 语音时长
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * 设置 语音时长
	 * @param duration 语音时长
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
    public String toString() {
        return "IMVoiceBody{" +
                "duration=" + duration +
                "} " + super.toString();
    }
}
