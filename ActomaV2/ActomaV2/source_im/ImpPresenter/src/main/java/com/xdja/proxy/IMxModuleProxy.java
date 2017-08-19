package com.xdja.proxy;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xdja.imp.domain.model.ConstDef;

/**
 * Created by XDJA_XA on 2016/3/22.
 */
public interface IMxModuleProxy {

    /**
     * 初始化密信服务
     * @return true or false
     */
    boolean startMXService(Context context, String account, String cardId, String ticket);

    /**
     * 关闭密信服务
     */
    void stopService(Context context);

    /**
     * 删除所有的信息
     * @return true or false
     */
    boolean dropAllMessages(Context context);

    /**
     * 清除通知栏
     * @return true or false
     */
    boolean clearNotifications(Context context);

    /**
     * 创建会话列表Fragment
     * @return Fragment
     */
    Fragment createChatListFragment();


    void startChatActivity(Context context, String account, @ConstDef.ChatType int chatType);
}
