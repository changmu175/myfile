package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.task.friend.TaskAcceptFriendReq;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.NotificationUtil;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/21.
 */
public class FriendRequestHistoryAdapter extends BaseAdapter implements IModuleHttpCallBack {

    private String TAG = "FriendRequestHistoryAdapter";
    private Context context;

    private ViewHolder holder;

    //[S]modify by tangsha@20170119 for show as friends but not friend
   // private int selectPosition;
    FriendRequestHistory acceptHistory;
    //[E]modify by tangsha@20170119 for show as friends but not friend

    private String showAccount;

    private List<FriendRequestHistory> dataSource;

    public FriendRequestHistoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ObjectUtil.collectionIsEmpty(dataSource) ? 0 : dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_request_list_item, null);
            holder.userPhoto = (CircleImageView) convertView.findViewById(R.id.user_photo);
            holder.atAccount = (TextView) convertView.findViewById(R.id.atAccount);
            holder.requestInfo = (TextView) convertView.findViewById(R.id.request_info);
            holder.judgeButton = (Button) convertView.findViewById(R.id.judge_button);
            holder.judgeText = (TextView) convertView.findViewById(R.id.judge_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FriendRequestHistory requestHistory = dataSource.get(position);
        if (!ObjectUtil.objectIsEmpty(requestHistory)) {
            if (ObjectUtil.stringIsEmpty(requestHistory.getActomaAccount().getNickname())) {
                //start:add by wal@xdja.com for 409
                if(!ObjectUtil.stringIsEmpty(requestHistory.getActomaAccount().getAlias())){
                    holder.atAccount.setText(requestHistory.getActomaAccount().getAlias());
                }else{
                    holder.atAccount.setText(requestHistory.getShowAccount());
                }
//                holder.atAccount.setText(requestHistory.getShowAccount());
                //end:add by wal@xdja.com for 409
            } else {
                holder.atAccount.setText(requestHistory.getActomaAccount().getNickname());
            }
            if (requestHistory.getRequestState() == FriendHistoryState.ALREADY_FRIEND) {
                holder.requestInfo.setText(requestHistory.getAuthInfo());
                holder.judgeText.setVisibility(View.VISIBLE);
                holder.judgeText.setText(context.getString(R.string.contact_friend_state_add));//modify by wal@xdja.com for string 已添加
                holder.judgeButton.setVisibility(View.GONE);
                //start:add for 1113 by wal@xdja.com
//                if(requestHistory.getReqAccount()==requestHistory.getShowAccount()){
//                    FriendRequestInfoService friendRequestInfoService = new FriendRequestInfoService();
//                    friendRequestInfoService.delete(requestHistory.getShowAccount());
//                }
                //end:add for 1113 by wal@xdja.com
            } else if (requestHistory.getRequestState() == FriendHistoryState.ACCEPT) {
                holder.requestInfo.setText(requestHistory.getAuthInfo());
                holder.judgeButton.setVisibility(View.VISIBLE);
                holder.judgeText.setVisibility(View.GONE);
                holder.judgeButton.setBackgroundResource(R.drawable.selector_btn_gold);
                holder.judgeButton.setAllCaps(false);
                holder.judgeButton.setText(context.getString(R.string.contact_friend_state_accepte));//modify by wal@xdja.com for string 接受
            } else if (requestHistory.getRequestState() == FriendHistoryState.WAIT_ACCEPT) {
                holder.requestInfo.setText(requestHistory.getAuthInfo());
                holder.judgeText.setVisibility(View.VISIBLE);
                holder.judgeText.setText(context.getString(R.string.contact_friend_state_wait_for_others));//modify by wal@xdja.com for string 等待对方验证
                holder.judgeButton.setVisibility(View.GONE);
            }
            holder.judgeButton.setOnClickListener(new JudgeButtonListener(position));
            Avatar avatar = requestHistory.getAvatar();
            if (!ObjectUtil.objectIsEmpty(avatar) && !ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                holder.userPhoto.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                notifyDataSetChanged();
            } else {
                holder.userPhoto.setImageResource(R.drawable.img_avater_40);
                notifyDataSetChanged();
            }

        }
        return convertView;
    }

    @Override
    public void onFail(HttpErrorBean httpErrorBean) {
        if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
            showDismissDialogCallback.dismissDialog();
        }
        if (!ObjectUtil.objectIsEmpty(httpErrorBean)) {
            String errorCode = httpErrorBean.getErrCode();
            LogUtil.getUtils().e("Actoma contact FriendRequestHistory Fail httpErrorBean getErrCode:"+errorCode);
            if (errorCode.equals("already_friend")) {
                XToast.show(context, R.string.already_is_friend);
                if (updateHistory()) {
                    if (updateFriend()) {
                        //start:add by wal@xdja.com for 好友事件通知
                        //modify ysp
                    /*List<String> friendAcountList=new ArrayList<String>();
                    friendAcountList.add(showAccount);
                    List<String> showNameList = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("",friendAcountList );
                    if (showNameList.size()>0){
                        FireEventUtils.pushFriendClickedAcceptButton(showAccount,showNameList.get(0));;
                    }else{
                        FireEventUtils.pushFriendClickedAcceptButton(showAccount,showAccount);;
                    }*/
                        //end:add by wal@xdja.com for 好友事件通知
                        BroadcastManager.refreshFriendList();
                        NotificationUtil.cancelNotification();
                        notifyDataSetChanged();
                    }
                }
            } else if (errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_NOT_VALID.getCode())
                    || errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_ERROR.getCode())) {
                XToast.show(context, R.string.version_too_low);
            } else if (errorCode.equals(ServiceErrorCode.INTERNAL_SERVER_ERROR.getCode())
                    || errorCode.equals(ServiceErrorCode.EXCEPTION_HANDLE_ERROR.getCode())) {
                XToast.show(context, R.string.server_is_busy);
                // } else if(errorCode.equals("friend_account_not_exists")){  modify by lwl
            } else if (ServiceErrorCode.EXCEPTION_FRIEND_NOT_EXIST.getCode().equals(errorCode)) {
                XToast.show(context, R.string.invalid_account);
                // } else if(errorCode.equals("no_friend_req")){
            } else if (ServiceErrorCode.EXCEPTION_NO_FRIEND_REQ.getCode().equals(errorCode)) {
                XToast.show(context, R.string.friend_request_invalided);
            } else if (errorCode.equals(ServiceErrorCode.FRIENDS_LIMIT_ERROR.getCode())) {//modify by lwl start 2319 好友数量达到上限
                XToast.show(context, ActomaController.getApp().getString(R.string.friend_friends_max_limit));//modify by xienana for multi language change @20161205
            } else {
                XToast.show(context, R.string.accept_friend_error);
            }
        }else{
            XToast.show(context, R.string.accept_friend_error);
            LogUtil.getUtils().e("Actoma contact FriendRequestHistory accept friend error,httpErrorBean is null");
        }
        acceptHistory = null;
        notifyDataSetChanged();
    }

    //更新本地历史请求记录
    private boolean updateHistory(){
        if (acceptHistory == null) return false;
        showAccount = acceptHistory.getShowAccount();
        LogUtil.getUtils().w(TAG+" updateHistory showAccount "+showAccount);
        FriendRequestService friendRequestService = new FriendRequestService();
        acceptHistory.setRequestState(FriendHistoryState.ALREADY_FRIEND);
        acceptHistory.setIsRead(FriendRequestHistory.READED);
        return friendRequestService.update(acceptHistory);
    }
    //更新本地好友数据信息
    private boolean updateFriend(){
        FriendService friendService = new FriendService();
        Friend friend = new Friend.Builder().buildNormalFriend(showAccount);
        int count = friendService.countFriends();
        if(count > 0){
            friend.setUpdateSerial("1");
        }
        return friendService.saveOrUpdate(friend);
    }
    /*[S]add by tangsha@20160718 for CKMS create group*/
    private String currentAccount = ContactUtils.getCurrentAccount();
    /*[E]add by tangsha@20160718 for CKMS create group*/

    @Override
    public void onSuccess(String body) {
        if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
            showDismissDialogCallback.dismissDialog();
        }
        if (updateHistory()) {
            if (updateFriend()) {
                //start:add by wal@xdja.com for 好友事件通知
                List<String> friendAcountList=new ArrayList<String>();
                friendAcountList.add(showAccount);
                List<String> showNameList = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("",friendAcountList );
                if (!showNameList.isEmpty()){
                    FireEventUtils.pushFriendClickedAcceptButton(showAccount,showNameList.get(0));
                }else{
                    FireEventUtils.pushFriendClickedAcceptButton(showAccount,showAccount);
                }
                //end:add by wal@xdja.com for 好友事件通知
                BroadcastManager.refreshFriendList();
                NotificationUtil.cancelNotification();
                notifyDataSetChanged();
            }else {
                XToast.show(context, context.getString(R.string.accept_friend_error));
                LogUtil.getUtils().e("Actoma contact FriendRequestHistory,accept friend error,updateFriend is false ");
                return;
            }
        }else {
            XToast.show(context,context.getString(R.string.accept_friend_error));
            LogUtil.getUtils().e("Actoma contact FriendRequestHistory,accept friend error,updateHistory is false");
        }
    }

    @Override
    public void onErr() {
        acceptHistory = null;
        if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
            showDismissDialogCallback.dismissDialog();
        }
    }


    private class JudgeButtonListener implements View.OnClickListener {

        private int position;

        private JudgeButtonListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == holder.judgeButton.getId()) {
                if (!ContactModuleService.checkNetWork()) return;
                acceptHistory = dataSource.get(position);
                final String account = acceptHistory.getShowAccount();
                //向后台发送接受好友请求
                if (ContactModuleService.checkNetWork()){
                    TaskAcceptFriendReq acceptFriendReqTask = new TaskAcceptFriendReq(context, new TaskAcceptFriendReq.IAcceptFriendCallback() {
                        @Override
                        public void onPreExecute() {
                            if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
                                showDismissDialogCallback.showDialog();
                            }
                        }

                        @Override
                        public void onPostExecute() {
                            if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
                                showDismissDialogCallback.dismissDialog();
                            }
                        }
                    }, FriendRequestHistoryAdapter.this);
                    acceptFriendReqTask.execute(account);
                    /*end:add by wal@xdja.com for ckms */
                }else{
                    showDismissDialogCallback.dismissDialog();
                }
            }
        }
    }

    class ViewHolder {
        //用户头像
        CircleImageView userPhoto;
        //安通Account
        TextView atAccount;
        //申请的验证信息
        TextView requestInfo;
        //接受请求
        Button judgeButton;

        TextView judgeText;
    }

    public List<FriendRequestHistory> getDataSource() {
        return dataSource;
    }


    public void setDataSource(List<FriendRequestHistory> dataSource) {
        if(ObjectUtil.collectionIsEmpty(dataSource))return;
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    private IShowDismissDialog showDismissDialogCallback;


    public void setShowDismissDialogCallback(IShowDismissDialog showDismissDialogCallback) {
        this.showDismissDialogCallback = showDismissDialogCallback;
    }

    public interface IShowDismissDialog {

        void showDialog();

        void dismissDialog();

    }
}
