package com.xdja.imsdk.model.body;

import com.xdja.imsdk.model.file.IMHDThumbFileInfo;
import com.xdja.imsdk.model.file.IMRawFileInfo;

/**
 * 项目名称：ImSdk                         <br>
 * 类描述  ：图片类型消息内容体数据模型        <br>
 * 创建时间：2016/11/21 16:15              <br>
 * 修改记录：                              <br>
 *
 * @author liming@xdja.com               <br>
 * @version V1.1.7                       <br>
 */
public class IMImageBody extends IMFileBody {
	/**
	 * 高清缩略图信息
	 */
    private IMHDThumbFileInfo hdTFileInfo;
    
    /**
     * 原图信息
     */
    private IMRawFileInfo rawFileInfo;

    public IMImageBody(int type) {
        super(type);
    }

	/**
	 * 获取高清缩略图信息
	 * @return the hdTFileInfo 高清缩略图信息
	 */
	public IMHDThumbFileInfo getHdTFileInfo() {
		return hdTFileInfo;
	}

	/**
	 * 设置 高清缩略图信息
	 * @param hdTFileInfo the 高清缩略图信息 to set
	 */
	public void setHdTFileInfo(IMHDThumbFileInfo hdTFileInfo) {
		this.hdTFileInfo = hdTFileInfo;
	}

	/**
	 * 获取原图信息
	 * @return the rawFileInfo 原图信息
	 */
	public IMRawFileInfo getRawFileInfo() {
		return rawFileInfo;
	}

	/**
	 * 设置 原图信息
	 * @param rawFileInfo the 原图信息 to set
	 */
	public void setRawFileInfo(IMRawFileInfo rawFileInfo) {
		this.rawFileInfo = rawFileInfo;
	}

    
}
