package com.xdja.imp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.presenter.command.ChatDetailPopWindowCommand;

import java.io.File;

/**
 * Created by wyc on 2015/1/29.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Bug 5662, modify for share and forward function by ycm at 20161103.
 */
public class ChatDetailLongClickPopup extends PopupWindow {

    private final ChatDetailPopWindowCommand command;

    private final Context context;
    private final LayoutInflater inflater;
    private ListView menuList;
    // 通过对象判断显示内容
    private final TalkMessageBean bean;
    // 点击处理
    private ActionOnItemClickCallBack onItemClick;

    public ChatDetailLongClickPopup(Context context, TalkMessageBean bean, ChatDetailPopWindowCommand handler) {
        this.command = handler;
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.bean = bean;
        initiView();
        initData();
        //add by lc@xdja.com,fix bug NACTOMA-185
        initAttrs();
    }

    /**
     * 初始化界面
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initiView() {
        LinearLayout popupLayout = (LinearLayout) inflater.inflate(R.layout.activity_message_popu, null);

        popupLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ChatDetailLongClickPopup.this.isShowing()) {
                    ChatDetailLongClickPopup.this.dismiss();
                }
                return false;
            }
        });

        menuList = (ListView) popupLayout.findViewById(R.id.actionlist);

        this.setContentView(popupLayout);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        this.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0xb0000000);

        this.setBackgroundDrawable(dw);
    }

    /**
     * 长按弹出菜单点击时间处理类
     *
     * @author fanjiandong
     */
    class ActionOnItemClickCallBack implements AdapterView.OnItemClickListener {

        private final ChatDetailPopWindowCommand handler;

        private final String[] action1;
        private final String[] action2;

        private final ChatDetailLongClickPopup pop;

        /**
         * @param action1 完整菜单集合
         * @param action2 过滤后的菜单集合
         * @param handler 回调实例
         * @param pop     pop对象
         */
        public ActionOnItemClickCallBack(String[] action1, String[] action2, ChatDetailPopWindowCommand handler, ChatDetailLongClickPopup pop) {
            this.action1 = action1;
            this.action2 = action2;
            this.handler = handler;
            this.pop = pop;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (this.pop != null) {
                this.pop.dismiss();
            }
            if (this.action2[arg2].equals(action1[0])) {// 复制
                if (this.handler != null) {
                    this.handler.copy(bean);
                }
            } else if (this.action2[arg2].equals(action1[1]) || this.action2[arg2].equals(action1[3])) {// 重发
                if (this.handler != null) {
                    this.handler.repeat(bean);
                }
            } else if (this.action2[arg2].equals(action1[2])) {// 删除
                if (this.handler != null) {
                    this.handler.delete(bean);
                }
            } else if (this.action2[arg2].equals(action1[3])) {// 重新接收
                if (this.handler != null) {
                    this.handler.reDown(bean);
                }
            } else if (this.action2[arg2].equals(action1[4])) {// 使用听筒播放语音
                if (this.handler != null) {
                    this.handler.playMediaInCall(bean);
                }
            } else if (this.action2[arg2].equals(action1[5])) {// 使用扬声器播放语音
                if (this.handler != null) {
                    this.handler.playMediaInLoudspeakers(bean);
                }
            } else if (this.action2[arg2].equals(action1[6])) {// 暂停操作
                if (this.handler != null) {
                    this.handler.suspend(bean);
                }
            } else if (this.action2[arg2].equals(action1[7])) {// 继续发送文件
                if (this.handler != null) {
                    this.handler.repeat(bean);
                }
            } else if (this.action2[arg2].equals(action1[8])) {// 继续接收文件
                if (this.handler != null) {
                    this.handler.reDown(bean);
                }
            } else if (this.action2[arg2].equals(action1[9])) { // 拨打电话
                if (this.handler != null) {
                    this.handler.callPhone(bean);
                }
            } else if (this.action2[arg2].equals(action1[10])) { // 转发
                if (this.handler != null) {
                    this.handler.forwardMessage(bean);// Task 2632
                }
            } else if (this.action2[arg2].equals(action1[11])) { //打开文件
                if(this.handler != null){
                    this.handler.openFile(bean);
                }
            }

        }
    }

