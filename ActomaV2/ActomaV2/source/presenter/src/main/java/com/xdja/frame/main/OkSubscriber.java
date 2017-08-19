package com.xdja.frame.main;

import android.app.Activity;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.NetworkException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.matcher.OkMatcher;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.R;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

import rx.Subscriber;

/**
 * <p>Summary:可以处理OkException的观察者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dependence.exeptions</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:11:08</p>
 */
public class OkSubscriber<T> extends Subscriber<T> {

    private OkMatcher matcher;
    private ExceptionHandler handler;
    /**
     * 业务标识
     */
    private String mark = "default_mark";

    /**
     * 设置错误信息匹配对象
     *
     * @param matcher 错误信息匹配对象
     */
    public void setMatcher(OkMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * 设置错误处理对象
     *
     * @param handler 错误处理对象
     */
    public void setHandler(ExceptionHandler handler) {
        this.handler = handler;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public OkSubscriber(OkMatcher matcher, ExceptionHandler handler) {
        this.matcher = matcher;
        this.handler = handler;
    }

    public OkSubscriber(OkMatcher matcher, ExceptionHandler handler, String mark) {
        this.matcher = matcher;
        this.handler = handler;
        this.mark = mark;
    }


    public OkSubscriber() {
    }

    @Override
    public void onNext(T t) {
        LogUtil.getUtils().i("=================onNext=================");
    }

    @Override
    public void onCompleted() {
        LogUtil.getUtils().i("=================onCompleted=================");
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.getUtils().e("=================onError=================");
        if (e == null) {
            return;
        }
        if (LogUtil.getLogFlag()) {
            e.printStackTrace();
        }
        LogUtil.getUtils().e(e.getStackTrace());

        if (e instanceof OkException) {
            OkException okException = (OkException) e;
            LogUtil.getUtils().e("捕获到OkException : " + okException.toString());

            String matchResult = "";
            if (this.matcher != null) {
                matchResult = matcher.match(okException);
                 //[S] modify by tangsha for only prompt defined exception
                /*if (TextUtils.isEmpty(matchResult)) {
                    matchResult = okException.getMessage();
                }*/
                //[E] modify by tangsha for only prompt defined exception
                if (TextUtils.isEmpty(matchResult)) {
                    LogUtil.getUtils().e("匹配到的用户信息为空");
                } else {
                    LogUtil.getUtils().e("匹配到的用户信息为 ： " + matchResult);
                }
            }

            if (this.handler != null) {
                boolean result = this.handler.handleOkException(
                        okException.getOkCode(),
                        matchResult,
                        okException,
                        mark
                );
                if (result) {
                    this.handler.defaultOkException(
                            okException.getOkCode(),
                            matchResult,
                            okException,
                            mark
                    );
                }
            }

        } else {
            //modify by alh@xdja.com to fix bug: 1118 2016-07-04 start (rummager : fanjiandong)
            LogUtil.getUtils().e("捕获到未定义的异常 : " + e + "," + e.getMessage());
            if (e instanceof java.net.SocketTimeoutException || e instanceof java.net.ConnectException) {
                Activity topActivity = ActivityStack.getInstanse().getTopActivity();
                int resId = R.string.net_timeout_and_retry;
                if (topActivity != null) {
                    if (!NetworkUtil.isNetworkConnect(topActivity.getApplicationContext())) {
                        resId = R.string.net_disable;
                    }
                }
                e = new NetworkException(NetworkException.CODE_NETWORK_CONN_FAILD, resId);
                if (e instanceof java.net.SocketTimeoutException || e instanceof java.net.ConnectException) {
                    e = new NetworkException(NetworkException.CODE_NETWORK_CONN_FAILD , R.string.net_timeout_and_retry);
                }
                //modify by alh@xdja.com to fix bug: 2221 2016-07-29 start(rummager:anlihuang)
            } else if (e instanceof SSLHandshakeException || (e.getMessage() != null && e.getMessage().contains("ExtCertPathValidatorException"))) {
                //modify by alh@xdja.com to fix bug: 2221 2016-07-29 end(rummager:anlihuang)
                e = new NetworkException(NetworkException.CODE_SSLHANDLE_FAILD , R.string.net_time_wrong_ex);
                //modify by xnn@xdja.com to fix bug: 1277 2016-07-08 start (rummager : wangchao1)
            } else if (e instanceof IOException || (e.getMessage() != null && e.getMessage().contains("unexpected end of stream"))) {
                e = new NetworkException(NetworkException.CODE_UNEXPECTED_END_OF_STREAM , R.string.net_timeout_and_retry);
            }
            //modify by xnn@xdja.com to fix bug: 1277 2016-07-08 end (rummager : wangchao1)

            if (this.handler != null) {
                boolean result = this.handler.handlerThrowable(e, mark);
                if (result) {
                    this.handler.defaultThrowable(e, mark);
                }
            }
            //modify by alh@xdja.com to fix bug: 1118 2016-07-04 end (rummager : fanjiandong)
        }
    }
}
