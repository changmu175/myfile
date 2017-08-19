package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.LocalPictureInfo;
import java.util.List;


/**
 * <p>Summary: 查询本机所有图片集合的接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:leilianglinag</p>
 * <p>Date:2016/07/17</p>
 * <p>Time:17:11</p>
 */
public interface QueryLocalPictures extends Interactor<List<LocalPictureInfo>>{

    /**
     * 查询本机图库中所有图片
     * @return 本机所有图片列表
     */
    QueryLocalPictures queryLocalPictures();
}
