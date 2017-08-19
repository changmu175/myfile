package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.CheckBox;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.DropMessageCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.DropMessageVu;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by luopeipei on 2015/11/9.
 */
@ContentView(value = R.layout.activity_drop_message)
public class ViewDropMessage extends ActivityView<DropMessageCommand> implements DropMessageVu {

    //进行删除聊天记录业务
    private final int DROP_MESSAGE = 1;
    //进行删除通话记录业务
    private final int CLEAR_CALL = 2;
    //删除聊天记录
    @OnClick(R.id.drop_all_message)
    public void dropAllMessageClick(){
        showDialog(R.string.drop_all_message_dialog,DROP_MESSAGE);
    }
    //删除通话记录
    @OnClick(R.id.clear_call_log)
    public void clearCallLog(){
        showDialog(R.string.clear_all_callLog_dialog,CLEAR_CALL);
    }

    @Bind(R.id.open_receiver_mode)
    public CheckBox openReceiverModeChx;

    //弹出删除聊天记录复选框
    @Override
    public void showDialog(int msg,final  int type) {
        final CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.setTitle(msg).setNegativeButton(getStringRes(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        }).setPositiveButton(getStringRes(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                //判断是删除信息还是通话记录操业务
                switch (type){
                    case DROP_MESSAGE:
                        getCommand().dropMessage();
                        break;
                    case CLEAR_CALL:
                        getCommand().clearCallLog();
                        break;
                    default:
                        break;
                }
//                if (type == DROP_MESSAGE) {
//                    getCommand().dropMessage();
//                }else if(type == CLEAR_CALL){
//                    getCommand().clearCallLog();
//                }
            }
        }).show();
        if (!customDialog.isShowing()){
            customDialog.show();
        }
    }

    @Override
    public void setReceiverMode(boolean isOn) {
        openReceiverModeChx.setChecked(isOn);
    }

    /**
     * 打开听筒模式-听筒模式开关单选项
     */
    @OnClick(R.id.receiver_mode)
    public void openReceiverModeClick(){
        openReceiverModeChx.setChecked(!openReceiverModeChx.isChecked());
        getCommand().openReceiverMode(openReceiverModeChx.isChecked());
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_drop_all_message_call);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
