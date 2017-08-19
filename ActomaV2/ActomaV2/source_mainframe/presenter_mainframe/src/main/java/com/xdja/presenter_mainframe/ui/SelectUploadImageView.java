package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.SelectUploadImageBean;
import com.xdja.presenter_mainframe.cmd.SelectUploadImageCommand;
import com.xdja.presenter_mainframe.presenter.adapter.SelectUploadImageAdapter;
import com.xdja.presenter_mainframe.ui.uiInterface.SelectUploadImageVu;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ALH on 2016/8/12.
 */
@ContentView(R.layout.activity_view_select_upload_image)
public class SelectUploadImageView extends ActivityView<SelectUploadImageCommand> implements SelectUploadImageVu {
    /**
     * 图片列表
     */
    @Bind(R.id.select_upload_image_gridview)
    GridView selectUploadImageGridview;
    /**
     * 确定按钮
     */
    @Bind(R.id.select_upload_image_button)
    Button selectUploadImageButton;

    @Override
    public void initGridView(final List<SelectUploadImageBean> list, final SelectUploadImageAdapter adapter) {
        //设置列表适配器
        selectUploadImageGridview.setAdapter(adapter);
        //设置列表item单击事件
        selectUploadImageGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCommand().clickItem(list, position, adapter);
            }
        });
    }

    /**
     * 是否显示确定按钮
     *
     * @param isShow 是否显示
     */
    @Override
    public void isShowBtn(boolean isShow) {
        selectUploadImageButton.setEnabled(isShow);
    }

    /**
     * 点击确认按钮
     */
    @OnClick(R.id.select_upload_image_button)
    public void clickButton() {
        getCommand().clickBtn();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_select_upload_images);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
