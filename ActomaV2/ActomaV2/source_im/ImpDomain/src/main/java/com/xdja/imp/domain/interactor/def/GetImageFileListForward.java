package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.FileInfo;

import java.util.List;


/**
 * 项目名称：ActomaV2
 * 类描述：分享图片时根据本地图片列表转化为图片信息列表，用来发送图片消息
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161130.
 */
public interface GetImageFileListForward extends Interactor<List<FileInfo>>{

    /**
     * 根据本地图片列表转化为图片信息列表，用来发送图片消息
     * @param pictureList
     * @return
     */
    GetImageFileListForward getImageFileList(List<FileInfo> pictureList, boolean isOriginal);
}
