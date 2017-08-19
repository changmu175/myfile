package com.xdja.presenter_mainframe.ui;

import android.text.InputFilter;
import android.text.InputType;

import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ResetPasswordVerifyFriendPhoneCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuResetPasswordVerifyFriendPhone;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.List;


/**
 * Created by ldy on 2016/6/2.
 */
public class ResetPasswordVerifyFriendPhoneView extends AbstractFillMessageView<ResetPasswordVerifyFriendPhoneCommand> implements VuResetPasswordVerifyFriendPhone {

    private TextInputView mAccount;
    private TextInputView mFriend1;
    private TextInputView mFriend2;
    private TextInputView mFriend3;

    private final int ET_MAX_SIZE = 11;

    @Override
    public void onCreated() {
        super.onCreated();
        mFriend1 = (TextInputView) fmvFillMessage.findViewById(R.id.a1);
        mFriend1.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        mFriend1.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        mFriend2 = (TextInputView) fmvFillMessage.findViewById(R.id.a2);
        mFriend2.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        mFriend2.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        mFriend3 = (TextInputView) fmvFillMessage.findViewById(R.id.a3);
        mFriend3.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        mFriend3.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    /**
     * 向{@link FillInMessageView}填充view
     *
     * @param viewBeanList 空的list
     * @return 需要使用的list
     */
    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        //modify by alh@xdja.com to fix bug: 927  2016-06-30 start (rummager : null)
        viewBeanList.add(new TextViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.please_write_at_account_1))));
        TextInputViewBean account = new TextInputViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.at_account)),null);
        account.setId(R.id.account);
        viewBeanList.add(account);
        viewBeanList.add(new TextViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.verify_friend_phone_write_friend_1))));
        //modify by alh@xdja.com to fix bug: 927  2016-06-30 start (rummager : null)
        TextInputViewBean friend1 = new TextInputViewBean(getStringRes(R.string.friend1), getStringRes(R.string.friend1_hint));
        friend1.setId(R.id.a1);
        viewBeanList.add(friend1);
        TextInputViewBean friend2 = new TextInputViewBean(getStringRes(R.string.friend2), getStringRes(R.string.friend2_hint));
        friend2.setId(R.id.a2);
        viewBeanList.add(friend2);
        TextInputViewBean friend3 = new TextInputViewBean(getStringRes(R.string.friend3), getStringRes(R.string.friend3_hint));
        friend3.setId(R.id.a3);
        viewBeanList.add(friend3);
        return viewBeanList;
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
    public void showAuthFailDialog(String message) {
        XDialog authFailDialog = new XDialog(getActivity());
        authFailDialog.setTitle(getStringRes(R.string.authentication_failed));
        authFailDialog.setMessage(message);
        authFailDialog.setNegativeButton(getStringRes(R.string.roger),null);
        authFailDialog.show();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_verify_friend_phone);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
