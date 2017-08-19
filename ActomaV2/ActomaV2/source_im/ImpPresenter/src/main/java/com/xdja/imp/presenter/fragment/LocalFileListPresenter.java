package com.xdja.imp.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.frame.imp.presenter.IMFragmentPresenter;
import com.xdja.imp.presenter.adapter.LocalFileListAdapter;
import com.xdja.imp.presenter.command.ILocalFileListCommand;
import com.xdja.imp.ui.ViewLocalFileList;
import com.xdja.imp.ui.vu.ILocalFileListVu;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author: leiliangliang  </br>
 * <p>Date: 2016/12/5 9:48   </br>
 * <p>Package: com.xdja.imp.presenter.fragment</br>
 * <p>Description: 聊天文件列表 </br>
 */
public class LocalFileListPresenter extends IMFragmentPresenter<ILocalFileListCommand, ILocalFileListVu>
        implements ILocalFileListCommand {

    private final int[] FILE_TYPE = new int[]{
            ConstDef.TYPE_VOICE, //音频
            ConstDef.TYPE_PHOTO, //图片
            ConstDef.TYPE_TXT,   //文档
            ConstDef.TYPE_APK,   //应用apk
            ConstDef.TYPE_OTHER  //其他
    };

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        LocalFileListAdapter mAdapter = buildAdapter(createFragment());

        getVu().setFragmentAdapter(mAdapter);
    }

    @NonNull
    @Override
    protected Class<? extends ILocalFileListVu> getVuClass() {
        return ViewLocalFileList.class;
    }

    @NonNull
    @Override
    protected ILocalFileListCommand getCommand() {
        return this;
    }

    /**
     * 构建文件类型Fragment
     *
     * @return
     */
    private List<Fragment> createFragment() {

        List<Fragment> fragments = new ArrayList<>();

        //设置Fragment
        for (int aFILE_TYPE : FILE_TYPE) {
            Fragment fragment = new FileListPresenter();
            Bundle bundle = new Bundle();
            bundle.putInt(ConstDef.ARGS_FILE_TYPE, aFILE_TYPE);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        return fragments;
    }

    private LocalFileListAdapter buildAdapter(final List<Fragment> fragments) {
        return new LocalFileListAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
    }
}
