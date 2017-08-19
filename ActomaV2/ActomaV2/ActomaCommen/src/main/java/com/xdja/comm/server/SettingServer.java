package com.xdja.comm.server;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.data.SafeLockDao;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.data.SettingDao;
import com.xdja.dependence.uitls.LogUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by geyao on 2015/7/17.
 */
public class SettingServer {
    /**
     * 新消息通知
     */
    public static String newsRemind;
    /**
     * 新消息通知-声音
     */
    public static String newsRemindRing;
    /**
     * 新消息通知-振动
     */
    public static String newsRemindShake;

    /**
     * 勿扰模式
     */
    public static String noDistrub;

    /**
     * 勿扰模式开始时间
     */
    public static String noDistrubBeginTime;

    /**
     * 勿扰模式结束时间
     */
    public static String noDistrubEndTime;

    /**
      * 听筒模式-设置开关
      */
    public static String openReceiverMode;

    /**
     * 安全锁
     */
    public static String safeLock;

    /**
     * 锁屏锁定
     */
    public static String  lockScreen;

    /**
     * 后台运行锁定
     */
    public static String backgroundLock;
    /**
     * 新增设置信息
     *
     * @param settingbean 将新增的设置Bean
     * @return 新增结果
     */
    public static boolean insertSetting(@Nullable SettingBean settingbean) {
        SettingDao settingdao = null;
        try {
            settingdao = SettingDao.instance().open();
            return settingdao.insert(settingbean);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (settingdao != null) {
                settingdao.close();
            }
        }
    }

    /**
     * 删除设置信息
     *
     * @param key     要删除的key
     * @return 删除结果
     */
    public static boolean deleteSetting(@Nullable String key) {
        SettingDao settingdao = null;
        try {
            settingdao = SettingDao.instance().open();
            return settingdao.delete(key);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (settingdao != null) {
                settingdao.close();
            }
        }
    }

    /**
     * 修改设置信息
     *
     * @param settingBean 要修改的设置Bean
     * @return 修改结果
     */
    public static boolean updateSetting(@Nullable SettingBean settingBean) {
        SettingDao settingdao = null;
        try {
            settingdao = SettingDao.instance().open();
            return settingdao.update(settingBean);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (settingdao != null) {
                settingdao.close();
            }
        }
    }

    /**
     * 获取全部设置信息
     *
     * @return 设置信息集合
     */
    @Nullable
    public static List<SettingBean> queryAllSetting() {
        List<SettingBean> list;
        SettingDao settingdao = null;
        try {
            settingdao = SettingDao.instance().open();
            list = settingdao.queryAll();
            return list;
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (settingdao != null) {
                settingdao.close();
            }
        }
    }

    /**
     * 获取设置信息
     *
     * @param key     获取设置信息所需的key
     * @return 设置信息
     */
    @Nullable
    public static SettingBean querySetting(@Nullable String key) {
        SettingBean settingbean;
        SettingDao settingdao = null;
        try {
            settingdao = SettingDao.instance().open();
            settingbean = settingdao.query(key);
            return settingbean;
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (settingdao != null) {
                settingdao.close();
            }
        }
    }

    /**
     * 获取新消息通知状态
     *
     * @return 新消息通知状态
     */
    public static boolean getNewsRemind() {
        //判断新消息通知是否有值
        if (newsRemind == null) {//新消息通知无值
            //查询新消息通知信息
            SettingBean bean = SettingServer.querySetting(SettingBean.NEWSREMIND);// modified by ycm for lint 2017/02/13
            //数据库内有匹配数据,返回对应状态
            return bean == null || Boolean.parseBoolean(bean.getValue());
        } else {//新消息通知是否有值
            //返回状态
            return Boolean.parseBoolean(newsRemind);
        }
    }

    /**
     * 获取新消息通知-声音状态
     *
     * @return 新消息通知-声音状态
     */
    public static boolean getNewsRemindRing() {
        //判断新消息通知是否开启
        if (getNewsRemind()) {//开启
            //判断新消息通知-声音是否有值
            if (newsRemindRing == null) {//新消息通知-声音无值
                //查询新消息通知-声音信息
                SettingBean bean = SettingServer.querySetting(SettingBean.NEWSREMIND_RING);// modified by ycm for lint 2017/02/13
                //数据库内有匹配数据, 返回对应状态
                return bean == null || Boolean.parseBoolean(bean.getValue());
            } else {//新消息通知-声音是否有值
                //返回状态
                return Boolean.parseBoolean(newsRemindRing);
            }
        } else {//未开启
            return false;
        }
    }

