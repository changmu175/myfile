package com.xdja.presenter_mainframe.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.xdja.comm.data.AppInfoBean;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext2Interactor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Lazy;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.domain</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/26</p>
 * <p>Time:16:48</p>
 */
public class DownloadImp implements IDownload {

    /**
     * http响应有错
     */
    private static int HTTP_ERROR = 0;
    /**
     * 文件父路径有错
     */
    private static int PARENTFILE_ERROR = 1;
    /**
     * 文件有错
     */
    private static int FILE_ERROR = 2;
    /**
     * 下载完成
     */
    private static int DOWNLOAD_COMPLETE = 3;
    /**
     * 下载未完成(暂停操作)
     */
    private static int DOWNLOAD_PAUSE = 4;
    /**
     * 下载出现错误
     */
    private static int DOWNLOAD_ERROR = 5;


    /**
     * 应用详情
     */
    private AppInfoBean infoBean;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 下载代理
     */
    private Context context;


    @Inject
    @InteractorSpe(value = DomainConfig.DOWNLOAD_FROM_APPSTORE)
    Lazy<Ext2Interactor<String,Long,Object>> downloadUseCase;
    /**
     * 是否暂停下载
     */
    private boolean isStop = false;
    /**
     * 回调句柄
     */
    private DownloadCallBack callBack;

    private FileSaveTask task;

    static Executor executorService;

    static {
        executorService = Executors.newCachedThreadPool();
    }

    public DownloadImp(AppInfoBean infoBean, String fileName, Context context) {
        this.infoBean = infoBean;
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    public void start(@Nullable final DownloadCallBack callBack) {
        this.callBack = callBack;
        //下载开始
        if (this.callBack != null) {
            this.callBack.onStart();
        }
       //[Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-04. Fix bug #2375. Review By Wangchao1.
        Request request = new Request.Builder().url(infoBean.getDownloadUrl())
                .header("Range", "bytes=" + Long.valueOf(infoBean.getDownSize()) + "-").get().build();
        Observable.just(request).map(new Func1<Request, Response>() {
            @SuppressWarnings("ReturnOfNull")
            @Override
            public Response call(Request request) {
                try {
                    return OkHttpsClient.getInstance(context).getOkHttpClient().newCall(request).execute();
                } catch (Exception e) {
                    com.xdja.dependence.uitls.LogUtil.getUtils().e(e.getMessage());
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e.getMessage());
                        if (callBack != null) {
                            callBack.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Response response) {
                        File file = new File(DownloadManager.FILEPATH + fileName + ".apk");
                        new FileSaveTask(response, file, Long.valueOf(infoBean.getDownSize()))
                                .executeOnExecutor(executorService);

                    }
                });
        //[End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-04. Fix bug #2375. Review By Wangchao1.

//        downloadUseCase.get().fill(infoBean.getDownloadUrl(), Long.valueOf(infoBean.getDownSize())).execute(new Subscriber<Response>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LogUtil.getUtils().e(e.getMessage());
//                if (callBack != null) {
//                    callBack.onError(e);
//                }
//            }
//
//            @Override
//            public void onNext(Response response) {
//                File file = new File(DownloadManager.FILEPATH + fileName + ".apk");
//                new FileSaveTask(response, file, Long.valueOf(infoBean.getDownSize()))
//                        .executeOnExecutor(executorService);
//            }
//        });
    }

    @Override
    public void stop() {
        if (downloadUseCase != null) {
            downloadUseCase.get().unSubscribe();
        }
        isStop = true;
    }

    /**
     * 强行停止
     */
    public void release() {
        if (downloadUseCase != null) {
            downloadUseCase.get().unSubscribe();
        }
        if (task != null) {
            task.cancel(true);
        }
    }

    class FileSaveTask extends AsyncTask<Void, Long, Integer> {

        /**
         * 存储地址
         */
        private File outputFile;

        /**
         * 服务器响应
         */
        private Response response;

        /**
         * 已下载大小
         */
        private long size;

        public FileSaveTask(Response response, File outputFile, long size) {
            this.outputFile = outputFile;
            this.response = response;
            this.size = size;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            InputStream in = null;
            RandomAccessFile raf = null;
            try {
                if (response == null) {
                    return HTTP_ERROR;
                }
                if (!response.isSuccessful()) {
                    return HTTP_ERROR;
                }
                if (!outputFile.getParentFile().exists()) {
                    boolean result = outputFile.getParentFile().mkdirs();
                    if (!result)
                        return PARENTFILE_ERROR;
                }
                if (!outputFile.exists()) {
                    boolean result = outputFile.createNewFile();
                    if (!result)
                        return FILE_ERROR;
                }
                in = response.body().byteStream();
                byte[] buffer = new byte[1024 * 10];
                raf = new RandomAccessFile(outputFile, "rw");
                long downloaded = size;
                raf.seek(this.size);
                //下载字节数
                int count = 0;
                //应用开始时间
                long beginTime = System.currentTimeMillis();
                //读写数据
                while ((count = in.read(buffer)) >= 0 && isStop == false) {
                    raf.write(buffer, 0, count);
                    downloaded += count;
                    if (System.currentTimeMillis() - beginTime > 1000) {
                        publishProgress(downloaded);
                        beginTime = System.currentTimeMillis();
                    }
                }
                //判断本地下载文件的大小是否跟服务器返回的文件大小一致 若一致则证明下载完成
                File file = new File(DownloadManager.FILEPATH + fileName + ".apk");
                if (file.length() == Long.parseLong(infoBean.getAppSize())) {
                    return DOWNLOAD_COMPLETE;
                } else {
                    return DOWNLOAD_PAUSE;
                }
            } catch (IOException e) {
                LogUtil.getUtils().e(e.getMessage());
                return DOWNLOAD_ERROR;
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (raf != null)
                        raf.close();
                } catch (IOException e) {
                    LogUtil.getUtils().e(e.getMessage());
                    return DOWNLOAD_ERROR;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            callBack.onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == DOWNLOAD_COMPLETE) {
                callBack.onComplete();
            } else if (result == DOWNLOAD_PAUSE) {
                callBack.onStop();
            } else if (result == DOWNLOAD_ERROR || result == HTTP_ERROR) {
                callBack.onError(null);
            }
        }
    }
}
