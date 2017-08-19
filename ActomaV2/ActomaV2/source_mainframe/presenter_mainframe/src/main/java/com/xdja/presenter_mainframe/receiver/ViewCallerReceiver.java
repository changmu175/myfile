package com.xdja.presenter_mainframe.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xdja.comm.server.NotificationIntent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.presenter_mainframe.presenter.activity.LauncherPresenter;

import java.util.LinkedList;

/**
 * 处理界面跳转的广播
 */
public class ViewCallerReceiver extends BroadcastReceiver {
    public ViewCallerReceiver() {
    }

    private String targetActivity = "";
    private Bundle preBundle;
    private final String NAME_MAIN_FRAME = "com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter";
//    private final String NAME_SHUT = "com.xdja.actoma.presenter.activity.ShutPresenter";

    private boolean validArgs(Intent preIntent) {
        if (preIntent == null) {
            return false;
        }
        preBundle = preIntent.getExtras();
        if (preBundle == null) {
            return false;
        }
        targetActivity = preBundle.getString(NotificationIntent.TAG_TARGET_ACTIVITY);
        //目标页面为空时，退出转换页面
        if (TextUtils.isEmpty(targetActivity)) {
            return false;
        }
        return true;
    }

    @Override
    public void onReceive(Context context, Intent preIntent) {
        //modify by alh@xdja.com to fix bug: 1707 2016-07-26 start (rummager : self)
        if (NotificationIntent.notificationAction.equals(preIntent.getAction())) {
            //modify by alh@xdja.com to fix bug: 1707 2016-07-26 end (rummager : self)
            if (!validArgs(preIntent)) {
                return;
            }
            //查找堆栈中的目标页面
            Activity activity = ActivityStack.getInstanse().getActivityByClassName(targetActivity);
            Intent intent = null;
            //堆栈中不存在该页面
            try {
                if (activity == null) {
                    //堆栈为空，登陆
                    if (ActivityStack.getInstanse().getAllActivities() == null ||
                            ActivityStack.getInstanse().getAllActivities().isEmpty()) {
                        //[S]modify by lixiaolong on 20160830. fix bug 3086. review by wangchao1.
                        // intent = new Intent("com.xdja.actoma.DEFAULT");
                        intent = new Intent(context, LauncherPresenter.class);
                        intent.setAction("android.intent.action.MAIN");
                        intent.addCategory("android.intent.category.LAUNCHER");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        preBundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, targetActivity);
                        //[E]modify by lixiaolong on 20160830. fix bug 3086. review by wangchao1.
                    } else {
                        Activity mainFrame = ActivityStack.getInstanse().getActivityByClassName(NAME_MAIN_FRAME);
                        if (mainFrame == null) {
                            LinkedList<Activity> activities = ActivityStack.getInstanse().getAllActivities();
                            if (activities != null && !activities.isEmpty()) {
                                Activity first = activities.getFirst();
                                if (first != null) {
                                    intent = new Intent(context, first.getClass());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                            }
                        } else {
                            intent = new Intent(context, Class.forName(targetActivity));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    }
                }
                //堆栈中存在该页面
                else {
                    // 拉起单例界面
                    ActivityStack.getInstanse().
                            popActivitiesUntil(
                                    (Class<? extends BasePresenterActivity>)
                                            Class.forName(targetActivity), true);

                    /**20160818-mengbo-start:如果存在更新Activity标记，销毁已有Activity,创建新的Activity**/
                    if(preBundle != null && preBundle.containsKey(NotificationIntent.EXTRA_UPDATE_ACTIVITY)){
                        ActivityStack.getInstanse().
                                popActivity(activity, true);
                    }
                    /**20160616-mengbo-end**/

                    intent = new Intent(context, Class.forName(targetActivity));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);

                }
                if (intent != null) {
                    intent.putExtras(preBundle);
                    context.startActivity(intent);
                }
            }catch (ClassNotFoundException ex){
                LogUtil.getUtils().i(ex.getMessage());
            }
        }
    }
}
