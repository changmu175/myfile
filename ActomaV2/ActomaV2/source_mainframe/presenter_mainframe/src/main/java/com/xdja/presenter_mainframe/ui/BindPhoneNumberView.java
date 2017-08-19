package com.xdja.presenter_mainframe.ui;

import android.view.View;

import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.BindPhoneNumberCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuBindPhoneNumber;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;

import java.util.List;

/**
 * Created by ldy on 16/4/29.
 */
public class BindPhoneNumberView extends AbstractVerifyPhoneNumberView<BindPhoneNumberCommand> implements VuBindPhoneNumber{
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
        TextViewBean textViewBean = new TextViewBean(getStringRes(R.string.bind_phone_number_top_text));
        viewBeanList.add(0,textViewBean);
        return viewBeanList;
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-29 add. fix text errors . review by wangchao1. Start
    @Override
    public void showChooseDialog() {
        final XDialog xDialog = new XDialog(getActivity());
        xDialog.setTitle(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.bind_phone_number_error_title)))
                .setMessage(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                        0, 0, 0, getStringRes(R.string.bind_phone_number_error)))
                .setPositiveButton(getStringRes(R.string.bind_phone_number), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xDialog.dismiss();
                        getCommand().forceBind();
                    }
                })
                .setNegativeButton(getStringRes(R.string.cancel),null)
                .show();
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-29 add. fix text errors . review by wangchao1. End

    //modify by alh@xdja.com to fix bug: 562 2016-06-29 start (rummager : wangchao1)
    @Override
    public void showSameMobileDialog() {
        final XDialog xDialog = new XDialog(getActivity());
        xDialog.setTitle(R.string.prompt)
                .setMessage(R.string.prompt_same_mobile)
                .setPositiveButton(getContext().getString(R.string.certain), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xDialog.dismiss();
                    }
                })
                .show();
    }
    //modify by alh@xdja.com to fix bug: 562 2016-06-29 end (rummager : wangchao1)

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_bind_phone_number);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
