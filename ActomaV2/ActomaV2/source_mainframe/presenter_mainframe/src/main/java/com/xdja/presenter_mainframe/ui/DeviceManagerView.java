package com.xdja.presenter_mainframe.ui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.xdja.data_mainframe.util.Util;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.DeviceInfoBean;
import com.xdja.presenter_mainframe.cmd.DeviceManagerCommand;
import com.xdja.presenter_mainframe.presenter.adapter.DeviceManagerAdapter;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDeviceManager;
import com.xdja.presenter_mainframe.widget.SettingBarView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemLongClick;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_device_manager)
public class DeviceManagerView extends ActivityView<DeviceManagerCommand> implements VuDeviceManager {

    @Bind(R.id.sb_device_manager_auth)
    SettingBarView sbDeviceManagerAuth;
    @Bind(R.id.lv_device_manager_devices)
    ListView lvDeviceManagerDevices;
    private DeviceManagerAdapter adapter;
    private List<DeviceInfoBean> deviceInfoList;

    @OnClick(R.id.sb_device_manager_auth)
    void authDeviceLogin() {
        getCommand().authDeviceLogin();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }


    @Override
    public void setDeviceInfoList(List<DeviceInfoBean> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
        if (adapter == null) {
            adapter = new DeviceManagerAdapter(deviceInfoList, getActivity());
            lvDeviceManagerDevices.setAdapter(adapter);
        } else {
            adapter.setItems(deviceInfoList);
        }
    }

    @OnItemLongClick(R.id.lv_device_manager_devices)
    boolean longClickDevice(final int position) {
        final XDialog longClickDeviceDialog = new XDialog(getActivity());
        longClickDeviceDialog.setCanceledOnTouchOutside(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_long_click_device, null);
        longClickDeviceDialog.setView(view);
        view.findViewById(R.id.tv_long_click_device_modify)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        longClickDeviceDialog.dismiss();
                        showModifyDeviceDialog(deviceInfoList.get(position));
                    }
                });
        view.findViewById(R.id.ll_long_click_device_delete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        longClickDeviceDialog.dismiss();
                        final XDialog xDialog = new XDialog(getActivity());
                        xDialog.setTitle(getStringRes(R.string.is_delete_device));
                        xDialog.setMessage(getStringRes(R.string.is_delete_device_content));
                        xDialog.setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                xDialog.dismiss();
                                longClickDeviceDialog.dismiss();
                                getCommand().deleteDevice(deviceInfoList.get(position));
                            }
                        }).setNegativeButton(getStringRes(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                xDialog.dismiss();
                            }
                        }).show();

                    }
                });
        longClickDeviceDialog.show();
        return true;
    }

    /**
     * 输入表情前的最后一个位置
     */
    private int mSelectionEnd;

    /**
     * 是否重置了Text
     */
    private boolean mResetText;

    /**
     *输入表情前的字符串
     */
    private String mInputAfterText;

    private void showModifyDeviceDialog(final DeviceInfoBean deviceInfoBean) {
        final XDialog modifyDeviceNameDialog = new XDialog(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_input_view, null);
        final EditText editText = (EditText) view.findViewById(R.id.edt_dialog_input_view);
        editText.setText(deviceInfoBean.getDeviceName());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        //modify by alh@xdja.com to fix bug: 603 2016-06-24 start(rummager: tangsha)
        editText.setSelection(editText == null || editText.getText() == null ? 0 :editText.getText().toString().trim().length());
        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 1116 . review by wangchao1. Start
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 1116 . review by wangchao1. End

        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-19 add. fix bug 4139 . review by wangchao1. Start
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mResetText) {
                    mSelectionEnd = editText.getSelectionEnd();
                    mInputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mResetText) {
                    if (count >= 2) {// 表情符号的字符长度最小为2
                        int length = mSelectionEnd + count < s.length() ? mSelectionEnd + count : s.length();
                        CharSequence input = s.subSequence(mSelectionEnd < s.length() ? mSelectionEnd : s.length(), length);
                        if (Util.containsEmoji(input.toString())) {
                            mResetText = true;
                            Toast.makeText(getContext(), R.string.device_format, Toast.LENGTH_SHORT).show();
                            editText.setText(mInputAfterText);
                            CharSequence text = editText.getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    mResetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //[s]modify by xienana for bug 5391 @20161031 [review by tangsha]
                if (s.length() >= 32) {
                    showToast(R.string.device_name_length_error);
                    return;
                }
                //[e]modify by xienana for bug 5391 @20161031 [review by tangsha]
            }
        });
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-19 add. fix bug 4139 . review by wangchao1. End

        modifyDeviceNameDialog.setCustomContentView(view);
        modifyDeviceNameDialog.setTitle(getStringRes(R.string.modify_device_name));
        modifyDeviceNameDialog.setPositiveButton(getStringRes(R.string.certain),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = editText.getText().toString().trim();
                        //modify by alh@xdja.com to fix bug: 603 2016-06-24 end(rummager: tangsha)
                        if (TextUtils.isEmpty(s)) {
                            showToast(getStringRes(R.string.input_device_name));
                            return;
                        }
                        modifyDeviceNameDialog.dismiss();
                        getCommand().modifyDeviceName(deviceInfoBean, s);
                    }
                });
        modifyDeviceNameDialog.setNegativeButton(getStringRes(R.string.cancel), null);
        modifyDeviceNameDialog.show();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_device_manager);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
