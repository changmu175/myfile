package com.xdja.presenter_mainframe.ui;

import android.graphics.drawable.Drawable;
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

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * <p>Summary:业务Activity的View基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.ui.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:32</p>
 */
public class ActivityView<T extends Command> extends ActivitySuperView<T> {

    protected Toolbar toolbar;

    public static final int CONTENT_INSET_START = 15;
    public static final int CONTENT_INSET_END = 0;
    public static final int LEFT = 0;
    public static final int TOP = 5;
    public static final int RIGHT = 36;
    public static final int BOTTOM = 60;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        if (getView() != null)
            toolbar = ButterKnife.findById(getView(), R.id.toolbar);
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
                    if (title != null) {
                        TextPaint tp = title.getPaint();
                        if (tp != null) tp.setFakeBoldText(true);
                    }
                    ImageView back = ButterKnife.findById(view, R.id.main_logo);
                    back.setImageResource(R.drawable.af_abs_ic_back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    String titleStr = getTitleStr();
                    if (titleStr.contains(getActivity().getString(R.string.title_mainFrame))) {
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
                    toolbar.setNavigationIcon(R.mipmap.af_abs_ic_back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishActivity();
                        }
                    });
                    break;
                case ToolbarDef.NAVIGATE_DEFAULT:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                    toolbar.setContentInsetsRelative(CONTENT_INSET_START, CONTENT_INSET_END);

                    View view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_title, null);
                    ImageView logo = ButterKnife.findById(view, R.id.main_logo);
                    TextView title = ButterKnife.findById(view, R.id.main_title);
                    if (title != null) {
                        TextPaint tp = title.getPaint();
                        if (tp != null) tp.setFakeBoldText(true);
                    }
                    logo.setImageResource(R.mipmap.af_abs_ic_logo);
					/*[S]modify by tangsha@20161011 for multi language*/
                    String titleStr = getTitleStr();
					/*[E]modify by tangsha@20161011 for multi language*/
                    if (titleStr.contains(getStringRes(R.string.title_mainFrame))) {
//                        if (titleStr.lastIndexOf("+") == titleStr.length() - 1) {
//                            title.setText(titleStr.substring(0, titleStr.length() - 1));
//                            Drawable plusDrawable = getDrawableRes(R.drawable.actionbar_plus);
//                            plusDrawable.setBounds(0, 5, 24, 53);
//                            title.setCompoundDrawables(null, null, plusDrawable, null);
//                        }
//                        title.setText(TextUtil.getActomaText(getActivity(),
//                                TextUtil.ActomaImage.IMAGE_TITLE, 0, 36, 60, titleStr));
                        title.setText(TextUtil.getActomaText(getActivity(),
                                TextUtil.ActomaImage.IMAGE_TITLE, 0, 0, 0, titleStr));
                    } else {
                        title.setText(titleStr);
                    }
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(view);
                    break;
                case ToolbarDef.NAVIGATE_OTHER:
                    getActivity().setTitle(getTitleStr());
                    toolbar.setNavigationIcon(R.drawable.af_abs_ic_back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    //反射获取标准toolbar中的标题控件
                    try {
                        Field mTitleTextView = toolbar.getClass().getDeclaredField("mTitleTextView");
                        if (mTitleTextView != null) {
                            mTitleTextView.setAccessible(true);
                            TextView titleView = (TextView) mTitleTextView.get(toolbar);
                            if (titleView != null) {
                                Drawable drawable = getDrawableRes(R.mipmap.at_title);
                                if(drawable != null){
                                    drawable.setBounds(LEFT, TOP, RIGHT, BOTTOM);
                                    titleView.setCompoundDrawables(null, null, drawable, null);
                                    toolbar.invalidate();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void finishActivity() {
        getActivity().finish();

    }

    /*[S]modify by tangsha@20161011 for multi language*/
    public String getTitleStr(){
        return getActivity().getTitle().toString();
    }
    /*[E]modify by tangsha@20161011 for multi language*/
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
}
