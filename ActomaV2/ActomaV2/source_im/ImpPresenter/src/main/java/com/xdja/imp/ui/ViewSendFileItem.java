package com.xdja.imp.ui;

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
import com.xdja.imp.util.FileSizeUtils;
import com.xdja.imp.util.HistoryFileUtils;

import java.io.File;

/**
 * Created by guorong on 2016/11/30.
 * 详情界面文件发送item
 */
public class ViewSendFileItem extends ViewChatDetailSendItem{

    //文件类型logo
    private ImageView logoIv;
    //文件名称
    private TextView fileNameTv;
    //文件大小
    private TextView fileSizeTv;
    //文件下载进度条
    private ProgressBar progressBar;
    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendfile;
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
        progressBar = (ProgressBar)view.findViewById(R.id.rateprogress);
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
        final FileInfo fileInfo = dataSource.getFileInfo();
        if(fileInfo == null){
            return;
        }
        logoIv.setImageResource(getCommand().getFileLogoId(dataSource));
        fileNameTv.setText(fileInfo.getFileName());
        fileSizeTv.setText(FileSizeUtils.getFileSize(fileInfo.getFileSize()));
        //要上传的文件已经存在并且未上传完成的情况下，显示进度条
        if(fileInfo.getFileState() == ConstDef.DONE || fileInfo.getFileState() == ConstDef.FAIL ||
                fileInfo.getFileState() == ConstDef.INACTIVE ||
                dataSource.getMessageState() == ConstDef.STATE_SEND_FAILD){
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(fileInfo.getPercent());
        }
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(fileInfo.getFilePath());
                if(!file.exists()){
                    Toast.makeText(getActivity(),getActivity().getString(R.string.history_send_file_not_exist),Toast.LENGTH_SHORT).show();
                }else{
                    HistoryFileUtils.intentBuilder(getActivity(), fileInfo.getFilePath(),
                            fileInfo.getSuffix());//getIntentWithSuffix(bean);
                }
            }
        });
    }

    /*private int getPercent(@NonNull FileInfo fileInfo){
        int percent;
        File file = new File(fileInfo.getFilePath());
        if(!file.exists()){
            percent = 0;
        }else{
            percent = (int)((fileInfo.getTranslateSize() / fileInfo.getFileSize()) * 100);
        }
        return percent;
    }*/

}
