package com.xdja.imp.frame.imp.view;

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
import com.xdja.comm.uitl.TextUtil;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.imp.R;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.frame.imp.view</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:19:19</p>
 */
public class ImpActivitySuperView<T extends Command> extends ActivitySuperView<T> {
    protected Toolbar toolbar;
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
                //alh@xdja.com<mailto://alh@xdja.com> 2017-02-22 add. fix bug 7626 . review by wangchao1. Start
                case ToolbarDef.NAVIGATE_CUSTOM_BACK: {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                    toolbar.setContentInsetsRelative(15, 0);
                    View view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_title, null);
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
                    if (titleStr.contains(getActivity().getString(R.string.anTongStr))) {
                        title.setText(TextUtil.getActomaText(getActivity(),
                                TextUtil.ActomaImage.IMAGE_TITLE, 0, 0, 0, titleStr));
                    } else {
                        title.setText(titleStr);
                    }
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(view);
                    break;
                }
                //alh@xdja.com<mailto://alh@xdja.com> 2017-02-22 add. fix bug 7626 . review by wangchao1. End
                case ToolbarDef.NAVIGATE_BACK:
                    toolbar.setNavigationIcon(R.drawable.af_abs_ic_back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    //[S]modify by lll@xdja.com for add the receiver mode 2016/11/18
                    //TitleBar标题文字省略样式
                    try {
                        Field mTitleTextView = toolbar.getClass().getDeclaredField("mTitleTextView");
                        if (mTitleTextView != null) {
                            mTitleTextView.setAccessible(true);
                            TextView titleView = (TextView) mTitleTextView.get(toolbar);
                            if (titleView != null) {
                                titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                                titleView.setSingleLine();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //[E]modify by lll@xdja.com for add the receiver mode 2016/11/18
                    break;
                case ToolbarDef.NAVIGATE_DEFAULT:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                    toolbar.setContentInsetsRelative(15, 0);

                    View view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_title, null);
                    ImageView logo = ButterKnife.findById(view, R.id.main_logo);
                    TextView title = ButterKnife.findById(view, R.id.main_title);

                    if (title != null) {
                        TextPaint tp = title.getPaint();
                        if (tp != null) tp.setFakeBoldText(true);
                    }

                    logo.setImageResource(R.drawable.af_abs_ic_logo);
                    String titleStr = getTitleStr();
                    if (titleStr.contains(getActivity().getString(R.string.anTongStr))) {
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
                    if (getActivity() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(view);
                    }
                    break;
                case ToolbarDef.NAVIGATE_OTHER:
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
                                Drawable drawable = getDrawableRes(R.drawable.at_title);
                                if(drawable != null){
                                    drawable.setBounds(0, 5, 36, 60);
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
    protected String getTitleStr(){
        return getActivity().getTitle().toString();
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
