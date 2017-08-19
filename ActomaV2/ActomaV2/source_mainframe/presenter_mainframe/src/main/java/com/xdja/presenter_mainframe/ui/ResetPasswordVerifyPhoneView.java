package com.xdja.presenter_mainframe.ui;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;

import java.util.List;

/**
 * Created by ldy on 16/4/18.
 */
public class ResetPasswordVerifyPhoneView extends AbstractVerifyPhoneNumberView<VerifyPhoneNumberCommand> {
    @Override
    public void onCreated() {
        super.onCreated();
        fmvVerifyPhoneNumber.setCompleteButtonText(getStringRes(R.string.next));
    }

    /**
     * 获取完成操作按钮的文字
     *
     * @return
     */
    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.next);
    }

    @Override
    protected List<BaseViewBean> changeViewList(List<BaseViewBean> viewBeanList) {
		//modify by alh@xdja.com to fix bug: 575 2016-06-20 start
        /*TextViewBean textViewBean = new TextViewBean(getStringRes(R.string.verify_phone_number_top_text));
        viewBeanList.add(0,textViewBean);*/
		//modify by alh@xdja.com to fix bug: 575 2016-06-20 end
        return viewBeanList;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_verify_phone_number);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

}
