package com.xdja.simcui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xdja.imp.R;
import com.xdja.simcui.recordingControl.view.AudioRecorderButton;

public class ChatInputView extends LinearLayout {
    private final Context mContext;
    /**
     * 更多按钮
     */
    public CheckBox moreCheck;
    /**
     * 隐藏的checkbox，用于控制表情面板切换等
     */
    public CheckBox virtualCheck;

    /**
     * 选中闪信
     */
    public CheckBox shanCheck;
    /**
     * 文字输入
     */
    public EditText inputEare;
    /**
     * 发送语音
     */
    public AudioRecorderButton sendVoic;
    /**
     * 输入方式
     */
    public CheckBox inputCheck;
    /**
     * 发送文本
     */
    private Button sendText;

    /**
     * 输入框最大输入长度
     */
    private final int MAX_INPUT_SIZE = 2048;
    /**
     * 提示显示对象
     */
    private Toast toast;


    public ChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public ChatInputView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toast = new Toast(mContext);
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = inflater.inflate(R.layout.sublayout_kakachat_operate, null);
        moreCheck = (CheckBox) view.findViewById(R.id.btn_chat_action);
        virtualCheck = (CheckBox) view.findViewById(R.id.btn_chat_toface);
        shanCheck = (CheckBox) view.findViewById(R.id.checkbox_shantype);
        inputEare = (EditText) view.findViewById(R.id.edit_chatinput);
        inputEare.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        sendVoic = (AudioRecorderButton) view.findViewById(R.id.btn_chatvoic);
        inputCheck = (CheckBox) view.findViewById(R.id.checkbox_chattype);
        sendText = (Button) view.findViewById(R.id.btn_sendtxtmsg);
        sendText.setEnabled(false);
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(view, lp);

        //fix bug 5797 by zya 20161130
        ((PastListenerEditText)inputEare).setMaxLengthListener(new PastListenerEditText.MaxLengthListener() {
            @Override
            public void showToast() {
                ChatInputView.this.showToast();
            }
        });//end
    }

    /**
     * 注册相关按钮的状态切换（是否选中、点击等）的回调事件
     *
     * @param icav 回调实例
     */
    public void registActionViewCallBack(final IChatActionView icav) {
        inputEare.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                if (length >= MAX_INPUT_SIZE) {
                    s = s.delete(MAX_INPUT_SIZE, length);
                }

				// 文本长度为0时最右边显示为语音切换
				if (s.length() == 0) {
					inputCheck.setVisibility(View.VISIBLE);
					sendText.setVisibility(View.GONE);
					inputCheck.setChecked(false);
				} else {// 否则显示为发送按钮
					inputCheck.setVisibility(View.GONE);
					sendText.setVisibility(View.VISIBLE);
				}
                if (s.length() == 0) {
                    sendText.setEnabled(false);
                } else {
                    sendText.setEnabled(true);
                }
            }
        });
        inputEare.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //add by zya 20170308
                requestFocusForEditText();
                //end by zya
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    icav.onInputEareTouchCallBack();
                }
                return false;
            }
        });

        inputCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {// 输入方式选择栏为选中状态时，将中间按钮切换为语音发送状态
                    inputEare.setVisibility(View.GONE);
                    sendVoic.setVisibility(View.VISIBLE);
                } else {// 否则为文字输入状态
                    inputEare.setText("");
                    inputEare.setVisibility(View.VISIBLE);
                    inputEare.setFocusable(true);
                    inputEare.requestFocus();
                    sendVoic.setVisibility(View.GONE);
                }
                // 避免快速点击出现的按钮状态显示错乱的问题
                inputCheck.setVisibility(View.VISIBLE);
                sendText.setVisibility(View.GONE);
                icav.onInputCheckChanged(isChecked);
            }
        });

        shanCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                icav.onShanCheckChanged(isChecked);
            }
        });

        moreCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                icav.onMoreCheckChanged(isChecked);
            }
        });

        virtualCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                virtualCheck.setVisibility(View.GONE);
                icav.onVirtualViewCallBack();
            }
        });

        sendText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                icav.onSendTextCallBack();
            }
        });
    }

    private void showToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
        if (mContext != null
                && !TextUtils.isEmpty(mContext.getResources().getString(
                R.string.input_length_warnning))) {
            this.toast = Toast.makeText(mContext, mContext.getResources()
                            .getString(R.string.input_length_warnning),
                    Toast.LENGTH_SHORT);
        }
        if (this.toast != null) {
            this.toast.show();
        }
    }

    //add by zya 20170308
    public void requestFocusForEditText(){
        inputEare.setFocusable(true);
        inputEare.setFocusableInTouchMode(true);
        inputEare.requestFocus();
    }//end by zya

    /**
     * 输入操作栏动作回调接口
     *
     * @author fanjiandong
     */
    public interface IChatActionView {
        /**
         * 点击隐藏按钮的回调
         */
        void onVirtualViewCallBack();

        /**
         * action面板控制控件的回调
         *
         * @param isChecked
         */
        void onMoreCheckChanged(boolean isChecked);

        /**
         * 闪信控制控件的回调
         *
         * @param isChecked
         */
        void onShanCheckChanged(boolean isChecked);

        /**
         * 输入方式控制控件的回调
         *
         * @param isChecked
         */
        void onInputCheckChanged(boolean isChecked);

        /**
         * 发送文本消息的回调
         */
        void onSendTextCallBack();

        /**
         * 输入控件被点击的回调
         */
        void onInputEareTouchCallBack();
    }
}
