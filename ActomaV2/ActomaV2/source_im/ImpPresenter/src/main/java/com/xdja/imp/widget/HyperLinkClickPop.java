package com.xdja.imp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.Gravity;
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
import com.xdja.imp.presenter.command.ChatDetailAdapterCommand;
import com.xdja.imp.util.XToast;

/**
 * 项目名称：ActomaV2
 * 类描述：超链接手机的点击事件
 * 创建人：yuchangmu
 * 创建时间：2016/11/04.
 * 修改人：yuchangmu
 * 修改时间：2016/11/04
 * 修改备注：
 * 1)Task 2632 modified by ycm for hyperlink click 2016/11/04
 * 2)Task 2632 modified by ycm for hyperlink click 2016/11/30
 */
public class HyperLinkClickPop extends PopupWindow {
    private final Context context;
    private final ChatDetailAdapterCommand command;
    private final LayoutInflater inflater;
    private ListView menuList;
    private final int[] type = new int[]{ConstDef.hyperlink_click_normal, ConstDef.hyperlink_click_addContact};
    private ActionOnItemClickCallBack onItemClick;// 点击处理
    private final String number;

    public HyperLinkClickPop(Context context, ChatDetailAdapterCommand command, int actionType, String number) {
        this.context = context;
        this.command = command;
        this.number = number;
        this.inflater = LayoutInflater.from(this.context);
        initiView();
        initData(actionType);
        setAttrs();
    }

    /**
     * 初始化界面
     */
    private void initiView() {
        LinearLayout popupLayout = (LinearLayout) inflater.inflate(R.layout.activity_message_popu, null);

        popupLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (HyperLinkClickPop.this.isShowing()) {
                    HyperLinkClickPop.this.dismiss();
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

    private void initData(int actionType) {
        if (context == null) {
            return;
        }
        final String[] actions = context.getResources().getStringArray(R.array.hyperlink_action);// 操作名称集合
        String[] actionTemp = null;
        if (actionType == type[0]) {
            actionTemp = new String[2];
            actionTemp[0] = actions[0];//呼叫
            actionTemp[1] = actions[1];//添加到现有联系人
        } else if (actionType == type[1]) {
            actionTemp = new String[2];
            actionTemp[0] = actions[2];//创建新的联系人
            actionTemp[1] = actions[3];//添加到已存在的联系人
        }
        if (actionTemp == null || actionTemp.length == 0) {
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_action_more, actionTemp);
        menuList.setAdapter(adapter);
        if (onItemClick == null) {
            onItemClick = new ActionOnItemClickCallBack(actions, actionTemp, this.command, this, number);
        }
        menuList.setOnItemClickListener(onItemClick);
    }

    /**
     * 长按弹出菜单点击时间处理类
     *
     * @author fanjiandong
     */
    class ActionOnItemClickCallBack implements AdapterView.OnItemClickListener {

        private final ChatDetailAdapterCommand handler;

        private final String[] actions;
        private final String[] actionTemp;

        private final HyperLinkClickPop hyperLinkClickPop;
        private final String number;

        /**
         * @param actions           完整菜单集合
         * @param actionTemp        过滤后的菜单集合
         * @param handler           回调实例
         * @param hyperLinkClickPop hyperLinkClickPop对象
         */
        public ActionOnItemClickCallBack(String[] actions, String[] actionTemp, ChatDetailAdapterCommand handler,
                                         HyperLinkClickPop hyperLinkClickPop, String number) {
            this.actions = actions;
            this.actionTemp = actionTemp;
            this.handler = handler;
            this.number = number;
            this.hyperLinkClickPop = hyperLinkClickPop;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position2, long arg3) {
                dismiss();
            if (this.actionTemp[position2].equals(actions[0])) {// 呼叫
                    callOut(number);
            } else if (this.actionTemp[position2].equals(actions[1])) {// 添加到手机通讯录
                    addToAccount(number, view);
            } else if (this.actionTemp[position2].equals(actions[2])) {// 创建新的联系人
                    insertNewContact(number);
            } else if (this.actionTemp[position2].equals(actions[3])) {// 添加到现有联系人
                    addToExistAccount(number);
            }
        }
    }

    private void setAttrs() {
        setFocusable(false);
        setOutsideTouchable(true);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void callOut(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        Uri uri = Uri.parse(ConstDef.TEL+number); // add by ycm for bug 20161201
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void addToAccount(String number, View view) {
        new HyperLinkClickPop(context, null,
                ConstDef.hyperlink_click_addContact, number)
                .showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void insertNewContact(String number) {
        Intent addIntent = new Intent(Intent.ACTION_INSERT,
                Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
        addIntent.setType("vnd.android.cursor.dir/person");
        addIntent.setType("vnd.android.cursor.dir/contact");
        addIntent.setType("vnd.android.cursor.dir/raw_contact");
        addIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(addIntent);
    }

    private void addToExistAccount(String number) {
        Intent oldConstantIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        oldConstantIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        int contactType = 1;
        oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, contactType);
        if (oldConstantIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(oldConstantIntent);
        } else {
            new XToast(context).display(R.string.add_contact_error);
        }
    }
}
