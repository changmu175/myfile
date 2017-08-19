package com.xdja.presenter_mainframe.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.DeviceInfoBean;

import java.util.List;

/**
 * Created by ldy on 16/5/3.
 */
public class DeviceManagerAdapter extends BaseAdapter {
    private List<DeviceInfoBean> deviceInfoBeanList;
    private Context context;

    public DeviceManagerAdapter(List<DeviceInfoBean> deviceInfoBeanList, Context context) {
        this.deviceInfoBeanList = deviceInfoBeanList;
        this.context = context;
    }

    public void setItems(List<DeviceInfoBean> deviceInfoBeanList){
        this.deviceInfoBeanList = deviceInfoBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (deviceInfoBeanList == null) {
            return 0;
        } else {
            return deviceInfoBeanList.size();
        }
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Object getItem(int position) {
        if (deviceInfoBeanList == null) {
            return null;
        } else {
            return deviceInfoBeanList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (deviceInfoBeanList == null) {
            return -1;
        } else {
            return position;
        }
    }

    //[s]modify by xienana for bug 5391 @20161031 review by [review by tangsha]
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device_manager, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.deviceStatus = (TextView) convertView.findViewById(R.id.device_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DeviceInfoBean deviceInfoBean = deviceInfoBeanList.get(position);
        viewHolder.deviceName.setText(deviceInfoBean.getDeviceName());
        viewHolder.deviceStatus.setText(deviceInfoBean.getStatus());
        return convertView;
    }

    public class ViewHolder {
        TextView deviceName;
        TextView deviceStatus;
    }
    //[e]modify by xienana for bug 5391 @20161031 review by [review by tangsha]
}
