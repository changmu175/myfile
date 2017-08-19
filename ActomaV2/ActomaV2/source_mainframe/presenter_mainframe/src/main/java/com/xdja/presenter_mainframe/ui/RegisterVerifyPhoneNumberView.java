package com.xdja.presenter_mainframe.ui;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.RegisterVerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuRegistVerifyPhoneNumber;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.xutils.util.LogUtils;

import java.util.List;

/**
 * Created by ldy on 16/4/13.
 */
public class RegisterVerifyPhoneNumberView extends AbstractVerifyPhoneNumberView<RegisterVerifyPhoneNumberCommand> implements VuRegistVerifyPhoneNumber {


    private TextView skipTv;
    /**
     * 获取完成操作按钮的文字
     *
     * @return
     */
    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.complete);
    }

    @Override
    protected List<BaseViewBean> changeViewList(List<BaseViewBean> viewBeanList) {
        TextViewBean textViewBean = new TextViewBean(getStringRes(R.string.register_verify_phone_number_top_text));
        viewBeanList.add(0, textViewBean);
        return viewBeanList;
    }

    @Override
    public void showChooseDialog() {
        final XDialog xDialog = new XDialog(getActivity());
        xDialog.setTitle(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_LIST, 0, 0,
                0, getStringRes(R.string.bind_phone_number_prompt)))
                .setMessage(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0, 0,
                        0, getStringRes(R.string.bind_phone_number_error_content)))
                .setPositiveButton(getStringRes(R.string.bind_account), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xDialog.dismiss();
                        getCommand().forceBind();
                    }
                })
                .setNegativeButton(getStringRes(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xDialog.dismiss();
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-24 add. fix bug 3333 . review by wangchao1. Start
                        setComBtnState(true);
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-24 add. fix bug 3333 . review by wangchao1. End
                    }
                })
                .show();
    }

    //[s]modify by xienana for multi languange text change @20161212
    @Override
    public void onCreated() {
        super.onCreated();
        skipTv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.skip_textview, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.register_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_skip);
        if(menuItem != null){
            menuItem.setActionView(skipTv);
        }else{
            LogUtils.e("RegisterVerifyPhone onCreateOptionsMenu menuItem is null");
        }
        skipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().skip();
            }
        });
        return true;
    }
    //[e]modify by xienana for multi languange text change @20161212

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(), maxLoginCount);
    }

    @Override
    protected int getToolbarType() {
        if (getCommand().isNoBackKey()) {
            return ToolbarDef.NAVIGATE_DEFAULT;
        }
        return super.getToolbarType();
    }

    public void setComBtnState(boolean state) {
        fmvVerifyPhoneNumber.setCompleteButtonEnable(state);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_verify_phone_number);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

}
