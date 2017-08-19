package com.xdja.imp.presenter.command;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 21:02
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 21:02
 * 修改备注：
 */
public interface IHistoryFileListAdapterCommand extends Command {

    void clickToDownloadOfOpen(TalkMessageBean bean);

    void updateItem(int groupPosition,long msgId);

    void toRefreshSelectHint();

    void longClickOnItem(int groupPosition,TalkMessageBean bean);

    /**
     * 获取账号
     * @param account
     * @return
     */
    ContactInfo getContactInfo(String account);

    /**
     * 获取群组成员信息
     * @param groupId 群组ID
     * @param account 成员账号
     * @return
     */
    ContactInfo getGroupMemberInfo(String groupId,String account);
}
