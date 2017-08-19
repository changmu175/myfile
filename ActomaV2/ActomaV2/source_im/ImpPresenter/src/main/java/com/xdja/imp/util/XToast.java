package com.xdja.imp.util;

import android.content.Context;
import android.widget.Toast;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.util</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/12</p>
 * <p>Time:10:41</p>
 */
public class XToast implements MsgDisplay {

    private Context context;

    private static Toast mToast;

    @Inject
    public XToast(@Scoped(value = DiConfig.CONTEXT_SCOPE_APP) Context context){
        this.context = context;
    }

    @Override
    public void display(String textMsg) {
        if (mToast == null){
            mToast = Toast.makeText(context, textMsg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(textMsg);
        }
        mToast.show();
    }

    @Override
    public void display(int resId) {
        if (mToast == null){
            mToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    //add by jyg@xdja.com 2017/3/1
    @Override
    public void display(int resId, int gravity, int x, int y, int type) {
        if (mToast == null){
            mToast = Toast.makeText(context, resId, type);
        } else {
            mToast.setText(resId);
        }
        mToast.setGravity(gravity, x, y);
        mToast.show();
    }
}
