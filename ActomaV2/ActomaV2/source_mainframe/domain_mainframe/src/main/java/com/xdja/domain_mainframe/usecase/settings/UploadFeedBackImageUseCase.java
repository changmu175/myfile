package com.xdja.domain_mainframe.usecase.settings;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.BitmapUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ALH on 2016/8/12.
 */
public class UploadFeedBackImageUseCase extends Ext2UseCase<Context, List<String>, String> {
    public static final String STOP_SUBMIT_TAG = "stop_submit";
    /**
     * 是否停止提交请求
     */
    public boolean isStopSubmit = false;

    private static final int WIDTH = 1440;
    private static final int HEIGHT = 900;
    private static final int MINI_MAP_MAX_SIZE = 200*1024;

    @Inject
    public UploadFeedBackImageUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    public static class UploadFeedBackImageResponseBean {
        private String fileid;

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }

        @Override
        public String toString() {
            return "UploadFeedBackImageResponseBean{" +
                    "fileid='" + fileid + '\'' +
                    '}';
        }
    }

    @Override
    public Observable<String> buildUseCaseObservable() {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
//                    "http://dfs.test.safecenter.com/upload"
                    if (isStopSubmit) {
                        subscriber.onNext(STOP_SUBMIT_TAG);
                        return;
                    }
                    String uploadUrl = PreferencesServer.getWrapper(p).gPrefStringValue("fastDfs");
                    if (p1.isEmpty()) {
                        subscriber.onNext("");
                        return;
                    }
                    for (int i = 0; i < p1.size(); i++) {
                        String result = "";
                        final String imgPath = p1.get(i);

                        //获取压缩后图片存放路径
                        if (TextUtils.isEmpty(imgPath)) return;

                        String fileName = imgPath.substring(imgPath.lastIndexOf("/"));
                        if (!TextUtils.isEmpty(fileName)) {
                            fileName = fileName.replaceAll("/", "");
                        }
                        String newImageDirPath = p.getExternalCacheDir() + "/test";
                        LogUtil.getUtils().i("newImagePath" + newImageDirPath);

                        //图片文件路径
                        String newImageFilePath = newImageDirPath + "/" + fileName;
                        if (!new File(newImageFilePath).exists()) {
                            //压缩图片
                            byte[] datas = BitmapUtil.getMiniMap(imgPath, WIDTH, HEIGHT, MINI_MAP_MAX_SIZE);
                            BitmapUtil.saveByteToLocal(newImageDirPath, fileName, datas);
                        }

                        if (!TextUtils.isEmpty(newImageFilePath)) {
                            File file = new File(newImageFilePath);
                            /*RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                                    .addFormDataPart("image", file.getName(), RequestBody.create(null, file)).addPart
                                            (Headers.of("Content-Disposition", "form-data; name=\"image\";" +
                                                    "filename=\"" + file.getName() + "\""), RequestBody.create
                                                    (MediaType.parse("application/octet-stream"), file)).build();*/
                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("image", file.getName(), RequestBody.create(null, file)).addPart
                                            (Headers.of("Content-Disposition", "form-data; name=\"image\";" +
                                                    "filename=\"" + file.getName() + "\""), RequestBody.create
                                                    (MediaType.parse("application/octet-stream"), file)).build();
                            Request request = new Request.Builder().url(uploadUrl).post(requestBody).build();
                            Response response = OkHttpsClient.getInstance(p).getOkHttpClient().newCall(request).execute();
                            if (response.isSuccessful()) {
                                UploadFeedBackImageResponseBean bean = new Gson().fromJson(response.body().string(),
                                        UploadFeedBackImageResponseBean.class);
                                if (bean != null) {
                                    result = bean.getFileid();
                                }
                                subscriber.onNext(result);
                            } else {
                                subscriber.onError(new Exception());
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
