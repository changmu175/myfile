package com.xdja.presenter_mainframe.util;

/**
 * <p>Summary:文件下载接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.domain.comm</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/25</p>
 * <p>Time:16:41</p>
 */
public interface IDownload {
    /**
     * 为业务层下载提供回调
     */
    interface DownloadCallBack {
        /**
         * 下载开始
         */
        void onStart();

        /**
         * 下载进度更新
         *
         * @param size  已下载大小
         */
        void onProgress(long size);

        /**
         * 下载停止
         */
        void onStop();

        /**
         * 下载完成
         */
        void onComplete();

        /**
         * 下载过程有错
         *
         * @param throwable 错误信息
         */
        void onError(Throwable throwable);
    }

    /**
     * 下载开始
     */
    void start(DownloadCallBack callBack);

    /**
     * 停止
     */
    void stop();
}
