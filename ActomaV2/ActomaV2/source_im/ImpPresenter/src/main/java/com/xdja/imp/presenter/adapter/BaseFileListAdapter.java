package com.xdja.imp.presenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.xdja.imp.R;
import com.xdja.imp.presenter.holder.FileGroupViewHolder;
import com.xdja.imp.presenter.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 10:58   </br>
 * <p>Package: com.xdja.imp.presenter.adapter</br>
 * <p>Description:            </br>
 */
public abstract class BaseFileListAdapter<VH extends ViewHolder>
        extends BaseExpandableListAdapter {

    static final int TYPE_VERTICAL = 0;
    public static final int TYPE_HORIZONTICAL = 1;

    private List<String> mGroupItems = new ArrayList<>();

    BaseFileListAdapter(List<String> groupItems) {
        this.mGroupItems = groupItems;
    }

    /**
     * 获取group分组总数
     *
     * @return group分组总数
     */
    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    /**
     * 获取指定子列表元素数
     *
     * @param groupPosition group分组索引
     * @return group分组对应的子列表总数
     */
    @Override
    public abstract int getChildrenCount(int groupPosition);

    /**
     * 获取指定group相关联数据
     *
     * @param groupPosition group分组对应索引值
     * @return group 指定分组数据
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    /**
     * 获取与指定分组、指定子项目关联数据
     *
     * @param groupPosition 子视图对应分组索引值
     * @param childPosition 子视图索引值
     * @return 子视图关联数据
     */
    @Override
    public abstract Object getChild(int groupPosition, int childPosition);

    /**
     * 创建子类分组对应的ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract VH onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * 子类分组ViewHolder绑定数据
     *
     * @param holder
     * @param groupPosition 子视图对应分组索引值
     * @param childPosition 子视图索引值
     */
    protected abstract void onBindChildViewHolder(VH holder, int groupPosition, int childPosition);


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 是否指定分组视图及其子视图的ID对应的后台数据改变也会保持该ID
     *
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * 指定位置的子视图是否可选择
     *
     * @param groupPosition 子视图对应分组索引值
     * @param childPosition 子视图索引值
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 创建group对应的ViewHolder
     *
     * @param parent
     * @return
     */
    private FileGroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filelist_item_group, parent, false);
        return new FileGroupViewHolder(convertView);
    }

    /**
     * group绑定数据
     *
     * @param holder
     * @param position
     */
    private void onBindGroupViewHolder(FileGroupViewHolder holder, int position) {
        if (holder != null) {
            holder.bindData(getGroup(position).toString());
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        FileGroupViewHolder holder;
        if (convertView == null) {
            holder = onCreateGroupViewHolder(parent);
            convertView = holder.getConvertView();
            convertView.setTag(holder);
        } else {
            holder = (FileGroupViewHolder) convertView.getTag();
        }
        onBindGroupViewHolder(holder, groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        VH holder;
        if (convertView == null) {
            holder = onCreateChildViewHolder(parent, 0);
            convertView = holder.getConvertView();
            convertView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        onBindChildViewHolder(holder, groupPosition, childPosition);
        return convertView;
    }
}
