package com.xdja.imp.util;

import com.xdja.dependence.uitls.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * 计算文件大小
 * Created by leill on 2016/6/21.
 */
public class FileSizeUtils {

    private static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    private static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    private static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    private static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils().d("获取文件大小失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getUtils().d("获取文件大小失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 根据文件路径获取文件大小
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath){

        File file = new File(filePath);
        if (file.exists()){
            return file.length();
        }
        return 0;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            LogUtil.getUtils().w("文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (File aFlist : flist) {
            if (aFlist.isDirectory()) {
                size = size + getFileSizes(aFlist);
            } else {
                size = size + getFileSize(aFlist);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
            if(fileSizeString.contains(".")){
                String[] s = fileSizeString.split("\\.");
                fileSizeString = s[0] + "B";
            }
			//guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
			//guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
            if(fileSizeString.contains(".")){
                String[] s = fileSizeString.split("\\.");
                fileSizeString = s[0] + "KB";
            }
			//guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取文件大小，目前仅为聊天详情界面文件消息显示提供
     * @param fileS
     * @return
     * @Author guorong
     */
    public static String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormat df1 = new DecimalFormat("#");
        DecimalFormat df2 = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
            fileSizeString = df1.format((int) fileS) + "B";
            if(fileSizeString.contains(".")){
                if(fileSizeString.contains(".")){
                    String[] s = fileSizeString.split("\\.");
                    fileSizeString = s[0] + "B";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1048576) {
            //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. begin
            if(fileS > 1024 * 1000){
                fileSizeString = "1MB";
                //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. end
            }else{
                if((int)fileS / 1024 < 100){
                    fileSizeString = df.format((int) fileS / 1024) + "KB";
                }else{
                    fileSizeString = df1.format((int) fileS / 1024) + "KB";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1073741824) {
            if((double) fileS / 1048576 >= 10){
                fileSizeString = df.format((double) fileS / 1048576) + "MB";
            }else{
                fileSizeString = df2.format((double) fileS / 1048576) + "MB";
            }
            //guorong@xdja.com 解决大小在图片大于10M的时候图片大小显示问题. end
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
