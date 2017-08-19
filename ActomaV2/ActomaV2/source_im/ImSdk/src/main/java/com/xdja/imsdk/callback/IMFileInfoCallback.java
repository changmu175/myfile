package com.xdja.imsdk.callback;

import com.xdja.imsdk.model.IMFileInfo;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：文件信息回调接口   <br>
 * 创建时间：2016/11/16 15:07  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public interface IMFileInfoCallback {
	/**
	 * 文件上传进度更新
	 * @param fileInfo 当前上传的文件回调信息
	 * @see IMFileInfo
	 * @return 0： 回调函数未处理 <br>
	 *         1： 回调函数已处理
     */
	int SendFileProgressUpdate(IMFileInfo fileInfo);
	
	/**
	 * 发送文件消息，文件上传失败
	 * @param fileInfo 当前发送的文件信息
	 * @see IMFileInfo
     * @return 0： 回调函数未处理<br>
	 *         1 ：回调函数已处理
     */
	int SendFileFail(IMFileInfo fileInfo);
	
	/**
	 * 发送文件消息，文件上传完成
	 * @param fileInfo 当前发送的文件信息
	 * @see IMFileInfo
	 * @return  0： 回调函数未处理<br>
	 *          1： 回调函数已处理
	 */
	int SendFileFinish(IMFileInfo fileInfo);


	/**
	 * 接收文件消息，更新文件下载进度
	 * @param fileInfo 当前接收的文件信息
	 * @see IMFileInfo
	 * @return  0： 回调函数未处理<br>
	 *          1 ：回调函数已处理
	 */
	int ReceiveFileProgressUpdate(IMFileInfo fileInfo);
	
	/**
	 * 接收文件消息，文件下载失败
	 * @param fileInfo 当前接收的文件信息
	 * @see IMFileInfo
	 * @return  0： 回调函数未处理<br>
	 *          1 ：回调函数已处理
	 */
	int ReceiveFileFail(IMFileInfo fileInfo);

	
	/**
	 * 
	 * 接收文件消息，下载完成
	 * @param fileInfo 当前接收的文件信息
	 * @return  0： 回调函数未处理<br>
	 *          1 ：回调函数已处理
	 */
	int ReceiveFileFinish(IMFileInfo fileInfo);

	/**
	 * 接收文件消息,下载暂停
	 * @param fileInfo 当前接收的文件信息
	 * @see IMFileInfo
	 * @return  0： 回调函数未处理<br>
	 *          1 ：回调函数已处理
	 * */
	int ReceiveFilePaused(IMFileInfo fileInfo);
}
