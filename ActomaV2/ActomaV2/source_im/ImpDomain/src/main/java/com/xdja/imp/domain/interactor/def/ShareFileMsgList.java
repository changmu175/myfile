package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkListBean;

import java.util.List;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，发送图片的UserCase
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface ShareFileMsgList extends Interactor<List<TalkListBean>> {

    /**
     * 发送消息
     * @param fileInfoList
     * @return
     */
    ShareFileMsgList send(List<TalkListBean> dataSource, List<FileInfo> fileInfoList);
}
