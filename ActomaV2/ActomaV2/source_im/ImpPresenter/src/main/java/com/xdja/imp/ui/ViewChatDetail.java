package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.imp.ImApplication;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.activity.ChatDetailActivity;
import com.xdja.imp.presenter.command.IChatDetailCommand;
import com.xdja.imp.ui.vu.IChatDetailVu;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.imp.util.IMMediaPlayer;
import com.xdja.simcui.recordingControl.view.AudioRecorderButton;
import com.xdja.simcui.view.ChatActionView;
import com.xdja.simcui.view.ChatInputView;
import com.xdja.simcui.view.SwpipeListViewOnScrollListener;
import com.xdja.simcui.view.TipsTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/11/23.
 */
public class ViewChatDetail extends ImpActivitySuperView<IChatDetailCommand>
        implements IChatDetailVu, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 底部输入操作栏
     */
    private ChatInputView inputAction;

    /**
     * 底部动作栏
     */
    private ChatActionView chatAction;

    /**
     * 中间拖拽列表
     */
    private ListView chatList;

    /**
     * 下拉刷新组件
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * 听筒模式提示框
     */
    private TipsTextView mTipsTv;

    /**
     * 键盘
     */
    private InputMethodManager inputMethodManager;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chat_detail;
    }

    // 初始化动作菜单
    private final List<ChatActionView.MenuBean> menus = new ArrayList<>();

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            inputAction = (ChatInputView)view.findViewById(R.id.layout_chat_operate);
            chatAction = (ChatActionView)view.findViewById(R.id.layout_chat_action);
            chatList = (ListView) view.findViewById(R.id.chatlist);
            swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
            mTipsTv = (TipsTextView) view.findViewById(R.id.tv_tips_receiver);
        }
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        inputMethodManager = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        swipeRefreshLayout.setColorSchemeColors(getColorRes(R.color.base_title_gold));
        swipeRefreshLayout.setOnRefreshListener(this);

        chatList.setOnScrollListener(new SwpipeListViewOnScrollListener(swipeRefreshLayout));
        View headView = getActivity().getLayoutInflater().inflate(R.layout.detail_listview_head, null);
        chatList.addHeaderView(headView);

        initChatList();

        initChatAction();

        initInputAction();
    }


    @Override
    public void initListView(BaseAdapter adapter) {
        if (chatList != null) {
            chatList.setAdapter(adapter);
        }

    }

    @Override
    public ListView getDisplayList() {
        return chatList;
    }

    @Override
    public void setListSelection(int selection) {
        LogUtil.getUtils().d("listSelection:selection=" + selection);
        if (chatList != null) {
            chatList.setSelection(selection);
        }
    }

    @Override
    public void setDownRefreshSelection(int selection) {
        LogUtil.getUtils().e("refreshSelection:selection=" + selection);
        if (chatList != null) {
            chatList.setSelection(selection);
        }
    }

    private void initChatList() {
        this.chatList.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (inputAction.moreCheck.isChecked()) {
                    restoreActionState();
                    return true;
                }
                //add by zya 20170308
                chatList.setFocusable(true);
                //end by zya
                displayInputKeyBoard(false, inputAction.inputEare);

                return false;
            }
        });
        this.chatList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //add by zya 20170308
                chatList.setFocusable(true);
                //end by zya
                if ((firstVisibleItem + visibleItemCount + 1) == totalItemCount) {
                    LogUtil.getUtils().d("onListScroller2Bottom");
                }

            }
        });
    }

    /**
     * 重置action面板状态
     */
    public void restoreActionState() {
        setBottomViewState(false);
        inputAction.virtualCheck.setVisibility(View.GONE);
        inputAction.moreCheck.setChecked(false);
    }

    /**
     * 更改底部view的状态
     *
     * @param isShowActionView 是否显示操作面板 true:显示操作面板，如果最后一条数据可见，设置listView滚动
     */
    private void setBottomViewState(boolean isShowActionView) {
        if (isShowActionView) {
            chatAction.setVisibility(View.VISIBLE);
            if (chatList != null && getCommand().getMessageList() != null) {
                // 如果当前最后一个itemView可见
                if (chatList.getLastVisiblePosition() == getCommand().getMessageList().size()) {
                    chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                } else {
                    chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                }
            }
        } else {
            chatAction.setVisibility(View.GONE);
            chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        }
    }

    /**
     * 菜单初始化
     */
    private void initActionMenus() {
        menus.clear();
        Resources resource = getContext().getResources();
        menus.add(chatAction.new MenuBean(R.drawable.icon_face, resource.getString(R.string.action_emoj)));
        menus.add(chatAction.new MenuBean(R.drawable.icon_picture, resource.getString(R.string.action_picture)));
        menus.add(chatAction.new MenuBean(R.drawable.icon_camera, resource.getString(R.string.action_photo)));
        //只有单聊才支持文件发送
        if (getCommand().getSessionType() == ConstDef.CHAT_TYPE_P2P && !inputAction.shanCheck.isChecked()) {
            menus.add(chatAction.new MenuBean(R.drawable.icon_file, resource.getString(R.string.action_file)));
            menus.add(chatAction.new MenuBean(R.drawable.icon_video, resource.getString(R.string.action_video)));
        }
        chatAction.renderMenuView(menus);
    }

    private void initChatAction() {
        this.chatAction.setAcceptInput(this.inputAction.inputEare);
        initActionMenus();
        this.chatAction.setOnItemClickCallBack(new ChatActionView.ChatActionOnItemClick() {

            @Override
            public void onItemClickCallBack(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 0://表情
                        break;
                    case 1://图片
                        getCommand().startToAlbum();
                        break;
                    case 2://拍照
                        /**
                         * 修改人 guorong
                         * 时间 2016-8-2 15:38:02
                         * 增加处理照相相关的权限问题的方法
                         * mate8权限问题 2237
                         * */
                        getCommand().handleTakePhotoPermission(0);
                        break;
                    case 3: //文件
                        getCommand().startToFileExplorer();
                        break;
					case 4://短视频
                        getCommand().handleTakePhotoPermission(1);
                        break;
                }
            }

            @Override
            public void onBeforeFacePanelShowCallBack() {
                inputAction.requestFocusForEditText();
            }
        });
    }

    private void displayInputKeyBoard(boolean isShow, EditText inputEare) {
        //add by zya 200170308
        setEditTextFocus(inputEare, isShow);
        //end by zya
        if (!isShow) {
            inputMethodManager.hideSoftInputFromWindow(inputEare.getWindowToken(), 0);
        } else {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //add by zya 20170308
    private void setEditTextFocus(EditText editText ,boolean isShow){
        if(isShow){
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        } else {
            editText.setFocusable(false);
        }
    }//end by zya

    private void initInputAction() {
        this.inputAction.sendVoic.setChatDetailActivity((ChatDetailActivity) getActivity());
        this.inputAction.registActionViewCallBack(new ChatInputView.IChatActionView() {
            // 标识是否为特殊情况
            // 若当前为语音状态时，展开action面板时需要做一下动作：
            // 1.将当前状态改为文字输入状态；
            // 2.阻止面板弹出（正常情况是需要弹出来的）
            private boolean isSpecial = false;

            @Override
            public void onMoreCheckChanged(boolean isChecked) {
                if (isChecked) {// 如果选择开启action面板
                    // 隐藏输入面板
                    displayInputKeyBoard(false, inputAction.inputEare);
                    new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            setBottomViewState(true);
                        }
                    }.sendEmptyMessageDelayed(0, 80);
                    //end zya@xdja.com

                    // 如果当前为语音输入状态
                    if (inputAction.inputCheck.isChecked()) {
                        // 复合特殊情况，进行标识
                        isSpecial = true;
                        // 将当前状态改为文字输入状态；
                        inputAction.inputCheck.setChecked(false);
                        return;
                    }
                } else {
                    //隐藏action面板
                    setBottomViewState(false);
                }
                chatAction.setFacePanelVisiable(false);
                isSpecial = false;
            }

            @Override
            public void onShanCheckChanged(boolean isChecked) {
                getCommand().setLimitFlagIsCheck(isChecked);

                //[S]modify by lll@xdja.com 2016/12/21
                //reason：文件支持条件：单聊非闪信
                if (getCommand().getIsSingleChat()) {
                    if (isChecked) {
                        if (menus.size() > 4){
                            menus.remove(4);//移除短视频
                            menus.remove(3);//移除文件
                            chatAction.renderMenuView(menus);
                        }
                    } else {
                        menus.add(chatAction.new MenuBean(R.drawable.icon_file,
                                getStringRes(R.string.action_file)));
                        menus.add(chatAction.new MenuBean(R.drawable.icon_video,
                                getStringRes(R.string.action_video)));
                        chatAction.renderMenuView(menus);
                    }
                }
                //[E]modify by lll@xdja.com 2016/12/21
            }

            @Override
            public void onInputCheckChanged(boolean isChecked) {
                // 输入方式按钮被点击,并且不是从语音状态切换过来的（非特殊情况），隐藏动作面板
                if (!isSpecial) {
                    restoreActionState();
                }
                if (isChecked) {// 语音输入，隐藏输入面板
                    displayInputKeyBoard(false, inputAction.inputEare);
                } else {
                    if (isSpecial) {// 特殊情况，隐藏输入面板
                        displayInputKeyBoard(false, inputAction.inputEare);
                        isSpecial = false;
                    } else {// 非特殊情况，弹出输入面板
                        displayInputKeyBoard(true, inputAction.inputEare);
                        setListSelection2Last();
                    }
                }
            }

            @Override
            public void onSendTextCallBack() {
                if (TextUtils.isEmpty(inputAction.inputEare.getText().toString().trim())) {
                    Toast.makeText(getContext(),getContext().getResources().getString(R.string.send_content_not_null),Toast.LENGTH_SHORT).show();
                    return;
                }

                SpannableString ss = BitmapUtils.formatSpanContent(inputAction.inputEare.getText().toString(), getContext(), ImApplication.FACE_ITEM_NORMAL_VALUE);
                if (getCommand().sendTextMessage(ss.toString())) {
                    inputAction.inputEare.setText("");
                }
            }

            @Override
            public void onInputEareTouchCallBack() {
                // 此处用于处理键盘弹出时上推消息列表的逻辑
                // 根据测试，发现一下现象：
                // 调用listView的setSelection()方法，将最后一条数据设为显示的话键盘弹出时可以将数据上推；
                // 如果列表中最后一条数据没显示，键盘弹出使不应该上推数据。
                initChatAction();
                restoreActionState();
                setListSelection2Last();
            }

            @Override
            public void onVirtualViewCallBack() {
                if (chatAction.getVisibility() == View.GONE) {
                    // chatAction.setVisibility(View.VISIBLE);
                    setBottomViewState(true);
                }
                chatAction.setFacePanelVisiable(false);
            }
        });
        this.inputAction.sendVoic.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                getCommand().sendVoiceMessage(filePath, ((int) seconds));
            }
        });
    }

    /**
     * 根据当前列表数据显示情况（最后一项listItem是否显示）设置是否跳转最后一项显示
     */
    public void setListSelection2Last() {
        if (chatList != null && getCommand().getMessageList() != null) {
            chatList.setSelection(getCommand().getMessageList().size());
        }
    }

    @Override
    public String getInputString() {
        if(inputAction != null){
            return inputAction.inputEare.getText().toString();
        }
        return "";
    }

    @Override
    public void setMessageText(String message) {
        if (!TextUtils.isEmpty(message)) {
            CharSequence messageStr = BitmapUtils.formatSpanContent(message, getActivity(), ImApplication.FACE_ITEM_NORMAL_VALUE);
            inputAction.inputEare.setText(messageStr);
            inputAction.inputEare.setFocusable(true);
            inputAction.inputEare.setSelection(messageStr.length());
        }else{
            inputAction.inputEare.setText("");
        }
    }

    @Override
    public void restoreInputAction() {
        initActionMenus();
        inputAction.shanCheck.setChecked(false);
        inputAction.moreCheck.setChecked(false);
    }

    @Override
    public void onReceiverModeChanged(boolean isOpen) {
        //如果没有播放，则不进行提示
        if (!IMMediaPlayer.isPlaying()) {
            return;
        }
        Drawable leftDrawable;
        if (isOpen) {//切换为听筒模式
            mTipsTv.setText(R.string.tips_receiver);
            leftDrawable = getDrawableRes(R.drawable.ic_tips_receiver);
        } else {    //切换扬声器模式（正常默认）

            mTipsTv.setText(R.string.tips_loudspeaker);
            leftDrawable = getDrawableRes(R.drawable.ic_tips_loudspeaker);
        }
        if (leftDrawable != null) {
            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
            mTipsTv.setCompoundDrawables(leftDrawable, null, null, null);
        }
        mTipsTv.showTips();
    }

    @Override
    public void onRefresh() {
        getCommand().downRefreshList();
    }

    @Override
    public void stopRefresh() {
        LogUtil.getUtils().e("stopRefresh : enabled = " + swipeRefreshLayout.isEnabled() +
                ",isRefreshing = " + swipeRefreshLayout.isRefreshing());
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getActivity().getMenuInflater().inflate(R.menu.menu_chat_info, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        switch (getCommand().getSessionType()) {
            case ConstDef.CHAT_TYPE_P2P:
                if(getCommand().getIsFriend()){
                    getActivity().getMenuInflater().inflate(R.menu.menu_chat_info, menu);
                }else {
                    getActivity().getMenuInflater().inflate(R.menu.menu_empty, menu);
                }
                break;
            case ConstDef.CHAT_TYPE_P2G:
                //TODO:gbc
                if(getCommand().getIsInGroup()){
                    getActivity().getMenuInflater().inflate(R.menu.menu_group_chat_info, menu);
                }else{
                    getActivity().getMenuInflater().inflate(R.menu.menu_group_empty, menu);
                }
                break;
            case ConstDef.CHAT_TYPE_ACTOMA:
                break;
            case ConstDef.CHAT_TYPE_DEFAULT:
                break;
            case ConstDef.CHAT_TYPE_P2M:
                break;
            case ConstDef.CHAT_TYPE_PIC_PREVIEW:
                break;
            case ConstDef.CHAT_TYPE_PIC_SELECT:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_item_voip) {
            getCommand().call();
        } else if (item.getItemId() == R.id.menu_item_chat_info) {
            //进入单人消息设置前判断是否被对方删除
            if(!getCommand().getIsFriend()){//如果被删除
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.not_friend),Toast.LENGTH_SHORT).show();
                getActivity().invalidateOptionsMenu();
                return true;
            }
            getCommand().startSettingPage();
        } else if (item.getItemId() == R.id.menu_item_group_chat_group_info) {
            //进入群消息设置前判断是否被踢出群
            if(!getCommand().getIsInGroup()){//如果被踢
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.not_group_member),Toast.LENGTH_SHORT).show();
                getActivity().invalidateOptionsMenu();
                return true;
            }
            getCommand().startGroupSettingPage();
        }
        return  true;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }
    //start fix bug 5146 by licong,reView by zya,2016/10/25
    @Override
    public void showPermissionDialog() {
        final CustomDialog customDialog = new CustomDialog(getContext());
        if (!customDialog.isShowing()) {
            customDialog.setTitle(getActivity().getString(R.string.camera_prompt)).setMessage(TextUtil.getActomaText(getActivity(), TextUtil
                    .ActomaImage.IMAGE_LIST, 0, 0, 0, getActivity().getString(R.string.camera_error))).setNegativeButton(getActivity().getString
                    (R.string.security_password_layout_confim), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            }).setCancelable(true).show();
        }
    }
    //end fix bug 5146 by licong,reView by zya,2016/10/25
}
