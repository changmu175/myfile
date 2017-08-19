package com.securevoip.utils;

import java.io.File;

public class FileNameUtil {

    /**
     * 去除文件名后缀
     * @param filename
     * @return
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }

    /**
     * 获取文件名中逗号
     * @param filename
     * @return
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    /**
     * 获取最后一个文件分隔符
     * @param filename
     * @return
     */
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        return Math.max(lastUnixPos, lastWindowsPos);
    }


    /**
     * 获取文件后缀名
     * @param filePath
     * @return
     */
    public static String getFileSuffix(String filePath) {
        if (ObjectUtil.stringIsEmpty(filePath) || !new File(filePath).isFile()) return "";
        int extensionPos = filePath.lastIndexOf('.');
        int lastSeparator = indexOfLastSeparator(filePath);
        return lastSeparator > extensionPos ? "" : filePath.substring( extensionPos + 1);
    }

    /**
     * 获取文件名
     * @param filename
     * @return
     */
    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? filename.substring( lastSeparator + 1) : filename.substring( extensionPos + 1);
    }

    /**
     * 获取去除后缀名之后的文件名
     */
    public static String getFileName(String filename) {
        return removeExtension(getName(filename));
    }
}
