package com.xdja.contact.presenter.adapter;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xdja.comm.event.BusProvider;

/**
 * Created by guoyaxin on 2015/11/17.
 */
public abstract class  ArcBaseAdapter extends BaseAdapter {

    protected ListView listView;
    public void setListView(ListView listView){
        this.listView=listView;
    }

    protected void setCenter(final View view) {
        if (listView==null)
            return;
        Rect listRect=new Rect();
        listView.getGlobalVisibleRect(listRect);

        Point center = new Point();
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        int height=view.getHeight();
        int deltaTop=rect.bottom-height;

        boolean isTop= deltaTop<listRect.top;

        if (isTop){
            center.y=rect.bottom-height/2;
        }else{
            center.y=rect.top+height/2;
        }
        center.x=rect.centerX();
        BusProvider.getMainProvider().post(center);

    }
}
