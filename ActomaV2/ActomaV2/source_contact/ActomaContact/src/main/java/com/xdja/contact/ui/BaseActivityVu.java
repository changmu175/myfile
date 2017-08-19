package com.xdja.contact.ui;

import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.uitl.TextUtil;
import com.xdja.contact.R;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;

import butterknife.ButterKnife;

/**
 * <p>Summary:业务Activity的View基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.ui.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:32</p>
 */
public class BaseActivityVu<T extends Command> extends ActivitySuperView<T> {

    protected Toolbar toolbar;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        if (getView() != null && getToolBarId() > 0) {
            toolbar = ButterKnife.findById(getView(), getToolBarId());
        }
        if(getCustomView(inflater, container) != null){
            toolbar.setContentInsetsRelative(15, 0);
            toolbar.addView(getCustomView(inflater, container));
        }
    }

    /**
     * 左上角返回按钮回调
     */
    public void onNavigateBackPressed(){

    }

    @Override
    public void onCreated() {
        super.onCreated();
        if (toolbar != null) {
            //设置toolbar显示
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            switch (getToolbarType()) {
                //处理导航后退
                case ToolbarDef.NAVIGATE_CUSTOM_BACK: {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                    toolbar.setContentInsetsRelative(15, 0);
                    View view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_tool_bar, null);
                    TextView title = ButterKnife.findById(view, R.id.main_title);
                    title.setSingleLine();
                    title.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    ImageView back = ButterKnife.findById(view, R.id.main_logo);
                    back.setImageResource(R.drawable.af_abs_ic_back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    String titleStr = getTitleStr();
                    if (title != null) {
                        TextPaint tp = title.getPaint();
                        if (tp != null) tp.setFakeBoldText(true);
                    }
                    if (titleStr.contains(getActivity().getString(R.string.title_app_name))) {
                        title.setText(TextUtil.getActomaText(getActivity(),
                                TextUtil.ActomaImage.IMAGE_TITLE, 0, 0, 0, titleStr));
                    } else {
                        title.setText(titleStr);
                    }
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(view);
                    break;
                }
                case ToolbarDef.NAVIGATE_BACK:
                    getActivity().setTitle(getTitleStr());
                    toolbar.setNavigationIcon(com.xdja.contact.R.drawable.back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNavigateBackPressed();
                            getActivity().finish();
                        }
                    });
                    break;
                case ToolbarDef.NAVIGATE_DEFAULT:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                    toolbar.setContentInsetsRelative(15,0);

                    View view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_title, null);
                    ImageView logo = ButterKnife.findById(view, R.id.main_logo);
                    TextView title = ButterKnife.findById(view, R.id.main_title);

                    logo.setImageResource(R.drawable.af_abs_ic_logo);
                    /*[S]modify by tangsha@20161013 for multi language*/
                    title.setText(getTitleStr());
                    /*[E]modify by tangsha@20161013 for multi language*/
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(view);
                    break;
                case ToolbarDef.NAVIGATE_OTHER:
                    getActivity().setTitle(getTitleStr());
                    break;
                default:
                    break;
            }
        }
    }

    protected int getToolBarId(){
        return 0;
    }

    protected View getCustomView(LayoutInflater inflater, ViewGroup container){
        return null;
    };

    /**
     * 获得导航显示方式
     *
     * @return 显示方式
     */
    @ToolbarDef.NavigateType
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_DEFAULT;
    }

    public static class ToolbarDef {
        /**
         * 导航显示为后退
         */
        public final static int NAVIGATE_BACK = 0;
        /**
         * 默认导航
         */
        public final static int NAVIGATE_DEFAULT = 1;
        /**
         * 未知，留待扩展
         */
        public final static int NAVIGATE_OTHER = 2;
        /**
         * 自定义的带返回键和一个标题和一个菜单的导航
         */
        public final static int NAVIGATE_CUSTOM_BACK = 3;

        /**
         * 导航显示类型
         */
        @IntDef({NAVIGATE_BACK, NAVIGATE_DEFAULT, NAVIGATE_OTHER, NAVIGATE_CUSTOM_BACK})
        public @interface NavigateType {
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    public String getTitleStr(){
        return getActivity().getTitle().toString();
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
