package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;


import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.NewsRemindCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.NewsRemindVu;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenbing on 2015/7/7.
 */
@ContentView(R.layout.activity_news_remind)
public class ViewNewsRemind extends ActivityView<NewsRemindCommand> implements NewsRemindVu {
    /**
     * 新消息通知单选框
     */
    @Bind(R.id.news_remind_checkbox)
    CheckBox newsRemindCheckbox;
    /**
     * 新消息通知-声音单选框
     */
    @Bind(R.id.news_remind_ring_checkbox)
    CheckBox newsRemindRingCheckbox;
    /**
     * 新消息通知-振动单选框
     */
    @Bind(R.id.news_remind_shake_checkbox)
    CheckBox newsRemindShakeCheckbox;
    /**
     * 声音及振动所在布局
     */
    @Bind(R.id.news_remind_ring_shake_layout)
    LinearLayout newsRemindRingShakeLayout;



    /**
     * 设置新消息通知是否开启
     *
     * @param isOn 是否开启
     */
    @Override
    public void setNewsRemind(boolean isOn) {
        if (isOn == false) {
            newsRemindRingShakeLayout.setVisibility(View.GONE);
            newsRemindRingShakeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        }
        newsRemindCheckbox.setChecked(isOn);
    }

    /**
     * 设置新消息通知-声音是否开启
     *
     * @param isOn 是否开启
     */
    @Override
    public void setNewsRemindByRing(boolean isOn) {
        newsRemindRingCheckbox.setChecked(isOn);
    }

    /**
     * 设置新消息通知-振动是否开启
     *
     * @param isOn 是否开启
     */
    @Override
    public void setNewsRemindByShake(boolean isOn) {
        newsRemindShakeCheckbox.setChecked(isOn);
    }

    /**
     * 点击新消息通知单选框
     */
    @OnClick(R.id.news_remind)
    public void newsRemindClick() {
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
    }

    /**
     * 点击新消息通知-声音单选框
     */
    @OnClick(R.id.news_remind_ring)
    public void newsRemindRingClick() {
        newsRemindRingCheckbox.setChecked(!newsRemindRingCheckbox.isChecked());
        getCommand().newsRemindByRing(newsRemindRingCheckbox.isChecked());
    }

    /**
     * 点击新消息通知-振动单选框
     */
    @OnClick(R.id.news_remind_shake)
    public void newsRemindShakeClick() {
        newsRemindShakeCheckbox.setChecked(!newsRemindShakeCheckbox.isChecked());
        getCommand().newsRemindByShake(newsRemindShakeCheckbox.isChecked());
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
