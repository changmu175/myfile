package com.xdja.atencryptservice;

interface IEncryptService
{

/*********安通+调用的接口***********/

//设置应用策略
int setAppStrategy(String strategy);
//为代理对象注册死亡通知
int registerDeathNotification(IBinder binder);
//设置微信图片加密成功时显示的图片
int setImageHead(inout byte[] imageHead);
//设置微信图片加密失败时显示的图片
int setFailedImageHead(inout byte[] failedImageHead);
//设置微信语音加密成功时播放的语音
int setVoiceHead(inout byte[] voiceHead);
//设置加密key和seckey
int setCurrentKey(in Map map);
//设置每个第三方应用的小开关
int setAppEncryptSwitch(in Map map);
//设置解密key和seckey
int setDecryptKey(in Map map);
//关闭指定用户指定应用的加密
int closeAccountAppEncryptSwitch();


/*********钩子调用的接口***********/

//查询应用是否受支持
boolean isAppSupported(String appPackageName);
//获取第三方应用的策略信息
String getAppStrategy(String appPackageName, String appVersion);
//初始化文件加密
Map initEncrypt();
//初始化文件解密
String initDecrypt(inout byte[] protocolHead);
//执行加解密
byte[] crypt(inout byte[] dataIn, String sessionKey, int action);
//文件加解密完成
void doFinal(String sessionKey);
//获取微信图片加密成功时显示的图片
byte[] getImageHead();
//获取微信图片加密失败时显示的图片
byte[] getFailedImageHead();
//获取微信语音加密成功时播放的语音
byte[] getVoiceHead();
//查询开关通道是否开启
Map queryAccountAppEncryptSwitchStatus();

}