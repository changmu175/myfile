package com.securevoip.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.xdja.comm.server.ActomaController;
import com.xdja.voipsdk.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 通话记录详情显示时间天、小时、分钟、秒
 */
public class ChatSourceHelper {
	/**
	 * 十分钟的间隔显示时间线
	 */
	public final static int TIMELINE_SIZE = 5 * 60 * 1000;
	/**
	 * 一天的时间
	 */
	private final static int TIME_UNINE = 24 * 60 * 60 * 1000;

	/**
	 * 一年的时间
	 */
	@SuppressLint("NumericOverflow")
	private final static int TIME_UNINE_YEAR = 365*24 * 60 * 60 * 1000;
	/**
	 * 允许的时间偏差
	 */
	public final static int TIME_OFFSET = 60 * 1000;

	/**
	 * 通话时长，如40分30秒
	 * 小时、分、秒的分割
	 * @param resources
	 * @param duration
	 * @return
	 */
    public static String makeDurationString(Resources resources, long duration) {
        StringBuilder str = new StringBuilder();
        long second = duration;
        int hours = (int)second/3600;
        if (hours>0) {
            str.append(hours);
            str.append(resources.getString(R.string.call_duration_string_hour));
            second -= hours*3600;
        }
        int min = (int)second/60;
        if (min > 0) {
            str.append(min);
            str.append(resources.getString(R.string.call_duration_string_minute));
            second -= min*60;
        } else {
			//0分就不显示
            /*str.append(0);
            str.append(resources.getString(R.string.call_duration_string_minute));*/
        }
        if (second>0) {
            str.append(second);
            str.append(resources.getString(R.string.call_duration_string_second));
			if (str.toString().startsWith("0")) {
				str = new StringBuilder(str.toString().replaceFirst("0", ""));
			}
        } else {
            str.append(0);
            str.append(resources.getString(R.string.call_duration_string_second));
        }
        str.append("");
        return str.toString();
    }

	 /**
	  * 某一天的小时和分，如13:56
	  * @param time
	  * @return
	  */
    public static String formatTimeHM(long time) {

		/**2016-12-2-wangzhen-start-modify：Change static final attrs to be values **/
		String DATE_FORMAT_3 = ActomaController.getApp().getString(R.string.DATE_FORMAT_3);
		/**2016-12-2 -wangzhen-end**/
        if (time <= 0) {
            return null;
        }
        Date date = new Date(time);
        return formatTimeStr(DATE_FORMAT_3, date);
    }

	/**
	 * 格式化时间提示字符串,如今天
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeWarnning(long time) {

		/**2016-12-2-wangzhen-start-modify：Change static final attrs to be values **/
		String DATE_FORMAT_0 =ActomaController.getApp().getString(R.string.DATE_FORMAT_0);
		String DATE_FORMAT_2 =ActomaController.getApp().getString(R.string.DATE_FORMAT_2);
		String DATE_FORMAT_3 =ActomaController.getApp().getString(R.string.DATE_FORMAT_3);
		/**2016-12-2 -wangzhen-end**/
		if (time <= 0) {
			return null;
		}
		Date date = new Date(time);
		// 时间差
		long timeDiff = System.currentTimeMillis() - time;
		// 当天0点到当前的时间差
		long tiemOffsetDiff = System.currentTimeMillis() - getTodayBeginTime();
		// 不再当天之内
		if (timeDiff > tiemOffsetDiff) {
			if (timeDiff > (tiemOffsetDiff + TIME_UNINE_YEAR)) {
				return formatTimeStr(DATE_FORMAT_0, date);
			}
			// 昨天之前
			else if (timeDiff > (tiemOffsetDiff + TIME_UNINE)) {
				return formatTimeStr(DATE_FORMAT_2, date);
			} else {// 昨天之内
				return ActomaController.getApp().getString(R.string.YESTERDAY) + "  "+formatTimeStr(DATE_FORMAT_3, date);
			}
		} else {// 当天之内
			return formatTimeStr(DATE_FORMAT_3, date);
		}
	}

	 /**
	  * 归类到哪一天，如今天
	  * @param time
	  * @return
	  */
	public static String formatTimeDayUnit(long time) {

		/**2016-12-2-wangzhen-start-modify：Change static final attrs to be values **/
		String DATE_FORMAT_0 =ActomaController.getApp().getString(R.string.DATE_FORMAT_0);
		String DATE_FORMAT_2 =ActomaController.getApp().getString(R.string.DATE_FORMAT_2);
		/**2016-12-2 -wangzhen-end**/
		if (time <= 0) {
			return null;
		}
		Date date = new Date(time);
		// 时间差
		long timeDiff = System.currentTimeMillis() - time;
		// 当天0点到当前的时间差
		long tiemOffsetDiff = System.currentTimeMillis() - getTodayBeginTime();
		// 不再当天之内
		if (timeDiff > tiemOffsetDiff) {
			if (timeDiff > (tiemOffsetDiff + TIME_UNINE_YEAR)) {
				return formatTimeStr(DATE_FORMAT_0, date);
			}
			// 昨天之前
			else if (timeDiff > (tiemOffsetDiff + TIME_UNINE)) {
				return formatTimeStr(DATE_FORMAT_2, date);
			} else {// 昨天之内
				return ActomaController.getApp().getString(R.string.YESTERDAY);
			}
		}else if(timeDiff < 0 && Math.abs(timeDiff) < Math.abs(TIME_UNINE - tiemOffsetDiff)){
			return ActomaController.getApp().getString(R.string.TODAY);
		}else if(timeDiff < 0 && Math.abs(timeDiff) > Math.abs(TIME_UNINE - tiemOffsetDiff)) {
			return formatTimeStr(DATE_FORMAT_0, date);
		}else {// 当天之内
			return ActomaController.getApp().getString(R.string.TODAY);
		}
	}

	public static String formatTimeDayFragment(long time) {

		/**2016-12-2-wangzhen-start-modify：Change static final attrs to be values **/
		String DATE_FORMAT_4 =ActomaController.getApp().getString(R.string.DATE_FORMAT_4);
		/**2016-12-2 -wangzhen-end**/
		if (time <= 0) {
			return null;
		}
		Date date = new Date(time);
		return formatTimeStr(DATE_FORMAT_4, date);
	}
	/**
	 * 获取当天0点时间
	 * 
	 * @return
	 */
	public static long getTodayBeginTime() {
		// 获得当天0点时间
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime();
	}

	@SuppressLint("AndroidLintSimpleDateFormat")
	public static String formatTimeStr(String formater, Date date) {
		SimpleDateFormat format = new SimpleDateFormat(formater);
		return format.format(date);
	}

//	private static final String DATE_FORMAT_0 = ActomaController.getApp().getString(R.string.DATE_FORMAT_0);
//	private static final String DATE_FORMAT_1 = ActomaController.getApp().getString(R.string.DATE_FORMAT_1) + "    " +ActomaController.getApp().getString(R.string.DATE_FORMAT_1_1);
//	private static final String DATE_FORMAT_2 = ActomaController.getApp().getString(R.string.DATE_FORMAT_2);
//	private static final String DATE_FORMAT_3 = ActomaController.getApp().getString(R.string.DATE_FORMAT_3);
//	private static final String DATE_FORMAT_4 = ActomaController.getApp().getString(R.string.DATE_FORMAT_4);

}
