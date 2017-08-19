package com.xdja.imp.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IChatPicPreviewCommand;
import com.xdja.imp.ui.vu.IChatPicPreviewVu;

/**
 * Created by guorong on 2016/7/6.
 */
public class ChatPicPreviewVu extends ImpActivitySuperView<IChatPicPreviewCommand> implements IChatPicPreviewVu{

    private ViewPager viewPager;
    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.imageshower;
    }

    @Override
    protected void injectView() {
        View view = getView();
        if(view != null){
            viewPager = (ViewPager) view.findViewById(R.id.imageShowerViewPager);
        }
    }

    @Override
    public void initViewPager(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setCurrentItem(int item) {
        viewPager.setCurrentItem(item);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.picture_preview);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
