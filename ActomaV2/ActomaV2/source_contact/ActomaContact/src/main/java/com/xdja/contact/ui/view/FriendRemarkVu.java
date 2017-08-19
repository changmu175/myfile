package com.xdja.contact.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.presenter.command.IFriendRemarkCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IFriendRemarkVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.view.MyEditText;

/**
 * Created by wanghao on 2015/7/23.
 */
public class FriendRemarkVu extends BaseActivityVu<IFriendRemarkCommand> implements IFriendRemarkVu {

    private MyEditText editText;

    private Button btn;

    private String remarkValue;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        toolbar.setTitle(R.string.remark_title);
        editText = (MyEditText)getView().findViewById(R.id.remark_input);
        btn = (Button)getView().findViewById(R.id.send_remark);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ContactModuleService.checkNetWork()) return;
                showCommonProgressDialog(R.string.save_remark_tips);
                getCommand().saveRemark();
            }
        });
        editText.setText(getCommand().getFriend().getRemark());
        editText.setSelection(editText.getText().length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                remarkValue = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ContactUtils.containsEmoji(s.toString())){
                    Toast.makeText(getActivity(), R.string.unsupport_emoji, Toast.LENGTH_LONG).show();
                    editText.setText(remarkValue);
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
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.friend_remark;
    }

    @Override
    public String getRemark() {
        return editText.getText().toString().trim();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.friend_set_remark);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