    /**
     * 获取新消息通知-振动状态
     *
     * @return 新消息通知-振动状态
     */
    public static boolean getNewsRemindShake() {
        //判断新消息通知是否开启
        if (getNewsRemind()) {//开启
            //判断新消息通知-振动是否有值
            if (newsRemindShake == null) {//新消息通知-振动无值
                //查询新消息通知-振动信息
                SettingBean bean = SettingServer.querySetting(SettingBean.NEWSREMIND_SHAKE);// modified by ycm for lint 2017/02/13
                //数据库内有匹配数据,返回对应状态
                return bean == null || Boolean.parseBoolean(bean.getValue());
            } else {//新消息通知-振动是否有值
                //返回状态
                return Boolean.parseBoolean(newsRemindShake);
            }
        } else {//未开启
            return false;
        }
    }

    /**
     * 听筒模式是否打开
     * @return
     */
    public static boolean isReceiverModeOn() {
        SettingBean bean = SettingServer.querySetting(SettingBean.RECEIVER_MODE);
        //数据库内有匹配数据
        //返回对应状态
        return bean != null && Boolean.parseBoolean(bean.getValue());// modified by ycm for lint 2017/02/13
    }

//    GetNoDistrubSettingUseCase.NoDistrubBean nodistrubBean = getCommand().getNodistrubBean();
//    if (nodistrubBean != null) {
//        t_hour = nodistrubBean.getEndHour();
//        t_minu = nodistrubBean.getEndMinu();
//    }

    /**
     * 勿扰模式是否开启
     *
     * @return
     */
    public static boolean getNoDisturbModelOpened() {
        SettingBean settingBean = SettingServer.querySetting(SettingBean.NODISTRUB);
        if (settingBean == null) {
            return false;
        }
        String value = settingBean.getValue();
        NoDistrubBean noDistrubBean = null;
        if (!TextUtils.isEmpty(value)) {
            noDistrubBean = new Gson().fromJson(value, NoDistrubBean.class);
        }

        return noDistrubBean != null && noDistrubBean.isOpen();// modified by ycm for lint 2017/02/15
    }

    /**
     * 勿扰模式的开始时间
     *
     * @return
     */
    public static long getNoDisturbModelBeginTime() {
        SettingBean settingBean = SettingServer.querySetting(SettingBean.NODISTRUB);
        if (settingBean == null) {
            return 23 * 3600 * 1000;
        }
        String value = settingBean.getValue();
        NoDistrubBean noDistrubBean = null;
        if (!TextUtils.isEmpty(value)) {
            noDistrubBean = new Gson().fromJson(value, NoDistrubBean.class);
        }

        return (noDistrubBean.getBeginHour() * 3600 + noDistrubBean.getBeginMinu() * 60) * 1000; // TODO: 2017/2/15 确认值为空时的替代值
    }

    /**
     * 获取勿扰模式结束时间配置
     *
     * @return
     */
    public static long getNoDisturbModelEndTime() {
        SettingBean settingBean = SettingServer.querySetting(SettingBean.NODISTRUB);
        if (settingBean == null) {
            return 8 * 3600 * 1000;
        }
        String value = settingBean.getValue();
        NoDistrubBean noDistrubBean = null;
        if (!TextUtils.isEmpty(value)) {
            noDistrubBean = new Gson().fromJson(value, NoDistrubBean.class);
        }
        return (noDistrubBean.getEndHour() * 3600 + noDistrubBean.getEndMinu() * 60) * 1000; // TODO: 2017/2/15 确认值为空时的替代值
    }

