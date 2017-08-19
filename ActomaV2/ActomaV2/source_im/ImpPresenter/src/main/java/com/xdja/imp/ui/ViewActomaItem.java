package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.util.BitmapUtils;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.refctor.ui</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:16:23</p>
 */
public class ViewActomaItem extends ViewChatItem {

    private TextView content;

    private TextView userName;

    @Override
    protected int getLayoutRes() {
        return R.layout.chatlist_item_actom;
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            content = (TextView) view.findViewById(R.id.content);
            userName = (TextView)view.findViewById(R.id.userName);
            timeTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkListBean dataSource) {
        super.bindDataSource(position, dataSource);

        content.setText(getContent());

        userName.setText(BitmapUtils.formatAnTongSpanContent(getStringRes(R.string.antong_team_name),
                getActivity(), 1.0f, BitmapUtils.AN_TONG_DETAIL_PLUS));


        ContactInfo info = getCommand().getContactInfo(dataSource.getTalkerAccount());
        setCircleImageUrl(info.getAvatarUrl(), getDefaultImageId());
    }

    @Override
    protected int getDefaultImageId() {
        return R.drawable.chatlist_actom_avatar_40;
    }

    private CharSequence getContent() {
        if (dataSource.getContent() != null) {
            String tmp = dataSource.getContent().toString();
            if (tmp.contains("$")) {
                //fix 2419 by licong, review zya, 2016/8/5
                return BitmapUtils.formatAnTongSpanContent(tmp.substring(tmp.lastIndexOf("$") + 1),
                        getActivity(), 0.75f, BitmapUtils.AN_TONG_DETAIL_PLUS);
            }
        }
        return "";
    }
}
