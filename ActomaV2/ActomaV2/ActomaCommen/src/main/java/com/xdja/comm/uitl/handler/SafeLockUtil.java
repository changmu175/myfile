package com.xdja.comm.uitl.handler;

/**
 * Created by xdjaxa on 2016/12/30.
 */
public class SafeLockUtil {
    //是否使用相机
    private static boolean useCameraOrFile = false;
    //是否转发消息
    private static boolean isForwardMessage = false;

    public static boolean isForwardMessage() {
        return isForwardMessage;
    }

    public static void setIsForwardMessage(boolean isForwardMessage) {
        SafeLockUtil.isForwardMessage = isForwardMessage;
    }

    public static boolean isUseCameraOrFile() {
        return useCameraOrFile;
    }

    public static void setUseCameraOrFile(boolean useCameraOrFile) {
        SafeLockUtil.useCameraOrFile = useCameraOrFile;
    }
}
