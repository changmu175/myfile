package com.xdja.presenter_mainframe.autoupdate;

/**
 * 升级版本配置信息
 * @author sunyunlei
 *
 */

public class UpdateVersionInfo {
	private String version ; // 最新版本号
	private String date; // 版本发布时间
	private String comment; // 注释信息
	private String updatetag;// 更新类型
	private String deleteDb;// 是否删除数据库checkCode
	private String checkCode;// 版本内容校验码
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUpdatetag() {
		return updatetag;
	}
	public void setUpdatetag(String updatetag) {
		this.updatetag = updatetag;
	}
	public String getDeleteDb() {
		return deleteDb;
	}
	public void setDeleteDb(String deleteDb) {
		this.deleteDb = deleteDb;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
}
