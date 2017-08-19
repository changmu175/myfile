package com.xdja.frame.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.dependence.uitls.LogUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fanjiandong
 * @summary Activity堆栈管理类
 * @description
 * @time 2014-12-1 下午4:06:17
 */
public class ActivityStack {

    @Nullable
    private static ActivityStack instance;

    @NonNull
    public static ActivityStack getInstanse() {
        if (instance == null) {
            synchronized (ActivityStack.class) {
                if (instance == null) {
                    instance = new ActivityStack();
                }
            }
        }
        return instance;
    }

    private ActivityStack() {
        activities = new LinkedList<>();
    }

    /**
     * activity堆栈
     */
    private LinkedList<Activity> activities;

    /**
     * 获取堆栈中的所有Activity
     *
     * @return {@link #activities}
     */
    @NonNull
    public LinkedList<Activity> getAllActivities() {
        return activities;
    }

    /**
     * 获取堆栈顶端的Activity
     *
     * @return
     */
    @Nullable
    public Activity getTopActivity() {
        if (activities == null || activities.isEmpty()) {
            return null;
        }
        return activities.getFirst();
    }

    /**
     * 将一个Activity入栈
     *
     * @param activity 目标Activity
     */
    public void pushActivity(@NonNull Activity activity) {
        activities.addFirst(activity);
    }

    /**
     * 从堆栈中弹出一个Activity
     *
     * @param activity 目标Activity
     * @param isFinish 出栈的同时是否结束Activity
     * @return 操作结果（若堆栈中不存在该Activity，弹出将会失败返回false）
     */
    public boolean popActivity(@NonNull Activity activity, boolean isFinish) {
        if (activities.isEmpty()) {
            return false;
        }
        if (activities.contains(activity)) {
            if (isFinish) {
                activity.finish();
            }
            return activities.remove(activity);
        }
        return false;
    }

    /**
     * 从堆栈中弹出所有的Activity对象
     *
     * @param isFinish 出栈的同时是否结束Activity
     * @return 操作结果
     */
    public void popAllActivities(boolean isFinish) {
        if (activities.isEmpty()) {
            return;
        }
        if (isFinish) {
            Iterator<Activity> iterator = activities.iterator();
            while (iterator.hasNext()) {
                iterator.next().finish();
            }
        }
        activities.clear();
    }

	// add by ycm for share function 20170204 [start]
    /**
     * 分享成功后返回第三方应用：先结束栈顶的界面即ChooseImActivity，如果还有界面则置于后台。
     */
    public void goBackApp() {
        Activity activity = getTopActivity();
        if (activity == null || !isAppOnForeground(activity)) {
            return;
        }
        popActivity(activity, true);//弹出栈顶的页面并结束
        moveToBackAllActivities();//使应用置于后台
    }
	// add by ycm for share function 20170204 [end]

    /** 20161103-mengbo-start: add !activity.hasWindowFocus() to solve app restart  review by alh **/
    //alh@xdja.com<mailto://alh@xdja.com> 2016-10-14 add. review by wangchao1. Start
    public void moveToBackAllActivities() {
        Activity activity = getTopActivity();
        if (activity == null || !isAppOnForeground(activity)) {
            return;
        }
        try {
            activity.moveTaskToBack(true);
        } catch (Exception e) {
            LogUtil.getUtils().e("moveToBackAllActivities->moveTaskToBack error : " + e != null ? e.toString() : "NULL");
            return;
        }
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-10-14 add. review by wangchao1. End
    /** 20161103-mengbo-end **/

    /**
     * 从堆栈中弹出Activity直到置顶的位置
     *
     * @param position 目标位置
     * @param isFinish 出栈的同时是否结束Activity
     */
    public void popActivitiesUntil(int position, boolean isFinish) {
        if (activities.isEmpty() || position < 0) {
            return;
        }

        for (int i = 0; i < position; i++) {
            if (isFinish) {
                activities.getFirst().finish();
            }
            activities.removeFirst();
        }
    }


    /**
     * 从堆栈中弹出Activity直到某个指定的Activity为止（不弹出指定的Activity）
     *
     * @param activity 目标Activity
     * @param isFinish 出栈的同时是否结束Activity
     */
    public void popActivitiesUntil(@NonNull Activity activity, boolean isFinish) {
        if (activities.isEmpty() || !activities.contains(activity)) {
            return;
        }
        int posistion = activities.indexOf(activity) - 1;
        popActivitiesUntil(posistion, isFinish);
    }

    /**
     * 弹出top以下的所有activity
     *
     * @param isFinish 是否finish其他界面
     */
    public void pop2TopActivity(boolean isFinish) {
        final int position = activities.size() - 1;
        for (int i = 0; i < position; i++) {
            if (isFinish) {
                activities.getLast().finish();
            }
            activities.removeLast();
        }
    }

    /**
     * 从堆栈中弹出Activity直到某个指定类型的Activity为止（不弹出指定类型的Activity）
     *
     * @param cls      目标Activity类型
     * @param isFinish 出栈的同时是否结束Activity
     */
    public <T extends Activity> void popActivitiesUntil(@NonNull Class<T> cls, boolean isFinish) {
        if (activities.isEmpty()) {
            return;
        }
        int posistion = -1;
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getClass().equals(cls)) {
                posistion = i;
                break;
            }
        }
        popActivitiesUntil(posistion, isFinish);
    }

    /**
     * 通过类名从堆栈中弹出Activity
     *
     * @param cs       Activity的class对象
     * @param isFinish 出栈的同时是否结束Activity
     */
    public void popActivityByClass(@NonNull Class<? extends Activity> cs, boolean isFinish) {
        Activity ac = getActivityByClass(cs);
        if (ac != null) {
            popActivity(ac, isFinish);
        }
    }

    /**
     * 完全退出
     */
    public void exitApp() {
        popAllActivities(true);
    }

    /**
     * 根据class name获取activity
     * <p/>
     * Acitivity名称
     *
     * @return 获得的对象
     */
    @Nullable
    public Activity getActivityByClassName(@NonNull String activityName) {
        if (activities.isEmpty()) {
            return null;
        }
        for (Activity ac : activities) {
            if (ac.getClass().getName().contains(activityName)) {
                return ac;
            }
        }
        return null;
    }

    /**
     * 根据Activity类名获取Activity对象
     *
     * @param cs Activity的class对象
     * @return 获得的对象
     */
    @Nullable
    public Activity getActivityByClass(Class<? extends Activity> cs) {
        if (activities.isEmpty()) {
            return null;
        }
        for (Activity ac : activities) {
            if (ac.getClass().equals(cs)) {
                return ac;
            }
        }
        return null;
    }
    /**
     * 程序是否在前台运行
     *
     * Created by licong ,2016/11/23
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appInfos =  am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info: appInfos) {
            if (info.processName.equals(context.getPackageName())) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

}
