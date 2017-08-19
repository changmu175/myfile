package com.xdja.frame.presenter.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.AdapterVu;

import java.util.List;

/**
 * <p>Summary:适配多个显示项的ListView适配器</p>
 * <p>Description: P Command接口的类型；T 数据源的类型</p>
 * <p>Package:com.hysel.picker.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/6/25</p>
 * <p>Time:11:36</p>
 */
public abstract class BasePresenterItemAdapter<P extends Command,D> extends BaseAdapter {

    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int itemViewType = getItemViewType(position);
        AdapterVu<P,D> adapterVu = null;
        if (convertView == null) {
            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            try {
                Class<? extends AdapterVu> aClass = getVuClassByViewType(itemViewType);
                //modify by zya@xdja.com,20161014,fix bug 4880
                if(aClass != null) {
                    adapterVu = ((AdapterVu<P,D>) aClass.newInstance());
                    adapterVu.setCommand(getCommand());
                    adapterVu.setActivity(this.getActivity());

                    adapterVu.init(inflater, parent);
                    convertView = adapterVu.getView();
                    convertView.setTag(adapterVu);
                    adapterVu.onViewCreated();
                }//end
            } catch (IllegalAccessException ex) {

            } catch (InstantiationException ex) {

            }
        } else {
            adapterVu = ((AdapterVu<P,D>) convertView.getTag());
            adapterVu.onViewReused();
        }
        //绑定View对应位置的数据源，初始化View
        //add by zya@xdja.com,20161014,fix bug 4880
        if(adapterVu != null) {
            adapterVu.bindDataSource(position, getDataSource(position));
        }//end
        return convertView;
    }

    /**
     * 单独更新列表中的 一个视图/一条数据（单条刷新）
     * @param position 视图/数据 位置
     */
    public void updateItem(int position) {

        if (getListView() != null) {
            // 当前listView显示的第一个元素的未知
            int firstVisPosition = getListView().getFirstVisiblePosition();
            // 当前listView显示的最后一个元素的位置
            int lastVisPosition = getListView().getLastVisiblePosition();
            // 如果要更新的元素不在当前屏幕显示中，阻止界面更新操作
            if (position < firstVisPosition - 1 || position > lastVisPosition) {
                return;
            }
            // listView.getChildAt()的参数为要更新的项的索引与当前屏幕内第一条可见条目的偏移量
            View view = getListView().getChildAt(position - firstVisPosition);
            if (view != null) {
                AdapterVu<P,D> tag = (AdapterVu<P,D>)view.getTag();
                tag.bindDataSource(position,getDataSource(position));
            }
        }
    }

    protected abstract List<Class<? extends AdapterVu<P,D>>> getVuClasses();

    protected abstract P getCommand();

    /**
     * 根据位置获取数据源
     *
     * @param position 位置
     * @return 对应的数据源
     */
    protected abstract D getDataSource(int position);

    /**
     * 根据View类型获取对应的View类型定义
     *
     * @return View类型
     */
    protected Class<? extends AdapterVu<P,D>> getVuClassByViewType(int itemViewType) {
        if (this.getVuClasses() != null) {
            if (itemViewType < this.getVuClasses().size()) {
                return this.getVuClasses().get(itemViewType);
            //add by zya@xdja.com,20161014,fix bug 4880
            } else {
                return getVuClasses().get(0);
            }//end
        }
        return null;
    }

    protected Activity getActivity(){
        return null;
    }

    protected ListView getListView(){
        return null;
    }

}
