package com.xdja.imsdk.manager;

import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkConfig;
import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.util.ToolUtils;
import com.xdja.imsdk.util.ValidateUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  ImSdk配置项                                   <br>
 * 创建时间：2016/11/27 下午4:24                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImSdkConfigManager {
    private static ImSdkConfigManager instance;
    private Map<String, String> propertyMap = new HashMap<>();                 //所有配置项
    private String md5Account;

    public static ImSdkConfigManager getInstance() {
        synchronized (ImSdkConfigManager.class) {
            if (instance == null) {
                instance =  Factory.getInstance();
            }
        }
        return instance;
    }

    private static class Factory {
        static ImSdkConfigManager getInstance() {
            return new ImSdkConfigManager();
        }
    }

    /**
     * 初始化
     * @param property 上层自定义的配置项
     * @return 保存结果
     */
    public int init(String account, Map<String, String> property) {
        md5Account = "." + ToolUtils.toMD5(account);
        propertyMap = ImSdkDbUtils.queryOptions();

        String path = property.get(ImSdkConfig.K_PATH);
        if (!TextUtils.isEmpty(path)) {
            property.put(ImSdkConfig.K_REC, path + ImSdkFileConstant.FILE_REC + File.separator);
            property.put(ImSdkConfig.K_PATH, path + md5Account + File.separator);
        }

        if (propertyMap.isEmpty()) {

            propertyMap = baseConfig();                                   // 无配置项，获取基本配置项

            if (add(property)) {
                save(propertyMap);                                        // 保存所有配置项
                return ImSdkResult.RESULT_OK;
            }
        } else {
            property.put(ImSdkConfig.K_DIFF, ImSdkConfig.V_CONFIG_0);     // 每次初始化，重置时间差值

            if (add(property)) {
                save(property);                                           // 保存自定义配置项
                return ImSdkResult.RESULT_OK;
            }
        }

        return ImSdkResult.RESULT_FAIL_PARA;
    }

    /**
     * 释放配置项
     */
    public void releaseAll() {
        propertyMap.clear();
        instance = null;
    }

    /**
     * 校验并保存自定义配置
     * @param property 自定义配置
     * @return 保存结果
     */
    public int saveConfig(Map<String, String> property) {
        if (add(property)) {
            save(property);                                              // 保存自定义配置项
            return ImSdkResult.RESULT_OK;
        }
        return ImSdkResult.RESULT_FAIL_PARA;
    }

    /**
     * 获取配置项
     * @param key 配置项key
     * @return 配置项value
     */
    public String getConfigByKey(String key) {
        return propertyMap.get(key);// TODO: 2016/12/9 liming time diff reset
    }

    /**
     * 自定义配置校验并加入到propertyMap
     * @param custom 自定义配置
     * @return 校验结果
     */
    private boolean add(Map<String, String> custom) {
        boolean checked = true;
        if (custom != null && !custom.isEmpty()) {
            Set<Map.Entry<String, String>> entrySet = custom.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (!ValidateUtils.verifyCustomConfig(entry.getKey(), entry.getValue())) {
                    checked = false;// TODO: 2016/12/28 liming 校验实现
                    break;
                }

                propertyMap.put(entry.getKey(), entry.getValue());
            }
        }

        return checked;
    }

    /**
     * 配置项保存
     * @param options 配置项
     */
    private void save(Map<String, String> options) {
        ImSdkDbUtils.saveOptions(options);
    }

    /**
     * 初始化默认配置项属性
     */
    private Map<String, String> baseConfig() {
        Map<String, String> base = new HashMap<>();
        base.put(ImSdkConfig.K_SERVER, ImSdkConfig.V_SERVER);                  //Im server 默认地址
        base.put(ImSdkConfig.K_FILE_ADDR, ImSdkConfig.V_FILE);                 //File server默认地址
        base.put(ImSdkConfig.K_FILE_PORT, ImSdkConfig.V_FILE_PORT);            //File server默认端口
        base.put(ImSdkConfig.K_STATE, ImSdkConfig.V_TRUE);                     //是否需要消息状态，默认开启 // TODO: 2016/12/12 liming
        base.put(ImSdkConfig.K_CHANGE, ImSdkConfig.V_TRUE);                    //是否需要会话展示最后消息状态，默认关闭
        base.put(ImSdkConfig.K_SESSION, ImSdkConfig.V_TRUE);                   //是否需要管理会话，默认开启
        base.put(ImSdkConfig.K_SIZE, ImSdkConfig.V_SIZE);                      //发送文件最大大小
        base.put(ImSdkConfig.K_THU, ImSdkConfig.V_FALSE);                      //是否需要ImSdk生成缩略图，默认关闭
        base.put(ImSdkConfig.K_THU_W, ImSdkConfig.V_THU_W);                    //缩略图宽度
        base.put(ImSdkConfig.K_THU_H, ImSdkConfig.V_THU_H);                    //缩略图高度
        base.put(ImSdkConfig.K_HD_W, ImSdkConfig.V_HD_W);                      //高清缩略图宽度
        base.put(ImSdkConfig.K_HD_H, ImSdkConfig.V_HD_H);                      //高清缩略图高度
        base.put(ImSdkConfig.K_PATH, ImSdkConfig.FILE_PATH);                   //ImSdk所有缓存文件路径
        base.put(ImSdkConfig.K_REC, ImSdkConfig.FILE_REC);                     //ImSdK保存接收的文件路径
        base.put(ImSdkConfig.K_PER, ImSdkConfig.V_CONFIG_1);                   //ImSdk回调文件进度，默认1%
        base.put(ImSdkConfig.K_ROAM, ImSdkConfig.V_3_DAY);                     //漫游周期，默认3天
        base.put(ImSdkConfig.K_SYNC, ImSdkConfig.V_CONFIG_0);                  //同步周期，默认关闭
        base.put(ImSdkConfig.K_DIFF, ImSdkConfig.V_CONFIG_0);                  //和后台同步时间差，默认为0
        base.put(ImSdkConfig.K_HTTPS, ImSdkConfig.V_TRUE);                     //Im是否开启https连接，默认开启
        base.put(ImSdkConfig.K_STORE, ImSdkConfig.V_EMPTY);                    //https连接使用的证书id
        base.put(ImSdkConfig.K_CERT, ImSdkConfig.V_EMPTY);                     //https连接使用的证书密码
        base.put(ImSdkConfig.K_ENCRYPT, ImSdkConfig.V_TRUE);                   //是否需要加密，默认开启
        base.put(ImSdkConfig.K_BOMB, ImSdkConfig.V_TRUE);                      //是否有闪信，默认开启 // TODO: 2016/12/12 liming  
        base.put(ImSdkConfig.K_PRELOAD, ImSdkConfig.V_FALSE);                  //是否需要预下载，默认开启
        base.put(ImSdkConfig.K_ORIGIN, ImSdkConfig.V_ORIGIN);                  //小图是否自动传输原图，默认开启

        return base;
    }

    /**
     * 获取ImServer地址
     * @return ImServer地址
     */
    public String getImServer() {
        return propertyMap.get(ImSdkConfig.K_SERVER);
    }

    /**
     * ImSdk文件保存路径
     * 获取当前账户根目录
     * @return /sdcard/AppName/XdjaIm/.md5(account)
     */
    public String getPath() {
        return propertyMap.get(ImSdkConfig.K_PATH);
    }

    /**
     * ImSdk接收到的文件保存路径
     * 获取当前账户根目录
     * @return /sdcard/AppName/XdjaIm/FileRec/
     */
    public String getRecPath() {
        return propertyMap.get(ImSdkConfig.K_REC);
    }

    /**
     * ImSdk漫游周期
     * @return 漫游周期
     */
    public long getRoam() {
        long roam = 0;
        try {
            roam = Long.parseLong(propertyMap.get(ImSdkConfig.K_ROAM));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return roam;
    }

    /**
     * ImSdk同步周期
     * @return 同步周期
     */
    public long getSync() {
        long sync = 0;
        try {
            sync = Long.parseLong(propertyMap.get(ImSdkConfig.K_SYNC));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sync;
    }

    /**
     * 同步时间差
     * @return 时间差
     */
    public long getDiff() {
        long diff = 0;
        try {
            diff = Long.parseLong(propertyMap.get(ImSdkConfig.K_DIFF));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /**
     * 发送文件最大尺寸
     * @return int 单位为(B)
     */
    public long getSize() {
        long size = 0;
        try {
            int sizeM = Integer.parseInt(propertyMap.get(ImSdkConfig.K_SIZE));
            size = sizeM * 1024 * 1024;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * https证书id
     * @return id
     */
    public int getKeyStore() {
        int id = 0;
        try {
            id = Integer.parseInt(propertyMap.get(ImSdkConfig.K_STORE));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * https证书密码
     * @return 证书密码
     */
    public String getCert() {
        return propertyMap.get(ImSdkConfig.K_CERT);
    }

    /**
     * 文件服务器地址
     * @return 地址
     */
    public String getFileServer() {
        return propertyMap.get(ImSdkConfig.K_FILE_ADDR);
    }

    /**
     * 文件服务器端口
     * @return 端口
     */
    public String getFilePort() {
        return propertyMap.get(ImSdkConfig.K_FILE_PORT);
    }

    /**
     * 是否开启https连接
     * @return boolean
     */
    public boolean isHttps() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_HTTPS))) {
            return true;
        }
        return false;
    }


    /**
     * 是否需要会话刷新最后一条消息状态
     * @return boolean
     */
    public boolean needChange() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_CHANGE))) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要加密
     * @return boolean
     */
    public boolean needEncrypt() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_ENCRYPT))) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要状态消息
     * @return boolean
     */
    public boolean needState() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_STATE))) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要会话
     * @return boolean
     */
    public boolean needSession() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_SESSION))) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要预下载
     * @return boolean
     */
    public boolean needPreload() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_PRELOAD))) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要ImSdk生成缩略图
     * @return boolean
     */
    public boolean needThu() {
        if (ImSdkConfig.V_TRUE.equalsIgnoreCase(propertyMap.get(ImSdkConfig.K_THU))) {
            return true;
        }
        return false;
    }

    /**
     * 缩略图宽度
     * @return 缩略图宽度
     */
    public int getThuW() {
        int w = 0;
        try {
            w = Integer.parseInt(propertyMap.get(ImSdkConfig.K_THU_W));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return w;
    }

    /**
     * 缩略图高度
     * @return 高度
     */
    public int getThuH() {
        int h = 0;
        try {
            h = Integer.parseInt(propertyMap.get(ImSdkConfig.K_THU_H));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return h;
    }

    /**
     * 高清缩略图宽度
     * @return 高清缩略图宽度
     */
    public int getHdThuW() {
        int w = 0;
        try {
            w = Integer.parseInt(propertyMap.get(ImSdkConfig.K_HD_W));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return w;
    }

    /**
     * 高清缩略图高度
     * @return 高清缩略图高度
     */
    public int getHdThuH() {
        int h = 0;
        try {
            h = Integer.parseInt(propertyMap.get(ImSdkConfig.K_HD_H));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return h;
    }

}
