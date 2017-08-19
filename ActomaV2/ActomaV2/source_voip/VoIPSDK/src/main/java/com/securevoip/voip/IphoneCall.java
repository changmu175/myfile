package com.securevoip.voip;

import android.content.Context;

import com.csipsimple.api.SipProfile;
import com.securevoip.voip.bean.IncomingInfo;

public interface IphoneCall
{

	int MAX_SESSION = 1;
	int STATUS_CODE_BASE = 0x1000;

	int SUCCESS = STATUS_CODE_BASE + 1;
	/**
	 * 账户不合法
	 */
	int ACC_INVALID = STATUS_CODE_BASE + 2;


	/**
	 * 拨打普通电话
	 * @param info      联系人信息
	 * @param _context  窗体句柄
	 */
	void phoneCall(IncomingInfo info, Context _context);   //普遍电话接口
	/**
	 * 加密电话接口
	 * @param info     联系人信息
	 * @param _context 窗体句柄
	 */
	void encryptPhoneCall(IncomingInfo info, Context _context);//加密电话接口
	/**
	 * 获取联系人在线状态
	 * @param info     联系人信息
	 * @param _context 窗体句柄
	 * @return         true or  false  在线或者不在线
	 */
	boolean getLineState(IncomingInfo info, Context _context); //获取在线状态

	/**
	 * 添加联系人账户
	 * @param _context    窗体句柄
	 * @param passwd    ticket;
	 */
	void addAccount(Context _context, String userId, String displayName, String passwd);  //添加用户


	/**
	 * 初始化安通帐号数据
	 * @param _context
	 * @param accName
	 * @param ticket
	 * @return
	 */
	int initAccount(Context _context, String accName, String ticket);

	/**
	 * 获取安通帐号
	 * @param _context
	 * @return
	 */
	SipProfile buildCustAccount(Context _context, String serverAddr);

	int online(Context _context);

	void offLine(Context _context);



	void stop(Context _context);


	/**
	 * 删除联系人账户
	 * @param _context  窗体句柄
	 * @return          影响条数
	 */
	int delAccount(Context _context);                        //删除用户
	/**
	 * 启动电话服务
	 * @param context  窗体句柄
	 */
	void startSipService(Context context);              //启动电话服务
	/**
	 * 系统发短信接口
	 * @param context    窗体句柄
	 * @param phoneNumber 接收方电话号码
	 */
	void sendSMS(Context context, String phoneNumber);   //系统发短信接口
/*    *//**
     * 启动联系人加载服务
     * @param context  窗体句柄
     *//*
	public void startTxlService(Context context); //启动联系人加载服务
*/}