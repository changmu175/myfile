package com.xdja.imp.handler;

import com.xdja.imp.data.error.OkException;
import com.xdja.imp.data.error.OkHandler;
import com.xdja.imp.data.error.OkNetMatcher;
import com.xdja.dependence.uitls.LogUtil;

import rx.Subscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:16:26</p>
 */
public class OkSubscriber<T> extends Subscriber<T> {

    private final OkHandler<OkException> handler;

//    public OkSubscriber() {
//
//    }

    public OkSubscriber(OkHandler<OkException> handler) {
        this.handler = handler;
    }

    @Override
    public void onCompleted() {
        LogUtil.getUtils().i("---------------------onCompleted-------------------");

    }

    @Override
    public void onError(Throwable e) {
        LogUtil.getUtils().e("---------------------onError-------------------");

        if (e == null) {
            return;
        }

        if (e instanceof OkException) {

            OkException oe = (OkException) e;

          //fix bug 2318 by licong,review by zya, 2016/8/5
          if (oe.getOkMessage() != null && oe.getOkMessage().contains("ExtCertPathValidatorException")) {
              oe.setOkCode(OkNetMatcher.CODE_SSLHANDLE_FAILD);
              oe.setOkMessage(OkNetMatcher.USSLHANDLE_FAILD_USERMSG);
          }
          if (handler != null) {
              handler.handle(oe);
          }//end
      } else {
          LogUtil.getUtils().e(e.getMessage());

      }
    }

    @Override
    public void onNext(T t) {
        LogUtil.getUtils().i("---------------------onNext-------------------");

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.getUtils().i("---------------------onStart-------------------");

    }
}
