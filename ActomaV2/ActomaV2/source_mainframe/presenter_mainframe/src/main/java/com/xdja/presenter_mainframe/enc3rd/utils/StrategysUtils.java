package com.xdja.presenter_mainframe.enc3rd.utils;

import android.content.Context;
import android.os.Build;

import com.xdja.comm.data.QuickOpenAppBean;
import com.xdja.comm.data.QuickOpenThirdAppListBean;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.encrypt.NewStrategyRequestBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.di.components.pre.AppComponent;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.safekeyjar.util.StringResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * 描述当前类的作用
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-17 17:18
 */
public class StrategysUtils {
    /**
     * 日志标签
     */
    private static final String TAG = "StrategysUtils";

    public static void queryQuickOpenApps(final Context context, final QueryQuickOpenAppsCallback callback){
        LogUtil.getUtils(TAG).i(">>> 查询本地策略并返回数据集...");
        queryStrategysData(context, callback);
    }

    public static void queryStrategys(final Context context){
        LogUtil.getUtils(TAG).i(">>> 查询本地策略...");
        queryStrategysData(context, null);
    }

    public static void queryStrategysData(final Context context, final QueryQuickOpenAppsCallback callback){
        AppComponent appComponent = ((ActomaApplication) ActomaApplication.getInstance()).getAppComponent();
        if (appComponent != null) {
            PreUseCaseComponent preUseCase = appComponent.plus(new PreUseCaseModule());
            preUseCase.queryStrategys().fill().execute(new Subscriber<List<EncryptAppBean>>() {
                @Override
                public void onCompleted() {
                    LogUtil.getUtils(TAG).i(">>> queryStrategysData - onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.getUtils(TAG).e(">>> queryStrategysData - onError");
                    e.printStackTrace();
                }

                @Override
                public void onNext(List<EncryptAppBean> encryptAppBeen) {
                    LogUtil.getUtils(TAG).i(">>> queryStrategysData - onNext");
                    List<QuickOpenAppBean> quickOpenApps = new ArrayList<>();
                    if (encryptAppBeen != null && !encryptAppBeen.isEmpty()) {
                        ThirdEncAppUtil.resetList(context, encryptAppBeen);//过滤相关短信的应用
                        List<QuickOpenThirdAppListBean> qotalBean = ListUtil.initQuickOpenThirdAppListData(context, encryptAppBeen);
                        if (qotalBean != null && !qotalBean.isEmpty()) {
                            for (QuickOpenThirdAppListBean b : qotalBean) {
                                quickOpenApps.add(b.getQuickOpenAppBean());
                            }
                            StateParams.getStateParams().setQuickOpenAppBeanList(quickOpenApps);
                        }
                    }

                    if (callback != null) {
                        callback.queryQuickOpenApps(quickOpenApps);
                    }
                }
            });
        }
    }

    public static void updateStrategys(Context context){
        LogUtil.getUtils(TAG).i(">>> 获取服务端策略...");
        //查询上一次更新策略时的策略id
        final SettingBean bean = SettingServer.querySetting(SettingBean.LAST_STRATEGY_ID);
        //默认策略id为0
        int lastStrategyId = 0;
        //若对象不为空证明有上次更新 赋值
        if (bean != null) {
            lastStrategyId = Integer.parseInt(bean.getValue());
        }
        //实例化请求参数对象
        NewStrategyRequestBean requestBean = new NewStrategyRequestBean();
        //协议版本号
        requestBean.setVersion("1.0");
        //批量条数
        requestBean.setBatchSize(10);
        //获取芯片卡号
        StringResult cardId = TFCardManager.getCardId();
        if (cardId == null) {
            return;
        }
        //手机型号
        String mobile = Build.MODEL;
        requestBean.setModel(mobile);
        //厂商信息
        String maufacturer = Build.MANUFACTURER;
        requestBean.setManufacturer(maufacturer);
        //设备芯片卡号
        requestBean.setCardNo(cardId.getResult());
        //最后策略更新ID
        requestBean.setLastStrategyId(lastStrategyId);
        //获取策略数据信息
        onUpdateStrategys(context, requestBean);
        //HookService连接安通+接口
        IEncryptUtils.setAppEncryptSwitch(null);
        //添加设置图片头
        IEncryptUtils.setImageHead(context);
        //添加设置图片失败头
        IEncryptUtils.setFailedImageHead(context);
        //添加设置语音头
        IEncryptUtils.setVoiceHead(context);
    }

    private static void onUpdateStrategys(final Context context, final NewStrategyRequestBean req) {
        AppComponent appComponent = ((ActomaApplication) ActomaApplication.getInstance()).getAppComponent();
        if (appComponent == null) {
            return;
        }
        PreUseCaseComponent preUseCase = appComponent.plus(new PreUseCaseModule());
        preUseCase.queryStrategyByMobile()
                .fill(req.getVersion(), req.getCardNo(),req.getModel(),req.getManufacturer(),req.getLastStrategyId(), req.getBatchSize())
                .execute(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.getUtils(TAG).i(">>> onUpdateStrategys - onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils(TAG).e(">>> onUpdateStrategys - onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer aInteger) {
                        LogUtil.getUtils(TAG).i(">>> onUpdateStrategys - onNext - aInteger=" + aInteger);
                        //[S]modify by lixiaolong on 20160918. fix first click the shield toast no strategys. review by myself.
                        if (aInteger > 0) {
                            req.setLastStrategyId(aInteger);
                            onUpdateStrategys(context, req);
                        } else if (aInteger == -1) {
                            queryStrategys(context);
                        }
                        //[E]modify by lixiaolong on 20160918. fix first click the shield toast no strategys. review by myself.
                    }
                });
    }

    public interface QueryQuickOpenAppsCallback {
        void queryQuickOpenApps(List<QuickOpenAppBean> apps);
    }
}
