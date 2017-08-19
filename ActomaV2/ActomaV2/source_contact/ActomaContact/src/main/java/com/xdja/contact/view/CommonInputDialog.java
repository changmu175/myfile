package com.xdja.contact.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by XDJA_XA on 2015/8/9.
 *
 *
 */
public class CommonInputDialog {

    public static final int TYPE_GROUP_NAME = 1;

    public static final int TYPE_GROUP_NICKNAME = 2;

    @IntDef({TYPE_GROUP_NAME, TYPE_GROUP_NICKNAME})
    public @interface InputDialogType {
    }

    /**
     * 输入对话框
     */
    private CustomDialog dialog;
    /**
     * 标题
     */
    private TextView titleTv;
    /**
     * 说明
     */
    private TextView descriptionTv;
    /**
     * 输入框
     */
    private EditText inputEt;
    /**
     * 清除按钮
     */
    private ImageButton clearButton;

    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 后续操作回调接口
     */
    private InputDialogInterface listener;
    /**
     * 原始文字
     */
    private String rawText;


    public CommonInputDialog(Context context, String rawText, @InputDialogType int type,
                             InputDialogInterface listener) {
        this.context = context;
        this.listener = listener;
        this.rawText = rawText;
        initDialog(type);
    }

    private void initForGroupNameType() {
        titleTv.setText(R.string.group_name_setting);
        inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        inputEt.setHint(R.string.group_name);

        if (!ObjectUtil.stringIsEmpty(rawText)) {
            inputEt.setText(rawText);
        }

        CharSequence text = inputEt.getText();
        if(text instanceof Spannable){
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        descriptionTv.setVisibility(View.GONE);
    }

    private void initForNickNameType() {
        titleTv.setText(R.string.my_nickname_setting);
        inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        inputEt.setHint(R.string.my_nickname_in_group);
        if (!ObjectUtil.stringIsEmpty(rawText)) {
            inputEt.setText(rawText);
        }
        CharSequence text = inputEt.getText();
        if(text instanceof Spannable){
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        descriptionTv.setText(R.string.nickname_description);
        descriptionTv.setVisibility(View.VISIBLE);
    }
    /**
     * 初始化安全口令验证对话框
     */
    public void initDialog(@InputDialogType final int type) {
        //实例化自定义的对话框
        dialog = new CustomDialog(context);
        //自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.common_input_dialog_layout, null);

        //标题
        titleTv = (TextView)view.findViewById(R.id.title);
        //说明
        descriptionTv = (TextView)view.findViewById(R.id.description);
        //输入框
        inputEt = (EditText) view.findViewById(R.id.input_edittext);

        clearButton = (ImageButton) view.findViewById(R.id.btn_search_clear);
        clearButton.setVisibility(View.GONE);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEt.getText().clear();
                clearButton.setVisibility(View.GONE);
            }
        });
        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //设置自定义布局
        if (type == TYPE_GROUP_NAME) {
            initForGroupNameType();
        } else if (type == TYPE_GROUP_NICKNAME) {
            initForNickNameType();
        }



        //设置对话框
        dialog.setContentView(view)
                //设置确定按钮监听
                .setPositiveButton(context.getString(R.string.contact_ok), new View.OnClickListener() {//modify by wal@xdja.com for string 确定
                    @Override
                    public void onClick(View v) {
                        dialog.getmPositiveButton().setClickable(false);
                        if(!ContactModuleService.checkNetWork()) {
                            dialog.getmPositiveButton().setClickable(true);
                            return;
                        }//add by lwl 2528
                        if (inputEt.getText().toString().equals(rawText) && !"".equals(rawText)) {
                            dialog.dismiss();
                            listener.onCancel();
                            return;
                        }
                        //验证安全口令
                        if(type == TYPE_GROUP_NAME){
                            //start:add by wal@xdja.com for fix 1837
                            if (!ObjectUtil.stringIsEmpty(inputEt.getText().toString())) {
                                if (GroupUtils.containsEmoji(inputEt.getText().toString().trim())) {
                                    XToast.show(ActomaController.getApp(), R.string.update_group_name_contact_emoji);
                                    dialog.getmPositiveButton().setClickable(true);
                                    return;
                                }
                            //end:add by wal@xdja.com for fix 1837
                                listener.onOk(inputEt.getText().toString().trim(), dialog);
                            }else {
                                if (type == TYPE_GROUP_NAME) {
                                    XToast.show(context, context.getString(R.string.groupname_cannot_empty));
                                    dialog.getmPositiveButton().setClickable(true);
                                }
                            }
                        }else{
                            if(!ObjectUtil.stringIsEmpty(inputEt.getText().toString().trim())){
                                //add by lwl start 1487
                                if (GroupUtils.containsEmoji(inputEt.getText().toString().trim())) {
                                    XToast.show(context, R.string.updating_nickname_fail);
                                    dialog.getmPositiveButton().setClickable(true);
                                    return;
                                }
                                //add by lwl end 1487
                            }
                            listener.onOk(inputEt.getText().toString().trim(), dialog);
                        }
                        /*if (!ObjectUtil.objectIsEmpty(inputEt.getText().toString()) && !"".equals(inputEt.getText().toString())) {
                            listener.onOk(inputEt.getText().toString().trim(), dialog);
                        } else {
                            if (type == TYPE_GROUP_NAME) {
                                XToast.show(context, context.getString(R.string.groupname_cannot_empty));
                                dialog.getmPositiveButton().setClickable(true);
                            } else if (type == TYPE_GROUP_NICKNAME) {
                                XToast.show(context, context.getString(R.string.nickname_cannot_empty));
                                dialog.getmPositiveButton().setClickable(true);
                            }
                        }*/
                    }
                })
                        //设置取消按钮监听
                .setNegativeButton(context.getString(R.string.contact_cancel), new View.OnClickListener() {//modify by wal@xdja.com for string 取消
                    @Override
                    public void onClick(View v) {
                        if(ContactUtils.isFastDoubleClick()){
                            return;
                        }
                        dialog.dismiss();
                        listener.onCancel();
                    }
                })
                        //设置点击对话框外部不消失
                .setCanceledOnTouchOutside(false)
                        //显示对话框
                .show();
    }

    public interface InputDialogInterface {
        void onOk(final String input, final CustomDialog dialog);
        void onCancel();
    }
}
