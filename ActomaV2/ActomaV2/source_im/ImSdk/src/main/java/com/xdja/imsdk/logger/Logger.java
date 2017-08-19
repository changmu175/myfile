package com.xdja.imsdk.logger;

import android.util.Log;

import java.util.Hashtable;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk log 管理                   <br>
 * 创建时间：2016/11/21 20:18                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class Logger {
    public final static String tag = "XdjaImSdkLog";

    private final static boolean logFlag = true;
    private final static int logLevel = Log.VERBOSE;// Log.VERBOSE(Debug), Log.INFO(Release)
    private static Hashtable<String, Logger> loggerTable = new Hashtable<String, Logger>();
    private static Logger logger;
    private static boolean isDebug = logLevel < Log.DEBUG;
    private String className;

    private Logger(String name) {
        className = name;
    }

    @SuppressWarnings("unused")
    private static Logger getLoggerClass(String className) {
        Logger classLogger = (Logger) loggerTable.get(className);
        if (classLogger == null) {
            classLogger = new Logger(className);
            loggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    public static Logger getLogger(String Tag) {
        if (logger == null) {
            logger = new Logger(Tag);
        }
        return logger;
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger(tag);
        }
        return logger;
    }

    /**
     * Verbose(2) 级别日志
     * @param str String
     */
    public void v(Object str) {
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * Debug(3) 级别日志
     * @param str String
     */
    public void d(Object str) {
        if (logFlag) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                } else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * Info(4) 级别日志
     * @param str String
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " - " + str);
                } else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * Warn(5) 级别日志
     * @param str String
     */
    public void w(Object str) {
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * Error(6) 级别日志
     * @param str String
     */
    public void e(Object str) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                } else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * Error(6) 异常信息日志
     * @param ex exception
     */
    public void e(Exception ex) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * Error(6) 异常信息日志
     * @param log String
     * @param tr throwable
     */
    public void e(String log, Throwable tr) {
        if (logFlag) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + className + line + ":] " + log
                    + "\n", tr);
        }
    }

    /**
     * 获取当前方法名
     * @return 方法名
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            Thread t = Thread.currentThread();
            long id = t.getId();
            String name = t.getName();
            long priority = t.getPriority();
            String groupName = t.getThreadGroup().getName();

            if (isDebug) {
                return "[P(p:" + android.os.Process.myPid() +
                        ", t:" + android.os.Process.myTid() +
                        ", u:" + android.os.Process.myUid() +
                        "), T(i:" + id +
                        ", n:" + name +
                        ", p:" + priority +
                        ", g:" + groupName +
                        "): " + st.getFileName() + ":"
                        + st.getLineNumber() + " " + st.getMethodName() + " ]";

            } else {
                return "(id: " + id + ", name:" + name + "): " + st.getFileName() + ":"
                        + st.getLineNumber() + " " + st.getMethodName() + " ]";
            }

        }
        return null;
    }
}
