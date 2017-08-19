package com.xdja.safeauth.log;

/**
 * Created by THZ on 2016/5/18.
 */
public class Log {
    /**
     * V
     */
    public static final int V_LEVEL = 1;

    /**
     * D
     */
    public static final int D_LEVEL = 2;

    /**
     * I
     */
    public static final int I_LEVEL = 3;

    /**
     * W
     */
    public static final int W_LEVEL = 4;

    /**
     * E
     */
    public static final int E_LEVEL = 5;

    /**
     * 高级别
     */
    public static final int H_LEVEL = 6;

    /**
     * 1 v
     * 2 d
     * 3 i
     * 4 w
     * 5 e
     */
    public static int logLevel = 0;


    /**
     * 日志的前标签
     */
    public static final String LOGTAG_BEFORE = "safeauth_";

    /**
     * 打印日志e
     * @param tag 标识
     * @param msg 内容
     */
    public static void e(String tag, String msg) {
        if (logLevel < H_LEVEL) {
            android.util.Log.e(LOGTAG_BEFORE + tag, msg);
        }
    }

    /**
     * 打印日志w
     * @param tag 标识
     * @param msg 内容
     */
    public static void w(String tag, String msg) {
        if (logLevel < E_LEVEL) {
            android.util.Log.w(LOGTAG_BEFORE + tag, msg);
        }
    }


    /**
     * 打印日志i
     * @param tag 标识
     * @param msg 内容
     */
    public static void i(String tag, String msg) {
        if (logLevel < W_LEVEL) {
            android.util.Log.i(LOGTAG_BEFORE + tag, msg);
        }
    }

    /**
     * 打印日志d
     * @param tag 标识
     * @param msg 内容
     */
    public static void d(String tag, String msg) {
        if (logLevel < I_LEVEL) {
            android.util.Log.d(LOGTAG_BEFORE + tag, msg);
        }
    }

    /**
     * 打印日志v
     * @param tag 标识
     * @param msg 内容
     */
    public static void v(String tag, String msg) {
        if (logLevel < D_LEVEL) {
            android.util.Log.v(LOGTAG_BEFORE + tag, msg);
        }
    }
}
