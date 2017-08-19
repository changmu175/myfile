package com.xdja.imp.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import util.DateUtil;

/**
 * Created by wanghao on 2015/11/26.
 */
public class DateUtils {


    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();

    private static final String YMDHM = "yyyy-MM-dd HH:mm";

    /**
     * 根据map中的key值，获取对应线程中的SimpleDateFormat实例
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getFormat(final String pattern) {

        ThreadLocal<SimpleDateFormat> formatThread = sdfMap.get(pattern);
        if (formatThread == null) {
            //双层检查，防止sdfMap多次放值
            synchronized (DateUtil.class) {
                formatThread = new ThreadLocal<SimpleDateFormat>() {

                    @Override
                    protected SimpleDateFormat initialValue() {
                        return new SimpleDateFormat(pattern);
                    }
                };
                sdfMap.put(pattern, formatThread);
            }
        }
        return formatThread.get();
    }

    /**
     * 时间转换
     * @param timestamp
     * @return
     */
    @SuppressLint("ConstantConditions")
    public static String displayTime(Context context, long timestamp)  {
        String timeStr;
        //modify by zya 20161201
        SimpleDateFormat yh = getFormat(ActomaController.getApp().getString(R.string.year_month_day));
        SimpleDateFormat mh = getFormat(ActomaController.getApp().getString(R.string.month_day));

        /*SimpleDateFormat yh = new SimpleDateFormat("y" + ActomaController.getApp().getString(R.string.year) + "M"+
                ActomaController.getApp().getString(R.string.month) + "d" + ActomaController.getApp().getString(R.string.day));
        SimpleDateFormat mh = new SimpleDateFormat("M" + ActomaController.getApp().getString(R.string.month) +
                "d" + ActomaController.getApp().getString(R.string.day));*/
        //end by zya
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        long currentSeconds = System.currentTimeMillis();//系统当前时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentSeconds);

        //系统当前年份、当前是哪一天
        int currentYear = cal.get(Calendar.YEAR);
        int currentDay  = cal.get(Calendar.DAY_OF_MONTH);

        //参数的年份，哪一天
        cal.setTimeInMillis(timestamp);
        int lastYear = cal.get(Calendar.YEAR);
        int lastDay  = cal.get(Calendar.DAY_OF_MONTH);



        if (currentYear!= lastYear) {
            timeStr = yh.format(timestamp) + "   " + hm.format(timestamp);
            return timeStr;
        }
        if (currentDay==lastDay) {
            // 今天
            timeStr = context.getResources().getString(R.string.today) + "   "  + hm.format(timestamp);
        } else if (((currentDay-lastDay)==1)) {
            // 昨天
            timeStr = context.getResources().getString(R.string.yesterday) + "   "  + hm.format(timestamp);
        }
        else {
            //今年
            if(currentYear==lastYear) {
                timeStr = mh.format(timestamp)+ "   " + hm.format(timestamp);
            }else{
                timeStr = yh.format(timestamp)+ "   " + hm.format(timestamp);
            }

        }

        return timeStr;
    }

    /**
     * 时间转换
     * @param timestamp
     * @return
     */
    public static String chatListDisplayTime(Context context, long timestamp)  {

        //今天  时间
        //昨天  昨天
        //当年  月日
        //其他  年月日
        String timeStr;
        //modify by zya 20161201
        SimpleDateFormat yh = new SimpleDateFormat(ActomaController.getApp().getString(R.string.year_month_day));
        SimpleDateFormat mh = new SimpleDateFormat(ActomaController.getApp().getString(R.string.month_day));

        /*SimpleDateFormat yh = new SimpleDateFormat("y" + ActomaController.getApp().getString(R.string.year) + "M" +ActomaController.getApp().getString(R.string.month) + "d" + ActomaController.getApp().getString(R.string.day));
        SimpleDateFormat mh = new SimpleDateFormat("M" + ActomaController.getApp().getString(R.string.month) + "d" + ActomaController.getApp().getString(R.string.day));*/
        //end by zya
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        long currentSeconds = System.currentTimeMillis();//系统当前时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentSeconds);

        //系统当前年份、当前是哪一天
        int currentYear = cal.get(Calendar.YEAR);
        int currentDay  = cal.get(Calendar.DAY_OF_MONTH);

        //参数的年份，哪一天
        cal.setTimeInMillis(timestamp);
        int lastYear = cal.get(Calendar.YEAR);
        int lastDay  = cal.get(Calendar.DAY_OF_MONTH);



        if (currentYear!= lastYear) {
            timeStr = yh.format(timestamp);
            return timeStr;
        }
        if (currentDay==lastDay) {
            // 今天
            timeStr =  hm.format(timestamp);
        } else if (((currentDay-lastDay)==1)) {
            // 昨天
            timeStr = context.getResources().getString(R.string.yesterday);
        }
        else {
            //今年
            if(currentYear==lastYear) {
                timeStr = mh.format(timestamp);
            }else{
                timeStr = yh.format(timestamp);
            }

        }

        return timeStr;
    }

    /**
     * 文件相关时间进行转化
     *
     * @param timestamp
     * @return
     */
    public static String convertFileModifyDate(long timestamp) {
        if (timestamp == 0) {
            return "";
        }
        return getFormat(YMDHM).format(timestamp);
    }
	
	//add by zya
	public static String contentWeekOfTime(Context context,long time){
        long currTime = System.currentTimeMillis();
        long weekTime = currTime - 7 * 24 * 3600 * 1000;

        SimpleDateFormat format = getFormat(context.getString(R.string.history_file_year_month));

        if(time <= currTime && time > weekTime){
            return context.getString(R.string.history_file_category_week);
        } else {
            return format.format(time);
        }
    }

    public static String displayShowTime(Context context, long timestamp)  {
        SimpleDateFormat yh = getFormat(context.getString(R.string.file_show_time));
        SimpleDateFormat hm = getFormat("HH:mm");
        return yh.format(timestamp)+ "   " + hm.format(timestamp);
    }

    public static boolean isOverdue(long time) {
        long currTime = System.currentTimeMillis();
        long result = currTime - time;
        if(result < 0){
            return true;
        } else {
            return result > 7 * 24 * 3600 * 1000 ;
        }
    }

    public static String stringOfOverdue(Context context,long time){
        SimpleDateFormat yh = getFormat(context.getString(R.string.file_show_time));
        if(!isOverdue(time)){
            long overdueTime = time + 7 * 24 * 3600 * 1000;
            return yh.format(overdueTime);
        }
        return "";
    }
}
