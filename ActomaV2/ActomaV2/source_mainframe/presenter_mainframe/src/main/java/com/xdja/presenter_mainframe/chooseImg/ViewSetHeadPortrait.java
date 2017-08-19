package com.xdja.presenter_mainframe.chooseImg;


import android.view.View;
import android.widget.AdapterView;

import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.comm.uitl.ImageLoader;
import com.xdja.comm.widget.LazyLoadGridView;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.ui.ActivityView;

import butterknife.Bind;


/**
 * Created by geyao
 * 设置头像
 */
@ContentView(value = R.layout.activity_view_set_head_portrait)
public class ViewSetHeadPortrait extends ActivityView<SetHeadPortraitCommand> implements SetHeadPortraitVu {

    /**
     * 列表
     */
    @Bind(R.id.setheadportrait_gridview)
    LazyLoadGridView setheadportraitGridview;

    @Bind(R.id.set_head_root)
    View pageRoot;

    /**
     * 设置头像列表适配器
     *
     * @param adapter 设置头像列表适配器
     */
    @Override
    public void setGridAdapter(SetHeadPortraitAdapter adapter) {
        //设置列表适配器
        setheadportraitGridview.setAdapter(adapter);
        //设置列表适配器item点击监听
        setheadportraitGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCommand().setGridItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (setheadportraitGridview != null) {
            setheadportraitGridview.setAdapter(null);
            setheadportraitGridview = null;
        }
        GcMemoryUtil.clearMemory(pageRoot);
        ImageLoader.getInstance().clearCache();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_set_head_portrait);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
