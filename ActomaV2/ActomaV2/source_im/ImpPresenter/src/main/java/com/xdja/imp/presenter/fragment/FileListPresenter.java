package com.xdja.imp.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ExpandableListView;

import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.QueryLocalFiles;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.frame.imp.presenter.IMFragmentPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.FileListAdapter;
import com.xdja.imp.presenter.command.IFileListCommand;
import com.xdja.imp.ui.ViewFileList;
import com.xdja.imp.ui.vu.IFileListVu;
import com.xdja.imp.util.FileInfoCollection;
import com.xdja.imp.util.XToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 19:25   </br>
 * <p>Package: com.xdja.imp.presenter.fragment</br>
 * <p>Description:            </br>
 */
public class FileListPresenter extends IMFragmentPresenter<IFileListCommand, IFileListVu>
        implements IFileListCommand{

    /** 最多可选择文件个数*/
    private static final int MAX_SELECT_COUNT = 9;

    //事件总线
    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<QueryLocalFiles> queryLocalFiles;

    private FileListAdapter mAdapter;

    /**
     * 分组标题
     */
    private final List<String> mGroupTitles = new ArrayList<>();

    /**
     * 分组对应的文件列表
     */
    private final List<List<LocalFileInfo>> mLocalFileList = new ArrayList<>();

    /**
     * type值，根据不同的类型，加载不同的文件数据
     */
    private int mFileType = 0;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        //初始化注入
        useCaseComponent.inject(this);
        //初始化事件总线
        busProvider.register(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mFileType = bundle.getInt(ConstDef.ARGS_FILE_TYPE);
        }

        //控件相关
        initViews();

        //加载数据
        loadData();
    }

    private void initViews() {

        getVu().getListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                LocalFileInfo fileInfo = mLocalFileList.get(groupPosition).get(childPosition);
                if (fileInfo != null) {

                    //文件选择个数限制
                    if (!fileInfo.isSelected()) {
                        if (FileInfoCollection.getInstance().getSelectedFileCount() >= MAX_SELECT_COUNT) {
                            new XToast(getContext()).display(String.format(getString(R.string.select_file_hint),
                                    MAX_SELECT_COUNT));
                            return false;
                        }
                    }

                    //通知刷新界面
                    fileInfo.setSelected(!fileInfo.isSelected());
                    if (fileInfo.isSelected()) {
                        FileInfoCollection.getInstance().putFileToSelectedCache(
                                fileInfo.getFilePath() + fileInfo.getModifiedDate(),
                                fileInfo);
                    } else {
                        FileInfoCollection.getInstance().removeToSelectedCache(
                                fileInfo.getFilePath() + fileInfo.getModifiedDate());
                    }
                    IMProxyEvent.FileSelectedEvent event = new IMProxyEvent.FileSelectedEvent(fileInfo);
                    busProvider.post(event);


                    //更新数据
                    mLocalFileList.get(groupPosition).remove(childPosition);
                    mLocalFileList.get(groupPosition).add(childPosition, fileInfo);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    /**
     * 获取数据
     */
    private void loadData() {

        queryLocalFiles
                .get()
                .queryLocalFiles(mFileType)
                .execute(new OkSubscriber<Map<String, List<LocalFileInfo>>>(this.okHandler){
                    @Override
                    public void onNext(Map<String, List<LocalFileInfo>> stringListMap) {
                        super.onNext(stringListMap);

                        if (stringListMap.size() == 0) {

                            getVu().setEmptyView();

                        } else {
                            mGroupTitles.clear();
                            mLocalFileList.clear();
                            for (String key : stringListMap.keySet()) {
                                mGroupTitles.add(key);
                                mLocalFileList.add(stringListMap.get(key));
                            }
                            mAdapter = new FileListAdapter(getContext(), mGroupTitles);
                            mAdapter.addLocalFiles(mLocalFileList);
                            getVu().setListAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        getVu().setEmptyView();
                    }
                });
    }

    @NonNull
    @Override
    protected Class<? extends IFileListVu> getVuClass() {
        return ViewFileList.class;
    }

    @NonNull
    @Override
    protected IFileListCommand getCommand() {
        return this;
    }


}
