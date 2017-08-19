package com.xdja.presenter_mainframe.ui;

import android.view.View;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyFriendPhoneCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyFriendPhone;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ldy on 16/4/18.
 */
@ContentView(R.layout.activity_commmon_fill_message)
public class VerifyFriendPhoneView extends ActivityView<VerifyFriendPhoneCommand> implements VuVerifyFriendPhone {
    @Bind(R.id.fmv_fill_message)
    FillInMessageView fmvVerifyFriendPhone;

    @Override
    public void onCreated() {
        super.onCreated();
        initViews();

    }

    private void initViews() {
        setView2FillInMessageView();
        fmvVerifyFriendPhone.setCompleteButtonText(getStringRes(R.string.verify));
        fmvVerifyFriendPhone.setCompleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().verify(fmvVerifyFriendPhone.getInputTextList());
            }
        });
    }

    /**
     * 向{@link FillInMessageView}填充view
     */
    private void setView2FillInMessageView() {
        List<BaseViewBean> viewBeanList = new ArrayList<>();
        TextViewBean topTextBean = new TextViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.verify_friend_phone_write_friend)));
        viewBeanList.add(topTextBean);

        TextInputViewBean friend1 = new TextInputViewBean(getStringRes(R.string.friend1), getStringRes(R.string.friend1_hint));
        viewBeanList.add(friend1);
        TextInputViewBean friend2 = new TextInputViewBean(getStringRes(R.string.friend2), getStringRes(R.string.friend2_hint));
        viewBeanList.add(friend2);
        TextInputViewBean friend3 = new TextInputViewBean(getStringRes(R.string.friend3), getStringRes(R.string.friend3_hint));
        viewBeanList.add(friend3);

        fmvVerifyFriendPhone.setViewList(viewBeanList);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(),maxLoginCount);
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
