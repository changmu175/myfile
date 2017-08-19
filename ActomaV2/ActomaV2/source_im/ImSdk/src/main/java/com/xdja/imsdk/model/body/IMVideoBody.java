package com.xdja.imsdk.model.body;

import com.xdja.imsdk.model.file.IMRawFileInfo;

/**
 * 项目名称：ImSdk                    <br>
 * 类描述  ：视频类型消息内容体数据模型   <br>
 * 创建时间：2016/11/21 16:15         <br>
 * 修改记录：                         <br>
 *
 * @author liming@xdja.com          <br>
 * @version V1.1.7                  <br>
 */
public class IMVideoBody extends IMFileBody {
    /**
     * 视频时长
     */
    private int duration;

    /**
     * 视频尺寸大小
     */
    private long size;

    /**
     * 视频原始文件信息
     */
    private IMRawFileInfo rawFileInfo;

    public IMVideoBody(int type) {
        super(type);
    }

    public IMVideoBody(int type, int duration) {
        super(type);
        this.duration = duration;
    }
    public IMVideoBody(int type, int duration, long size) {
        super(type);
        this.duration = duration;
        this.size = size;
    }

	/**
	 * 获取视频时长
	 * @return the duration 视频时长
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * 设置 视频时长
	 * @param duration 视频时长
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * 获取视频大小
	 * @return the size 视频大小
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 设置 视频大小
	 * @param size 视频大小
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * 获取视频原始文件信息
	 * @return the rawFileInfo 视频原始文件信息
	 */
	public IMRawFileInfo getRawFileInfo() {
		return rawFileInfo;
	}

	/**
	 * 设置 视频原始文件信息
	 * @param rawFileInfo 视频原始文件信息
	 */
	public void setRawFileInfo(IMRawFileInfo rawFileInfo) {
		this.rawFileInfo = rawFileInfo;
	}

    
}
