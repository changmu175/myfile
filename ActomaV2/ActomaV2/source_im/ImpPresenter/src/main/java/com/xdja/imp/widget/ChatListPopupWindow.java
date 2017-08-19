package com.xdja.imp.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.util.ObjectUtil;

/**
 * Created by wanghao on 2015/11/20.
 */
public class ChatListPopupWindow {

    private static ChatListPopupWindow instance;

    private ChatListPopupWindow() {
    }

    public static ChatListPopupWindow getInstance() {
        if (ObjectUtil.objectIsEmpty(instance)) {
            instance = new ChatListPopupWindow();
        }
        return instance;
    }

    private CustomDialog delDialog;

    public <T> void showPopupDialog(Context context, final PopupWindowEvent<T> event,final T dataSource) {
        LogUtil.getUtils().e("showPopupDialog context = " + context);
        if(context == null){
            return;
        }
        if (delDialog == null) {
            LayoutInflater factory = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = factory.inflate(R.layout.chatlist_pop_dialog, null);
            delDialog = new CustomDialog(context).setView(view);
            delDialog.setCanceledOnTouchOutside(true);
        }

        if(delDialog == null){
            return;
        }
        LinearLayout del = (LinearLayout) delDialog.getView().findViewById(R.id.delLayout);
        LinearLayout top = (LinearLayout) delDialog.getView().findViewById(R.id.topLayout);
        TextView topTxt = (TextView) delDialog.getView().findViewById(R.id.txtTop);

        //fix bug 3166 by licong, reView by zya, 2016/8/23
        if (event.isShowTop(dataSource)) {
            topTxt.setText(context.getText(R.string.talk_dalog_cancel_top_Content));
            top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.deleteTop(dataSource);
                    delDialog.dismiss();
                }
            });
        } else {
            topTxt.setText(context.getText(R.string.talk_dalog_top_Content));
            top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.settingTop(dataSource);
                    delDialog.dismiss();
                }
            });
        }
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.deleteSession(dataSource);
                delDialog.dismiss();
            }
        });
        LogUtil.getUtils().e("showPopupDialog context = " + context);
        try {
            delDialog.show();
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        }
    }

    public void dismissDialog(){
        if(delDialog != null){
            delDialog = null;
        }
    }

    public interface PopupWindowEvent<T> {
        /**
         * 删除会话
         *
         * @param dataSource 数据源
         */
        void deleteSession(T dataSource);

        /**
         * 设置置顶聊天
         *
         * @param dataSource 数据源
         */
        void settingTop(T dataSource);

        /**
         * 取消置顶聊天
         *
         * @param dataSource 数据源
         */
        void deleteTop(T dataSource);


        /**
         * 获取当前会话是否已经处于置顶状态
         *
         * @param dataSource 数据源
         * @return 置顶状态
         */
        boolean isShowTop(T dataSource);
    }


}
