package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.INewsRemindCommand;
import com.xdja.imp.ui.vu.INewsRemindVu;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/12/7.
 */
public class NewsRemindVu extends ImpActivitySuperView<INewsRemindCommand> implements INewsRemindVu,View.OnClickListener{

    private CheckBox newsRemindCheckbox;

    private CheckBox newsRemindRingCheckbox;

    private CheckBox newsRemindShakeCheckbox;

    private LinearLayout newsRemindRingShakeLayout;

    private RelativeLayout newRemindRelayout;

    private RelativeLayout newRemindRingRelayout;

    private RelativeLayout newRemindShakeRelayout;

    @Override
    public void init(@NonNull LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        newsRemindCheckbox = ButterKnife.findById(getView(), R.id.news_remind_checkbox);
        newsRemindRingCheckbox = ButterKnife.findById(getView(), R.id.news_remind_ring_checkbox);
        newsRemindShakeCheckbox = ButterKnife.findById(getView(), R.id.news_remind_shake_checkbox);
        newsRemindRingShakeLayout = ButterKnife.findById(getView(), R.id.news_remind_ring_shake_layout);

        newRemindRelayout = ButterKnife.findById(getView(), R.id.news_remind);
        newRemindRingRelayout = ButterKnife.findById(getView(), R.id.news_remind_ring);
        newRemindShakeRelayout = ButterKnife.findById(getView(), R.id.news_remind_shake);

        initListener();
    }

    private void initListener(){
        newRemindRelayout.setOnClickListener(this);
        newRemindRingRelayout.setOnClickListener(this);
        newRemindShakeRelayout.setOnClickListener(this);
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_news_remind;
    }

    /**
     * 设置新消息通知是否开启
     *
     * @param isOn 是否开启
     */
    @Override
    public void setNewsRemind(boolean isOn) {
        if (!isOn) {
            newsRemindRingShakeLayout.setVisibility(View.GONE);
            newsRemindRingShakeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        }
        newsRemindCheckbox.setChecked(isOn);
    }

    @Override
    public void setNewsRemindByRing(boolean isOn) {
        newsRemindRingCheckbox.setChecked(isOn);
    }

    @Override
    public void setNewsRemindByShake(boolean isOn) {
        newsRemindShakeCheckbox.setChecked(isOn);
    }

    @Override
    public void onClick(View view) {
        if(view == newRemindRelayout){
            newsRemindCheckbox.setChecked(!newsRemindCheckbox.isChecked());
            //如果上边接收通知总开关关闭，下部跟着关闭
            if (!newsRemindCheckbox.isChecked()) {
                newsRemindRingShakeLayout.setVisibility(View.GONE);
                newsRemindRingShakeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
            } else {//如果总开关开启，下部按钮恢复上次选中的状态
                newsRemindRingShakeLayout.setVisibility(View.VISIBLE);
                newsRemindRingShakeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_withgroup));
            }
            getCommand().newsRemind(newsRemindCheckbox.isChecked());
        }else if(view == newRemindRingRelayout ){
            newsRemindRingCheckbox.setChecked(!newsRemindRingCheckbox.isChecked());
            getCommand().newsRemindByRing(newsRemindRingCheckbox.isChecked());
        }else{
            newsRemindShakeCheckbox.setChecked(!newsRemindShakeCheckbox.isChecked());
            getCommand().newsRemindByShake(newsRemindShakeCheckbox.isChecked());
        }
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.setting_news_remind);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
