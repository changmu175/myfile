package com.xdja.imsdk.http.file;

import android.content.Context;

import com.xdja.imsdk.manager.ImSdkConfigManager;
import com.xdja.imsdk.util.ToolUtils;
import com.xdja.imsdk.volley.VolleyLog;

import java.io.IOException;
import java.util.Properties;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午4:23                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class FastDfsHttpClientConfig {
    // 服务器地址配置文件,位于应用的assets目录下
    public static final String CONF_PATH = "fdfs_http_client.conf";

    private int timeout = 3000;
    private String host;
    private String port;
    private String uploadUrl;
    private String appendUrl;
    private String downloadUrl;
    private String deleteUrl;
    private String infoUrl;
    private String crc32Url;

    public FastDfsHttpClientConfig(Context context) {
        try {
            Properties prop = new Properties();
            prop.load(context.getAssets().open(CONF_PATH));
            //如果有相应配置项，使用配置项，如果没有相应配置项，从配置文件中读取
            host = isEmpty(ImSdkConfigManager.getInstance().getFileServer())?
                    prop.getProperty("http.host") : ImSdkConfigManager.getInstance().getFileServer();

            port = isEmpty(ImSdkConfigManager.getInstance().getFilePort())?
                    prop.getProperty("http.port") : ImSdkConfigManager.getInstance().getFilePort();

            uploadUrl = isEmpty(prop.getProperty("http.upload")) ? normalize("{HOST}:{PORT}/upload") : normalize(prop.getProperty("http.upload"));

            appendUrl = isEmpty(prop.getProperty("http.append")) ? normalize("{HOST}:{PORT}/append/{FID}") : normalize(prop.getProperty("http.append"));

            downloadUrl = isEmpty(prop.getProperty("http.download")) ? normalize("{HOST}:{PORT}/download/{FID}") : normalize(prop.getProperty("http.download"));

            deleteUrl = isEmpty(prop.getProperty("http.delete")) ? normalize("{HOST}:{PORT}/delete/{FID}") : normalize(prop.getProperty("http.delete"));

            infoUrl = isEmpty(prop.getProperty("http.info")) ? normalize("{HOST}:{PORT}/info/{FID}") : normalize(prop.getProperty("http.info"));

            crc32Url = isEmpty(prop.getProperty("http.crc32")) ? normalize("{HOST}:{PORT}/crc32/{FID}") : normalize(prop.getProperty("http.crc32"));

            VolleyLog.d("uploadUrl:" + uploadUrl + ",appendUrl:" + appendUrl + ",downloadUrl:" + downloadUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String normalize(String property) {
        return "http://" + property.replace("{HOST}", host).replace("{PORT}", port);
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getAppendUrl(String fid) {
        return appendUrl.replace("{FID}", fid);
    }

    public String getDownloadUrl(String fid) {
        return downloadUrl.replace("{FID}", fid);
    }

    public String getDeleteUrl(String fid) {
        return deleteUrl.replace("{FID}", fid);
    }

    public String getInfoUrl(String fid) {
        return infoUrl.replace("{FID}", fid);
    }

    public String getCrc32Url(String fid) {
        return crc32Url.replace("{FID}", fid);
    }

    public int getTimeout() {
        return timeout;
    }

    private boolean isEmpty(String str){
        return ToolUtils.isEmpty(str);
    }
}
