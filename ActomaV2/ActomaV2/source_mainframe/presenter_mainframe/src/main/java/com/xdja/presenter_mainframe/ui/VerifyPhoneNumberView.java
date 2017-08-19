package com.xdja.presenter_mainframe.ui;

import android.annotation.SuppressLint;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyLoginVerifyPhoneNumber;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;

import java.util.List;

/**
 * Created by ldy on 16/5/19.
 */
public class VerifyPhoneNumberView extends AbstractVerifyPhoneNumberView<VerifyPhoneNumberCommand> implements VuVerifyLoginVerifyPhoneNumber {
    TextView tvVerifyPhoneNumberMobile;
    private LinearLayout constantPhone;
    private String phoneNumber;

    @Override
    protected List<BaseViewBean> changeViewList(List<BaseViewBean> viewBeanList) {
        viewBeanList.remove(0);
        return viewBeanList;
    }

    @Override
    public void onCreated() {
        super.onCreated();
        constantPhone = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.view_constant_phone, null);
        tvVerifyPhoneNumberMobile = (TextView) constantPhone.findViewById(R.id.tv_verify_phone_number_mobile);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-12 add. fix hujun -> UI problems . review by wangchao1. Start
        fmvVerifyPhoneNumber.addView(constantPhone, 0);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-12 add. fix hujun -> UI problems . review by wangchao1. End
        inputVerifyCode.setButtonEnabled(true);
        isPhoneNoReg = true;
    }

    /**
     * 获取完成操作按钮的文字
     *
     * @return
     */
    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.verify);
    }

    @Override
    public String getPhoneInputText() {
        return phoneNumber;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (phoneNumber!=null){
            String mid4 = phoneNumber.substring(3, 7);
            String[] split = phoneNumber.split(mid4);
            tvVerifyPhoneNumberMobile.setText(split[0]+"****"+split[1]);
        }
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(),maxLoginCount);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_verify_phone_number);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
