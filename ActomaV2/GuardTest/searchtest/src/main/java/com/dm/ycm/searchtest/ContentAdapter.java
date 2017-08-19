package com.dm.ycm.searchtest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ycm on 2017/4/18.
 */

public class ContentAdapter extends BaseAdapter {
    private List<String> dataSource;
    private ListView listView;
    private Context context;

    ContentAdapter(Context context) {
        this.context = context;
    }

    public List<String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return dataSource == null ? 0 : dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource == null ? null :dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item, null);
            HolderView holderView = new HolderView(convertView);
            holderView.textView.setText(dataSource.get(position));
            convertView.setTag(holderView);
//        }
        return convertView;
    }

    private class HolderView {
        private TextView textView;
        HolderView(View view) {
            if (view != null) {
                textView = (TextView) view.findViewById(R.id.item);
            }
        }
    }
}
