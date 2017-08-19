package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.comm.event.BrushTicketEvent;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.autoupdate.RepeatUpdateResultHandle;
import com.xdja.presenter_mainframe.enc3rd.utils.StrategysUtils;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;

import javax.inject.Inject;

/**
 * Created by chenbing on 2015/7/30.
 * 用于接收定时更新的广播
 */
public class UpdateReceiver extends BroadcastReceiver {

    public static String ACTION_TRIGGER_UPDATE = "com.xdja.actoma.singleupdate";

    @Inject
    BusProvider busProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.getUtils().i("UpdateReceiver - onReceive : " + intent);
        if (intent != null) {
            //alh@xdja.com<mailto://alh@xdja.com> 2016-12-21 add. fix bug 7205 . review by wangchao1. Start
            if (busProvider != null) {
                busProvider.post(new BrushTicketEvent());
            }
            //alh@xdja.com<mailto://alh@xdja.com> 2016-12-21 add. fix bug 7205 . review by wangchao1. End
            if (AutoUpdate.ACTION_CHECK_UPDATE_TIMER.equals(intent.getAction())) {
                LogUtil.getUtils().i("UpdateReceiver - 接收到了定时广播");
                LogUtil.getUtils().i("UpdateReceiver - 接收到了定时广播 - 1.开始检测升级和检测是否需要刷新Ticket...");
                // 检测升级
                new AutoUpdate(context, new RepeatUpdateResultHandle()).updateStart();

                if (UniversalUtil.isXposed()) {
                    LogUtil.getUtils().i("UpdateReceiver - 接收到了定时广播 - 2.开始获取策略...");
                    //2.获取策略文件
                    // getStrategyFile(context);
                    //3.初始化支持的第三方应用信息列表
                    // initAppList(context);
                    /*start create by geyao 2015.12.09 第三方加密重构添加定时获取新策略数据*/
                    StrategysUtils.updateStrategys(context);
                    /*end*/
                    LogUtil.getUtils().i("UpdateReceiver - 接收到了定时广播 - 获取策略完成");
                }
                LoginHelper.startBrushTicket();
                LoginHelper.loginCountReport();
            } else if (ACTION_TRIGGER_UPDATE.equals(intent.getAction())) {
                LogUtil.getUtils().i("UpdateReceiver - 接收到了第三方解密时检测升级的广播");
                LogUtil.getUtils().i("UpdateReceiver - 接收到了第三方解密时检测升级的广播 - 开始检测升级...");
                // 检测升级
                new AutoUpdate(context, new RepeatUpdateResultHandle()).updateStart();
            }
        }
    }

    /**
     * 获取策略文件
     */
    /*public void getStrategyFile(Context context) {
        GetStrategyUseCase strategyUseCase = new GetStrategyUseCase(context);
        strategyUseCase.execute(new ActomaUseCase.ActomaSub<File>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(File file) {

                if (file.exists()) {
                    //赋值策略文件
                    try {
                        FileReader fileReader = new FileReader(file);
                        ActomaApp.getActomaApp().getAccountInfo().
                                setAtBean(new Gson().fromJson(fileReader, AtBean.class));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    LogUtil.getUtils().i("自动获取策略文件成功了");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.getUtils().i("自动获取策略文件失败了");

            }
        });
    }*/

    /**
     * 初始化支持的第三方应用信息列表
     */
    /*private void initAppList(Context context) {
        GetSupportAppUseCase getSupportAppUseCase = new GetSupportAppUseCase(context);
        getSupportAppUseCase.execute(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().i("更新支持的第三方应用的信息出错：\r\n" + e.getMessage());
            }

            @Override
            public void onNext(Boolean isNeedNotify) {
                LogUtil.getUtils().i("更新支持的第三方应用结果： " + isNeedNotify);
            }
        });
    }*/
}
