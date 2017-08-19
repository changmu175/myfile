package com.xdja.dependence.uitls;

import com.orhanobut.logger.Logger;

/**
 * Log工具类
 *
 * @author hkb
 * @since 2014年11月12日
 */
public class LogUtil {
    private static boolean logFlag = true;

    public static void setLogFlag(boolean logFlag) {
        LogUtil.logFlag = logFlag;
    }

    public static boolean getLogFlag() {
        return logFlag;
    }

    public final static String tag = "actomaFrame";
//    private final static int logLevel = Log.VERBOSE;
//    private String mClassName;

    private static LogUtil logger;

    private LogUtil() {
//        mClassName = name;
//        Logger.init(name);
    }

    public static LogUtil getUtils(String Tag) {
        if (logger == null) {
            logger = new LogUtil();
        }
        Logger.init(Tag);
        return logger;
    }

    public static LogUtil getUtils() {
        if (logger == null) {
            logger = new LogUtil();
        }
        Logger.init(tag);
        return logger;
    }

//    /**
//     * 获取当前方法路径名
//     *
//     * @return 当前方法路径名
//     * @作者 hkb
//     * @since 2014年11月12日 下午5:13:27
//     */
//    private String getFunctionName() {
//        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
//        if (sts == null) {
//            return null;
//        }
//        for (StackTraceElement st : sts) {
//            if (st.isNativeMethod()) {
//                continue;
//            }
//            if (st.getClassName().equals(Thread.class.getName())) {
//                continue;
//            }
//            if (st.getClassName().equals(this.getClass().getName())) {
//                continue;
//            }
//            return mClassName + "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":"
//                    + st.getLineNumber() + " " + st.getMethodName() + " ]";
//        }
//        return null;
//    }

    /**
     * Info 级别日志
     *
     * @param str log messages
     * @since 2014年11月12日 下午5:14:50
     */
    public void i(Object str) {
        if (logFlag) {
//            if (logLevel <= Log.INFO) {
//                Logger.i(str.toString());
//            }
            Logger.i(str != null ? str.toString() : "");
        }

    }

    /**
     * Debug 级别日志
     *
     * @param str log messages
     * @since 2014年11月12日 下午5:15:27
     */
    public void d(Object str) {
        if (logFlag) {
//            if (logLevel <= Log.DEBUG) {
//                Logger.d(str.toString());
//            }
            Logger.d(str != null ? str.toString() : "");
        }
    }

    /**
     * Verbose 级别日志
     *
     * @param str log messages
     * @since 2014年11月12日 下午5:16:00
     */
    public void v(Object str) {
        if (logFlag) {
//            if (logLevel <= Log.VERBOSE) {
//                Logger.v(str.toString());
//            }
            Logger.v(str != null ? str.toString() : "");
        }
    }

    /**
     * Warn 级别日志
     *
     * @param str log messages
     * @since 2014年11月12日 下午5:16:07
     */
    public void w(Object str) {
        if (true/*logFlag*/) {
//            if (logLevel <= Log.WARN) {
//                Logger.w(str.toString());
//            }
            Logger.w(str != null ? str.toString() : "");
        }
    }

    /**
     * Error 级别日志
     *
     * @param str log messages
     * @since 2014年11月12日 下午5:16:55
     */
    public void e(Object str) {
//        if (logFlag) {
//            if (logLevel <= Log.ERROR) {
//                Logger.e(str.toString());
//            }
//        }
        Logger.e(str != null ? str.toString() : "");
    }

    /**
     * Error 异常信息日志
     *
     * @param ex Exception
     * @since 2014年11月12日 下午5:17:14
     */
    public void e(Exception ex) {
//        if (/*logFlag*/true) {
//            if (logLevel <= Log.ERROR) {
//                Logger.e(ex, null);
//            }
//        }
        Logger.e(ex, null);
    }

    /**
     * Error 异常信息日志
     *
     * @param log log messages
     * @param tr  Throwable
     * @since 2014年11月12日 下午5:17:14
     */
    public void e(String log, Throwable tr) {
//        if (logFlag) {
//            Logger.e(tr, log);
//        }
        Logger.e(tr, log);

    }

    public void json(String json) {
        if (logFlag) {
            Logger.json(json);
        }
    }

    public void xml(String xml) {
        if (logFlag) {
            Logger.xml(xml);
        }
    }

    public boolean isDebug() {
        return logFlag;
    }


}
