package com.xdja.imp.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.imp.component.NotificationBean;
import com.xdja.imp.messageNotification.NotificationConfigBean;
import com.xdja.imp.messageNotification.NotifySettingUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 敬菲菲
 *         <p/>
 *         通知栏提醒的条件
 *         1、安通+在后台才显示通知栏提醒，其他情况都不显示
 *         声音和震动提醒条件
 *         1、手机系统的情景模式
 *         2、安通+主框架的勿扰模式是否有效（包括是否开启，以及时间段范围）
 *         3、单人会话设置是否开启免打扰模式
 */
public class NotificationUtil/* implements RemindNewMessage*/ {
   private final Context context;

    private final static String NOTIFICATION_GROUP = "groupTalk";
    private final static String NOTIFICATION_SINGLE = "singleTalk";
    private static final int DISTANCE_TIME = 3000;

    /**
     * 详情界面标识
     */
    private static final String NOT_NOTIFY_ACTIVITY = "com.xdja.imp.presenter.activity.ChatDetailActivity";

    //add by licong fix bug 2644
    private static final String NOT_NOTIFY_FRAGMENT = "com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter";


    /**
     * 存储单人提醒消息列表
     */
    private final ArrayList<NotificationBean> singleNotifyList = new ArrayList<>();
    /**
     * 存储群组提醒消息列表
     */
    private final ArrayList<NotificationBean> groupNotifyList = new ArrayList<>();
    /**
     * 通知栏展示的消息提醒标题
     */
    private String notifyTitle = null;

    /**
     * 通知栏展示的消息提醒内容
     */
    private String notifyMessage = null;

    private long addPushTime;

    private NotifySettingUtil notifySettingUtil = null;

    private NotificationConfigBean notiConfigBean;

    private boolean isCurrentTalker = false;

    //private TalkListBean listBean;

    //private List<TalkMessageBean> msgBeanList;

    private static NotificationUtil instance;

    //add by zya,fix bug 4172,20160918
    private MediaPlayer player = null;


