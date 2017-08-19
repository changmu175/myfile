package com.xdja.domain_mainframe.usecase.settings;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.SettingServer;
import com.xdja.domain_mainframe.R;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by chenbing on 2015/10/16.
 * 勿扰模式
 */
public class GetNoDistrubSettingUseCase extends Ext1UseCase<Context,GetNoDistrubSettingUseCase.NoDistrubBean> {
    /**
     * 上下文句柄
     */
    private Context context;

    @Inject
    public GetNoDistrubSettingUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable<NoDistrubBean> buildUseCaseObservable() {
        context = p;
        return Observable.just(context).flatMap(new Func1<Context, Observable<NoDistrubBean>>() {
            @Override
            public Observable<NoDistrubBean> call(Context context) {
                String value = SettingServer.querySetting(SettingBean.NODISTRUB).getValue();
                NoDistrubBean noDistrubBean = null;
                if (!TextUtils.isEmpty(value)) {
                    noDistrubBean = new Gson().fromJson(value, NoDistrubBean.class);
                }
                return Observable.just(noDistrubBean);
            }
        });
    }

    public static class NoDistrubBean {
        /**
         * 勿扰模式是否开启
         */
        private boolean isOpen;
        /**
         * 开始时间小时数
         */
        private int beginHour = 23;
        /**
         * 开始时间分钟数
         */
        private int beginMinu = 0;
        /**
         * 结束小时数
         */
        private int endHour = 8;
        /**
         * 结束分钟数
         */
        private int endMinu = 0;

        public boolean isOpen() {
            return isOpen;
        }

        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

        public int getBeginHour() {
            return beginHour;
        }

        public void setBeginHour(int beginHour) {
            this.beginHour = beginHour;
        }

        public int getBeginMinu() {
            return beginMinu;
        }

        public void setBeginMinu(int beginMinu) {
            this.beginMinu = beginMinu;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMinu() {
            return endMinu;
        }

        public void setEndMinu(int endMinu) {
            this.endMinu = endMinu;
        }

        /**
         * 获取开始时间显示的内容
         */
        public String getBeginTime() {
            StringBuffer stringBuffer = new StringBuffer();

            //晚上 18：00 - 23：59
            if (this.beginHour >= 18) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.night));
                int t_hour = this.beginHour - 12;
                stringBuffer.append(t_hour);
            }

            //下午 13：00 - 17：59
            else if (this.beginHour >= 13) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.pm));
                int t_hour = beginHour - 12;
                stringBuffer.append(t_hour);
            }

            //中午 12：00 - 12：59
            else if (this.beginHour == 12) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.noon));
                int t_hour = 12;
                stringBuffer.append(t_hour);
            }

            //上午 06：00 - 11：59
            else if (this.beginHour >= 6) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.am));
                if (this.beginHour < 10) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(this.beginHour);
            }

            //凌晨 00：00 - 05：59
            else {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.dawn));
                stringBuffer.append("0" + this.beginHour);
            }

            stringBuffer.append(":");
            if (this.beginMinu > 9) {
                stringBuffer.append(this.beginMinu);
            } else {
                stringBuffer.append("0" + this.beginMinu);
            }
            return stringBuffer.toString();
        }

        /**
         * 获取结束时间显示的内容
         */
        public String getEndTime() {
            StringBuffer stringBuffer = new StringBuffer();

            //晚上 18：00 - 23：59
            if (this.endHour >= 18) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.night));
                int t_hour = this.endHour - 12;
                stringBuffer.append(t_hour);
            }

            //下午 13：00 - 17：59
            else if (this.endHour >= 13) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.pm));
                int t_hour = endHour - 12;
                stringBuffer.append(t_hour);
            }

            //中午 12：00 - 12：59
            else if (this.endHour == 12) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.noon));
                int t_hour = 12;
                stringBuffer.append(t_hour);
            }

            //上午 06：00 - 11：59
            else if (this.endHour >= 6) {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.am));
                if (this.endHour < 10) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(this.endHour);
            }

            //凌晨 00：00 - 05：59
            else {
                stringBuffer.append(ActomaController.getApp().getResources().getString(R.string.dawn));
                stringBuffer.append("0" + this.endHour);
            }

            stringBuffer.append(":");
            if (this.endMinu > 9) {
                stringBuffer.append(this.endMinu);
            } else {
                stringBuffer.append("0" + this.endMinu);
            }
            return stringBuffer.toString();
        }
    }
}
