package com.xdja.presenter_mainframe.ui;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyPhoneNumber;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.inputView.ButtonTextInputView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public abstract class AbstractVerifyPhoneNumberView<V extends VerifyPhoneNumberCommand> extends AbstractFillMessageView<VerifyPhoneNumberCommand> implements VuVerifyPhoneNumber {


    protected FillInMessageView fmvVerifyPhoneNumber;
    protected ButtonTextInputView inputPhone;
    protected ButtonTextInputView inputVerifyCode;

    /**
     * 手机号是否符合规则
     */
    protected boolean isPhoneNoReg = false;
    /**
     * 验证码是否符合规则
     */
    protected boolean isVerifyCodeNoReg = false;
    /**
     * 是否处于发送验证码冷却中
     */
    private boolean isSendCD = false;

    private final Handler handler = new VerPhoneNumHandler(this);

    private XDialog certainMessageSendDialog;

    @Override
    public void onCreated() {
        super.onCreated();
        fmvVerifyPhoneNumber = (FillInMessageView) getActivity().findViewById(R.id.fmv_fill_message);
        initViews();
    }

    private void initViews() {
        inputPhone = (ButtonTextInputView) getActivity().findViewById(R.id.verify_phone_number_phone);
        inputVerifyCode = (ButtonTextInputView) getActivity().findViewById(R.id.verify_phone_number_verify_code);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-09 add. fix bug 5830 . review by wangchao1. Start
        if (inputVerifyCode != null && inputVerifyCode.getEditText() != null){
            inputVerifyCode.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-09 add. fix bug 5830 . review by wangchao1. End
        sendMessageCertainDialog(inputVerifyCode, getCommand());
        inputVerifyCode.setButtonEnabled(false);
        fmvVerifyPhoneNumber.setCompleteButtonEnable(false);
        if (inputPhone != null) {
            inputPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    isPhoneNoReg = TextUtil.isRulePhoneNumber(s.toString());
                    setButtonEnabled();
                }
            });
            inputPhone.setEditInputType(InputType.TYPE_CLASS_NUMBER);
        }
        inputVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isVerifyCodeNoReg = TextUtil.isRuleVerifyCode(s.toString());
                setButtonEnabled();
            }
        });
        inputVerifyCode.setEditInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @SuppressWarnings("UnusedParameters")
    public void sendMessageCertainDialog(ButtonTextInputView buttonTextInputView, V command) {
        inputVerifyCode.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(certainMessageSendDialog != null){
                    return;
                }
                certainMessageSendDialog = new XDialog(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_certain_send_message, null);
                ((TextView) view.findViewById(R.id.tv_certain_send_message_phone)).setText(getPhoneInputText());
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 2526 . review by wangchao1. Start
                certainMessageSendDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        certainMessageSendDialog = null;
                    }
                });
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 2526 . review by wangchao1. End
                certainMessageSendDialog.setTitle(getStringRes(R.string.send_verify_code_mms));
                certainMessageSendDialog.setCustomContentView(view);
                certainMessageSendDialog.setPositiveButton(getStringRes(R.string.send),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                certainMessageSendDialog.dismiss();
                                //modify by alh@xdja.com to fix bug: 566 2016-06-22 start (rummager : guobinchang)
                                setVerifyCodeSend();
                                getCommand().getVerifyCode(getPhoneInputText());
                                //modify by alh@xdja.com to fix bug: 566 2016-06-22 end (rummager : guobinchang)
                            }
                        });
                certainMessageSendDialog.setNegativeButton(getStringRes(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                certainMessageSendDialog.dismiss();
                            }
                        });
                certainMessageSendDialog.show();
            }
        });
    }

    private void setButtonEnabled() {
        if (isPhoneNoReg && isVerifyCodeNoReg) {
            fmvVerifyPhoneNumber.setCompleteButtonEnable(true);
        } else {
            fmvVerifyPhoneNumber.setCompleteButtonEnable(false);
        }
        if (isPhoneNoReg && !isSendCD) {
            inputVerifyCode.setButtonEnabled(true);
        } else {
            inputVerifyCode.setButtonEnabled(false);
        }
    }

    /**
     * 发送验证码后的具体操作
     */
    private void setVerifyCodeSend() {
        //一旦获取验证码开始，手机号码不能进行修改
        if (inputPhone != null){
            inputPhone.getEditText().setEnabled(false);
            //2016-6-1 ldy 手机号锁定后不能清除
            inputPhone.isShowClear(false);
        }
        //获取验证码按钮的变化
        if (inputVerifyCode != null)
            inputVerifyCode.setButtonEnabled(false);
        verifyCodeCD();
    }


    /**
     * 向{@link FillInMessageView}填充view
     *
     * @param viewBeanList 空的list
     * @return 需要使用的list
     */
    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        TextInputViewBean<ButtonTextInputView> phone =
                new TextInputViewBean(getStringRes(R.string.phone_number), getStringRes(R.string.phone_number_hint));
        phone.setId(R.id.verify_phone_number_phone);
        phone.setInputViewType(ButtonTextInputView.class);
        viewBeanList.add(phone);

        TextInputViewBean<ButtonTextInputView> verifyCode =
                new TextInputViewBean(getStringRes(R.string.verify_code), getStringRes(R.string.verify_code_hint));
        verifyCode.setShowAssistView(true);
        verifyCode.setId(R.id.verify_phone_number_verify_code);
        verifyCode.setInputViewType(ButtonTextInputView.class);
        viewBeanList.add(verifyCode);
        return changeViewList(viewBeanList);
    }

    protected abstract List<BaseViewBean> changeViewList(List<BaseViewBean> viewBeanList);

    @Override
    public String getPhoneInputText() {
        return inputPhone.getInputText();
    }

    @Override
    public String getVerifyCodeInputText() {
        return inputVerifyCode.getInputText();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_commmon_fill_message;
    }

    @Override
    public V getCommand() {
        return (V) super.getCommand();
    }

    private Timer timer;

    /**
     * 获取验证码冷却时间(默认60s)
     */
    private int reGettime = 60;
    private TimerTask task;
    public void verifyCodeCD() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);
    }


    public void handleMsg() {
        reGettime--;
        if (reGettime != 0) {
            isSendCD = true;
            inputVerifyCode.setButtonText(String.format(getStringRes(R.string.resend) , reGettime));
        } else {
            resetVerifyCode();
        }
    }

    private static class VerPhoneNumHandler extends Handler {

        private WeakReference<AbstractVerifyPhoneNumberView> mAbstractVerifyPhoneNumWeakRef;

        public VerPhoneNumHandler(AbstractVerifyPhoneNumberView abstractVerifyPhoneNumberView) {
            mAbstractVerifyPhoneNumWeakRef = new  WeakReference<>(abstractVerifyPhoneNumberView);
        }

        @Override
        public void handleMessage(Message msg) {
            AbstractVerifyPhoneNumberView abstractVerifyPhoneNumberView = mAbstractVerifyPhoneNumWeakRef.get();
            if (!ObjectUtil.objectIsEmpty(abstractVerifyPhoneNumberView)) {
                abstractVerifyPhoneNumberView.handleMsg();
            }
        }
    }

    /**
     * 重置获取验证码的状态
     */
    @Override
    public void resetVerifyCode() {
        //当遇到网络请求超时走到onError时,先清除倒计时
        //[S]modify by xienana for bug 4997 @2016/10/14 [review by anlihuang]
        isSendCD = false;
        if (timer != null)
            timer.cancel();
        if (task!=null) {
            task.cancel();
        }
        //[E]modify by xienana for bug 4997 @2016/10/14 [review by anlihuang]
        //设置按钮重新发送
        inputVerifyCode.setButtonText(getStringRes(R.string.resend1));
        inputVerifyCode.setButtonEnabled(true);
        reGettime = 60;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        if (task!=null) {
            task.cancel();
        }
    }
}
