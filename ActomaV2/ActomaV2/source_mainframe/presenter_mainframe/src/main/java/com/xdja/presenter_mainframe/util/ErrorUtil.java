package com.xdja.presenter_mainframe.util;

import android.app.Activity;

import com.xdja.dependence.exeptions.ServerException;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.presenter.activity.login.LoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.MessageVerifyLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordPresenter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ldy on 16/5/12.
 */
public class ErrorUtil {
    /**
     * 出现内部验证码无效的情况时,返回初始登录界面
     * @param okCode    错误码
     */
    public static void loginPop2First(String okCode){
        if (okCode!=null){
            if (okCode.equals(ServerException.INNER_AUTH_CODE_INVALID)){
                ActivityStack activityStack = ActivityStack.getInstanse();
                List<Activity> activities = activityStack.getAllActivities();
                for (Activity activity:activities){
                    Class activityClass = activity.getClass();
                    if (activityClass.equals(LoginPresenter.class)){
                        activityStack.popActivitiesUntil(activityClass,true);
                    }else if (activityClass.equals(MessageVerifyLoginPresenter.class)){
                        activityStack.popActivitiesUntil(activityClass,true);
                    }
                }
            }
        }
    }
    /**
     * 出现账号不一致时,返回初始界面
     * @param okCode    错误码
     */
    public static void registerPop2First(String okCode) {
        if (okCode != null) {
            if (okCode.equals(ServerException.INNER_AUTH_CODE_INVALID)) {
                ActivityStack activityStack = ActivityStack.getInstanse();
                //返回到能返回到的第一个界面
                LinkedList<Activity> allActivities = activityStack.getAllActivities();
                activityStack.popActivitiesUntil(allActivities.getLast(), true);
            }
        }
    }

    /**
     * 出现账号不一致时,返回初始注册界面
     * @param okCode    错误码
     */
    public static void resetPasswordPop2First(String okCode){
        if (okCode!=null){
            if (okCode.equals(ServerException.INNER_AUTH_CODE_INVALID)){
                ActivityStack activityStack = ActivityStack.getInstanse();
                List<Activity> activities = activityStack.getAllActivities();
                for (Activity activity:activities){
                    Class activityClass = activity.getClass();
                    if (activityClass.equals(ResetPasswordPresenter.class)){
                        activityStack.popActivitiesUntil(activityClass,true);
                    }
                }
            }
        }
    }


}
