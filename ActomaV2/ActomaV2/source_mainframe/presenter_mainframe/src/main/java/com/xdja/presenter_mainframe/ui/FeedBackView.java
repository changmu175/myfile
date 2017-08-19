package com.xdja.presenter_mainframe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FeedBackCommand;
import com.xdja.presenter_mainframe.presenter.adapter.UploadImageAdapter;
import com.xdja.presenter_mainframe.ui.uiInterface.VuFeedBack;
import com.xdja.presenter_mainframe.util.Function;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ALH on 2016/8/12.
 */
@ContentView(R.layout.activity_view_feedback)
public class FeedBackView extends ActivityView<FeedBackCommand> implements VuFeedBack {
    /**
     * 问题和意见输入框
     */
    @Bind(R.id.feedback_opinion_edittext)
    EditText feedbackOpinionEdittext;
    /**
     * 问题截图gradview
     */
    @Bind(R.id.feedback_upload_image_gridview)
    GridView feedbackUploadImageGridview;
    /**
     * 联系电话输入框
     */
    @Bind(R.id.feedback_contact_phone_edittext)
    EditText feedbackContactPhoneEdittext;
    /**
     * 提交按钮
     */
    @Bind(R.id.feedback_submit_button)
    Button feedbackSubmitButton;
    /**
     * 提交成功布局
     */
    @Bind(R.id.feedback_layout2)
    LinearLayout feedbackLayout2;
    /**
     * /**
     * 问题反馈布局
     */
    @Bind(R.id.feedback_layout1)
    ScrollView feedbackLayout1;

    @Bind(R.id.feedback_hint)
    TextView feedbackHint;
    /**
     * 问题和意见最大字符数
     */
    private final int MAX_OPINION_LENGTH = 800;
    /**
     * 联系电话最大字符数
     */
    private final int MAX_PHONE_LENGTH = 11;

    @Override
    public void onCreated() {
        super.onCreated();
        //设置列表点击时获取焦点
        feedbackUploadImageGridview.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    feedbackUploadImageGridview.requestDisallowInterceptTouchEvent(true);
                } else {
                    feedbackUploadImageGridview.requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
        //设置问题和意见输入限制
        feedbackOpinionEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_OPINION_LENGTH) {
                    feedbackOpinionEdittext.setText(s.subSequence(0, MAX_OPINION_LENGTH));
                    Selection.setSelection(feedbackOpinionEdittext.getText(), MAX_OPINION_LENGTH);
                    XToast.show(getContext(), getStringRes(R.string.feedback_opinion_toast));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                feedbackSubmitButton.setEnabled(s.length() > 0);
            }
        });
        //设置联系电话输入限制
        feedbackContactPhoneEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_PHONE_LENGTH) {
                    feedbackContactPhoneEdittext.setText(s.subSequence(0, MAX_PHONE_LENGTH));
                    Selection.setSelection(feedbackContactPhoneEdittext.getText(), MAX_PHONE_LENGTH);
                    XToast.show(getContext(), getStringRes(R.string.feedback_phone_number_toast));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 点击提交按钮
     */
    @OnClick(R.id.feedback_submit_button)
    public void clickSubmit() {
        //强制隐藏键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.getView(), InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);

        String opinion = feedbackOpinionEdittext.getText().toString();
        String phone = feedbackContactPhoneEdittext.getText().toString();
        if (opinion.length() > MAX_OPINION_LENGTH) {
            XToast.show(getContext(), getStringRes(R.string.feedback_opinion_toast));
        } else if (phone.length() > MAX_PHONE_LENGTH) {
            XToast.show(getContext(), getStringRes(R.string.feedback_phone_number_toast));
        } else if (!Function.isNetConnect(getActivity())) {
            XToast.show(getContext(), getStringRes(R.string.feedback_network_error));
        } else {
            getCommand().submit(opinion, phone);
        }

    }


    /**
     * 初始化问题截图gridview
     *
     * @param adapter 适配器
     */
    @Override
    public void initGridView(final UploadImageAdapter adapter, final List<String> list) {
        //设置适配器
        feedbackUploadImageGridview.setAdapter(adapter);
        //设置列表点击监听
        feedbackUploadImageGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断点击的截图是否含有图片
                if (TextUtils.isEmpty(list.get(position))) {
                    int size = list.size();
                    if (list.get(position) == null) {
                        size = 0;
                    }
                    //跳转到图片选择页面
                    getCommand().selectUploadImages(size - 1);
                } else {
                    //显示图片大图
                    getCommand().showBigImage(list.get(position));
                }
            }
        });
        //设置列表长按点击监听
        feedbackUploadImageGridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //判断点击的截图是否含有图片
                if (!TextUtils.isEmpty(list.get(position))) {
                    //弹出删除对话框
                    getCommand().isDeleteImage(adapter, list, position);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 是否显示提交成功页面
     *
     * @param isShow 是否显示
     */
    @Override
    public void isShowSubmitSuccessLayout(boolean isShow) {
        if (isShow) {
            feedbackLayout1.setVisibility(View.GONE);
            feedbackLayout2.setVisibility(View.VISIBLE);
            //add by xienana for bug 6803 @20161213
            feedbackHint.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_LIST,
                    0, 0, 0, getStringRes(R.string.feedback_success_info)));
            //add by xienana for bug 6803 @20161213
        } else {
            feedbackLayout2.setVisibility(View.GONE);
            feedbackLayout1.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示等待框
     *
     * @param msg 显示信息
     */
    @Override
    public void showDialog(String msg) {
        showCommonProgressDialog(msg);
    }

    /**
     * 关闭等待框
     */
    @Override
    public void closeDialog() {
        dismissCommonProgressDialog();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_feed_back);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
