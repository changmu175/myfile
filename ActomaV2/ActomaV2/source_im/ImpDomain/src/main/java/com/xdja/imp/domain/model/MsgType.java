package com.xdja.imp.domain.model;

 /**
 *  ImSdk的消息的类型，是Sdk已定义的值，也可以自行扩展
 */
public class MsgType {
	 /**
	  * 文本消息
	  */
	 public static final int MSG_TYPE_TEXT = 1;  //文本消息

	 /**
	  * 文件消息
	  */
	 public static final int MSG_TYPE_FILE = 2;	//文件消息

	 /**
	  * 群组消息
	  */
	 public static final int MSG_TYPE_GROUP = 4;	//群组消息

	 /**
	  * 闪信
	  */
	 public static final int MSG_TYPE_BOMB = 8;  //闪信


	 /**
	  * 自定义消息类型
	  */
	 public static final int MSG_TYPE_PRESENTATION = 128;
}