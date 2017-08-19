package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * Created by jing on 2015/12/28.
 * 功能描述
 */
public class ViewChatDetailRecItem extends ViewChatDetailBaseItem {

    /**
     * 头像
     */
    private CircleImageView circleImageView;

    /**
     * 发送者名称
     */
    private TextView senderNameTextView;

    /**
     * 内容布局
     */
    LinearLayout contentLayout;

    /**
     * 销毁后的文本显示
     */
    private TextView destroyView;

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            circleImageView = (CircleImageView)view.findViewById(R.id.header);
            senderNameTextView = (TextView)view.findViewById(R.id.txt_chat_senderName);
            contentLayout = (LinearLayout)view.findViewById(R.id.content_layout);
            destroyView = (TextView)view.findViewById(R.id.chat_destroy);
        }

    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        ContactInfo info;
        if (dataSource.isGroupMsg()) {
            info = getCommand().getGroupMemberInfo(dataSource.getTo(), dataSource.getFrom());
        } else {
            info = getCommand().getContactInfo(dataSource.getFrom());
        }
        setSenderNameText(info.getName());
        if(dataSource.isGroupMsg()){
            setShowSenderName(true);
        } else {
            setShowSenderName(false);
        }
        initView(dataSource, info);
    }

    /**
     * 设置头像的点击事件
     * @param listener
     */
    private void setHeaderImageClickListener(View.OnClickListener listener) {
        if (circleImageView != null) {
            circleImageView.setOnClickListener(listener);
        }
    }

    private  void initView(final TalkMessageBean dataSource, final ContactInfo info){
        if (contentLayout != null){
            contentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    getCommand().longClickMessage(dataSource, view);
                    return true;
                }
            });
        }

        setCircleImageUrl(info.getThumbnailUrl(), R.drawable.corp_user_40dp);
        setHeaderImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().startContactDetailActivity(info.getAccount());
            }
        });
    }

    void longClickMsg(){
        getCommand().longClickMessage(dataSource, contentLayout);
    }


    private void setCircleImageUrl(String url, int defaultImageId) {
        //TODO：gbc
        //fix bug 2705 by licong, reView zya, 2016/08/17
        if (!getCommand().getActivityIsDestroy()) {
            circleImageView.loadImage(url, true, defaultImageId);
        }//end

        /*HeadImgParamsBean imgBean = HeadImgParamsBean.getParams(url);
        circleImageView.loadImage(
                imgBean.getHost(), true, imgBean.getFileId(),
                imgBean.getSize(), defaultImageId);*/
    }


    /**
     * 设置是否显示发送者姓名
     *
     * @param isShow
     */
    private void setShowSenderName(boolean isShow) {
        if (senderNameTextView != null) {
            if (isShow) {
                senderNameTextView.setVisibility(View.VISIBLE);
            } else {
                senderNameTextView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置发送者姓名
     *
     * @param senderName
     */
    private void setSenderNameText(CharSequence senderName) {
        if (senderNameTextView != null) {
            senderNameTextView.setText(senderName);
        }
    }

    /**
     * 是否是闪信
     * @param isDestroy
     */
    void setMessageDestroy(boolean isDestroy){
        //优化问题：图片和视频控件，不用设置背景图片 fixed by leiliangliang
        if (dataSource.getMessageType() == ConstDef.MSG_TYPE_PHOTO ||
                dataSource.getMessageType() == ConstDef.MSG_TYPE_VIDEO) {
            return;
        }
        if(contentLayout != null){
            if(isDestroy){
                contentLayout.setBackgroundResource(R.drawable.bg_shan_text_selector);
            }else{
                contentLayout.setBackgroundResource(R.drawable.bg_pao_left_selector);
            }
        }
    }


    void setDestroyView(boolean isDestroy, String destroyStr){

        if (destroyView != null) {
            if(isDestroy){
                destroyView.setVisibility(View.VISIBLE);
                destroyView.setTextColor(getCommand().getLimitTextColor());
                destroyView.setText(destroyStr);
            } else {
                destroyView.setVisibility(View.GONE);
            }
        }
    }
}
