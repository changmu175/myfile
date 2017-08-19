package com.xdja.imsdk.model.file;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：高清缩略图文件信息  <br>
 * 创建时间：2016/11/19 18:12  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class IMHDThumbFileInfo {
    /**
     * 高清缩略图文件名称
     */
    private String hdTDisplayName;

    /**
     * 高清缩略图文件本地存储路径
     */
    private String hdTLocalPath;

    /**
     * 高清缩略图文件大小
     */
    private long hdTFileSize;

    /**
     * 高清缩略图文件传输大小
     */
    private long hdTFileTranslateSize;

    /**
     * 高清缩略图文件状态
     * @see FileState
     */
    private FileState hdState;

    public IMHDThumbFileInfo() {
    }

    public IMHDThumbFileInfo(String hdTLocalPath) {
        this.hdTLocalPath = hdTLocalPath;
    }

	/**
	 * 获取高清缩略图文件名称
	 * @return the hdTDisplayName 高清缩略图文件名称
	 */
	public String getHdTDisplayName() {
		return hdTDisplayName;
	}

	/**
	 * 设置 高清缩略图文件名称
	 * @param hdTDisplayName 高清缩略图文件名称
	 */
	public void setHdTDisplayName(String hdTDisplayName) {
		this.hdTDisplayName = hdTDisplayName;
	}

	/**
	 * 获取高清缩略图文件本地存储路径
	 * @return the hdTLocalPath 高清缩略图文件本地存储路径
	 */
	public String getHdTLocalPath() {
		return hdTLocalPath;
	}

	/**
	 * 设置 高清缩略图文件本地存储路径
	 * @param hdTLocalPath 高清缩略图文件本地存储路径
	 */
	public void setHdTLocalPath(String hdTLocalPath) {
		this.hdTLocalPath = hdTLocalPath;
	}

	/**
	 * 获取高清缩略图文件大小
	 * @return the hdTFileSize 高清缩略图文件大小
	 */
	public long getHdTFileSize() {
		return hdTFileSize;
	}

	/**
	 * 设置 高清缩略图文件大小
	 * @param hdTFileSize 高清缩略图文件大小
	 */
	public void setHdTFileSize(long hdTFileSize) {
		this.hdTFileSize = hdTFileSize;
	}

	/**
	 * 获取高清缩略图文件传输大小
	 * @return the hdTFileTranslateSize 高清缩略图文件传输大小
	 */
	public long getHdTFileTranslateSize() {
		return hdTFileTranslateSize;
	}

	/**
	 * 设置 高清缩略图文件传输大小
	 * @param hdTFileTranslateSize 高清缩略图文件传输大小
	 */
	public void setHdTFileTranslateSize(long hdTFileTranslateSize) {
		this.hdTFileTranslateSize = hdTFileTranslateSize;
	}

	/**
	 * 获取高清缩略图文件状态
	 * @return the hdState 高清缩略图文件状态
	 */
	public FileState getHdState() {
		return hdState;
	}

	/**
	 * 设置 高清缩略图文件状态
	 * @param hdState 高清缩略图文件状态
	 */
	public void setHdState(FileState hdState) {
		this.hdState = hdState;
	}

    
}
