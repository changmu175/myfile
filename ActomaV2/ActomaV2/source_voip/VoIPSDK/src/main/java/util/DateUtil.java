package util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by zjc on 2016/5/13.
 */
public class DateUtil {

     public static long randomTimeStamp() {
          Time time = formatTimeStamp(System.currentTimeMillis());

          Random random = new Random();

          StringBuilder sb = new StringBuilder();
          sb.append(formatTime(time.getYear()));
          sb.append(formatTime(random.nextInt(time.getMonth()) + 1));
          sb.append(formatTime(random.nextInt(time.getDay()) + 1));
          sb.append(formatTime(random.nextInt(time.getHour()) + 1));
          sb.append(formatTime(random.nextInt(time.getMinute()) + 1));
          sb.append(formatTime(random.nextInt(time.getSecond()) + 1));

          return time2TimeStamp(sb.toString());
     }

     /**
      * 时间戳格式化成时分秒的数字字符
      *
      * @param timeStamp
      * @return
      */
     private static Time formatTimeStamp(long timeStamp) {
          String currentTime = timeStamp2Time(timeStamp);
          Time time = new Time();
          time.setYear(Integer.parseInt(currentTime.substring(0, 4)));
          time.setMonth(Integer.parseInt(currentTime.substring(4, 6)));
          time.setDay(Integer.parseInt(currentTime.substring(6, 8)));
          return time;
     }

     static class Time {
          private int year;
          private int month;
          private int day;
          private final int hour = 12;
          private final int minute = 59;
          private final int second = 59;

          public int getYear() {
               return year;
          }

          public void setYear(int year) {
               this.year = year;
          }

          public int getMonth() {
               return month;
          }

          public void setMonth(int month) {
               this.month = month;
          }

          public int getDay() {
               return day;
          }

          public void setDay(int day) {
               this.day = day;
          }

          public int getHour() {
               return hour;
          }

          public int getMinute() {
               return minute;
          }

          public int getSecond() {
               return second;
          }
     }

     /**
      * 时分秒，个位数首位补零
      *
      * @param time
      * @return
      */
     public static String formatTime(int time) {
          return String.format("%02d", time);
     }

     /**
      * 时间戳转换成时分秒格式的数字字符串
      *
      * @param timeStamp
      * @return
      */
     @SuppressLint("AndroidLintSimpleDateFormat")
     public static String timeStamp2Time(long timeStamp) {
          SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
          //Long time = new Long(timeStamp);
          Long time = timeStamp;
          String d = format.format(time);
          Date date = null;
          try {
               date = format.parse(d);
          } catch (ParseException e) {
               e.printStackTrace();
          }
          return d;
     }

     /**
      * 时分秒格式的数字字符串转换成时间戳
      *
      * @param time
      * @return
      */
     @SuppressLint("AndroidLintSimpleDateFormat")
     private static long time2TimeStamp(String time) {
          SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
          Date date = null;
          try {
               date = format.parse(time);
          } catch (ParseException e) {
               e.printStackTrace();
          }
          return date.getTime();
     }

}
