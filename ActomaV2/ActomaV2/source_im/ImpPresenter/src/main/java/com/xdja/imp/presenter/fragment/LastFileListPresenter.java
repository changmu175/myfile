package com.xdja.imp.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ExpandableListView;

import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.domain.interactor.def.QueryLastFiles;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.frame.imp.presenter.IMFragmentPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.FileListAdapter;
import com.xdja.imp.presenter.command.ILastFileListCommand;
import com.xdja.imp.ui.ViewLastFileList;
import com.xdja.imp.ui.vu.ILastFileListVu;
import com.xdja.imp.util.FileInfoCollection;
import com.xdja.imp.util.XToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * <p>Author: xdjaxa         </br>
 * <p>Date: 2016/12/2 16:53   </br>
 * <p>Package: com.xdja.imp.presenter.fragment</br>
 * <p>Description: 文件列表显示fragment          </br>
 */
public class LastFileListPresenter extends IMFragmentPresenter<ILastFileListCommand, ILastFileListVu>
        implements ILastFileListCommand {

    /** 最多可选择文件个数*/
    private static final int MAX_SELECT_COUNT = 9;

    //事件总线
    @Inject
    BusProvider busProvider;

    //查询最近所有聊天文件
    @Inject
    Lazy<QueryLastFiles> queryLastFiles;

    /**
     * 文件列表适配器
     */
    private FileListAdapter mAdapter;

    /**
     * 分组标题
     */
    private final List<String> mGroupTitles = new ArrayList<>();

    /**
     * 分组对应的文件列表
     */
    private final List<List<LocalFileInfo>> mLocalFileList = new ArrayList<>();

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        //初始化注入
        useCaseComponent.inject(this);

        //初始化事件总线
        busProvider.register(this);

        initViews();

        loadData();
    }

    private void initViews() {
        //添加子控件Item点击事件
        getVu().getListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                //点击子控件，进行选择
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

    private void loadData() {
        queryLastFiles
                .get()
                .queryLastFiles()
                .execute(new OkSubscriber<Map<String, List<LocalFileInfo>>>(this.okHandler) {

                    @Override
                    public void onNext(Map<String, List<LocalFileInfo>> stringListMap) {
                        super.onNext(stringListMap);

                        if (stringListMap.size() == 0) {
                            //为空显示
                            getVu().setEmptyView();
                        } else {
                            mGroupTitles.clear();
                            mLocalFileList.clear();
                            for (String key : stringListMap.keySet()) {
                                mGroupTitles.add(fileLineTimeConvert(key));
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

    /**
     * 最近文件按照时间类型进行排序，时间线转化为对应的字符串
     *
     * @param timeLine
     * @return
     */
    private String fileLineTimeConvert(String timeLine) {
        String timeLineTitle = "";
        int timeLineType = Integer.valueOf(timeLine);
        switch (timeLineType) {
            case IMFileUtils.TIME_WITHIN_TODAY:
                timeLineTitle = getString(R.string.today);
                break;
            case IMFileUtils.TIME_YESTERDAY:
                timeLineTitle = getString(R.string.yesterday);
                break;
            case IMFileUtils.TIME_WITHIN_WEEK:
                timeLineTitle = getString(R.string.within_a_week);
                break;
            case IMFileUtils.TIME_WITHIN_MONTH:
                timeLineTitle = getString(R.string.within_a_month);
                break;
            case IMFileUtils.TIME_WITHIN_MONTH_AGO:
                timeLineTitle = getString(R.string.a_month_ago);
                break;
        }
        return timeLineTitle;
    }

    @NonNull
    @Override
    protected Class<? extends ILastFileListVu> getVuClass() {
        return ViewLastFileList.class;
    }

    @NonNull
    @Override
    protected ILastFileListCommand getCommand() {
        return this;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }
}
