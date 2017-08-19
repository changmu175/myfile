package com.xdja.imp.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.presenter.holder.FileListViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/5 17:34   </br>
 * <p>Package: com.xdja.imp.presenter.adapter</br>
 * <p>Description:            </br>
 */
public class FileListAdapter extends BaseFileListAdapter<FileListViewHolder> {

    private Context mContext;

    private List<List<LocalFileInfo>> mLocalFiles = new ArrayList<>();

    public FileListAdapter(Context context, List<String> groupTitles) {
        super(groupTitles);
        this.mContext = context;
    }

    /**
     * 填充子列表数据，并进行更新
     *
     * @param localFiles 子类表数据
     */
    public void addLocalFiles(List<List<LocalFileInfo>> localFiles) {
        this.mLocalFiles = localFiles;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mLocalFiles == null ? 0 : mLocalFiles.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<LocalFileInfo> fileInfos = mLocalFiles.get(groupPosition);
        if (fileInfos != null) {
            return fileInfos.get(childPosition);
        }
        return null;
    }

    @Override
    public FileListViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {

        View convertView;
        if (viewType == TYPE_VERTICAL) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.filelist_item_vertical, parent, false);
        } else {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.filelist_item_grid, parent, false);
        }
        return new FileListViewHolder(mContext, convertView);
    }

    @Override
    public void onBindChildViewHolder(FileListViewHolder holder, int groupPosition, int childPosition) {
        final LocalFileInfo fileInfo = (LocalFileInfo) getChild(groupPosition, childPosition);
        if (fileInfo != null) {
            holder.bindData(fileInfo);
        }
    }
}
