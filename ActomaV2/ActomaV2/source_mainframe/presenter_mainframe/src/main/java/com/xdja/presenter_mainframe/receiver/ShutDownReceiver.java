package com.xdja.presenter_mainframe.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.frame.data.excutor.ThreadExecutorImp;
import com.xdja.frame.data.excutor.UIThreadImp;
import com.xdja.frame.domain.usecase.UseCase;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.service.UpdateService;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by geyao on 2015/8/31.
 * 关机或重启修改第三方应用加密开关状态为关闭状态
 */
public class ShutDownReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        SDReceiverToDoUseCase useCase = new SDReceiverToDoUseCase(context, intent);
        useCase.execute(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    /**
     * 用于处理开机关机重启广播业务的异步任务
     */
    class SDReceiverToDoUseCase extends UseCase<Object> {
        private Context context;
        private Intent intent;

        public SDReceiverToDoUseCase(Context context, Intent intent) {
            super(new ThreadExecutorImp(), new UIThreadImp());
            this.context = context;
            this.intent = intent;
        }

        @Override
        public Observable<Object> buildUseCaseObservable() {
            //修改数据库内第三方应用加密服务的值
            SettingServer.deleteSetting(SettingBean.SEVER);
            // [Start] Modify by LiXiaolong<mallTo: lxl@xdja.com> on 2016-08-15. Fix bug 2769. Review by Wangchao1.
            String account = ContactUtils.getCurrentAccount();
            if(!ObjectUtil.stringIsEmpty(account)){
                //联系人清空开启加解密通道的对象
                new EncryptRecordService(context).closeSafeTransfer();
            }
            // [End] Modify by LiXiaolong<mallTo: lxl@xdja.com> on 2016-08-15. Fix bug 2769. Review by Wangchao1.
            //修改外部调用第三方加密服务值
//            StateParams.getStateParams().setIsSeverOpen(true);
            //修改外部加解密对象安通账号
            StateParams.getStateParams().setEncryptAccount(null);
            //修改第三方加解密模块所需第三方加密服务是否开启值
            //TODO gbc
            //ActomaApp.getActomaApp().getAccountInfo().setIsEncryptSeverOpen(String.valueOf(true));
            //修改第三方加解密模块所需加解密对象安通账号
            //TODO gbc
            //ActomaApp.getActomaApp().getAccountInfo().setEncryptAccount(null);
            LogUtil.getUtils().i("接收到关机或重启或开机广播--已修改第三方加密相关设置--" + intent.getAction());
            //如果接收到开机广播，就进行自启定时更新功能
            // [S] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3670.
            if (intent != null && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // [E] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3670.
                // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
                //启动自动更新服务
                Intent intent1 = new Intent(context, UpdateService.class);
                intent.setAction(AutoUpdate.ACTION_CHECK_UPDATE);
                context.startService(intent1);
                // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
            }
            return Observable.just(null);
        }
    }
}
