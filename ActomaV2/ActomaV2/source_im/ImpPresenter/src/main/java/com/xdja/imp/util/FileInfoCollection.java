package com.xdja.imp.util;

import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guorong on 2016/7/4.
 * 该类用于会话中图片发送与接收相关的功能涉及到的图片的选择记录
 */
public class FileInfoCollection {

    /**
     * 本地已选图片集合
     */
    private static Map<String, LocalPictureInfo> localPicInfo;

    /**
     * 本地已选文件集合
     */
    private static final Map<String, LocalFileInfo> mLocalFileInfo = new HashMap<>();

    private long mSelectedFileSize = 0;

    private static class SingletonInstance {
        private static final FileInfoCollection mInstance = new FileInfoCollection();
    }

    private FileInfoCollection() {
    }

    public static FileInfoCollection getInstance() {
        return SingletonInstance.mInstance;
    }


    public static synchronized Map<String, LocalPictureInfo> getLocalPicInfo() {
        if (localPicInfo == null) {
            localPicInfo = new LinkedHashMap<>();
        }
        return localPicInfo;
    }

    /**
     * 从缓存中获取文件实体类
     *
     * @param key
     * @return
     */
    private LocalFileInfo getFileFromCache(String key) {
        return mLocalFileInfo.get(key);
    }

    /**
     * 文件实体加入到已选缓存中
     *
     * @param key
     * @param fileInfo
     */
    public void putFileToSelectedCache(String key, LocalFileInfo fileInfo) {
        if (fileInfo != null) {
            mSelectedFileSize += fileInfo.getFileSize();
            mLocalFileInfo.put(key, fileInfo);
        }
    }

    /**
     * 从缓存中移除文件
     *
     * @param key
     */
    public void removeToSelectedCache(String key) {
        LocalFileInfo fileInfo = getFileFromCache(key);
        if (fileInfo != null) {
            mSelectedFileSize -= fileInfo.getFileSize();
        }
        mLocalFileInfo.remove(key);
    }

    /**
     * 获取所有已选文件
     *
     * @return
     */
    public List<LocalFileInfo> getAllSelectFiles() {

        List<LocalFileInfo> fileInfoList = new ArrayList<>();
        for (String key : mLocalFileInfo.keySet()) {
            fileInfoList.add(mLocalFileInfo.get(key));
        }
        return fileInfoList;
    }

    /**
     * 获取已选文件的个数
     *
     * @return
     */
    public int getSelectedFileCount() {
        return mLocalFileInfo.size();
    }

    /**
     * 获取已选择文件大小
     *
     * @return
     */
    public long getSelectedFileSize() {
        return mSelectedFileSize;
    }

    /**
     * 清除缓存等相关数据
     */
    public void clearCache() {
        mSelectedFileSize = 0;
        mLocalFileInfo.clear();
    }

}
