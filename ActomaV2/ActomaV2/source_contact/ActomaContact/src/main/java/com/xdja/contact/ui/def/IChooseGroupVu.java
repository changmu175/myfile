package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.adapter.ChooseGroupAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupAdapter;
import com.xdja.contact.presenter.command.IChooseGroupCommand;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，选择更多联系人，选择群的适配接口
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface IChooseGroupVu extends FragmentVu<IChooseGroupCommand> {
    /**
     * 设置群组列表适配器
     *
     * @param adapter 群组列表适配器
     */
    void setAdapter(ChooseGroupAdapter adapter);

    void setChooseGroupsAdapter(ChooseGroupAdapter chooseGroupAdapter);

    void setSearchResultAdapter(SearchGroupAdapter searchGroupAdapter);
}
