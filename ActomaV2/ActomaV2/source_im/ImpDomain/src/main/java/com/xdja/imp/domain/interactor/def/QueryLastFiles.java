package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.LocalFileInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/15 14:31   </br>
 * <p>Package: com.xdja.imp.domain.interactor.def</br>
 * <p>Description:            </br>
 */
public interface QueryLastFiles extends Interactor<Map<String, List<LocalFileInfo>>> {

    /**
     * 查询最近聊天文件
     *
     * @return
     */
    QueryLastFiles queryLastFiles();

}
