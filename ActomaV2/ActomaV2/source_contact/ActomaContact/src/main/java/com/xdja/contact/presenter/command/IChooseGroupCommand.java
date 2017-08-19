package com.xdja.contact.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，选择更多联系人，选择群组界面中的事件响应接口.
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface IChooseGroupCommand extends Command {
    /**
     * 进入群聊天界面
     */
    void startChatActivity(int position);

    /**
     * 开始搜索
     * @param keyWord
     */
    void startSearch(String keyWord);

     /**
      * 结束搜索
      */
    void endSearch();

}
