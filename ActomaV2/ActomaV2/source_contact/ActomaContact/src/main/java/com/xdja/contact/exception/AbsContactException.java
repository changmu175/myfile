package com.xdja.contact.exception;

import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by wanghao on 2016/3/9.
 *  联系人模块所有异常处理基类
 */
public abstract class AbsContactException extends Exception {

    protected final int default_line_numeber = -1;

    protected String className;

    protected String methodName;

    protected String fileName;

    protected int lineNumber = default_line_numeber;

    public AbsContactException(String message){
        super(message);
        printTrace();
    }

    public AbsContactException(Exception exception){
        super(exception);
        template(exception);
    }

    protected void template(Exception e){
        StackTraceElement[] elements = e.getStackTrace();
        StackTraceElement element = elements[elements.length-1];
        this.methodName = element.getMethodName();
        this.className = element.getClassName();
        this.fileName = element.getFileName();
        this.lineNumber = element.getLineNumber();
        LogUtil.getUtils().e("AbsContactException error:"+e.getMessage());
        //printException();
    }

    protected abstract String getClsName();

    protected void printException(){
        StringBuffer errorInfo = new StringBuffer();
        errorInfo.append("出现问题:文件名%1$s");
        errorInfo.append("类名称:%2$s");
        errorInfo.append("函数名:%3$s");
        errorInfo.append("行数:%4$s");
        String errorResult = String.format(errorInfo.toString(), fileName, className, methodName, lineNumber);
        LogUtil.getUtils().i(":" + errorResult);
    }

    public void printTrace(){
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement o :elements ) {
            LogUtil.getUtils().i("文件名:"+o.getClassName() + "函数名:"+ o.getMethodName() + "调用函数" + o.getLineNumber());
        }
    }

}
