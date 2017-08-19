package com.xdja.imsdk.model.file;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;

/**
 * 项目名称：ImSdk                                <br>
 * 类描述  ：原始文件信息，包括原图，视频文件等        <br>
 * 创建时间：2016/11/19 18:13                     <br>
 * 修改记录：                                     <br>
 *
 * @author liming@xdja.com                      <br>
 * @version V1.1.7                              <br>
 */
public class IMRawFileInfo {
    /**
     * 原始文件名称
     */
    private String rawDisplayName;

    /**
     * 原始文件本地存储路径
     */
    private String rawLocalPath;

    /**
     * 原始文件大小
     */
    private long rawFileSize;

    /**
     * 原始文件已传输大小
     */
    private long rawFileTranslateSize;

    /**
     * 原始文件状态
     * @see FileState
     */
    private FileState rawState;

    public IMRawFileInfo() {
    }

    public IMRawFileInfo(String rawLocalPath) {
        this.rawLocalPath = rawLocalPath;
    }

	/**
	 * 获取原始文件名称
	 * @return the rawDisplayName 原始文件名称
	 */
	public String getRawDisplayName() {
		return rawDisplayName;
	}

	/**
	 * 设置 原始文件名称
	 * @param rawDisplayName 原始文件名称
	 */
	public void setRawDisplayName(String rawDisplayName) {
		this.rawDisplayName = rawDisplayName;
	}

	/**
	 * 获取原始文件本地存储路径
	 * @return the rawLocalPath 原始文件本地存储路径
	 */
	public String getRawLocalPath() {
		return rawLocalPath;
	}

	/**
	 * 设置 原始文件本地存储路径
	 * @param rawLocalPath 原始文件本地存储路径
	 */
	public void setRawLocalPath(String rawLocalPath) {
		this.rawLocalPath = rawLocalPath;
	}

	/**
	 * 获取原始文件大小
	 * @return the rawFileSize 原始文件大小
	 */
	public long getRawFileSize() {
		return rawFileSize;
	}

	/**
	 * 设置 原始文件大小
	 * @param rawFileSize 原始文件大小
	 */
	public void setRawFileSize(long rawFileSize) {
		this.rawFileSize = rawFileSize;
	}

	/**
	 * 获取原始文件已传输大小
	 * @return the rawFileTranslateSize 原始文件已传输大小
	 */
	public long getRawFileTranslateSize() {
		return rawFileTranslateSize;
	}

	/**
	 * 设置 原始文件已传输大小
	 * @param rawFileTranslateSize 原始文件已传输大小
	 */
	public void setRawFileTranslateSize(long rawFileTranslateSize) {
		this.rawFileTranslateSize = rawFileTranslateSize;
	}

	/**
	 * 获取原始文件状态
	 * @return the rawState 原始文件状态
	 */
	public FileState getRawState() {
		return rawState;
	}

	/**
	 * 设置 原始文件状态
	 * @param rawState 原始文件状态
	 */
	public void setRawState(FileState rawState) {
		this.rawState = rawState;
	}

    
}