    @SuppressLint("ConstantConditions")
    private void initData() {
        if (context == null || bean == null) {
            return;
        }
        // 操作名称集合
        final String[] actions = context.getResources().getStringArray(R.array.action_name);
        String[] actionTemp = null;
        if (bean == null) {
            return;
        }
        // 卡咔消息
        // 发送失败标识
        boolean isFaild = false;
        // 接收失败标识
        boolean isRecFaild = false;

        // 如果发送失败并且是本人发送的消息
        if ((bean.getMessageState() == ConstDef.STATE_SEND_FAILD)
                && bean.isMine()) {
            isFaild = true;
        }

        switch (bean.getMessageType()) {
            //start add by lyq 2014-5-8-5 无请求内容类型
            case ConstDef.MSG_TYPE_PRESENTATION:
                actionTemp = new String[1];
                // 删除
                actionTemp[0] = actions[2];
                break;
            case ConstDef.MSG_TYPE_NO_CONTENT:
                if (isRecFaild) {
                    actionTemp = new String[3];
                    // 接收失败,重新接收
                    actionTemp[1] = actions[3];
                    // 删除
                    actionTemp[2] = actions[2];
                    // 转发
                    actionTemp[0] = actions[10];// Task 2632
                } else {
                    actionTemp = new String[2];
                    // 删除
                    actionTemp[1] = actions[2];
                    // 转发
                    actionTemp[0] = actions[10];// Task 2632
                }
                break;
            //end add by lyq 2014-5-8-5 无请求内容类型
            // 文本类型
            case ConstDef.MSG_TYPE_TEXT:
                if (isFaild) {
                    actionTemp = new String[4];
                    actionTemp[1] = actions[0];
                    actionTemp[2] = actions[1];
                    actionTemp[3] = actions[2];
                    // 转发
                    actionTemp[0] = actions[10];// Task 2632
                } else {
                    //如果是闪信并且是接收
                    if (!bean.isMine() && bean.getLimitTime() > 0) {
                        if(bean.getMessageState()==ConstDef.STATE_DESTROY) {//如果已销毁只能删除
                            actionTemp = new String[1];
                            actionTemp[0] = actions[2];
                        }else {//如果未销毁可以复制
                            actionTemp = new String[3];
                            actionTemp[1] = actions[0];
                            actionTemp[2] = actions[2];
                            // 转发
                            actionTemp[0] = actions[10];// Task 2632
                        }
                    } else {
                        actionTemp = new String[3];
                        actionTemp[1] = actions[0];
                        actionTemp[2] = actions[2];
                        // 转发
                        actionTemp[0] = actions[10];// Task 2632
                    }
                }
                break;
            // 语音类型
            case ConstDef.MSG_TYPE_VOICE:
                if (isFaild || isRecFaild) {
                    if (isFaild) {
                        actionTemp = new String[2];
                        //重发
                        actionTemp[0] = actions[1];
                        // 删除
                        actionTemp[1] = actions[2];
                    } else {
                        actionTemp = new String[2];
                        //重新接收
                        actionTemp[0] = actions[3];
                        //删除
                        actionTemp[1] = actions[2];
                    }


                } else {
                    actionTemp = new String[1];
                    // 删除
                    actionTemp[0] = actions[2];
                }
                break;
            // 图片类型
            case ConstDef.MSG_TYPE_PHOTO:
                if (isFaild || isRecFaild) {
                    actionTemp = new String[2];
                    if (isFaild) {
                        // 发送失败
                        actionTemp[0] = actions[1];
                    } else if (isRecFaild) {
                        // 接收失败
                        actionTemp[0] = actions[3];
                    }
                    // 删除
                    actionTemp[1] = actions[2];
                } else {
                    //for bug 5662 by ycm [start]
                    if (!bean.isMine() && bean.getLimitTime() > 0) {
                        if(bean.getMessageState()==ConstDef.STATE_DESTROY) {//如果已销毁只能删除
                            actionTemp = new String[1];
                            actionTemp[0] = actions[2];
                        }else {//如果未销毁可以删除和转发
                            actionTemp = new String[2];
                            actionTemp[1] = actions[2];
                            // 转发
                            actionTemp[0] = actions[10];// Task 2632
                        }
                    } else {
                        actionTemp = new String[2];
                        actionTemp[1] = actions[2];
                        // 转发
                        actionTemp[0] = actions[10];// Task 2632
                    }
                }
                break;
            case ConstDef.MSG_TYPE_FILE: // 文件转发
                if (isFaild || isRecFaild) {
                    actionTemp = new String[2];
                    if (isFaild) {//发送失败
                        actionTemp[0] = actions[1];//重发
                    } else if (isRecFaild) {//接收失败
                        actionTemp[0] = actions[3];
                    }
                    actionTemp[1] = actions[2];//删除
                } else {
                    if (!bean.isMine()) {
                        if (bean.getFileInfo().getFileState() == ConstDef.DONE) {//下载完成的文件
                            actionTemp = new String[2];
                            actionTemp[0] = actions[10];//转发
                            actionTemp[1] = actions[2];//删除
                        } else {//未下载完成的文件
                            actionTemp = new String[1];
                            actionTemp[0] = actions[2];//删除
                        }
                    } else {
                        actionTemp = new String[2];
                        actionTemp[0] = actions[10];//转发
                        actionTemp[1] = actions[2];//删除
                    }
                }
                break;

            case ConstDef.MSG_TYPE_VIDEO: // 短视频转发
                if (isFaild || isRecFaild) {
                    actionTemp = new String[2];
                    if (isFaild) {//发送失败
                        actionTemp[0] = actions[1];//重发
                    } else if (isRecFaild) {//接收失败
                        actionTemp[0] = actions[3];
                    }
                    actionTemp[1] = actions[2];//删除
                } else {
                    if (!bean.isMine()) {
                        VideoFileInfo videoFileInfo = (VideoFileInfo) bean.getFileInfo();
                        File file = new File(videoFileInfo.getExtraInfo().getRawFileUrl());
                        if (file.exists() && file.length() == videoFileInfo.getExtraInfo().getRawFileSize()){
                            actionTemp = new String[2];
                            actionTemp[0] = actions[10];//转发
                            actionTemp[1] = actions[2];//删除
                        } else {//未下载完成的文件
                            actionTemp = new String[1];
                            actionTemp[0] = actions[2];//删除
                        }
                    } else {
                        actionTemp = new String[2];
                        actionTemp[0] = actions[10];//转发
                        actionTemp[1] = actions[2];//删除
                    }
                }
                break;
            /*//普通文件
            case ConstDef.MSG_TYPE_FILE:
                if(bean.isMine()){
                    if(bean.getFileInfo().getFileState() == ConstDef.DONE){
                        actionTemp = new String[4];
                        actionTemp[0] = actions[11];
                        actionTemp[1] = actions[10];
                        actionTemp[2] = actions[2];
                        actionTemp[3] = actions[3];
                    }else{
                        actionTemp = new String[3];
                        actionTemp[0] = actions[10];
                        actionTemp[1] = actions[2];
                        actionTemp[2] = actions[3];
                    }

                }else{
                    actionTemp = new String[2];
                    actionTemp[0] = actions[10];
                    actionTemp[1] = actions[2];
                }
                break;*/
				//网页消息
            case ConstDef.MSG_TYPE_WEB:
                if (isFaild || isRecFaild) {
                    actionTemp = new String[2];
                    if (isFaild) {
                        // 发送失败
                        actionTemp[0] = actions[1];
                    } else if (isRecFaild) {
                        // 接收失败
                        actionTemp[0] = actions[3];
                    }
                    // 删除
                    actionTemp[1] = actions[2];
                } else {
                    //for bug 5662 by ycm [start]
                    actionTemp = new String[2];
                    actionTemp[1] = actions[2];
                    // 转发
                    actionTemp[0] = actions[10];// Task 2632
                }
                break;
            case ConstDef.MSG_TYPE_DEFAULT:
                break;
            default:
                break;

        }


        if (actionTemp == null || actionTemp.length == 0) {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_action_more, actionTemp);
        menuList.setAdapter(adapter);
        if (onItemClick == null) {
            onItemClick = new ActionOnItemClickCallBack(actions, actionTemp, this.command, this);
        }
        menuList.setOnItemClickListener(onItemClick);
    }

    //add by lc@xdja.com,view by zya@xdja.com ,fix bug NACTOMA-185
    private void initAttrs(){
        setFocusable(false);
        setOutsideTouchable(true);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
