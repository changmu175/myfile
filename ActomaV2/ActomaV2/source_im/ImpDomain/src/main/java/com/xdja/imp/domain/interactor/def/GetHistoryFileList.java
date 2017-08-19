package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/12 17:40
 * 修改人：xdjaxa
 * 修改时间：2016/12/12 17:40
 * 修改备注：
 */
public interface GetHistoryFileList extends Interactor<Map<HistoryFileCategory,List<TalkMessageBean>>>{

    GetHistoryFileList deliverParams(String talkId);
}
