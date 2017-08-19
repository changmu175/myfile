package com.xdja.presenter_mainframe.ui;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuMessageLogin;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;

import java.util.List;

/**
 * Created by ldy on 16/4/13.
 */
public class MessageVerifyLoginView extends AbstractVerifyPhoneNumberView<VerifyPhoneNumberCommand> implements VuMessageLogin {

    /**
     * 获取完成操作按钮的文字
     *
     * @return
     */
    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.login);
    }

    @Override
    protected List<BaseViewBean> changeViewList(List<BaseViewBean> viewBeanList) {
        return viewBeanList;
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(),maxLoginCount);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_message_verify_code_login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
