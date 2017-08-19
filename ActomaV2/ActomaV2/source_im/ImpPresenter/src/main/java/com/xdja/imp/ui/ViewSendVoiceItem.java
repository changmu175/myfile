package com.xdja.imp.ui;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
public class ViewSendVoiceItem extends ViewChatDetailSendItem {

    /**
     * 播放语音显示容器
     */
    private ImageView voiceAnimImg;

    private RelativeLayout.LayoutParams layoutParams;

    private ScreenInfo screenInfo;


    public ViewSendVoiceItem(){
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendvoic;
    }

    @Override
    public void bindDataSource(int position, @NonNull final TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        View view = getView();
        if (view != null) {
            voiceAnimImg = (ImageView)view.findViewById(R.id.img_anim_voic_me);
            voiceAnimImg.setBackgroundResource(R.drawable.animlist_voic_me);
            if(dataSource.isBomb()){
                voiceAnimImg.setBackgroundResource(R.drawable.animlist_voic_white_me);
            }
        }

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().clickVoiceMessage(dataSource);
            }
        });
        if (screenInfo == null){
            screenInfo = Functions.getScreenInfo(getActivity());
        }
        initView();
    }

    private void initView() {
        //根据语音时间长度设置语音气泡长度
        layoutParams = (RelativeLayout.LayoutParams) contentLayout.getLayoutParams();
        String voiceLength = null;
        if (getCommand().getVoiceLength(dataSource) != null) {
            voiceLength = getCommand().getVoiceLength(dataSource).toString().replace(" \"", "");
        }

        showBackground(voiceLength);

        setVoiceLengthText(getCommand().getVoiceLength(dataSource));

        LogUtil.getUtils().d("发送内容：dataSource =  " + dataSource.getContent() + " state = " + dataSource.getMessageState());

        if (dataSource.getFileInfo() == null) return;

        FileInfo fileInfo = dataSource.getFileInfo();
        if (fileInfo != null &&
                getCommand().getVoiceMessageIsPlaying(fileInfo.getFilePath(), fileInfo.getTalkMessageId())) {
            startVoiceAnim();
        } else {
            stopVoiceAnim();
        }

    }

    private void showBackground(String voiceLength){
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
        layoutParams.width = (int) bubbleLength;
        contentLayout.setLayoutParams(layoutParams);
    }

    /**
     * 设置语音时长
     * @param voiceLengthText
     */
    private void setVoiceLengthText(CharSequence voiceLengthText){
        if(voiceLengthTextView != null){
            voiceLengthTextView.setText(voiceLengthText);
        }
    }


    /**
     * 开始语音播放动画
     */
    private void startVoiceAnim() {
        if (voiceAnimImg != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) voiceAnimImg.getBackground();
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
        if (voiceAnimImg != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) voiceAnimImg.getBackground();
            if (voiceAnim != null) {
                voiceAnim.stop();
                voiceAnim.selectDrawable(0);
            }
        }
    }
}