   public static NotificationUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationUtil.class) {
                if (instance == null) {
                    instance = new NotificationUtil(context);
                }
            }
        }
        return instance;
    }

    private NotificationUtil(Context context) {
        this.context = context;
        if (notifySettingUtil == null) {
            notifySettingUtil = new NotifySettingUtil();
        }
    }

    private void showHeadNotification(final TalkListBean listBean, final String partner, final String[] showMsgs) {
        String imageUrl = null;
        if (listBean != null && listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G) {
            if (groupNotifyList.size() > 0) {
                NotificationBean lastBean = groupNotifyList.get(0);
                imageUrl = lastBean.getImageUrl();
            }
        } else {
            if (singleNotifyList.size() > 0) {
                NotificationBean lastBean = singleNotifyList.get(0);
                imageUrl = lastBean.getImageUrl();
            }
        }
        Glide.get(context).register(GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(OkHttpsClient.getInstance(context).getOkHttpClient()));
        //modify by lwl start 1777
        Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
        //Glide.with(context).load(RequestHeadImage.getGlideUrl(imageUrl)).asBitmap().into(new SimpleTarget<Bitmap>() {
        //modify by lwl end 1777
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                //start: wangchao for 2906
                if (listBean == null) {
                    LogUtil.getUtils().e("onLoadFailed listBean is null");
                    return;
                }
                //end: wangchao for 2906
                Resources r = context.getResources();
                Drawable drawable = r.getDrawable(R.drawable.corp_user_40dp);//默认为单人头像
                if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G) {
                    drawable = r.getDrawable(R.drawable.group_avatar_40);
                }
                Bitmap largeBitmap = Functions.drawableToBitmap(drawable);
                notifyMessage(largeBitmap, listBean, partner, showMsgs);
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap bitmap = BitmapUtils.GetRoundedCornerBitmap(resource);
                notifyMessage(bitmap, listBean, partner,showMsgs);
            }
        });

    }

    /**
     * 初始化声音震动以及提示内容
     *
     * @param listBean 添加的会话的对象
     * @param bean     通知消息的集合
     */
    private void initData(ContactInfo info, TalkListBean listBean, List<TalkMessageBean> bean, String partener) {
        if (listBean == null || bean == null) {
            return;
        }
        //add by zya 20170221,fix bug 8643
        String[] showMessages = new String[2];
        //end by zya
        isCurrentTalker = false;
        notiConfigBean = notifySettingUtil.getNotifySetting(context, listBean.isNewMessageIsNotify());
        ArrayList<NotificationBean> notifyList = null;
        //fix bug 4096 by licong, reView by zya, 2016/9/13
        if (listBean.isNewMessageIsNotify()) {
            if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G ) {
                notifyList = addNotificationList(info, listBean, bean, groupNotifyList);
            } else {
                notifyList = addNotificationList(info, listBean, bean, singleNotifyList);
            }
        }//end

        if (notifyList == null) {
            return;
        }
        // 获取通知 声音 震动 提醒设置
        if (notiConfigBean.getIsRemind()) {
            //判断当前系统情景模式来设置提示模式
            //需要添加获取未读条数及人员数量接口
            int newCount = 0;
            StringBuilder notifiName = new StringBuilder();

            for (int i = 0; i < notifyList.size(); i++) {
                NotificationBean notificationBean = notifyList.get(i);
                LogUtil.getUtils().d("NotificationUtil initData " + notificationBean.toString() + ",i:" + i);
                //fix bug 3247 by licong , reView by zya,  2016/8/24
                String name;
                if (TextUtils.isEmpty(notificationBean.getAccountName())) {
                    name = context.getResources().getString(R.string.group_name_default);
                } else {
                    name = notificationBean.getAccountName();
                }//end
                notifiName.append(name);
                newCount += notificationBean.getMsgCount();
                if (i != notifyList.size() - 1) {
                    if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G) {
                        notifiName.append("；");//多个群组名称之间用“；”隔开
                    } else {
                        notifiName.append("、");//多个单人名称之间用“、”隔开
                    }
                }
            }
            //add by zya 20170221,fix bug 8643
            showMessages[0] = notifiName.toString();
            showMessages[1] = context.getResources().getString(R.string.notify_prestr) + newCount
                    + context.getResources().getString(R.string.notify_endstr);
            //end by zya
        }

        //add log zya@xdja.com,20161018
        LogUtil.getUtils().d("NotificaitonUtil initData listBean:" + listBean + "notifyTitle:" + showMessages[0] +
                ",notifyMessage:" + showMessages[1]);
        //end zya

        //modify by zya,fix bug ,20160927
        //start fix bug 4544 by licong , reView by zya, 2016/9/28
        showHeadNotification(listBean, partener,showMessages);
        //end fix bug 4544 by licong , reView by zya, 2016/9/28

    }

    /**
     * 消息提醒
     *
     * @param bean            会话ID
     * @param talkMessageBean 目标消息
     */
    //@Override
    public void remindMessage(ContactInfo info, SessionConfig sessionConfig, final TalkListBean bean,
                              final List<TalkMessageBean> talkMessageBean, final String partener) {
        notifyMessage = null;
        notifyTitle = null;

        bean.setNewMessageIsNotify(true);
        if (sessionConfig != null) {
            bean.setNewMessageIsNotify(!sessionConfig.isNoDisturb());
        }

        LogUtil.getUtils().d("4966 NotificationUtil remindMessage " + bean.toString());
        //start fix bug 4544 by licong , reView by zya, 2016/9/28
        initData(info, bean, talkMessageBean, partener);
        //end fix bug 4544 by licong , reView by zya, 2016/9/28
    }

    /**
     * 添加系统通知栏消息通知
     */
    private void addNotification(TalkListBean listBean, Bitmap bitmap, String[] showMsgs) {
        //add by zya 20170221,fix bug 8643
        String notifyMsg = showMsgs[1];
        String notifyTitleMsg = showMsgs[0];
        //end by zya

        // 发送通知栏消息 fix bug 3446 by zya@xdja.com,20160830
        if (notifyMsg == null || TextUtils.isEmpty(notifyTitleMsg) || listBean == null) {
            return;
        }

        LogUtil.getUtils().d("NotificaitonUtil addNotification listBean:" + listBean + ",notifyTitle:" + notifyTitleMsg +
                ",notifyMessage:" + notifyMsg);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        NotificationIntent intent = new NotificationIntent("ChatDetailActivity");

        Bundle bundle = new Bundle();
        bundle.putInt("pageIndex", TabTipsEvent.INDEX_CHAT);

        //modify by licong,fix bug 2644,review by zya@xdja.com,20160810
        /*  if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2P && singleNotifyList.size() == 1) {
                bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_ACTIVITY);
        }*/

        if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2P && singleNotifyList.size() > 0) {
            if (singleNotifyList.size() == 1) {
                bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_ACTIVITY);
            } else {
                bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_FRAGMENT);
            }
        }

        /* if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G && groupNotifyList.size() == 1) {
            bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_ACTIVITY);
        }*/

        if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G && groupNotifyList.size() > 0) {
            if (groupNotifyList.size() == 1) {
                bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_ACTIVITY);
            } else {
                bundle.putString(NotificationIntent.TAG_TARGET_ACTIVITY, NOT_NOTIFY_FRAGMENT);
            }
        }//end

        String notificationTag;
        if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G) {
            notificationTag = NOTIFICATION_GROUP;
        } else {
            notificationTag = NOTIFICATION_SINGLE;
        }

        bundle.putString(ConstDef.TAG_TALKERID, listBean.getTalkerAccount());

        bundle.putInt(ConstDef.TAG_TALKTYPE, listBean.getTalkType());
        intent.putExtras(bundle);

        int requestId = 0;
        PendingIntent pendingIntent = intent.buildPendingIntent(
                context, ((int) System.currentTimeMillis()));
        notificationBuilder.setContentIntent(pendingIntent);

        int res = R.drawable.af_abs_ic_logo; //R.drawable.chatlist_actom_avatar_40;
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // step1
        notificationBuilder
                .setContentText(notifyMsg)
                .setContentTitle(notifyTitleMsg)
                .setSmallIcon(res)
                .setTicker(notifyMsg)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent);


        // step3 构造出通知对象
        Notification notification = notificationBuilder.build();
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 1;
        notification.ledOffMS = 0;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        // 这个函数中第一个参数代表identifier.如果要同时弹出多条通知，每个通知的这个参数必须不同。否则，后面的会覆盖前面的通知。
        manager.notify(notificationTag, requestId, notification);
    }


    /**
     * 取消推送消息通知
     */
    public void clearPNNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_SINGLE, 0);
        manager.cancel(NOTIFICATION_GROUP, 0);
        singleNotifyList.clear();
        groupNotifyList.clear();
    }

    /**
     * 声音震动消息提醒
     */
    private void addMediaNotification(Context context) {
        long bootTime = SystemClock.elapsedRealtime();
        long time = bootTime - addPushTime;
        if (time > DISTANCE_TIME) {
            addPushTime = SystemClock.elapsedRealtime();

            //remove by zya,20160918,fix bug 4172
            // 添加声音提示
            //MediaPlayer player = null;
            //end
            if (notiConfigBean != null && notiConfigBean.getHasVoice()) {
                try {
                    //没有判空是因为更改系统提示音，消息提示音应同步修改
                    player = MediaPlayer.create(context,
                            RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION));
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (player != null && !player.isPlaying() && !isCurrentTalker) {
                        player.start();
                    }
                }
            }

            // 添加振动提示
            if (notiConfigBean != null) {
                if (notiConfigBean.getHasShake()) {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(300);
                    }
                }
            }
        }

        //add by zya@xdja.com,20160928
        isCurrentTalker = false;
        //end
    }


    /**
     *
     */
    @SuppressLint("EqualsBetweenInconvertibleTypes")
    private ArrayList<NotificationBean> addNotificationList(ContactInfo info,
                                                                   TalkListBean listBean,
                                                                   List<TalkMessageBean> msgList,
                                                                   ArrayList<NotificationBean> notifyList) {
        if (info == null) {
            return null;
        }
        //modify by licong, fix bug 1905,view by zya@xdja.com
        String account = info.getAccount();
        if (TextUtils.isEmpty(account)) {
            account = listBean.getTalkerAccount();
        }
        String imageUrl = info.getAvatarUrl();

        int containIndex = -1;
        String talkerId = listBean.getTalkerAccount();

        int size = notifyList.size();
        for (int i = 0; i < size; i++) {
            if (notifyList.get(i).equals(account)) {
                containIndex = i;
                break;
            }
        }
        if (containIndex > -1) {
            NotificationBean notificationBean = notifyList.get(containIndex);
            if (notificationBean != null) {
                int preMsgCount = notificationBean.getMsgCount();
                notificationBean.setAccountName(info.getName());
                notificationBean.setMsgCount(preMsgCount + msgList.size());
            } else {
                notificationBean = new NotificationBean();
                notificationBean.setMsgCount(msgList.size());
                notificationBean.setAccount(talkerId);
                notificationBean.setAccountName(info.getName());
            }
            notifyList.remove(containIndex);
            notificationBean.setImageUrl(imageUrl);
            notifyList.add(0, notificationBean);
        } else {
            NotificationBean notificationBean = new NotificationBean();
            notificationBean.setMsgCount(msgList.size());
            notificationBean.setAccount(talkerId);
            notificationBean.setAccountName(info.getName());
            notificationBean.setImageUrl(imageUrl);
            notifyList.add(0, notificationBean);
        }//end licong
        return notifyList;
    }

    /**
     * 根据当前应用程序是否在前端
     * 设置通知栏提醒还是媒体提醒
     */
    private void notifyMessage(Bitmap bitmap, TalkListBean listBean, String partener,String[] showMsgs) {
        isCurrentTalker = false;
        if (notiConfigBean != null) {

            if (notiConfigBean.getIsRemind()) {
                if (!Functions.isAppOnForeground(context)) {
                    addMediaNotification(context);
                    addNotification(listBean, bitmap, showMsgs);
                } else {
                    String currentActivityName = Functions.getCurrentActivityName(context);
                    if (currentActivityName == null) {
                        addMediaNotification(context);
                        addNotification(listBean, bitmap, showMsgs);
                    } else if (Functions.isScreenOffOrLock(context)) {
                        addMediaNotification(context);
                        // 锁屏，黑屏。发消息
                        addNotification(listBean, bitmap ,showMsgs);
                        LogUtil.getUtils().d("4968 NotificationUtil notifyMessage isScreenOffOrLock");
                    } else if (currentActivityName.equals(NOT_NOTIFY_ACTIVITY)
                            && !TextUtils.isEmpty(partener) && listBean != null && partener.equals(listBean.getTalkerAccount())) {
                        isCurrentTalker = true;
                        addMediaNotification(context);
                        // 屏幕在前台，屏幕亮，且解锁,正在聊天界面与当前人员聊天
                    } else if (currentActivityName.equals(NOT_NOTIFY_ACTIVITY)
                            && !TextUtils.isEmpty(partener) && listBean != null && !partener.equals(listBean.getTalkerAccount())) {
                        // 在前台，并且与当前人员聊天界面，实现：只震动。

                        addMediaNotification(context);
                    } else {
                        // 只做提醒，不显示通知消息
                        addMediaNotification(context);
                        LogUtil.getUtils().d("4968 NotificationUtil notifyMessage");
                    }
                }
            }
        }
    }


}
