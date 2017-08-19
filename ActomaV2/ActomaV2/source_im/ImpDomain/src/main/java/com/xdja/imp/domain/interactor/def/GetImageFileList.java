package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;

import java.util.List;

/**
 * <p>Summary: 获取图片文件信息列表</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:leill</p>
 * <p>Date:2016/08/05</p>
 * <p>Time:09:55</p>
 */
public interface GetImageFileList extends Interactor<List<FileInfo>>{

    /**
     * 根据本地图片列表转化为图片信息列表，用来发送图片消息
     * @param pictureList
     * @return
     */
    GetImageFileList getImageFileList(List<LocalPictureInfo> pictureList);
}
