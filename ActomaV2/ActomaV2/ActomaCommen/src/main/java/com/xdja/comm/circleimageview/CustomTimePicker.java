package com.xdja.comm.circleimageview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xdja.comm.R;
import com.xdja.dependence.uitls.LogUtil;

import java.util.Date;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.circleimageview</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/17</p>
 * <p>Time:16:58</p>
 */
public class CustomTimePicker extends AlertDialog {

    private final View view;

    private final TimePicker timePicker;

    private final TextView positiveButton;

    private AlertDialog dialog;

    private final AlertDialog.Builder builder;

    private final TimerPickerCallBack callBack;

    private int t_hourOfDay;
    private int t_minute;

    @SuppressLint("InflateParams")
    public CustomTimePicker(Context context, final TimerPickerCallBack callBack) {
        super(context);
        this.callBack = callBack;
        this.builder = new AlertDialog.Builder(context);
        this.view = LayoutInflater.from(getContext()).inflate(R.layout.view_timepicker, null);
        this.timePicker = ((TimePicker) view.findViewById(R.id.timepicker));
        this.positiveButton = ((TextView) view.findViewById(R.id.dialog_ok));

        this.positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callBack != null) {
                    callBack.onTimeSelected(t_hourOfDay, t_minute);
                }
            }
        });
    }

    /**
     * 初始化时间选择对话框
     *
     * @param currentHour 要设置的小时数，如果设置为当前则为负数即可
     * @param currentMin  要设置的分钟数，如果设置为当前则为负数即可
     * @return 对话框对象
     */
    public AlertDialog createView(int currentHour, int currentMin) {
        if (dialog == null) {
            builder.setView(view);
            builder.setCancelable(true);
            dialog = builder.create();
        }
        if (currentHour < 0 || currentMin < 0) {
            Date dNow = new Date(System.currentTimeMillis());
            t_hourOfDay = dNow.getHours();
            t_minute = dNow.getMinutes();
        } else {
            t_hourOfDay = currentHour;
            t_minute = currentMin;
            LogUtil.getUtils().i("传入的时间 ： " + currentHour + "=======" + currentMin);
            timePicker.setCurrentHour(t_hourOfDay);
            timePicker.setCurrentMinute(t_minute);
            LogUtil.getUtils().i("设置的时间 ： " + t_hourOfDay + "=======" + t_minute);
        }
        LogUtil.getUtils().i("初始化时间------HourOfDay : " + t_hourOfDay);
        LogUtil.getUtils().i("初始化时间------Minute : " + t_minute);

        this.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                selectedTime
                LogUtil.getUtils().i("hourOfDay : " + hourOfDay);
                LogUtil.getUtils().i("minute:" + minute);
                t_hourOfDay = hourOfDay;
                t_minute = minute;
            }
        });

        return dialog;
    }

    /**
     * 显示时间选择对话框
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface TimerPickerCallBack {
        /**
         * 时间选择完成后的回调句柄
         *
         * @param hourOfDay 选择的小时
         * @param minute    选择的分钟
         */
        void onTimeSelected(int hourOfDay, int minute);
    }
}
