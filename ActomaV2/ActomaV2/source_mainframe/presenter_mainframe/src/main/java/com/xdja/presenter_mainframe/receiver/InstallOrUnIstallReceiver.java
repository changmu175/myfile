package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.NotifyAppStoreEvent;
import com.xdja.comm.event.NotifyStrategysEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.enc3rd.utils.StrategysUtils;

/**
 * Created by geyao on 2015/11/5.
 * 安装-卸载广播接收处理
 *
 * modify by LiXiaoLong on 2016-08-16. fix Bug 2915.
 */
public class InstallOrUnIstallReceiver extends BroadcastReceiver {
    /**
     * 安装应用
     */
    private static final int TYPE_ADDED = 0;
    /**
     * 卸载应用
     */
    private static final int TYPE_REMOVED = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        // [S] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3671.
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {//接收安装广播
            String packageName = intent.getDataString().replace("package:", "");
            if (!TextUtils.isEmpty(packageName)) {
                isSupportApp(context, packageName, TYPE_ADDED);
            }
        } else if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {////接收卸载广播
            //通知应用市场刷新列表界面
            BusProvider.getMainProvider().post(new NotifyAppStoreEvent());

            String packageName = intent.getDataString().replace("package:", "");
            if (!TextUtils.isEmpty(packageName)) {
                isSupportApp(context, packageName, TYPE_REMOVED);
            }
        }
        // [E] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3671.
    }

    /**
     * 判断该应用是否是支持的应用
     *
     * @param context     上下文句柄
     * @param packageName 包名
     * @param type        类型 0-安装 1-卸载
     */
    private void isSupportApp(final Context context, final String packageName, final int type) {
//        List<SupportAppDes> supportAppDes = EncDecSever.queryAllSupportApp(context);
//        if (supportAppDes == null || supportAppDes.size() < 1) {
//            LogUtil.getUtils().i("supportAppDes list is null, "+packageName);
//            return;
//        }

        if (TextUtils.isEmpty(packageName)) {
            return;
        }
//        List<EncryptListBean> list = EncryptListServer.queryAllEncryptListData(context);
//        if (list == null || list.size() < 1) {
//            LogUtil.getUtils().i("supportAppDes list is null, "+packageName);
//            return;
//        }
//        //修改数据
//        updateData(context, list, packageName, type);

        StrategysUtils.queryStrategys(context);

        //发送事件通知第三方加密页面列表刷新
        BusProvider.getMainProvider().post(new NotifyStrategysEvent());
        LogUtil.getUtils("InstallOrUnIstallReceiver").i("type(0-安装 1-卸载): " + type + " - packageName: " + packageName);
    }

//    /**
//     * 修改数据
//     *
//     * @param context       上下文句柄
//     * @param appList       来源集合
//     * @param packageName   包名
//     * @param type          类型
//     */
//    private void updateData(Context context, List<EncryptListBean> appList, String packageName, int type) {
//        //循环匹配到对应包名的数据对象
//        for ( EncryptListBean bean : appList) {
//            if (bean.getPackageName().equals(packageName)) {
//                switch (type) {
//                    case TYPE_ADDED://安装应用
//                        //查询全部数据
//                        List<QuickOpenAppBean> quickOpenAppBeans = QuickOpenAppServer.queryAllData(context);
//                        //若数据不为空 将新数据追加到末尾
//                        if (quickOpenAppBeans != null) {
//                            QuickOpenAppBean qoaBean = new QuickOpenAppBean();
//                            qoaBean.setSort(quickOpenAppBeans.get(quickOpenAppBeans.size() - 1).getSort() + 1);
//                            qoaBean.setPackageName(bean.getPackageName());
//                            qoaBean.setAppName(bean.getAppName());
//                            qoaBean.setType(QuickOpenAppBean.TYPT_SHOW);
//                            QuickOpenAppServer.insertData(context, qoaBean);
//                        }
//                        break;
//                    case TYPE_REMOVED://卸载应用
//                        //删除对应数据
//                        QuickOpenAppServer.deleteDataByField(context, QuickOpenAppDao.FIELD_PACKAGENAME, packageName);
//                        break;
//                }
//            }
//        }
//    }


//    /**
//     * 用于通知刷新第三方加密相关列表数据队形
//     */
//    public static class NotifyEncryptList {
//
//    }

//    /**
//     * 用于通知应用市场刷新界面
//     */
//    public static class NotifyAppStore {
//
//    }
}
