package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.LocalFileInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>Author: leiliangliang    </br>
 * <p>Date: 2016/11/30 10:37   </br>
 * <p>Package: com.xdja.imp.domain.interactor.def</br>
 * <p>Description:IM查询本机的文件信息               </br>
 * <p>
 */
public interface QueryLocalFiles extends Interactor<Map<String, List<LocalFileInfo>>>{

    /**
     * 查询本地媒体库文件
     * @param fileType 文件类型
     * @return
     */
    QueryLocalFiles queryLocalFiles(int fileType);
}
