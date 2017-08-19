package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.util.DateUtils;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 20:12
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 20:12
 * 修改备注：
 */
public abstract class BaseFileItemAdapterPresenter<P extends Command,D> extends BaseExpandableListAdapter {

    Context mContext;

    private LayoutInflater inflater;

    List<HistoryFileCategory> mTitles;

    protected Map<HistoryFileCategory,List<D>> mDatas;

    @Override
    public int getGroupCount(){
        return mTitles != null ? mTitles.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<D> children = mDatas.get(mTitles.get(groupPosition));
        return mDatas != null && children != null ? children.size() : 0;
    }

    @Override
    public String getGroup(int groupPosition) {
        return mTitles != null ? DateUtils.contentWeekOfTime(mContext,mTitles.get(groupPosition).getTime()) : "";
    }

    @Override
    public D getChild(int groupPosition, int childPosition) {
        List<D> results = mDatas.get(mTitles.get(groupPosition));
        return results != null ? results.get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AdapterVu<P,String> adapterVu = null;
        if(convertView == null) {
            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            try{
                Class<? extends AdapterVu<P,String>> aClass = getGroupVuClassByViewType(0);
                if (aClass != null) {
                    adapterVu = aClass.newInstance();
                    adapterVu.setCommand(getCommand());
                    adapterVu.setActivity(this.getActivity());

                    adapterVu.init(inflater, parent);
                    convertView = adapterVu.getView();
                    convertView.setTag(adapterVu);
                    adapterVu.onViewCreated();
                }
            }catch(Exception e){
                LogUtil.getUtils().e(e.getMessage());
            }
        } else {
            adapterVu = (AdapterVu<P,String>) convertView.getTag();
            adapterVu.onViewReused();
        }
        if (adapterVu != null) {
            adapterVu.bindDataSource(groupPosition,getGroup(groupPosition));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AdapterVu<P,D> adapterVu = null;
        if(convertView == null) {
            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            try{
                Class<? extends AdapterVu<P,D>> aClass = getVuClassByViewType(0);
                if (aClass != null) {
                    adapterVu = aClass.newInstance();
                    adapterVu.setCommand(getCommand());
                    adapterVu.setActivity(this.getActivity());

                    adapterVu.init(inflater, parent);
                    convertView = adapterVu.getView();
                    convertView.setTag(adapterVu);
                    adapterVu.onViewCreated();
                }
            }catch(Exception e){
                LogUtil.getUtils().e(e.getMessage());
            }
        } else {
            adapterVu = (AdapterVu<P,D>) convertView.getTag();
            adapterVu.onViewReused();
        }
        if (adapterVu != null) {
            adapterVu.bindDataSource(groupPosition,getChild(groupPosition,childPosition));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    protected abstract List<Class<? extends AdapterVu<P,D>>> getVuClasses();

    protected abstract List<Class<? extends AdapterVu<P,String>>> getGroupVuClasses();

    protected abstract P getCommand();

    private Class<? extends AdapterVu<P,D>> getVuClassByViewType(int itemViewType) {
        if (this.getVuClasses() != null) {
            if (itemViewType < this.getVuClasses().size()) {
                return this.getVuClasses().get(itemViewType);
            }
        }
        return null;
    }

    private Class<? extends AdapterVu<P,String>> getGroupVuClassByViewType(int itemViewType) {
        if (this.getVuClasses() != null) {
            if (itemViewType < this.getVuClasses().size()) {
                return this.getGroupVuClasses().get(itemViewType);
            }
        }
        return null;
    }

    public Activity getActivity(){
        return null;
    }
}
