package com.xdja.presenter_mainframe.widget.FillInMessage;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.comm.server.ActomaController;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.DensityUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 16/4/12.<p/>
 * 一个用于填写信息使用的基本组件,包含了{@link TextView},{@link TextInputView},{@link Button},三个组件,
 * 其中肯定会存在一个button且位于最下方,所以button不需要特别添加,只需要设置显示文字就可以了,
 * textview和inputView需要调用{@link #setViewList(List)},需要注意的是,该组件默认无任何组件,只有调用此方法后
 * 才会添加组件,button也会在调用此方法后自动添加,在此之后调用{@link #setCompleteButtonText(String)}才可以改变
 * 按钮显示文字
 */
public class FillInMessageView extends LinearLayout {
    private final static int INPUT_HEIGHT = 48;

    private final static int TEXT_MARGIN_TOP_INPUT = 0;

    private final static int INPUT_MARGIN_TOP_TEXT = 0;
    private final static int INPUT_MARGIN_TOP_INPUT = 0;

    private final static int PADDING_LEFT_RIGHT = 0;
    private final static int PADDING_TOP = 0;

    private final static int COMPLETE_BUTTON_HEIGHT = 40;
    private final static int COMPLETE_BUTTON_MARGIN_HORIZONTAL = 16;
    private final static int COMPLETE_BUTTON_MARGIN_VERTICAL = 16;

    private final Context mContext;
    private List<TextInputView> inputViews = new ArrayList<>();
    private Button btnComplete;

    public FillInMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(VERTICAL);
        setPadding(dip2px(PADDING_LEFT_RIGHT), dip2px(PADDING_TOP), dip2px(PADDING_LEFT_RIGHT), 0);
    }

    /**
     * 设置viewListBean,可以根据该集合生成想要的InputView与textview组成的界面,如果传递的值中有id,
     * 可以使用{@link #findViewById(int)}方法找到对应view,会根据view的排列顺序决定Margin_Top属性
     *
     * @param viewBeanList viewBean集合
     */
    public void setViewList(@NonNull List<BaseViewBean> viewBeanList) {
        if (viewBeanList == null || viewBeanList.isEmpty()) {
            return;
        }
        for (int i = 0, lenth = viewBeanList.size(); i < lenth; i++) {
            BaseViewBean viewBean = viewBeanList.get(i);
            if (i == 0) {
                addBaseView(viewBean, 0);
            } else {
                if (viewBean instanceof TextInputViewBean) {
                    if (viewBeanList.get(i - 1) instanceof TextViewBean) {
                        addInputView((TextInputViewBean) viewBean, dip2px(INPUT_MARGIN_TOP_TEXT));
                    } else {
                        addInputView((TextInputViewBean) viewBean, dip2px(INPUT_MARGIN_TOP_INPUT));
                    }
                } else if (viewBean instanceof TextViewBean) {
                    if (viewBeanList.get(i - 1) instanceof TextInputViewBean) {
                        addTextView((TextViewBean) viewBean, dip2px(TEXT_MARGIN_TOP_INPUT));
                    } else {
                        //// TODO: 16/4/13 添加两个连续的textview的情况
                        throw new IllegalArgumentException(ActomaController.getApp().getString(R.string.fillinmessage_error));
                    }
                } else {
                    // // TODO: 16/4/14 添加别的类型扩展
                    throw new IllegalArgumentException(ActomaController.getApp().getString(R.string.fillinmessage_error1));
                }
            }
        }
        addCompleteButton();

    }

    private void addBaseView(BaseViewBean baseViewBean, int marginTop) {
        if (baseViewBean instanceof TextViewBean) {
            addTextView((TextViewBean) baseViewBean, marginTop);
        } else if (baseViewBean instanceof TextInputViewBean) {
            addInputView((TextInputViewBean) baseViewBean, marginTop);
        }
    }

    private int dip2px(float dpValue) {
        return DensityUtil.dip2px(mContext, dpValue);
    }

    @SuppressWarnings("deprecation")
    private void addTextView(TextViewBean textViewBean, int marginTop) {
        TextView textView = new TextView(mContext);
        Resources resources = getResources();
        textView.setTextColor(resources.getColor(R.color.blade_black_65));
        textView.setTextSize(14);
        textView.setPadding(dip2px(16), dip2px(13), dip2px(16), dip2px(13));
        textView.setText(TextUtils.isEmpty(textViewBean.getText()) ? textViewBean.getSpanned() : textViewBean.getText());
        if (textViewBean.getId() != -1) {
            textView.setId(textViewBean.getId());
        }
        LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(0, marginTop, 0, 0);
        addView(textView,textLayoutParams);
    }

    private void addInputView(TextInputViewBean inputViewBean, int marginTop) {
        TextInputView inputView = null;
        if (inputViewBean.getInputViewType() == null) {
            inputView = new TextInputView(mContext);
        } else {
            try {
                inputView = (TextInputView) inputViewBean.getInputViewType().
                        getConstructor(Context.class).newInstance(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(inputViewBean.getFirstText())){
            inputView.setFirstViewText(inputViewBean.getFirstText());
        }else{
            inputView.setFirstViewText(inputViewBean.getFirstSpanned());
        }
        inputView.setInputText(inputViewBean.getInputText());

        if (!TextUtils.isEmpty(inputViewBean.getInputHint())){
            inputView.setInputHint(inputViewBean.getInputHint());
        }else{
            inputView.setInputHint(inputViewBean.getHintSpanned());
        }

        if (inputViewBean.getId() != -1) {
            inputView.setId(inputViewBean.getId());
        }
        inputView.setIsShowAssistView(inputViewBean.isShowAssistView());
        LayoutParams inputViewLayoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, INPUT_HEIGHT));
        inputViewLayoutParams.setMargins(0, marginTop, 0, 0);
        inputView.setLayoutParams(inputViewLayoutParams);
        addView(inputView);
        inputViews.add(inputView);
    }


    private void addCompleteButton() {
        btnComplete = new Button(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(COMPLETE_BUTTON_HEIGHT));
        layoutParams.setMargins(dip2px(COMPLETE_BUTTON_MARGIN_HORIZONTAL)
                , dip2px(COMPLETE_BUTTON_MARGIN_VERTICAL), dip2px(COMPLETE_BUTTON_MARGIN_HORIZONTAL), 0);
        btnComplete.setBackgroundResource(R.drawable.sel_bg_gold);
        btnComplete.setTextColor(getResources().getColorStateList(R.color.text_selector));
        btnComplete.setTextSize(14);
        addView(btnComplete, layoutParams);
    }

    public void setCompleteButtonText(String text) {
        btnComplete.setAllCaps(false);
        btnComplete.setText(text);
    }

    public void setCompleteClickListener(OnClickListener onClickListener) {
        btnComplete.setOnClickListener(onClickListener);
    }

    /**
     * 在调用{@link #setViewList(List)}后调用有效
     */
    public void setCompleteButtonEnable(boolean isEnable){
        if (btnComplete!=null){
            btnComplete.setEnabled(isEnable);
        }
    }

    /**
     * 按从上到下的次序返回InputView的字符串
     */
    public List<String> getInputTextList() {
        List<String> inputs = new ArrayList<>();
        for (TextInputView inputView : inputViews) {
            inputs.add(inputView.getInputText());
        }
        return inputs;
    }

}
