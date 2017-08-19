package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.presenter.adapter.ChatDetailPictureAdapterPresenter;
import com.xdja.imp.presenter.command.IChatPicPreviewCommand;
import com.xdja.imp.ui.ChatPicPreviewVu;
import com.xdja.imp.ui.vu.IChatPicPreviewVu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guorong on 2016/7/6.
 */
public class ChatDetailPicPreviewActivity extends IMActivityPresenter<IChatPicPreviewCommand,
        IChatPicPreviewVu> implements IChatPicPreviewCommand {

    private ChatDetailPictureAdapterPresenter adapter;

    private List<FileInfo> fileInfoList = new ArrayList<>();

    private  String fileName;
    @Override
    protected Class<? extends IChatPicPreviewVu> getVuClass() {
        return ChatPicPreviewVu.class;
    }

    @Override
    protected IChatPicPreviewCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null){
            fileInfoList = intent.getParcelableArrayListExtra(ConstDef.SEESION_FILE_INFOS);
            fileName = intent.getStringExtra(ConstDef.CUR_FILE);
        }

        getVu().initViewPager(adapter);

        int index = 0;
        for(int i=0; i<fileInfoList.size(); i++){
            if(fileInfoList.get(i).getFileName().equals(fileName)){
                index = i;
            }
        }

        getVu().setCurrentItem(index);
    }
}
