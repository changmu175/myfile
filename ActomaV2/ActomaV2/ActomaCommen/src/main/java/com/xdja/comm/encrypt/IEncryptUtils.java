package com.xdja.comm.encrypt;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;

import com.google.gson.Gson;
import com.xdja.atencryptservice.IEncryptService;
import com.xdja.comm.R;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.SettingServer;
import com.xdja.dependence.uitls.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by geyao on 2015/11/16.
 * 第三方加密重构-本地服务交互工具类
 */
 @SuppressWarnings("ConstantConditions")
public class IEncryptUtils {
    /**
     * 日志标签
     */
    private static final String TAG = "IEncryptUtils";
    /**
     * 服务名称
     */
    private static final String SERVICE_NAME = "trust-actom-service";
    /**
     * 调用成功
     */
    private static final int RESULT_OK = 0;

    public final static int APP_VERSION_NAME_LENGTH = 3;

    /**
     * 设置应用策略
     *
     * @param nowStrategyId 本次策略id
     * @param content        策略对象
     */
    public static void setStrategys(String nowStrategyId, List<NewStrategyContentBean> content) {
        long start = System.currentTimeMillis();
        if (content != null && content.size() > 0) {
            try {
                IEncryptService service =
                        IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
                if (service != null) {
                    SettingBean settingBean;
                    String json = new Gson().toJson(content);
                    int result = service.setAppStrategy(json);
                    switch (result) {
                        case RESULT_OK://为0证明成功
                            LogUtil.getUtils(TAG).i("setAppStrategy 调用成功 json: " + json);
                            //实例化设置对象
                            settingBean = new SettingBean();
                            settingBean.setKey(SettingBean.LAST_STRATEGY_ID);
                            settingBean.setValue(nowStrategyId);
                            //保存策略id
                            SettingServer.insertSetting(settingBean);
                            break;
                        default:
                            LogUtil.getUtils(TAG).e("setAppStrategy 调用失败 - result[" + result + "]");
                            break;
                    }
                } else {
                    LogUtil.getUtils(TAG).e("setAppStrategy 服务没启动");
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.getUtils(TAG).e(e.getMessage());
            }
        } else {
            LogUtil.getUtils(TAG).e("setAppStrategy 调用失败 - 策略为空");
        }
        LogUtil.getUtils(TAG).i("setAppStrategy 耗时: " + (System.currentTimeMillis() - start));
    }
//    /**
//     * 设置应用策略
//     *
//     * @param context       上下文句柄
//     * @param nowStrategyId 本次策略id
//     * @param bean          策略对象
//     */
//    public static void setAppStrategy(Context context, String nowStrategyId, NewStrategyResponseBean bean) {
//        long setAppStrategyStart = System.currentTimeMillis();
//        if (bean != null) {
//            try {
//                IEncryptService service =
//                        IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
//                if (service != null) {
//                    SettingBean settingBean;
//                    String json = new Gson().toJson(bean.getContent());
//                    int result = service.setAppStrategy(json);
//                    switch (result) {
//                        case IEncryptParams.RESULT_OK://为0证明成功
//                            LogUtil.getUtils().i(IEncryptParams.TAG + "setAppStrategy 调用成功 bean: " + bean);
//                            //实例化设置对象
//                            settingBean = new SettingBean();
//                            settingBean.setKey(SettingBean.LAST_STRATEGY_ID);
//                            settingBean.setValue(nowStrategyId);
//                            //保存策略id
//                            SettingServer.insertSetting(context, settingBean);
//                            break;
//                        default:
//                            LogUtil.getUtils().i(IEncryptParams.TAG + "setAppStrategy 调用失败");
//                            break;
//                    }
//                } else {
//                    LogUtil.getUtils().e(IEncryptParams.TAG + "setAppStrategy 服务没启动");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                LogUtil.getUtils().e(e.getMessage());
//            }
//        } else {
//            LogUtil.getUtils().i(IEncryptParams.TAG + "setAppStrategy 调用失败--bean 为空");
//        }
//        LogUtil.getUtils().i(IEncryptParams.TAG + "setAppStrategy 耗时: " +
//                (System.currentTimeMillis() - setAppStrategyStart));
//    }

//    /**
//     * 设置第三方加密大开关信息
//     *
//     * @param state 开关状态
//     */
//    public static void setEncryptSwitch(boolean state) {
        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. Start
       /* long setEncryptSwitchStart = System.nanoTime();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                int result = service.setEncryptSwitch(state);
                switch (result) {
                    case IEncryptParams.RESULT_OK://为0证明成功
                        LogUtil.getUtils().i(IEncryptParams.TAG + "setEncryptSwitch 调用成功 state: " + state);
                        break;
                    default:
                        LogUtil.getUtils().i(IEncryptParams.TAG + "setEncryptSwitch 调用失败");
                        break;
                }
            } else {
                LogUtil.getUtils().e(IEncryptParams.TAG + "setEncryptSwitch 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils().e(e.getMessage());
        }
        LogUtil.getUtils().i(IEncryptParams.TAG + "setEncryptSwitch 耗时: " +
                (System.nanoTime() - setEncryptSwitchStart));*/
        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. End
//    }

    /**
     * 设置每个第三方应用的小开关
     * 【2016-08-23日与王旭东沟通，此接口中的map已无用，可传空，但此接口必须在操作第三方加解密之前调用一次，里面做了连接安通+的操作。李晓龙】
     *
     * @param map map集合【传空就行】
     */
    public static void setAppEncryptSwitch(Map<String, Boolean> map) {
        long start = System.currentTimeMillis();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                int result = service.setAppEncryptSwitch(map);
                switch (result) {
                    case RESULT_OK://为0证明成功
                        if (map == null) {
                            LogUtil.getUtils(TAG).i("setAppEncryptSwitch 调用成功 Map集合为空");
                        } else {
                            LogUtil.getUtils(TAG).i("setAppEncryptSwitch 调用成功 Map: " + map.size());
                        }
                        break;
                    default:
                        if (map == null) {
                            LogUtil.getUtils(TAG).e("setAppEncryptSwitch 调用失败 Map集合为空");
                        } else {
                            LogUtil.getUtils(TAG).e("setAppEncryptSwitch 调用失败 Map: " + map.size());
                        }
                        break;
                }
            } else {
                LogUtil.getUtils(TAG).e("setAppEncryptSwitch 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("setAppEncryptSwitch 耗时: " + (System.currentTimeMillis() - start));
    }

    /**
     * 设置密钥信息
     *
     * @param context  上下文句柄
     * @param encksxes 密钥数据集合
     */
/*    public static void setKsxInfo(Context context, ArrayList<AccountKsx> encksxes) {
        SetKsxInfoTask task = new SetKsxInfoTask(context, encksxes);
        task.execute();
    }
*/

//    /**
//     * 设置当前安全通信中的ksf信息
//     *
//     * @param context 上下文句柄
//     * @param account 当前建立安全通道对方的安通账号
//     */
//    public static void setCurrentKsxId(Context context, String account) {
        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. Start
       /* long setCurrentKsxIdStart = System.nanoTime();
        long ksxId = 0;
        if (!TextUtils.isEmpty(account)) {
//           KSManager manager = new KSManager(context);
//            AccountKsx accountKsf = manager.getAccountKsf(account);
//            if (accountKsf != null) {
//                ksxId = Long.parseLong(accountKsf.getKsxId());
//            }

        }
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                int result = service.setCurrentKsxId(ksxId);
                switch (result) {
                    case IEncryptParams.RESULT_OK://为0证明成功
                        LogUtil.getUtils().i(IEncryptParams.TAG + "setCurrentKsxId 调用成功 account: " + account +
                                " ksxId: " + ksxId);
                        break;
                    default:
                        LogUtil.getUtils().i(IEncryptParams.TAG + "setCurrentKsxId 调用失败");
                        break;
                }
            } else {
                LogUtil.getUtils().e(IEncryptParams.TAG + "setCurrentKsxId 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils().e(e.getMessage());
        }
        LogUtil.getUtils().i(IEncryptParams.TAG + "setCurrentKsxId 耗时: " +
                (System.nanoTime() - setCurrentKsxIdStart));*/
        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. End
//    }

    /**
     * 为代理对象注册死亡通知
     */
    public static void registerDeathNotification() {
        long start = System.currentTimeMillis();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                IBinder binder = new Binder();
                int result = service.registerDeathNotification(binder);
                switch (result) {
                    case RESULT_OK://为0证明成功
                        LogUtil.getUtils(TAG).i("registerDeathNotification 调用成功");
                        break;
                    default://其他证明失败
                        LogUtil.getUtils(TAG).e("registerDeathNotification 调用失败");
                        break;
                }
            } else {
                LogUtil.getUtils(TAG).e("registerDeathNotification 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("registerDeathNotification 耗时: " + (System.currentTimeMillis() - start));
    }

    /**
     * 添加设置图片头
     *
     * @param context 上下文句柄
     */
    public static void setImageHead(Context context) {
        long start = System.currentTimeMillis();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                byte[] bytes = InputStreamToByte(context, "encrypt_success.jpg");
                if (bytes != null) {
                    LogUtil.getUtils(TAG).e("setImageHead bytes长度:" + bytes.length);
                    int result = service.setImageHead(bytes);
                    switch (result) {
                        case RESULT_OK://为0证明成功
                            LogUtil.getUtils(TAG).i("setImageHead 调用成功");
                            break;
                        default://其他证明失败
                            LogUtil.getUtils(TAG).e("setImageHead 调用失败");
                            break;
                    }
                } else {
                    LogUtil.getUtils(TAG).e("setImageHead jpg文件转byte数组 数组为空");
                }
            } else {
                LogUtil.getUtils(TAG).e("setImageHead 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("setImageHead 耗时: " + (System.currentTimeMillis() - start));
    }

    /**
     * 添加设置图片失败头
     *
     * @param context 上下文句柄
     */
    public static void setFailedImageHead(Context context) {
        long start = System.currentTimeMillis();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                byte[] bytes = InputStreamToByte(context, "encrypt_fail.jpg");
                if (bytes != null) {
                    LogUtil.getUtils(TAG).e("setFailedImageHead bytes长度:" + bytes.length);
                    int result = service.setFailedImageHead(bytes);
                    switch (result) {
                        case RESULT_OK://为0证明成功
                            LogUtil.getUtils(TAG).i("setFailedImageHead 调用成功");
                            break;
                        default://其他证明失败
                            LogUtil.getUtils(TAG).e("setFailedImageHead 调用失败");
                            break;
                    }
                } else {
                    LogUtil.getUtils(TAG).e("setFailedImageHead jpg文件转byte数组 数组为空");
                }
            } else {
                LogUtil.getUtils(TAG).e("setFailedImageHead 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("setFailedImageHead 耗时: " + (System.currentTimeMillis() - start));
    }

    /**
     * 添加设置语音头
     *
     * @param context 上下文句柄
     */
    public static void setVoiceHead(Context context) {
        long start = System.currentTimeMillis();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                byte[] bytes = InputStreamToByte(context, "voice.amr");
                if (bytes != null) {
                    LogUtil.getUtils(TAG).e("setVoiceHead bytes长度:" + bytes.length);
                    int result = service.setVoiceHead(bytes);
                    switch (result) {
                        case RESULT_OK://为0证明成功
                            LogUtil.getUtils(TAG).i("setVoiceHead 调用成功");
                            break;
                        default://其他证明失败
                            LogUtil.getUtils(TAG).e("setVoiceHead 调用失败");
                            break;
                    }
                } else {
                    LogUtil.getUtils(TAG).e("setVoiceHead voice文件转byte数组 数组为空");
                }
            } else {
                LogUtil.getUtils(TAG).e("setVoiceHead 服务没启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("setVoiceHead 耗时: " + (System.currentTimeMillis() - start));
    }

    /**
     * 文件转换成byte数组
     *
     * @param context  上下文句柄
     * @param fileName 文件名称带后缀格式
     * @return byte[]
     */
    private static byte[] InputStreamToByte(Context context, String fileName) {
        int BUFFER_SIZE = 4096;
        try {
            InputStream open = context.getAssets().open(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count;
            while ((count = open.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
            data = null;
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(fileName + "文件转byte数组出现异常");
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        return null;
    }

//    /**
//     * 开启指定用户指定应用的加密
//     *
//     * @param ksfId       密钥id
//     * @param ksf         密钥
//     * @param appPackage  指定需要加密的应用
//     * @param destAccount 安通账号
//     * @return 是否执行结果状态码
//     */
//    public static int openAccountAppEncryptSwitch(long ksfId, byte[] ksf, String appPackage, String destAccount) {
//        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. Start
//        /*long startOpenAccountAppEncryptSwitch = System.nanoTime();
//        LogUtil.getUtils().i(IEncryptParams.TAG + "openAccountAppEncryptSwitch ksfId: " + ksfId);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "openAccountAppEncryptSwitch ksf: " + ksf.length);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "openAccountAppEncryptSwitch appPackage: " + appPackage);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "openAccountAppEncryptSwitch destAccount: " + destAccount);
//        try {
//            IEncryptService service =
//                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
//            if (service != null) {
//                Map map = new HashMap();
//                map.put("ksfid", ksfId);
//                map.put("ksf", ksf);
//                map.put("appPackage", appPackage);
//                map.put("destAccount", destAccount);
//                int result = service.openAccountAppEncryptSwitch(map);
//                return result;
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            LogUtil.getUtils().e(e.getMessage());
//        }
//        LogUtil.getUtils().i(IEncryptParams.TAG + "openAccountAppEncryptSwitch 耗时: " +
//                (System.nanoTime() - startOpenAccountAppEncryptSwitch));*/
//        //tangsha@xdja.com 2016-08-09 remove. for no use. review by self. End
//        return 1;
//    }

    /**
     * 关闭应用的加密
     *
     * @return 是否执行成功
     */
//    public static boolean closeAccountAppEncryptSwitch(long ksfId, byte[] ksf, String appPackage, String destAccount) {
    public static boolean closeAccountAppEncryptSwitch() {
        long start = System.nanoTime();
//        LogUtil.getUtils().i(IEncryptParams.TAG + "closeAccountAppEncryptSwitch ksfId: " + ksfId);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "closeAccountAppEncryptSwitch ksf: " + ksf.length);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "closeAccountAppEncryptSwitch appPackage: " + appPackage);
//        LogUtil.getUtils().i(IEncryptParams.TAG + "closeAccountAppEncryptSwitch destAccount: " + destAccount);

        boolean isSuccess;
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
//                Map map = new HashMap();
//                map.put("ksfid", ksfId);
//                map.put("ksf", ksf);
//                map.put("appPackage", appPackage);
//                map.put("destAccount", destAccount);
//                int result = service.closeAccountAppEncryptSwitch(map);
                int result = service.closeAccountAppEncryptSwitch();
                switch (result) {
                    case RESULT_OK://为0证明成功
                        isSuccess = true;
                        LogUtil.getUtils(TAG).i("closeAccountAppEncryptSwitch 调用成功");
                        break;
                    default://其他证明失败
                        isSuccess = false;
                        LogUtil.getUtils(TAG).e("closeAccountAppEncryptSwitch 调用失败");
                        break;
                }
            } else {
                isSuccess = false;
                LogUtil.getUtils(TAG).e("closeAccountAppEncryptSwitch 服务没启动");
            }
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("closeAccountAppEncryptSwitch 耗时: " + (System.nanoTime() - start));
        return isSuccess;
    }

    /**
     * 查询与某人的某应用安全通信状态
     *
     * @return 若无数据则返回Null
     */
    public static Map queryAccountAppEncryptSwitchStatus() {
        long start = System.nanoTime();
        try {
            IEncryptService service =
                    IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (service != null) {
                Map map = service.queryAccountAppEncryptSwitchStatus();
                if (map == null) {
                    LogUtil.getUtils(TAG).e("queryAccountAppEncryptSwitchStatus Map集合为空");
                } else {
                    LogUtil.getUtils(TAG).e("queryAccountAppEncryptSwitchStatus Map.size: " + map.size());
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e(e.getMessage());
        }
        LogUtil.getUtils(TAG).i("queryAccountAppEncryptSwitchStatus 耗时: " + (System.nanoTime() - start));
        return null;
    }

    /**
     * 获取包名对应的应用名称
     *
     * @param pkgName 应用包名
     * @return 应用名称
     */
    public static String getAppName(String pkgName) {
        String result = "";
        //[S] modify by lixiaolong on 20160824.pkgName NullPoitException.review by myself.
        if ("com.tencent.mm".equals(pkgName)) {//匹配微信
            result = ActomaController.getApp().getString(R.string.we_chat);
        } else if ("com.tencent.mobileqq".equals(pkgName)) {//匹配QQ
            result = "QQ";
        } else if ("com.alibaba.android.rimet".equals(pkgName)) {//匹配钉钉
            result = ActomaController.getApp().getString(R.string.dingding);
        } else if ("com.immomo.momo".equals(pkgName)) {//匹配陌陌
            result = ActomaController.getApp().getString(R.string.momo);
        } else if ("com.android.mms".equals(pkgName)) {//匹配原生短信
            result = ActomaController.getApp().getString(R.string.short_message);
        } else if ("com.jb.gosms".equals(pkgName)) {//匹配go短信
            result = "Go" + ActomaController.getApp().getString(R.string.short_message);
        } else if ("com.hellotext.hello".equals(pkgName)) {//匹配hello短信
            result = "Hello" + ActomaController.getApp().getString(R.string.short_message);
        } else if ("com.snda.youni".equals(pkgName)) {//匹配youni短信
            result = "Youni" + ActomaController.getApp().getString(R.string.short_message);
        } else if ("com.tencent.pb".equals(pkgName)) {//匹配微信通讯录
            result = ActomaController.getApp().getString(R.string.we_chat_commun);
        } else if ("cn.com.fetion".equals(pkgName)) {//匹配飞信
            result = ActomaController.getApp().getString(R.string.fetion);
        }
        //[E] modify by lixiaolong on 20160824.pkgName NullPoitException.review by myself.
        return result;
    }

    /*[S]add by tangsha for ckms third encrypt*/
    public static String THIRD_KEY = "key";
    public static String THIRD_SEC_KEY = "seckey";
    public static String THIRD_PACKAGE_NAME = "appPackage";
    public static String THIRD_DEST_ACCOUNT = "destAccount";
    public static String THIRD_GROUP_ID = "sgroupId";
    public static int setCurrentKey(Map map){
        int suc = -1;
        LogUtil.getUtils(TAG).d("setCurrentKey " + map.toString());
        IEncryptService service =
                IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
        try {
            if(service != null) {
                suc = service.setCurrentKey(map);
            }else{
                LogUtil.getUtils(TAG).e("setCurrentKey service is null-----");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e("setCurrentKey RemoteException " + e.toString());
        }
        return suc;
    }

    public static int setDecryptKey (Map map){
        int suc = -1;
        LogUtil.getUtils(TAG).d("setDecryptKey "+map.toString());
        IEncryptService service =
                IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
        try {
            if(service != null) {
                suc = service.setDecryptKey(map);
            }else{
                LogUtil.getUtils(TAG).e("setDecryptKey service is null-----");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(TAG).e("setDecryptKey RemoteException "+e.toString());
        }
        return suc;
    }
    /*[E]add by tangsha for ckms third encrypt*/


    /**
     * 是否支持第三方加密
     *
     * @param context 上下文句柄
     * @param appPackageName 包名
     * @return 判断结果
     */
    public static boolean isOrNotSupportThrEncrypt(Context context, String appPackageName) {
        IEncryptService service = IEncryptService.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));

        if (service == null) { // add by ycm for lint 2017/02/15
            return false;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(appPackageName, 0);

            String versionName = pi.versionName;
            LogUtil.getUtils().i("app version:" + versionName + " versionCode:" + pi.versionCode);

            String formedVersion = "";
            String[] splitVersionName = versionName.split("\\.");
            int versionLevel = splitVersionName.length < APP_VERSION_NAME_LENGTH ? splitVersionName.length : APP_VERSION_NAME_LENGTH;
            for (int i = 0; i < versionLevel; i++) {
                formedVersion += splitVersionName[i] + ".";
            }
            formedVersion = formedVersion.substring(0, formedVersion.length() - 1);
            LogUtil.getUtils().i("packageName : " + appPackageName + " Version : " + formedVersion);
            String appVersionStrategy ;

            String metaDataMd5 = getMetaDataMd5(appPackageName, context);
            appVersionStrategy = service.getAppStrategy(appPackageName, metaDataMd5);
            LogUtil.getUtils().i("app strategy query metaDataMd5 : " + appVersionStrategy);

            if (null == appVersionStrategy || "".equals(appVersionStrategy)) {

                String apkMd5 = getApkFileMd5(context);
                appVersionStrategy = service.getAppStrategy(appPackageName, apkMd5);
                LogUtil.getUtils().i("app strategy query apkFileMd5 : " + appVersionStrategy);

                if (null == appVersionStrategy || "".equals(appVersionStrategy)) {

                    appVersionStrategy = service.getAppStrategy(appPackageName, formedVersion);
                    LogUtil.getUtils().i("app strategy qurey version  : " + appVersionStrategy);

                    if (null == appVersionStrategy || "".equals(appVersionStrategy)) {
                        LogUtil.getUtils().i("当前版本不支持第三方加密模块");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String encodeHex(byte[] bytes) {
        StringBuffer hex = new StringBuffer(bytes.length * 2);

        for (byte aByte : bytes) {// modified by ycm for lint 2017/02/13
            int byteIntValue = aByte & 0xff;
            if (byteIntValue < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toString(byteIntValue, 16));
        }

        return hex.toString();
    }

    public static String getApkFileMd5(Context context) {

        String apkPath = context.getPackageResourcePath();
        LogUtil.getUtils().i("apkPath : " + apkPath);
        File file = new File(apkPath);
        LogUtil.getUtils().d("in getFileMd5 file path: " + file.getAbsolutePath());
        MappedByteBuffer byteBuffer;
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
        } catch (FileNotFoundException e) {
            LogUtil.getUtils().e( "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.getUtils().e("digest compute fail");
            e.printStackTrace();
        }
        return encodeHex(messagedigest.digest());
    }

    public static String getAllMetaData(String pkgName, Context context){
        StringBuffer MetaData = new StringBuffer();
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appInfo == null) { // add by ycm for lint 2017/02/15
            return "";
        }

        Bundle appMetaData = appInfo.metaData;
        if (appMetaData != null) {
            Set<String> metaDataKeySet = appMetaData.keySet();
            if (!metaDataKeySet.isEmpty()) {
                for (String key:metaDataKeySet) {
                    String curMetaData = appMetaData.get(key).toString();
                    MetaData.append(curMetaData);
                }
            }
        }
        return String.valueOf(MetaData);
    }

    public static String getStringMd5(String message) {
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messagedigest.update(message.getBytes());
        return encodeHex(messagedigest.digest());
    }

    public static String getMetaDataMd5(String pkgName, Context context){
        String allMetaData = getAllMetaData(pkgName, context);
        if(allMetaData == null || allMetaData.equals("")){
            return "";
        }
        else{
            return getStringMd5(allMetaData);
        }
    }

}
