package com.xdja.comm.server;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xdja.comm.event.NotifyAuthEvent;
import com.xdja.comm.event.BusProvider;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.server</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/10</p>
 * <p>Time:9:07</p>
 */
public class ActomaController {

    /**
     * 重新认证获取Ticket接口
     *
     * @param oldTicket 已经存在的Ticket
     */
    public static void reAuthTicket(@Nullable String oldTicket) {
        NotifyAuthEvent event = new NotifyAuthEvent();
        event.setOldTicket(oldTicket);
        BusProvider.getMainProvider().post(event);
    }
    /**
     * Application单例
     */
    private static Application app;
    public static void setApp(Application actomaApp) {
        ActomaController.app = actomaApp;
        Log.e("lyq","setApp"+ActomaController.app);
    }

    public static Application getApp(){
        return app;
    }

    public static int getFlag() {
        return Flag;
    }

    public static void setFlag(int flag) {
        Flag = flag;
    }

    private static int Flag = 0;

}