    /**
     * 勿扰模式 当前时间是否生效
     * @return true or false
     */
    public static boolean isNoDisturbModeValidNow() {
        boolean model = SettingServer.getNoDisturbModelOpened();

        if(model){
            long noDisturbModelBT = SettingServer.getNoDisturbModelBeginTime();
            long noDisturbModelET = SettingServer.getNoDisturbModelEndTime();
            long currentSystemMillons = System.currentTimeMillis();
            final Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(currentSystemMillons);
            int mHour=calendar.get(Calendar.HOUR_OF_DAY);
            int mMinutes=calendar.get(Calendar.MINUTE);
            long currentTime = (mHour * 3600 + mMinutes * 60) * 1000;

            //如果结束时间小于开始时间，说明是跨天
            if(noDisturbModelET < noDisturbModelBT){
                if((currentTime >= noDisturbModelBT && currentTime<=24*3600*1000)
                        || (currentTime>=0&& currentTime<=noDisturbModelET)){
                    return true;
                }
            }else {
                if (currentTime >= noDisturbModelBT && currentTime <= noDisturbModelET) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 安全锁通知状态
     *
     * @return 安全锁通知状态
     * 状态有三个值
     * true 安全锁开启    false 安全锁关闭  -1 安全锁为设置
     */
    public static String getSafeLock() {
        //判断安全锁通知是否有值
        if (safeLock == null) {
            //查询安全锁通知信息
            SettingBean bean = SettingServer.querySafeLock(SettingBean.SAFE_LOCK);
            if (bean != null) {//数据库内有匹配数据
                //返回对应状态
                return bean.getValue();
            } else {//数据库内无匹配数据
                //返回默认状态
                return  -1 + "";
            }
        } else {
            return safeLock;
        }

    }


    /**
     * 锁屏锁定
     * @return 锁屏锁定
     */
    public static boolean getLockScreen() {
        //判断安全锁通知是否开启
        if (getSafeLock().equals("true")) {//开启
            if (lockScreen == null) {
                //查询锁屏锁定信息
                SettingBean bean = SettingServer.querySafeLock(SettingBean.LOCK_SCREEN);
                return bean != null && Boolean.parseBoolean(bean.getValue()); //modified by ycm for lint 2017/02/16
            } else {
                return Boolean.parseBoolean(lockScreen);
            }

        } else {//未开启
            return false;
        }
    }



    /**
     * 后台运行锁定
     *
     * @return 后台运行锁定
     */
    public static boolean getLockBackground() {
        //判断安全锁通知是否开启
        if (getSafeLock().equals("true")) {//开启
            if (backgroundLock == null) {
                //查询后台运行锁定信息
                SettingBean bean = SettingServer.querySafeLock(SettingBean.LOCK_BACKGROUND);
                //数据库内有匹配数据//返回对应状态
                return bean != null && Boolean.parseBoolean(bean.getValue());// add by ycm for lint 2017/02/16

            } else {
                return Boolean.parseBoolean(backgroundLock);
            }
        } else {//未开启
            return false;
        }
    }

    /**
     * 获取安全锁信息
     *
     * @param key     获取安全锁信息所需的key
     * @return 设置信息
     */
    @Nullable
    public static SettingBean querySafeLock(@Nullable String key) {
        SettingBean settingbean;
        SafeLockDao safeLockDao = null;
        try {
            safeLockDao = SafeLockDao.instance().open();// modified by ycm for lint 2017/02/13
            settingbean = safeLockDao.query(key);
            return settingbean;
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (safeLockDao != null) {
                safeLockDao.close();
            }
        }
    }

    /**
     * 保存安全锁的所有设置信息
     * @param settingbeans 设置bean
     * @return
     */
    public static boolean insertSafeLock(@Nullable SettingBean[] settingbeans) {
        SafeLockDao safeLockDao = null;
        try {
            safeLockDao = SafeLockDao.instance().open();
            return safeLockDao.insertAll(settingbeans);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (safeLockDao != null) {
                safeLockDao.close();
            }

        }
    }

    public static void  clearSafeLockSate() {
        safeLock = null;
        lockScreen = null;
        backgroundLock = null;
    }


    public static class NoDistrubBean {
        /**
         * 勿扰模式是否开启
         */
        private boolean isOpen;
        /**
         * 开始时间小时数
         */
        private int beginHour = 23;
        /**
         * 开始时间分钟数
         */
        private int beginMinu = 0;
        /**
         * 结束小时数
         */
        private int endHour = 8;
        /**
         * 结束分钟数
         */
        private int endMinu = 0;

        public boolean isOpen() {
            return isOpen;
        }

        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

        public int getBeginHour() {
            return beginHour;
        }

        public void setBeginHour(int beginHour) {
            this.beginHour = beginHour;
        }

        public int getBeginMinu() {
            return beginMinu;
        }

        public void setBeginMinu(int beginMinu) {
            this.beginMinu = beginMinu;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMinu() {
            return endMinu;
        }

        public void setEndMinu(int endMinu) {
            this.endMinu = endMinu;
        }

    }
}
