package com.xdja.imp.handler;

import android.support.annotation.Nullable;

import com.xdja.imp.data.error.OkException;
import com.xdja.imp.data.error.OkHandler;
import com.xdja.imp.util.MsgDisplay;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/9</p>
 * <p>Time:16:46</p>
 */
public class OkHandlerImp<T extends OkException> implements OkHandler<T> {

    private final MsgDisplay display;

    @Inject
    public OkHandlerImp(MsgDisplay display){
        this.display = display;
    }

    @Override
    public void handle(@Nullable T exception) {
        if (exception == null || exception.match() == null) {
            return;
        }
        this.display.display(exception.match());
    }
}
