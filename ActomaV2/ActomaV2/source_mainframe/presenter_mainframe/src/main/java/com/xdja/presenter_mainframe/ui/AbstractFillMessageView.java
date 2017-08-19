package com.xdja.presenter_mainframe.ui;

import android.view.View;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ldy on 16/5/3.
 */
public abstract class AbstractFillMessageView<T extends FillMessageCommand> extends ActivityView<T> {
    @Bind(R.id.fmv_fill_message)
    protected FillInMessageView fmvFillMessage;

    @Override
    public void onCreated() {
        super.onCreated();
        fmvFillMessage.setViewList(setView2FillInMessageView(new ArrayList<BaseViewBean>()));
        fmvFillMessage.setCompleteButtonText(getCompleteButtonText());
        fmvFillMessage.setCompleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeButtonClicked(v);
            }
        });
    }

    /**
     * 向{@link FillInMessageView}填充view
     * @param viewBeanList  空的list
     * @return  需要使用的list
     */
    protected abstract List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList);

    /**
     * 获取完成操作按钮的文字
     * @return
     */
    protected abstract String getCompleteButtonText();

    /**
     * 如果想要获得completeButton的点击事件,可以覆盖此方法
     */
    @SuppressWarnings("UnusedParameters")
    protected void completeButtonClicked(View v){
        getCommand().complete(fmvFillMessage.getInputTextList());
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_commmon_fill_message;
    }
    @Override
    protected int getToolbarType() {
        return ActivityView.ToolbarDef.NAVIGATE_BACK;
    }
}
