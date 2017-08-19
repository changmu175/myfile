package com.xdja.imsdk.http;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.http.HttpStatus;
import com.xdja.imsdk.constant.internal.FileError;
import com.xdja.imsdk.http.callback.IHttpCallback;
import com.xdja.imsdk.http.config.ImRequestConfig;
import com.xdja.imsdk.http.error.HttpErrorCode;
import com.xdja.imsdk.http.file.FastDfsHttpClientConfig;
import com.xdja.imsdk.http.file.FileEntry;
import com.xdja.imsdk.http.file.callback.IFileDownloadCallback;
import com.xdja.imsdk.http.file.callback.IFileUploadCallback;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.ImSdkConfigManager;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.request.RequestInfo;
import com.xdja.imsdk.volley.request.RequestManager;
import com.xdja.imsdk.volley.toolbox.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  网络请求接口                                  <br>
 * 创建时间：2016/11/27 下午3:35                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class HttpUtils {
    private static HttpUtils instance;

    /**
     * post请求content-type内容
     */
    private static final Pattern UPLOAD_PATTERN = Pattern.compile(".+\"fileid\":\"(.+?)\".+");

    /**
     * 文件服务器fast dfs配置
     */
    private FastDfsHttpClientConfig dfsHttpConfig;

    public static HttpUtils getInstance(){
        synchronized(HttpUtils.class) {
            if(instance == null){
                instance =  Factory.getInstance();
            }
        }
        return instance;
    }

    private static class Factory {
        static HttpUtils getInstance() {
            return new HttpUtils();
        }
    }

    /**
     * 初始化
     * @param context 上下文
     */
    public void init(Context context){
        dfsHttpConfig = new FastDfsHttpClientConfig(context);

        boolean isHttps = ImSdkConfigManager.getInstance().isHttps();
        int keyStoreId = ImSdkConfigManager.getInstance().getKeyStore();
        String keyStorePwd = ImSdkConfigManager.getInstance().getCert();
        RequestManager.getInstance().init(context.getApplicationContext(), isHttps, keyStoreId, keyStorePwd);
    }

    /**
     * 发送文本消息                                              <br/>
     * 应用场景：文本消息，状态消息
     * @param object 消息实体JSON对象
     * @param callback 消息回调接口
     */
    public void sendPostRequest(final JSONObject object, ImRequestConfig config,
                                final IHttpCallback callback){

        if (callback == null) {
            return;
        }
        if (object == null){
            Logger.getLogger().e("Request url is null!!");
            callback.onFailed(HttpErrorCode.PARAM_ERROR, new JSONObject());
            return;
        }

        //获取IM服务器url
        String url = ImSdkConfigManager.getInstance().getImServer();
        if (TextUtils.isEmpty(url)) {
            Logger.getLogger().e("Request url is null!!");
            callback.onFailed(HttpErrorCode.PARAM_ERROR, object);
            return;
        }

        //请求参数配置
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.tag = config.getMsgId();
        requestInfo.needRetry = config.isNeedRetry();
        if (config.getOptions().size() > 0) {
            requestInfo.headers = config.getOptions();
        }

        RequestManager.getInstance().post(requestInfo, object, new HttpCallback() {

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String result) {
                try {
                    callback.onSuccess(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailed(HttpErrorCode.UNKNOWN_ERROR,object);
                }
            }

            @Override
            public void onError(VolleyError e) {
                Logger.getLogger().e("Request error:" + e);

                if (e == null){
                    callback.onFailed(HttpErrorCode.UNKNOWN_ERROR, object);
                } else if (e.getStatusCode() == HttpStatus.SC_BAD_REQUEST ||
                        e.getStatusCode() == HttpStatus.SC_UNAUTHORIZED ){
                    callback.onFailed(HttpErrorCode.TICKET_EXPIRE, object);
                } else {
                    callback.onFailed(e.getStatusCode(), object);
                }
            }

            @Override
            public void onNetChanged(int code, String message) {
                callback.onNetChanged(code, object);
            }

            @Override
            public void onCanceled() {
            }

            @Override
            public void onLoading(long count, long current, int percent) {
            }
        });
    }


    /**
     * 开始上传文件，请求文件fid                                          <br/>
     * 上传文件至服务器，分为如下步骤：                                    <br/>
     *  1）向服务器请求fid                                               <br/>
     *  2）上传文件至服务器                                              <br/>
     *  3）发送文件消息                                                  <br/>
     * @param entry 文件实体
     * @param callback 回调接口
     */
    public void uploadStart(final FileEntry entry, final IFileUploadCallback callback){

        //文件信息内容为空处理
        if (!checkImFileAvailable(entry)){
            Logger.getLogger().e("File is not available!!");
            callback.uploadFileError(FileError.PARAM_ERR, entry);
            return ;
        }

        Logger.getLogger().d("start upload file:" + entry.getPath() +
                ", requestTag:" + entry.getId());

        //获取上传fast dfs服务器路径
        String url = dfsHttpConfig.getUploadUrl();
        if (TextUtils.isEmpty(url)){
            Logger.getLogger().e("File url is null!!");
            callback.uploadFileError(FileError.PARAM_ERR, entry);
            return;
        }

        //filename为空处理，用于在服务器上生成文件存储路径
        String fileName = entry.getName();
        if (TextUtils.isEmpty(fileName)) {
            //从文件路径中获取
            fileName = entry.getPath().substring(entry.getPath().lastIndexOf(File.separator));
            entry.setName(fileName);
        }

        String requestTag = String.valueOf(entry.getId());

        //发送网络请求
        RequestManager.getInstance().sendFidRequest(url, fileName, requestTag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String result) {
                //解析fid
                String fid = parseGroupId(result);
                if (TextUtils.isEmpty(fid)){
                    Logger.getLogger().e("request fid failed");
                    callback.uploadFileError(FileError.FID_ERR, entry);
                    return;
                }

                entry.setFid(fid);
                uploadFile(entry, callback);
            }

            @Override
            public void onError(VolleyError e) {
                Logger.getLogger().e("Request error:" + e.getMessage());
                callback.uploadFileError(FileError.FID_ERR, entry);
            }

            @Override
            public void onNetChanged(int code, String message) {
                callback.uploadFileNetChanged(code, entry);
            }

            @Override
            public void onCanceled() {
                callback.uploadFileError(FileError.FID_ERR, entry);
            }

            @Override
            public void onLoading(long count, long current, int percent) {
            }
        });
    }

    /**
     * 上传文件内容至fast dfs服务器
     * @param entry 上传的文件
     * @param callback 回调
     */
    private void uploadFile(final FileEntry entry,
                           final IFileUploadCallback callback){

        //获取上传fast dfs服务器路径
        String url = dfsHttpConfig.getAppendUrl(entry.getFid());
        if (TextUtils.isEmpty(url)){
            Logger.getLogger().e("Upload file url is null, so return!");
            callback.uploadFileError(FileError.PARAM_ERR, entry);
            return;
        }

        //生成请求信息
        String requestTag = String.valueOf(entry.getId());

        //上传文件
        RequestManager.getInstance().uploadFileStart(url, entry.getEncryptPath(),
                requestTag, new HttpCallback() {

                    @Override
                    public void onStart() {
                        callback.uploadFileStart(entry);
                    }

                    @Override
                    public void onFinish() {
                        callback.uploadFileFinish(entry);
                    }

                    @Override
                    public void onResult(String result) {
                    }

                    @Override
                    public void onError(VolleyError e) {
                        Logger.getLogger().e("Upload file error:" + e.getMessage());
                        callback.uploadFileError(FileError.UPLOAD_ERR, entry);
                    }

                    @Override
                    public void onNetChanged(int code, String message) {
                        callback.uploadFileNetChanged(code, entry);
                    }

                    @Override
                    public void onCanceled() {
                        callback.uploadFileError(FileError.UPLOAD_ERR, entry);
                    }

                    @Override
                    public void onLoading(long count, long translateSize, int percent) {
                        if (translateSize > 0){
                            entry.settSize(translateSize);
                            callback.uploadFileProgressUpdate(percent, entry);
                        }
                    }
                });
    }

    /**
     * 恢复上传文件
     */
    public void uploadFileResume(FileEntry entry){
        if (entry == null){
            Logger.getLogger().e("Upload file is null!!");
            return;
        }

        try {
            //防止转换失败
            String msgId = String.valueOf(entry.getId());
            if (TextUtils.isEmpty(msgId)){
                return;
            }
            RequestManager.getInstance().uploadFileResume(msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停上传文件
     */
    public void uploadFilePause(FileEntry entry){
        if (entry == null){
            Logger.getLogger().e("Upload file is null!!");
            return;
        }

        try {
            //防止转换失败
            String msgId = String.valueOf(entry.getId());
            if (TextUtils.isEmpty(msgId)){
                return;
            }
            RequestManager.getInstance().uploadFilePause(msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止上传文件
     */
    public void uploadFileStop(FileEntry entry){
        if (entry == null){
            Logger.getLogger().e("Upload file is null!!");
            return;
        }

        try {
            //防止转换失败
            String msgId = String.valueOf(entry.getId());
            if (TextUtils.isEmpty(msgId)){
                return;
            }
            RequestManager.getInstance().uploadFileStop(msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件（该接口默认支持断点下载）
     * @param entry 文件实体类
     * @param callback 回调接口
     */
    public void downloadStart(final FileEntry entry, final IFileDownloadCallback callback){
        download(entry, false, callback);
    }

    /**
     * 下载文件（该接口默认支持断点下载）
     * @param entry 文件实体类
     * @param isSupportRange 是否支持断点
     * @param callback 回调接口
     */
    private void download(final FileEntry entry,
                         boolean isSupportRange,
                         final IFileDownloadCallback callback){

        if (!checkDownFileParamsAvailable(entry)) {
            callback.downloadFileError(FileError.PARAM_ERR, entry);
            return;
        }

        //文件下载URL
        String url = dfsHttpConfig.getDownloadUrl(entry.getFid());
        if (TextUtils.isEmpty(url)){
            Logger.getLogger().e("ERROR:File url is null.");
            callback.downloadFileError(FileError.PARAM_ERR, entry);
            return;
        }

        //文件保存路径
        String fileSavePath = entry.getEncryptPath();
        if (TextUtils.isEmpty(fileSavePath)) {
            Logger.getLogger().e("ERROR: File save path is null.");
            callback.downloadFileError(FileError.PARAM_ERR, entry);
            return;
        }

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.isSupportRange = isSupportRange;
        requestInfo.tag = String.valueOf(entry.getId());

        RequestManager.getInstance().downloadFile(requestInfo, fileSavePath, entry.getEncryptSize(), new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String result) {
                //防止发送过来的文件大小为0
                if (entry.getSize() < entry.gettSize()){
                    entry.setSize(entry.gettSize());
                }
                callback.downloadFileFinish(entry);
            }

            @Override
            public void onError(VolleyError e) {
                Logger.getLogger().e("onError:" + e.getMessage());
                callback.downloadFileError(FileError.DOWNLOAD_ERR, entry);
            }

            @Override
            public void onNetChanged(int code, String message) {
                callback.downloadNetChanged(code, entry);
            }

            @Override
            public void onCanceled() {
                callback.downloadFilePause(entry);
            }

            @Override
            public void onLoading(long count, long current, int percent) {
                entry.settSize(current);
                callback.downloadFileProgressUpdate(percent, entry);
            }
        });
    }

    /**
     * 恢复下载
     * @param entry 恢复下载的文件
     */
    public void downloadFileResume(FileEntry entry, IFileDownloadCallback callback){
        //暂停下载时，释放资源，不然其他任务无法执行。在重新进行下载时，已经支持的断点续传功能
        download(entry, true, callback);
    }

    /**
     * 暂停下载
     * @param entry 暂停下载的文件
     */
    public void downloadFilePause(FileEntry entry){
        //参数校验
        if (!checkDownFileParamsAvailable(entry)){
            return ;
        }
        String url = dfsHttpConfig.getDownloadUrl(entry.getFid());
        RequestManager.getInstance().downloadFilePause(url, entry.getId()+ "");
    }

    public void downloadFileStop(FileEntry entry){
        if (!checkDownFileParamsAvailable(entry)){
            return ;
        }
        String url = dfsHttpConfig.getDownloadUrl(entry.getFid());
        RequestManager.getInstance().downloadFileStop(url, entry.getEncryptPath());
    }


    /**
     * 取消指定tag标识的请求
     * @param msgIds 取消网络对应的消息id集合
     */
    public void cancelRequest(List<Long> msgIds){
        for (Long msgId : msgIds) {
            RequestManager.getInstance().cancelRequest(msgId + "");
        }
    }

    /**
     * 取消指定tag标识的请求
     * @param msgId 取消网络对应的消息id
     */
    public void cancelRequest(Long msgId){
        RequestManager.getInstance().cancelRequest(msgId + "");
    }

    /**
     * 取消所有的请求
     */
    public void cancelAll(){
        RequestManager.getInstance().cancelAll();
    }


    /**
     * 检查文件参数是否正确，包括fid,url, storePath等信息。
     * @param entry 文件信息
     * @return 结果
     */
    private boolean checkDownFileParamsAvailable(FileEntry entry){
        if (entry == null){
            Logger.getLogger().e("ERROR: File entry is null.");
            return false;
        }

        if (TextUtils.isEmpty(entry.getFid())){
            Logger.getLogger().e("ERROR: File fid is null.");
            return false;
        }
        return true;
    }

    /**
     * 校验文件实体类有效性
     * @param entry 文件实体类
     * @return 结果
     */
    private boolean checkImFileAvailable(FileEntry entry){
        if (entry == null || TextUtils.isEmpty(entry.getEncryptPath())) {
            Logger.getLogger().e("File params error!");
            return false;
        }

        File file = new File(entry.getEncryptPath());
        if (!file.exists()) {
            Logger.getLogger().e("File not exist!");
            return false;
        }
        return true;
    }

    /**
     * 获取fid
     * @param response 响应
     * @return fid
     */
    private String parseGroupId(String response){
        if (TextUtils.isEmpty(response)){
            return null;
        }
        String res = response.replace("\\/", "/").trim();
        Matcher matcher = UPLOAD_PATTERN.matcher(res);
        if (!matcher.matches()) {
            Logger.getLogger().e("Server return data error !!!");
            return null;
        }
        if(matcher.group(1) == null || "".equals(matcher.group(1))){
            return null;
        }
        return matcher.group(1);
    }

}
