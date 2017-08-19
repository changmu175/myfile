package com.xdja.frame.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.frame.presenter.R;


/**
 * Created by chenbing on 2015-7-16.
 */
public class XDialog {
    private boolean                   mCancel;
    private boolean                   mCancelable = true;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private Builder    mBuilder;
    private View mView;
    private int                       mTitleResId;
    private CharSequence mTitle;
    private int                       mMessageResId;
    private CharSequence mMessage;
    private TextView mPositiveButton;
    private boolean                   positiveButtonExit;
    private TextView mNegativeButton;
    private boolean                   negativeButtonExit;
    private RelativeLayout buttonLayout;
    private LinearLayout contentView;
    private LinearLayout customContentView;

    private boolean mHasShow = false;
    private Drawable mBackgroundDrawable;
    private int                               mBackgroundResId;
    private View mMessageContentView;
    private View mCustomContentView;

    private DialogInterface.OnDismissListener mOnDismissListener;

    private String mPositiveButtonTitle;
    private View.OnClickListener  mPositiveButtonClick;

    private String mNegativeButtonTitle;
    private View.OnClickListener  mNegativeButtonClick;



    public XDialog(Context context) {
        this.mContext = context;
    }


    public boolean isShowing(){
        if (mAlertDialog != null){
            return mAlertDialog.isShowing();
        }else {
            return false;
        }
    }

    public void show() {
        if (mHasShow == false)
            mBuilder = new Builder();
        else
            mAlertDialog.show();

        mHasShow = true;
    }

    public XDialog setView(View view) {
        mView = view;
        if (mBuilder != null) {
            mBuilder.setView(view);
        }
        return this;
    }

    public View getView(){
        if (mView != null){
            return mView;
        }else {
            return null;
        }
    }

    /**
     * 自定义title和button之间的内容
     * @param view 自定义的view
     * @return
     */
    public XDialog setContentView(View view) {
        mMessageContentView = view;
        if (mBuilder != null) {
            mBuilder.setContentView(mMessageContentView);
        }
        return this;
    }

    /**
     * 设置message和按钮之间的内容
     * @param view 自定义view
     * @return
     */
    public XDialog setCustomContentView(View view){
        mCustomContentView = view;
        if (mBuilder != null){
            mBuilder.setCustomContentView(view);
        }
        return this;
    }

    public XDialog setBackground(Drawable drawable) {
        mBackgroundDrawable = drawable;
//        if (mBuilder != null) {
//            mBuilder.setBackground(mBackgroundDrawable);
//        }
        return this;
    }

    public XDialog setBackgroundResource(int resId) {
        mBackgroundResId = resId;
//        if (mBuilder != null) {
//            mBuilder.setBackgroundResource(mBackgroundResId);
//        }
        return this;
    }


    /**
     * 取消提示框
     */
    public void dismiss() {
        mAlertDialog.dismiss();
    }


    /**
     * 设置标题
     * @param resId
     * @return
     */
    public XDialog setTitle(int resId) {
        mTitleResId = resId;
        if (mBuilder != null) {
            mBuilder.setTitle(resId);
        }
        return this;
    }

