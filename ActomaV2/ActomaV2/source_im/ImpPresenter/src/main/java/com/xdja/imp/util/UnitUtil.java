package com.xdja.imp.util;


import android.content.Context;

import com.xdja.imp.R;

import java.text.DecimalFormat;

/**
 * 项目名称：安通+V2             <br>
 * 类描述  ：单位格式转化     <br>
 * 创建时间：2017/2/23       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class UnitUtil {

    /**
     * 获取短视频时长显示格式
     * @param duration 短视频时长
     * @return 返回短视频显示时长
     */
    public static String getVideoRecordDuration (int duration, Context context) {
        return duration + context.getResources().getString(R.string.video_record_unit);
    }

    /**
     * 获取短视频录制显示格式
     * @param duration 短视频时长
     * @return 返回短视频显示时长
     */
    public static String getVideoDuration(int duration) {
        if (duration >= 10) {
            return "00:" + duration;
        } else {
            return "00:0" + duration;
        }
    }
    /**
     * 获取短视频文件大小格式
     * @param size 当前短视频
     * @return 返回短视频大小显示格式
     */
    public static String getVideoFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormat df1 = new DecimalFormat("#");
        DecimalFormat df2 = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
            fileSizeString = df1.format((int) size) + "B";
            if(fileSizeString.contains(".")){
                if(fileSizeString.contains(".")){
                    String[] s = fileSizeString.split("\\.");
                    fileSizeString = s[0] + "B";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (size < 1048576) {
            //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. begin
            if(size > 1024 * 1000){
                fileSizeString = "1MB";
                //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. end
            }else{
                if((int)size / 1024 < 100){
                    fileSizeString = df.format((int) size / 1024) + "KB";
                }else{
                    fileSizeString = df1.format((int) size / 1024) + "KB";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (size < 1073741824) {
            if((double) size / 1048576 >= 10){
                fileSizeString = df.format((double) size / 1048576) + "MB";
            }else{
                fileSizeString = df2.format((double) size / 1048576) + "MB";
            }
            //guorong@xdja.com 解决大小在图片大于10M的时候图片大小显示问题. end
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

}
