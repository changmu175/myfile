package com.xdja.presenter_mainframe.autoupdate;

/**
 * 升级文件本地版本信息
 * @author sunyunlei
 *
 */

public class LocalVersionInfo {

	// 本地版本信息
	private  String serverIP; // 升级服务器IP
	private  String serverPort;// 升级服务端端口
	private  String factory; // 手机厂商
	private  String mod; // 手机型号
	private  String os; // 手机操作系统
	private  String soft; // 升级软件名称
	private  String username;// 用户
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	public String getMod() {
		return mod;
	}
	public void setMod(String mod) {
		this.mod = mod;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getSoft() {
		return soft;
	}
	public void setSoft(String soft) {
		this.soft = soft;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
