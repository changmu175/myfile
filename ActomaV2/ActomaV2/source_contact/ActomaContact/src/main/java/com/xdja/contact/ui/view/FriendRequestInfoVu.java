package com.xdja.contact.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.presenter.command.IFriendRequestInfoCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IFriendRequestInfoVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.MyEditText;


/**
 * Created by wanghao on 2015/8/3.
 *
 */
public class FriendRequestInfoVu extends BaseActivityVu<IFriendRequestInfoCommand> implements IFriendRequestInfoVu {

    private MyEditText editText;

    private String validateInfo;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        toolbar.setTitle(R.string.request_info_title);
        editText = (MyEditText)getView().findViewById(R.id.edit_request_info);
        Button sendBtn = (Button)getView().findViewById(R.id.request_info_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ContactModuleService.checkNetWork()){
                    return;
                }
                showCommonProgressDialog(R.string.request_info_tips);
                getCommand().executeSendRequestInfo();
            }
        });
        AccountBean accountBean = AccountServer.getAccount();
        String nickName = accountBean.getNickname();
        if(ObjectUtil.stringIsEmpty(nickName)){
            editText.setText(String.format(getActivity().getString(R.string.default_request_info), ""));
        }else{
            editText.setText(String.format(getActivity().getString(R.string.default_request_info), nickName));
        }
        editText.setSelection(editText.getText().length());
        editTextFilterEmoji();
    }

    private void editTextFilterEmoji(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validateInfo = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ContactUtils.containsEmoji(s.toString())){
                    Toast.makeText(getActivity(), R.string.unsupport_emoji, Toast.LENGTH_LONG).show();
                    editText.setText(validateInfo);
                    editText.selectAll();
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void dismissLoading() {
        dismissCommonProgressDialog();
    }

    @Override
    public void showCommonProgressDialog(String msg) {
        super.showCommonProgressDialog(msg);
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.friend_request_info_send;
    }

    @Override
    public String getVerificationInfo() {
        return editText.getText().toString();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.detail_info_title);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
