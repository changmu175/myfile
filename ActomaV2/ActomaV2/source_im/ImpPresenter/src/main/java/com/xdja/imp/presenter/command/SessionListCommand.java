package com.xdja.imp.presenter.command;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.frame.presenter.mvp.Command;

import java.util.List;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，分享界面会话选择界面功能接口
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface SessionListCommand extends Command {
    /**
     * 列表项被点击
     * @param position 被点击的位置
     */
    void onListItemClick(int position);

    /**
     * 创建新会话
     */
    void createNewSession();

    /**
     * 更多联系人
     */
    void moreContact();

    /**
     * 根据群id获取群信息
     * @param groupId
     * @return
     */
    ContactInfo getGroupInfo(String groupId);

    /**
     * 获取账号
     * @param account
     * @return
     */
    ContactInfo getContactInfo(String account);

    /**
     * 开始搜索
     * @param keyWord
     * @return
     */
    List<LocalCacheDto> startSearch(String keyWord);

    /**
     * 准备搜索
     * @param keyWord
     */
    void preSearch(String keyWord);

    /**
     * 结束搜索
     */
    void endSearch();

}

