package com.xdja.imp.frame.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.frame.mvp.view.AdapterVu;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.hysel.picker.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/6/25</p>
 * <p>Time:11:36</p>
 */
public abstract class BasePresenterAdapter<P extends Command,D> extends BaseAdapter {

    private AdapterVu<P,D> vu;

    private LayoutInflater inflater;

    private Activity activity;

    public AdapterVu<P,D> getVu(){
        return this.vu;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            try {
                vu = getVuClass().newInstance();
                vu.setCommand(getCommand());
                vu.setActivity(this.activity);
                vu.init(inflater, parent);
                convertView = vu.getView();
                convertView.setTag(vu);
                vu.onViewCreated();
            } catch (IllegalAccessException ex) {
                LogUtil.getUtils().e(ex.getMessage());
            } catch (InstantiationException ex) {
                LogUtil.getUtils().e(ex.getMessage());
            }
        } else {
            vu = ((AdapterVu<P,D>) convertView.getTag());
            vu.onViewReused();
        }

        vu.bindDataSource(position,getDataSource(position));

        return convertView;
    }
    /**
     * 根据位置获取数据源
     *
     * @param position 位置
     * @return 对应的数据源
     */
    protected abstract D getDataSource(int position);

    @NonNull
    protected abstract Class<? extends AdapterVu<P,D>> getVuClass();

    @NonNull
    protected abstract P getCommand();
}
