package com.xdja.imp.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ScreenInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.util.Functions;

/**
 * Created by Administrator on 2016/4/12.
 * 功能描述
 */
public class ViewRecVoiceItem extends ViewChatDetailRecItem {
    private RelativeLayout voiceLayout;
    /**
     * 语音时长
     */
    private TextView voiceLengthTextView;

    /**
     * 播放语音显示容器
     */
    private ImageView voiceImageView;

    /**
     * 加载进度条
     */
    private ProgressBar bar;


    /**
     * 播放销毁动画的容器
     */
    private ImageView bombAnimImageView;

    /**
     * 下载失败的标识
     */
    private ImageView downFailedImageView;

    private ScreenInfo screenInfo;

    /**
     * 新消息标识
     */
    private ImageView newMessageTagImageView;


    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_recvoic;
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            voiceLayout= (RelativeLayout) view.findViewById(R.id.voice_layout);
            voiceLengthTextView = (TextView) view.findViewById(R.id.txt_chat_voiclength);
            voiceImageView = (ImageView) view.findViewById(R.id.img_anim_voice_he);
            bar = (ProgressBar) view.findViewById(R.id.recprogress);
            bombAnimImageView = (ImageView) view.findViewById(R.id.bomb_anim);
            downFailedImageView = (ImageView) view.findViewById(R.id.downFailed);
            newMessageTagImageView = (ImageView) view.findViewById(R.id.newMessageTag);
        }

        if (contentLayout != null) {
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().clickVoiceMessage(dataSource);
                }
            });
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        if (screenInfo == null){
            screenInfo = Functions.getScreenInfo(getActivity());
        }
        initView();
    }

    private void initView() {
        if (dataSource != null) {
            //如果消息已经销毁
            if (dataSource.getMessageState() == ConstDef.STATE_DESTROY) {
                setMessageDestroy(true);
                setDestroyView(true, getStringRes(R.string.voiceMessageIsDestoryed));
                voiceLayout.setVisibility(View.GONE);
                setDownFailedImageViewIsShow(false);
                setProgressBarIsShow(false);
                setClickListener(null);
                setNewMessageTagIsShow(false);
            } else {
                voiceLayout.setVisibility(View.VISIBLE);
                setNewMessageTagIsShow(false);
                if (dataSource.getMessageState() < ConstDef.STATE_READED) {
                    setNewMessageTagIsShow(true);
                }

                //根据语音时间长度设置语音气泡长度
                setVoiceLayoutParams();

                setMessageDestroy(false);
                setDestroyView(false, "");
                setClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        getCommand().clickVoiceMessage(dataSource);
                    }
                });
                setVoiceLengthText(getCommand().getVoiceLength(dataSource));

                //开始执行销毁动画
                if (dataSource.getMessageState() == ConstDef.STATE_DESTROYING) {
                    startBombAnim();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            getCommand().postDestroyAnimate(dataSource);
                        }
                    }, 700);

                }
            }
            FileInfo fileInfo = dataSource.getFileInfo();
            if (fileInfo != null){
                if(getCommand().getVoiceMessageIsPlaying(fileInfo.getFilePath(), fileInfo.getTalkMessageId())){
                    startVoiceAnim();
                    setNewMessageTagIsShow(false);
                } else {
                    stopVoiceAnim();
                }
            }

        }
    }

    /**
     * 根据语音时间长度设置语音气泡长度
     */
    private void setVoiceLayoutParams(){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) voiceLayout.getLayoutParams();
        String voiceLength = null;
        if (getCommand().getVoiceLength(dataSource) != null) {
            voiceLength = getCommand().getVoiceLength(dataSource).toString().replace(" \"", "");
        }

        int recordingTime = 0;//录音时间
        float bubbleLength;//语音气泡长度
        if (!TextUtils.isEmpty(voiceLength)) {
            recordingTime = Integer.parseInt(voiceLength);
        }
        if (recordingTime <= 10) {
            bubbleLength = (ConstDef.MIN_DP + (ConstDef.FIRST_STEP_DP - ConstDef.MIN_DP) / 10 *
                    recordingTime) * screenInfo.getDensity();
        } else if (recordingTime <= 20) {
            bubbleLength = (ConstDef.FIRST_STEP_DP + (ConstDef.SECOND_STEP_DP - ConstDef.FIRST_STEP_DP) / 10 *
                    (recordingTime - 10)) * screenInfo.getDensity();
        } else if (recordingTime <= 30) {
            bubbleLength = (ConstDef.SECOND_STEP_DP + (ConstDef.MAX_DP - ConstDef.SECOND_STEP_DP) / 10 *
                    (recordingTime - 20)) * screenInfo.getDensity();
        } else {
            bubbleLength = ConstDef.MAX_DP * screenInfo.getDensity();
        }
        layoutParams.width=(int) bubbleLength;
        voiceLayout.setLayoutParams(layoutParams);
    }

    /**
     * 设置是否是新消息
     *
     * @param isShow
     */
    private void setNewMessageTagIsShow(boolean isShow) {
        if (newMessageTagImageView != null) {
            if (isShow) {
                newMessageTagImageView.setVisibility(View.VISIBLE);
            } else {
                newMessageTagImageView.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 开始播放消息销毁动画
     */
    private void startBombAnim() {
        if (bombAnimImageView != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) bombAnimImageView.getBackground();
            if (voiceAnim != null) {
                voiceAnim.start();
            }
        }
    }



    /**
     * 设置下载失败标识是否显示
     *
     * @param isShow
     */
    private void setDownFailedImageViewIsShow(boolean isShow) {
        if (downFailedImageView != null) {
            if (isShow) {
                downFailedImageView.setVisibility(View.VISIBLE);
            } else {
                downFailedImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置录音时长
     *
     * @param voiceLengthText
     */
    private void setVoiceLengthText(CharSequence voiceLengthText) {
        if (voiceLengthTextView != null) {
            voiceLengthTextView.setText(voiceLengthText);
        }
    }

    /**
     * 设置进度条是否显示
     *
     * @param isShow
     */
    private void setProgressBarIsShow(boolean isShow) {
        if (bar != null) {
            if (isShow) {
                bar.setVisibility(View.VISIBLE);
            } else {
                bar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 开始语音播放动画
     */
    private void startVoiceAnim() {
        //add log for bug NACTOMA-377 by zya@xdja.com
        LogUtil.getUtils().i("startVoiceAnim");
        if (voiceImageView != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) voiceImageView.getBackground();
            if (voiceAnim != null) {
                if (!voiceAnim.isRunning()) {
                    voiceAnim.start();
                }
            }
        }
    }

    /**
     * 停止语音播放动画
     */
    private void stopVoiceAnim() {
        if (voiceImageView != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) voiceImageView.getBackground();
            if (voiceAnim != null) {
                voiceAnim.stop();
                voiceAnim.selectDrawable(0);
            }
        }
    }




    /**
     * 是否是闪信
     * @param isDestroy
     */
    public void setMessageDestroy(boolean isDestroy){
        if(contentLayout != null){
            if(isDestroy){
                contentLayout.setBackgroundResource(R.drawable.bg_shan_text_selector);
            }else{
                contentLayout.setBackgroundResource(R.drawable.bg_pao_left_selector);
            }
        }
    }


    /**
     * 设置点击事件
     *
     * @param listener
     */
    private void setClickListener(View.OnClickListener listener) {
        if (contentLayout != null) {
            contentLayout.setOnClickListener(listener);
        }
    }

}
