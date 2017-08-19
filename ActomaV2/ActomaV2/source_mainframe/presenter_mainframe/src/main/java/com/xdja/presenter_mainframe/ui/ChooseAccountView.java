package com.xdja.presenter_mainframe.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ChooseAccountCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuChooseAccount;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.PartClickTextView;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
@ContentView(R.layout.activity_choose_account)
public class ChooseAccountView extends ActivityView<ChooseAccountCommand> implements VuChooseAccount {
    @Bind(R.id.pctv_choose_account_mid_text)
    PartClickTextView pctvChooseAccountMidText;
    @Bind(R.id.tv_choose_account_account)
    TextView tvChooseAccountAccount;
    @Bind(R.id.choose_account)
    TextView choose_account;
    @Bind(R.id.btn_choose_account_next)
    Button btnChooseAccountNext;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3982 . review by wangchao1. Start
        choose_account.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.choose_account_top_text)));
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-12 add. fix bug 3982 . review by wangchao1. End
        pctvChooseAccountMidText.appendClickableText(getStringRes(R.string.choose_account_mid_text2), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().switchOther();
            }
        });
        pctvChooseAccountMidText.append(getStringRes(R.string.choose_account_mid_text3));
        pctvChooseAccountMidText.appendClickableText(getStringRes(R.string.choose_account_mid_text4), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().setAccountBySelf();
            }
        });
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @OnClick(R.id.btn_choose_account_next)
    void next() {
        getCommand().next();
    }

    @Override
    public void setAtAccount(String account) {
        tvChooseAccountAccount.setText(account);
    }

    @Override
    public void removeSetAccountSelfText() {
        pctvChooseAccountMidText.setText("");
//        pctvChooseAccountMidText.appendClickableText(getStringRes(R.string.choose_account_mid_text2), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getCommand().switchOther();
//            }
//        });
//        pctvChooseAccountMidText.append("”，或摇一摇手机选择新安通+帐号。");
    }

    @Override
    public void modifyCertainButton(String msg) {
        btnChooseAccountNext.setText(msg);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_choose_account);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
