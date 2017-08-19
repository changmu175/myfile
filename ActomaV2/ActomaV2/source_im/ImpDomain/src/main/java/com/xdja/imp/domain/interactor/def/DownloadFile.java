package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/3/23.
 * 功能描述
 */
public interface DownloadFile extends Interactor<Integer>  {

    DownloadFile downLoad(List<FileInfo> fileInfos);
}
