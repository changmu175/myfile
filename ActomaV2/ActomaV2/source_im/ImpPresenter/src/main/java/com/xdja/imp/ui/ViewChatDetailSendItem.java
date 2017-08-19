package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * Created by jing on 2015/12/28.
 * 功能描述
 */
public class ViewChatDetailSendItem extends ViewChatDetailBaseItem {

    /**
     * 头像
     */
    private CircleImageView circleImageView;

    /**
     * 发送的文本内容
     */
    TextView sendContentTextView;

    /**
     * 发送的语音长度
     */
    TextView voiceLengthTextView;

    /**
     * 内容布局
     */
    RelativeLayout contentLayout;


    /**
     * 发送状态
     */
    private TextView sendStateTextView;

    /**
     * 重发按钮
     */
    private ImageButton resendImageButton;


    /**
     * 发送中进度条
     */
    private ProgressBar sendingProgressBar;

    private String thumbUrl;

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            circleImageView = (CircleImageView)view.findViewById(R.id.header);
            sendContentTextView = (TextView)view.findViewById(R.id.sendcontent);
            contentLayout = (RelativeLayout)view.findViewById(R.id.content_layout);
            sendStateTextView = (TextView)view.findViewById(R.id.sendState);
            resendImageButton = (ImageButton) view.findViewById(R.id.reSend);
            sendingProgressBar = (ProgressBar)view.findViewById(R.id.sendProgress);
            voiceLengthTextView= (TextView) view.findViewById(R.id.voicelength);
        }
        AccountBean account = AccountServer.getAccount();
        if (account != null) {
            thumbUrl = account.getThumbnail();
        }

    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView(dataSource);
    }

    /**
     * 设置头像的点击事件
     * @param listener
     */
    public void setHeaderImageClickListener(View.OnClickListener listener) {
        if (circleImageView != null) {
            circleImageView.setOnClickListener(listener);
        }
    }
    void longClickMsg(){
        getCommand().longClickMessage(dataSource, contentLayout);
    }
    private  void initView(final TalkMessageBean dataSource){
        setCircleImageUrl(thumbUrl, R.drawable.corp_user_40dp);
        if (contentLayout != null){
            contentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    getCommand().longClickMessage(dataSource, view);
                    return true;
                }
            });
        }
        //重发
        if (dataSource.getMessageState() == ConstDef.STATE_SEND_FAILD) {
            setResendImageButtonIsShow(true);
            setResendOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CommonUtils.isFastDoubleClick() &&
                            dataSource.getMessageState() != ConstDef.STATE_SENDING) {

                        //fix by juyingang 20160902 bug 3576 begin
                        //点击重发后，显示进度条控件，隐藏重发按钮
                        setResendImageButtonIsShow(false);
                        setSendingProgressBarIsShow(true);
                        //fix by juyingang 20160902 bug 3576 end

                        //if(isNeedResend(imageFileInfo)){
                        getCommand().reSendMessage(dataSource);

                        //} else {
                        //    //TODO 文件大小需要统一定义
                        //    new XToast(getActivity()).display(String.format(getStringRes(
                        //            R.string.max_send_image_size), 5));
                        //}
                    }
                }
            });
        } else {
            setResendImageButtonIsShow(false);
        }

        //发送进度
        if (dataSource.getMessageState() == ConstDef.STATE_SENDING) {
            setSendingProgressBarIsShow(true);
        } else {
            setSendingProgressBarIsShow(false);
        }


        //消息状态
        @ConstDef.MsgState int messageState = dataSource.getMessageState();
        setSendStateText(ConstDef.mapMsgState(messageState));

        //设置背景
        setSendLayoutBackground(dataSource.isBomb());
    }

    private void setCircleImageUrl(String url, int defaultImageId) {
        //TODO: gbc
        if (getCommand().getActivityIsShowing()) {
            circleImageView.loadImage(url, true, defaultImageId);
        }
        /*HeadImgParamsBean imgBean = HeadImgParamsBean.getParams(url);
        circleImageView.loadImage(
                imgBean.getHost(), true, imgBean.getFileId(),
                imgBean.getSize(), defaultImageId);*/
    }

    /**
     * 根据是否是闪信设置背景
     * @param isLimit
     */
    private void setSendLayoutBackground(boolean isLimit){
        //优化问题：图片和视频控件，不用设置背景图片 fixed by leiliangliang
        if (dataSource.getMessageType() == ConstDef.MSG_TYPE_PHOTO ||
                dataSource.getMessageType() == ConstDef.MSG_TYPE_VIDEO) {
            return;
        }
        if(contentLayout != null){
            if(isLimit){
                contentLayout.setBackgroundResource(R.drawable.bg_shan_sendmessage_selector);
                setTxtContentColor(getCommand().getLimitTextColor());
            }else{
                contentLayout.setBackgroundResource(R.drawable.bg_pao_right_selector);
                setTxtContentColor(getCommand().getNormalTextColor());
            }
        }
    }


    private void setTxtContentColor(int color){
        if(sendContentTextView!=null) {
            sendContentTextView.setTextColor(color);
        }
        if(voiceLengthTextView!=null){
            voiceLengthTextView.setTextColor(color);
        }
    }

    /**
     * 设置发送进度条是否显示
     * @param isShow
     */
    private void setSendingProgressBarIsShow(boolean isShow){
        if(sendingProgressBar != null){
            if(isShow){
                sendingProgressBar.setVisibility(View.VISIBLE);
                if(sendStateTextView != null){
                    sendStateTextView.setVisibility(View.GONE);
                }
            }else{
                sendingProgressBar.setVisibility(View.GONE);
                if(sendStateTextView != null){
                    sendStateTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 设置重发按钮是否显示
     * @param isShow
     */
    private void setResendImageButtonIsShow(boolean isShow){
        if(resendImageButton != null){
            if(isShow){
                resendImageButton.setVisibility(View.VISIBLE);
            }else{
                resendImageButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置重发按钮的点击事件
     * @param listener
     */
    private void setResendOnClickListener(View.OnClickListener listener){
        if(resendImageButton != null){
            resendImageButton.setOnClickListener(listener);
        }
    }

    /**
     * 设置消息的发送状态
     * @param sendStateText
     */
    private void setSendStateText(CharSequence sendStateText) {
        if (sendStateTextView != null) {
            if(dataSource.getMessageState() == ConstDef.STATE_SEND_FAILD &&
                    dataSource.getFailCode() == ConstDef.FAIL_FRIEND){
                sendStateTextView.setVisibility(View.VISIBLE);
                if(dataSource.isGroupMsg()){
                    sendStateTextView.setText(getContext().getResources().getString(R.string.not_group_member));
                } else {
                    sendStateTextView.setText(getContext().getResources().getString(R.string.not_friend));
                }
            } else {
                if(dataSource.isGroupMsg()){
                    sendStateTextView.setVisibility(View.GONE);
                } else {
                    sendStateTextView.setVisibility(View.VISIBLE);
                    sendStateTextView.setText(sendStateText);
                }
            }
        }
    }
}
