package com.xdja.imp.domain.model;

import java.util.List;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/13 20:59  </br>
 * <p>Package: com.xdja.imp.domain.model</br>
 * <p>Description:本地发文件分类  </br>
 */
public class LocalFileCategory {

    private String category;

    private List<LocalFileInfo> fileInfoList;

    public LocalFileCategory() {
    }

    public LocalFileCategory(String category, List<LocalFileInfo> fileInfoList) {
        this.category = category;
        this.fileInfoList = fileInfoList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<LocalFileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List<LocalFileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    @Override
    public String toString() {
        return "LocalFileCategory{" +
                "category='" + category + '\'' +
                ", fileInfoList=" + fileInfoList +
                '}';
    }
}
