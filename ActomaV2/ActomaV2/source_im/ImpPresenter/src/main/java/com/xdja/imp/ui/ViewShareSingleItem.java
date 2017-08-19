package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkListBean;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，分析界面会话选择列表单聊条目视图
 * 创建人：ycm
 * 创建时间：2016/11/1 20:15
 * 修改人：ycm
 * 修改时间：2016/11/1 20:15
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ViewShareSingleItem extends ViewChatItem {
    private TextView userName;

    @Override
    protected int getLayoutRes() {
        return R.layout.share_item_single;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null) {
            userName = (TextView) view.findViewById(R.id.userName);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkListBean dataSource) {
        super.bindDataSource(position, dataSource);
        String username = showChatName();
        userName.setText(username);
    }

    private String showChatName() {
        if (dataSource != null && dataSource.getTalkerAccount() != null) {
            String name = getCommand().getContactInfo(dataSource.getTalkerAccount()).getName();
            if (TextUtils.isEmpty(name)) {
                return dataSource.getTalkerAccount();
            }
            return name;
        }
        return "";
    }
}
