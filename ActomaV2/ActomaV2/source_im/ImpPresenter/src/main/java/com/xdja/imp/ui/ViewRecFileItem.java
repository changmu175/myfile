package com.xdja.imp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.presenter.activity.ChatDetailFileCheckPresenter;
import com.xdja.imp.util.DateUtils;
import com.xdja.imp.util.FileSizeUtils;

/**
 * Created by guorong on 2016/11/30.
 * 详情界面文件接收item
 */
public class ViewRecFileItem extends ViewChatDetailRecItem{
    private static final String MSG_BEAN = "msg_bean";
    private static final String TITLE = "title";
    //文件类型logo
    private ImageView logoIv;
    //文件名称
    private TextView fileNameTv;
    //文件大小
    private TextView fileSizeTv;
    //文件下载进度条
    private ProgressBar progressBar;
    //下载状态图标
    private ImageView stateIv;

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_recfile;
    }

    @Override
    public void onViewReused() {
        super.onViewReused();
        logoIv.setImageBitmap(null);
        fileNameTv.setText("");
        fileSizeTv.setText("");
        progressBar.setProgress(0);
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        logoIv = (ImageView)view.findViewById(R.id.file_logo);
        fileNameTv = (TextView)view.findViewById(R.id.file_name);
        fileSizeTv = (TextView)view.findViewById(R.id.file_size);
        progressBar = (ProgressBar)view.findViewById(R.id.recprogress);
        stateIv = (ImageView)view.findViewById(R.id.state_iv);

        //点击整体布局
        //如果文件下载完成，直接打开选择应用界面
        //如果文件未下载完成，点击开始下载
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //过期文件，不允许下载
                if(dataSource != null){
                    FileInfo fileInfo = dataSource.getFileInfo();
                    if(DateUtils.isOverdue(dataSource.getShowTime()) && fileInfo.getFileState() != ConstDef.DONE){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.history_select_all_overdue),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getActivity() , ChatDetailFileCheckPresenter.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(MSG_BEAN , dataSource);
                        intent.putExtras(bundle);
                        String title = getActivity().getTitle().toString();
                        intent.putExtra(TITLE , title);
                        getActivity().startActivityForResult(intent , ConstDef.REQUEST_CODE_FILE_CHECK);
                    }
                }
            }
        });
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        if(dataSource == null){
            return;
        }
        initView();
    }

    private void initView(){
        FileInfo fileInfo = dataSource.getFileInfo();
        if(fileInfo == null){
            return;
        }
        if(fileInfo.getTranslateSize() > 0 && fileInfo.getPercent() == 0 && fileInfo.getFileSize() > 0){
            fileInfo.setPercent((int) ((fileInfo.getTranslateSize() * 100) / fileInfo.getFileSize()));
        }
        if(fileInfo.getFileState() != ConstDef.DONE
                && fileInfo.getFileState() != ConstDef.INACTIVE
                && fileInfo.getFileState() != ConstDef.FAIL){
            stateIv.setImageResource(getStateImage(fileInfo));
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(fileInfo.getPercent());
        }else{
            if(fileInfo.getFileState() == ConstDef.FAIL){
                stateIv.setImageResource(getStateImage(fileInfo));
            }else{
                stateIv.setImageDrawable(null);
            }
            progressBar.setVisibility(View.GONE);
        }
        logoIv.setImageResource(getCommand().getFileLogoId(dataSource));
        fileNameTv.setText(fileInfo.getFileName());
        fileSizeTv.setText(FileSizeUtils.getFileSize(fileInfo.getFileSize()));
        if (dataSource.getMessageState() < ConstDef.STATE_READED) {
            //如果当前界面正在显示，并且消息状态是初始状态，发送已阅读回执
            if (getCommand().getActivityIsShowing()) {
                //发送阅读回执
                getCommand().sendReadReceipt(dataSource);
            }
        }
    }
    private int getStateImage(FileInfo fileInfo){
        int res = 0;
        if(fileInfo.getFileState() == ConstDef.LOADING){
            res = R.drawable.ic_download_normal;
        }else if(fileInfo.getFileState() == ConstDef.FAIL){
            res = R.drawable.ic_again_normal;
        }else if(fileInfo.getFileState() == ConstDef.PAUSE){
            res = R.drawable.ic_suspend_normal;
        }
        return res;
    }
}
