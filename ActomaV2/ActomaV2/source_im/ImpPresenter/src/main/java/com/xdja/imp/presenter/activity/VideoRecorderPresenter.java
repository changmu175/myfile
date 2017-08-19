package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.presenter.command.IVideoRecordCommand;
import com.xdja.imp.ui.ViewVideoRecordView;
import com.xdja.imp.ui.vu.IVideoRecordVu;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制界面     <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class VideoRecorderPresenter extends IMActivityPresenter<IVideoRecordCommand, IVideoRecordVu>
        implements IVideoRecordCommand {

    @NonNull
    @Override
    protected Class<? extends ViewVideoRecordView> getVuClass() {
        return ViewVideoRecordView.class;
    }

    @NonNull
    @Override
    protected IVideoRecordCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //设置全屏
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
        getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.video_activity_close);
    }

    @Override
    public void sendVideoMessage(VideoFileInfo videoFileInfo) {

        Intent intent = new Intent(this, ChatDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstDef.TAG_SELECTVIDEO, videoFileInfo);
        intent.putExtras(bundle);
        setResult(ConstDef.REQUEST_CODE_VIDEO, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        int code = getVu().onKeyBack();
        if (code == 0){
            super.onBackPressed();
        }
    }
}