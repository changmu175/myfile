package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.securevoipcommon.VoipFunction;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.ActionUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.DropMessageCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewDropMessage;
import com.xdja.proxy.imp.MxModuleProxyImp;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

/**
 * Created by luopeipei on 2015/11/9.
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class DropMessagePresenter extends PresenterActivity<DropMessageCommand, ViewDropMessage> implements DropMessageCommand{

    private DropMessageReceiver receiver;

    private static boolean openReceiverMode = false;

    @Inject
    @InteractorSpe(DomainConfig.GET_RECEIVERMODE_SETTINGS)
    Lazy<Ext1Interactor<Context,SettingBean[]>> getReceiverModeSettingUseCase;

    @Inject
    @InteractorSpe(DomainConfig.SET_RECEIVERMODE_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[], Boolean[]>> saveReceiverModeSettingUseCase;

    @Override
    protected Class<? extends ViewDropMessage> getVuClass() {
        return ViewDropMessage.class;
        //return ViewDropMessage.class;
    }

    @Override
    protected DropMessageCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        receiver = new DropMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionUtil.ACTION_DROP_MESSAGE);
        registerReceiver(receiver, filter);

        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //读取听筒模式设置
        getReceiverModeSettingUseCase.get().fill(this).execute(new Action1<SettingBean[]>() {
            @Override
            public void call(SettingBean[] settingBeans) {
                for (int i = 0; i < settingBeans.length; i++) {
                    if (settingBeans[i].getKey().equals(SettingBean.RECEIVER_MODE)) {
                        getVu().setReceiverMode(Boolean.valueOf(settingBeans[i].getValue()));
                        openReceiverMode = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.newsRemind = settingBeans[i].getValue();
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        //实例化保存听筒模式设置
        SettingBean bean = new SettingBean();
        bean.setKey(SettingBean.RECEIVER_MODE);
        bean.setValue(String.valueOf(openReceiverMode));

        saveReceiverModeSettingUseCase.get().fill(this, new SettingBean[]{bean})
                .execute(new Action1<Boolean[]>() {
                    @Override
                    public void call(Boolean[] booleen) {
                        //查询听筒模式开关
                        SettingBean receiverMode = SettingServer.querySetting(SettingBean.RECEIVER_MODE);
                        //修改供其他模块调用的新消息通知状态值
                        if (receiverMode != null) {
                            SettingServer.openReceiverMode = receiverMode.getValue();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {

            }

        }
    }

    //进行删除聊天记录
    @Override
    public void dropMessage() {

        getVu().showCommonProgressDialog(getString(R.string.delete_chat_history));
        /*DeleteChatHistoryUseCase useCase = new DeleteChatHistoryUseCase(this);
        useCase.execute(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().e("删除聊天记录操作出错");
                getVu().dismissCommonProgressDialog();
                Toast.makeText(getBaseContext(), "删除聊天记录失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Boolean dropMessageResult) {
                //TODO:此处接收业务收据
                //TODO kgg:该用例只是启动SimcUiService来删除聊天记录，并不能得到删除结果
                //删除聊天记录是否成功
                if(!dropMessageResult){
                    Toast.makeText(getBaseContext(),"删除聊天记录失败！",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        try {
            MxModuleProxyImp.getInstance().dropAllMessages(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除通话记录
    @Override
    public void clearCallLog() {
        getVu().showCommonProgressDialog(getString(R.string.delete_call_history));
        /*DeleteCallHistoryUseCase useCase = new DeleteCallHistoryUseCase(this);
        useCase.execute(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().e("删除通话记录操作出错");
                getVu().dismissCommonProgressDialog();
                Toast.makeText(getBaseContext(), "删除通话记录失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Boolean clearDailLogResult) {
                getVu().dismissCommonProgressDialog();
                //删除通话记录是否成功
                if(!clearDailLogResult){
                    Toast.makeText(getBaseContext(),"删除通话记录失败！",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        try {
            //wxf@xdja.com 2016-08-02 add. fix bug 2141 . review by mengbo. Start
            new AsyncTask<Object,Object,Object>(){

                @SuppressWarnings("ReturnOfNull")
                @Override
                protected Object doInBackground(Object[] params) {
                    VoipFunction.getInstance().clearAllCallLog();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    getVu().dismissCommonProgressDialog();
                    //wxf@xdja.com 2016-08-08 add. fix bug 2510 . review by mengbo. Start
                    Toast.makeText(getBaseContext(),  getString(R.string.delete_call_history_succeed), Toast.LENGTH_SHORT).show();
                    //wxf@xdja.com 2016-08-08 add. fix bug 2510 . review by mengbo. End
                }
            }.execute();
            //wxf@xdja.com 2016-08-02 add. fix bug 2141 . review by mengbo. End
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openReceiverMode(boolean isOn) {
        openReceiverMode = isOn;
        SettingServer.openReceiverMode = String.valueOf(isOn);
    }

    class DropMessageReceiver extends BroadcastReceiver{//接收聊天记录删除结果的广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            if (null != result) {
                getVu().dismissCommonProgressDialog();
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
