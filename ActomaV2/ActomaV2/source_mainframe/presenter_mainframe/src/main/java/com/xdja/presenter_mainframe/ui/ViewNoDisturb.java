package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CustomTimePicker;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.usecase.settings.GetNoDistrubSettingUseCase;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.NoDisturbCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.NoDisturbVu;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenbing on 2015/10/16.
 */
@ContentView(R.layout.activity_no_distrub)
public class ViewNoDisturb extends ActivityView<NoDisturbCommand> implements NoDisturbVu {
    /**
     * 勿扰模式单选框
     */
    @Bind(R.id.no_distrub_checkbox)
    CheckBox noDistrubCheckbox;
    /**
     * 开始时间 显示框
     */
    @Bind(R.id.nodistrub_begin_text)
    TextView nodistrubBeginText;

    /**
     * 结束时间
     */
    @Bind(R.id.nodistrub_end_text)
    TextView nodistrubEndText;

    /**
     * 下部 开始时间和结束时间 布局
     */
    @Bind(R.id.nodistrub_time_layout)
    LinearLayout nodistrubTimeLayout;
    @Bind(R.id.nodistrub_begin_container)
    RelativeLayout nodistrubBeginContainer;
    @Bind(R.id.nodistrub_end_container)
    RelativeLayout nodistrubEndContainer;
    @Bind(R.id.nodistrub_total)
    RelativeLayout nodistrubTotal;


    /**
     * 根据配置设置开始时间
     *
     * @param beginTime 开始时间
     */
    @Override
    public void setBeginTime(String beginTime) {
        nodistrubBeginText.setText(beginTime);
    }

    /**
     * 根据配置设置结束时间
     *
     * @param endTime 结束时间
     */
    @Override
    public void setEndTime(String endTime) {
        nodistrubEndText.setText(endTime);
    }


    /**
     * 根据配置设置勿扰模式是否开启
     *
     * @param isOn 是否开启
     */
    @Override
    public void setNoDistrub(boolean isOn) {
        if (!isOn) {
            nodistrubTimeLayout.setVisibility(View.GONE);
            nodistrubTimeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        }else{
            nodistrubTimeLayout.setVisibility(View.VISIBLE);
        }
        noDistrubCheckbox.setChecked(isOn);
    }


    /**
     * 勿扰模式单选框点击事件
     */
    @OnClick(R.id.nodistrub_total)
    public void newsRemindClick() {
        noDistrubCheckbox.setChecked(!noDistrubCheckbox.isChecked());
        //如果上边勿扰模式总开关关闭，下部跟着关闭
        if (!noDistrubCheckbox.isChecked()) {
            nodistrubTimeLayout.setVisibility(View.GONE);
            nodistrubTimeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_nogroup));
        } else {//如果总开关开启，下部按钮恢复上次选中的状态
            nodistrubTimeLayout.setVisibility(View.VISIBLE);
            nodistrubTimeLayout.setBackgroundColor(getColorRes(R.color.listview_bg_withgroup));
        }

        getCommand().setNoDistrubOpen(noDistrubCheckbox.isChecked());
        getCommand().saveNoDisturbSettng();
    }

    /**
     * 勿扰模式 开始时间点击事件
     */
    @OnClick(R.id.nodistrub_begin_container)
    public void newsRemindRingClick() {
        int t_hour = -1;
        int t_minu = -1;
        GetNoDistrubSettingUseCase.NoDistrubBean nodistrubBean = getCommand().getNodistrubBean();
        if (nodistrubBean != null) {
            t_hour = nodistrubBean.getBeginHour();
            t_minu = nodistrubBean.getBeginMinu();
        }
        new CustomTimePicker(getContext(), new CustomTimePicker.TimerPickerCallBack() {
            @Override
            public void onTimeSelected(int hourOfDay, int minute) {
                LogUtil.getUtils().i("设置完成 === 小时数 ： " + hourOfDay);
                LogUtil.getUtils().i("设置完成 === 分钟数 ： " + minute);
                getCommand().setNoDistrubBeginTime(hourOfDay, minute);
                setBeginTime(getCommand().getNodistrubBean().getBeginTime());

                getCommand().saveNoDisturbSettng();
            }
        }).createView(t_hour, t_minu).show();
    }

    /**
     * 勿扰模式 结束时间点击事件
     */
    @OnClick(R.id.nodistrub_end_container)
    public void newsRemindShakeClick() {
        int t_hour = -1;
        int t_minu = -1;
        GetNoDistrubSettingUseCase.NoDistrubBean nodistrubBean = getCommand().getNodistrubBean();
        if (nodistrubBean != null) {
            t_hour = nodistrubBean.getEndHour();
            t_minu = nodistrubBean.getEndMinu();
        }
        new CustomTimePicker(getContext(), new CustomTimePicker.TimerPickerCallBack() {
            @Override
            public void onTimeSelected(int hourOfDay, int minute) {
                LogUtil.getUtils().i("设置完成 === 小时数 ： " + hourOfDay);
                LogUtil.getUtils().i("设置完成 === 分钟数 ： " + minute);
                getCommand().setNoDistrubEndTime(hourOfDay, minute);
                setEndTime(getCommand().getNodistrubBean().getEndTime());
                getCommand().saveNoDisturbSettng();
            }
        }).createView(t_hour, t_minu).show();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.nodistrub_top_message);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
