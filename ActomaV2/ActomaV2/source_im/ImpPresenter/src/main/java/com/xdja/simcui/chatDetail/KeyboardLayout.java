package com.xdja.simcui.chatDetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import android.widget.RelativeLayout;

/**
 * Created by cxp on 2015/9/8.
 */
public class KeyboardLayout extends RelativeLayout {

    private static final String TAG = KeyboardLayout.class.getSimpleName();
    private static final byte KEYBOARD_STATE_SHOW = -3;
    private static final byte KEYBOARD_STATE_HIDE = -2;
    private static final byte KEYBOARD_STATE_INIT = -1;
    private boolean mHasInit;
    private boolean mHasKeyboard;
    private int mHeight;
    private onKeyBoardChangeListener mListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public KeyboardLayout(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mHasInit) {
            mHasInit = true;
            mHeight = b;
            if (mListener != null) {
                mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            }
        } else {
            mHeight = mHeight < b ? b : mHeight;
        }
        if (mHasInit && mHeight > b) {
            mHasKeyboard = true;
            if (mListener != null){
                mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
            }
        }
        if (mHasInit && mHasKeyboard && mHeight == b) {
            mHasKeyboard = false;
            if (mListener != null) {
                mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
            }
        }
    }
    /**
     * set keyboard state listener
     */
    public void setOnKeyboardStateListener(onKeyBoardChangeListener listener){
        mListener = listener;
    }
    public interface onKeyBoardChangeListener{
        void onKeyBoardStateChange(int state);
    }
}
