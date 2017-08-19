package com.xdja.imsdk.model;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：文件操作参数                       <br>
 * 创建时间：2016/11/24 19:24                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class IMFileInfo {
    /**
     * 操作的文件所属的消息
     * @see IMMessage
     */
    private IMMessage message;

    /**
     * 操作的文件所属的会话标识
     * @see IMSession
     */
    private String tag;

    /**
     * 操作的文件的类型
     * @see FileType
     */
    private FileType fileType;

    /**
     * 操作的文件状态码
     * @see FileState
     */
    private FileState state;

    /**
     * 操作的文件传输进度
     */
    private int percent;

    public IMFileInfo() {
    }

    public IMFileInfo(IMMessage message) {
        this.message = message;
    }

	/**
	 * 获取操作的文件的类型
	 * @return the fileType 操作的文件的类型
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * 设置 操作的文件的类型
	 * @param fileType 操作的文件的类型
	 */
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	
	

	/**
	 * 获取操作的文件所属的消息
	 * @return the message 操作的文件所属的消息
	 */
	public IMMessage getMessage() {
		return message;
	}

	/**
	 * 设置 操作的文件所属的消息
	 * @param message 操作的文件所属的消息
	 */
	public void setMessage(IMMessage message) {
		this.message = message;
	}

	/**
	 * 获取操作的文件所属的会话标识
	 * @return the tag 操作的文件所属的会话标识
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 设置 操作的文件所属的会话标识
	 * @param tag 操作的文件所属的会话标识
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 获取操作的文件状态码
	 * @return the state 操作的文件状态码
	 */
	public FileState getState() {
		return state;
	}

	/**
	 * 设置 操作的文件状态码
	 * @param state 操作的文件状态码
	 */
	public void setState(FileState state) {
		this.state = state;
	}

	/**
	 * 获取操作的文件传输进度
	 * @return the percent 操作的文件传输进度
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * 设置 操作的文件传输进度
	 * @param percent 操作的文件传输进度
	 */
	public void setPercent(int percent) {
		this.percent = percent;
	}

	@Override
    public String toString() {
        return "IMFileInfo{" +
                "message=" + message +
                ", tag='" + tag + '\'' +
                ", fileType=" + fileType +
                ", state=" + state +
                ", percent=" + percent +
                '}';
    }
}
