package com.xdja.imsdk.model.body;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;
import com.xdja.imsdk.model.file.IMExtraFileInfo;

/**
 * 项目名称：ImSdk                                                 <br>
 * 类描述  ：文件类型消息内容数据模型                                 <br>
 *          此类中描述的文件即为展示在消息列表中的文件信息，说明如下：    <br>
 *          语音类型为语音原始文件                                   <br>
 *          图片类型为图片普通缩略图文件                              <br>
 *          视频类型为视频缩略图文件                                 <br>
 * 创建时间：2016/11/21 16:14                                      <br>
 * 修改记录：                                                      <br>
 *
 * @author liming@xdja.com                                       <br>
 * @version V1.1.7                                               <br>
 */
public class IMFileBody extends IMMessageBody {
    /**
     * 文件在本地的存储路径
     */
    private String localPath;

    /**
     * 文件显示名称
     */
    private String displayName;

    /**
     * 文件总大小
     */
    private long fileSize;

    /**
     * 文件已传输大小
     */
    private long translateSize;

    /**
     * 原始文件后缀名称后缀
     */
    private String suffix;

    /**
     * 文件类型
     * @see com.xdja.imsdk.constant.ImSdkFileConstant
     */
    private int type;

    /**
     * 文件状态
     * @see FileState
     */
    private FileState state;

    /**
     * 构造方法
     * @param type {@link #type}
     */
    public IMFileBody(int type) {
        this.type = type;
    }

    

    /**
	 * 获取文件在本地的存储路径
	 * @return the localPath 文件在本地的存储路径
	 */
	public String getLocalPath() {
		return localPath;
	}



	/**
	 * 设置 文件在本地的存储路径
	 * @param localPath 文件在本地的存储路径
	 */
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}



	/**
	 * 获取文件显示名称
	 * @return the displayName 文件显示名称
	 */
	public String getDisplayName() {
		return displayName;
	}



	/**
	 * 设置 文件显示名称
	 * @param displayName 文件显示名称
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	/**
	 * 获取文件总大小
	 * @return the fileSize 文件总大小
	 */
	public long getFileSize() {
		return fileSize;
	}



	/**
	 * 设置 文件总大小
	 * @param fileSize 文件总大小
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}



	/**
	 * 获取文件已传输大小
	 * @return the translateSize 文件已传输大小
	 */
	public long getTranslateSize() {
		return translateSize;
	}



	/**
	 * 设置 文件已传输大小
	 * @param translateSize 文件已传输大小
	 */
	public void setTranslateSize(long translateSize) {
		this.translateSize = translateSize;
	}



	/**
	 * 获取原始文件后缀名称后缀
	 * @return the suffix 原始文件后缀名称后缀
	 */
	public String getSuffix() {
		return suffix;
	}



	/**
	 * 设置 原始文件后缀名称后缀
	 * @param suffix 原始文件后缀名称后缀
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}



	/**
	 * 获取文件类型
	 * @return the type 文件类型
	 */
	public int getType() {
		return type;
	}



	/**
	 * 设置 文件类型
	 * @param type the 文件类型
	 */
	public void setType(int type) {
		this.type = type;
	}



	/**
	 * 获取文件状态
	 * @return the state 文件状态
	 */
	public FileState getState() {
		return state;
	}



	/**
	 * 设置 文件状态
	 * @param state 文件状态
	 */
	public void setState(FileState state) {
		this.state = state;
	}



	public boolean isNormal() {
        return type == ImSdkFileConstant.FILE_NORMAL;
    }

    public boolean isVoice() {
        return type == ImSdkFileConstant.FILE_VOICE;
    }

    public boolean isImage() {
        return type == ImSdkFileConstant.FILE_IMAGE;
    }

    public boolean isVideo() {
        return type == ImSdkFileConstant.FILE_VIDEO;
    }
}