    public XDialog setTitle(CharSequence title) {
        mTitle = title;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
        }
        return this;
    }

    public XDialog setMessage(int resId) {
        mMessageResId = resId;
        if (mBuilder != null) {
            mBuilder.setMessage(resId);
        }
        return this;
    }

    public XDialog setMessage(CharSequence message) {
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setMessage(message);
        }
        return this;
    }


    public XDialog setPositiveButton(String text, final View.OnClickListener listener) {
        positiveButtonExit = true;
        mPositiveButtonTitle = text;
        mPositiveButtonClick = listener;
        if (mBuilder != null){
            mBuilder.setPositiveButton(text, listener);
        }
        return this;
    }

    public XDialog setNegativeButton(String text, final View.OnClickListener listener) {
        negativeButtonExit = true;
        mNegativeButtonTitle = text;
        mNegativeButtonClick = listener;
        if (mBuilder != null){
            mBuilder.setNegativeButton(text, listener);
        }
        return this;
    }

    /**
     * 设置是否外部点击可取消
     * @param cancel 是否可取消
     * @return
     */
    public XDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mCancel = cancel;
        if (mBuilder != null) {
            mBuilder.setCanceledOnTouchOutside(mCancel);
        }
        return this;
    }

    /**
     * 设置返回键是否可取消
     * @param cancelable 是否可取消
     * @return
     */
    public XDialog setCancelable(boolean cancelable){
        this.mCancelable = cancelable;
        if (mBuilder != null){
            mBuilder.setCancelable(mCancelable);
        }
        return this;
    }

    public XDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }

    public TextView getmPositiveButton(){
        TextView positiveButton = null;
        if(mBuilder != null){
            positiveButton = mBuilder.getmPositiveButton();
        }
        return positiveButton;
    }


    public TextView getmNegativeButton(){
        TextView negativeButton = null;
        if(mBuilder != null){
            negativeButton = mBuilder.getmNegativeButton();
        }
        return negativeButton;
    }


    private class Builder {

        private TextView mTitleView;
        private TextView mMessageView;
        private View contv;

        private Builder() {
            mAlertDialog = new AlertDialog.Builder(mContext).create();

            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            contv = LayoutInflater.from(mContext).inflate(R.layout.view_normal_dialog, null);
            mAlertDialog.setView(contv);

            mTitleView = (TextView) contv.findViewById(R.id.dialog_title);
            mMessageView = (TextView) contv.findViewById(R.id.dialog_message);
            mPositiveButton = (TextView) contv.findViewById(R.id.dialog_ok);
            mNegativeButton = (TextView) contv.findViewById(R.id.dialog_cancel);
            buttonLayout = (RelativeLayout)contv.findViewById(R.id.buttonlayout);
            contentView = (LinearLayout)contv.findViewById(R.id.contentview);
            customContentView = (LinearLayout)contv.findViewById(R.id.contentmiddle_view);

            if (mTitleResId != 0) {
                setTitle(mTitleResId);
            }
            if (mTitle != null) {
                setTitle(mTitle);
            }
            if (mTitle == null && mTitleResId == 0) {
                mTitleView.setVisibility(View.GONE);
            }
            if (mMessageResId != 0) {
                setMessage(mMessageResId);
            }
            if (mMessage != null) {
                setMessage(mMessage);
            }
            if (mMessage == null && mMessageResId == 0) {
                mMessageView.setVisibility(View.GONE);
            }
            if (positiveButtonExit) {
                setPositiveButton(mPositiveButtonTitle, mPositiveButtonClick);
                mPositiveButton.setVisibility(View.VISIBLE);
            }else {
                mPositiveButton.setVisibility(View.GONE);
            }
            if (negativeButtonExit) {
                setNegativeButton(mNegativeButtonTitle, mNegativeButtonClick);
                mNegativeButton.setVisibility(View.VISIBLE);
            }else {
                mNegativeButton.setVisibility(View.GONE);
            }
            if (!negativeButtonExit && !positiveButtonExit) {
                buttonLayout.setVisibility(View.GONE);
            }else {
                buttonLayout.setVisibility(View.VISIBLE);
            }

            if (mMessageContentView != null) {
                this.setContentView(mMessageContentView);
            }
            if (mCustomContentView != null){
                this.setCustomContentView(mCustomContentView);
            }
            if (mView != null){
                this.setView(mView);
            }
            mAlertDialog.setCanceledOnTouchOutside(mCancel);
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(mOnDismissListener);
            }

            mAlertDialog.setCancelable(mCancelable);

            mAlertDialog.show();
        }

        public void setTitle(int resId) {
            mTitleView.setText(resId);
        }

        public void setTitle(CharSequence title) {
            mTitleView.setText(title);
        }

        public void setMessage(int resId) {
            mMessageView.setText(resId);
        }

        public void setMessage(CharSequence message) {
            mMessageView.setText(message);
        }

        public TextView getmPositiveButton() {
            return mPositiveButton;
        }

        public TextView getmNegativeButton() {
            return mNegativeButton;
        }

        /**
         * set positive button
         *
         * @param text     the name of button
         * @param listener
         */
        public void setPositiveButton(String text, final View.OnClickListener listener) {
            mPositiveButton.setText(text);
            mPositiveButton.setOnClickListener(listener);
        }

        /**
         * set negative button
         *
         * @param text     the name of button
         * @param listener
         */
        public void setNegativeButton(String text, final View.OnClickListener listener) {
            mNegativeButton.setText(text);
            mNegativeButton.setOnClickListener(listener);
            if (listener == null){
                mNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        }

        public void setView(View view) {
            mAlertDialog.setView(view);
        }

        public void setContentView(View view) {
            contentView.removeAllViews();
            contentView.addView(view);
        }
        public void setCustomContentView(View view) {
            customContentView.removeAllViews();
            customContentView.addView(view);
        }

        public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }

        public void setCancelable(boolean cancelable){
            mAlertDialog.setCancelable(cancelable);
        }
    }
}


