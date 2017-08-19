package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.mvp.view.AdapterSuperView;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.ChatListAdapterCommand;
import com.xdja.imp.util.DateUtils;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.refctor.ui</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:15:28</p>
 */
public class ViewChatItem extends AdapterSuperView<ChatListAdapterCommand, TalkListBean>
        implements AdapterVu<ChatListAdapterCommand, TalkListBean> {
    /**
     * 头像
     */
    private CircleImageView circleImageView;

    /**
     * 未读消息条数
     */
    private TextView notReadCountTextView;

    /**
     * 消息提醒
     */
    private TextView notReadTextView;

    /**
     * 时间
     */
    TextView timeTextView;

    /**
     * 新消息提醒
     */
    private ImageView notifyImageView;

    /**
     * 会话列表项布局
     */
    private LinearLayout layout;

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            circleImageView = (CircleImageView)view.findViewById(R.id.header);
            notReadCountTextView = (TextView)view.findViewById(R.id.notReadCount);
            notReadTextView = (TextView)view.findViewById(R.id.notRead);
            timeTextView = (TextView)view.findViewById(R.id.time);
            notifyImageView = (ImageView)view.findViewById(R.id.notify);
            layout = (LinearLayout)view.findViewById(R.id.talk_list_item_layout);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkListBean dataSource) {
        super.bindDataSource(position, dataSource);

        if (dataSource == null) {
            return;
        }

        setNotifyImage(false);
        setUnreadCount();

        if (dataSource.isShowOnTop()) {
            setLayoutBack(R.drawable.selector_talk_list_item_top);
        } else {
            setLayoutBack(R.drawable.selector_talk_list_item);
        }
        ContactInfo info = getCommand().getContactInfo(dataSource.getTalkerAccount());
        setTime(getShowTime());
        setCircleImageUrl(info.getAvatarUrl(), getDefaultImageId());
    }

    private void setLayoutBack(int drawable) {
        if (layout != null) {
            layout.setBackgroundResource(drawable);
        }
    }

    private void setTime(CharSequence showTime) {
        if (timeTextView != null) {
            timeTextView.setText(showTime);
        }
    }

    private void setNotifyImage(boolean isShow) {
        if (notifyImageView != null) {
            if (isShow) {
                notifyImageView.setVisibility(View.VISIBLE);
            } else {
                notifyImageView.setVisibility(View.GONE);
            }
        }
    }

    private void setUnreadCount() {
        int notReadCount = dataSource.getNotReadCount();
        if (notReadCount > 0 && dataSource.isNewMessageIsNotify()) {
            setNotReadShow(false);
            setNotReadCountShow(true);
            if (notReadCount > 99) {
                setNotReadCount(Html.fromHtml("<b>...</b>"));
            } else {
                setNotReadCount(notReadCount + "");
            }
        } else if (!dataSource.isNewMessageIsNotify() && notReadCount > 0) {
            //显示无数字统计红点
            setNotReadShow(true);
            setNotReadCountShow(false);
        } else {
            setNotReadShow(false);
            setNotReadCountShow(false);
        }
    }

    private void setNotReadShow(boolean isShow) {
        if (notReadTextView != null) {
            if (isShow) {
                notReadTextView.setVisibility(View.VISIBLE);
            } else {
                notReadTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setNotReadCount(CharSequence countText) {
        if (notReadCountTextView != null) {
            notReadCountTextView.setText(countText);
        }
    }

    void setCircleImageUrl(String url, int defaultImageId) {
        //TODO: gbc
        circleImageView.loadImage(url, true, defaultImageId);
        /*HeadImgParamsBean imgBean = HeadImgParamsBean.getParams(url);
        circleImageView.loadImage(
                imgBean.getHost(), true, imgBean.getFileId(),
                imgBean.getSize(), defaultImageId);*/
    }

    private void setNotReadCountShow(boolean isShow) {
        if (notReadCountTextView != null) {
            if (isShow) {
                notReadCountTextView.setVisibility(View.VISIBLE);
            } else {
                notReadCountTextView.setVisibility(View.GONE);
            }
        }
    }

    //这里需要根据TalkListBean ownerId + talkType 获取TalkMessageBean
    private String getShowTime() {
        if (dataSource.getDisplayTime() > 0) {
            return DateUtils.chatListDisplayTime(getActivity(), dataSource.getDisplayTime());
        }
        return "";
    }

    int getDefaultImageId() {
        return R.drawable.corp_user_40dp;
    }
}
