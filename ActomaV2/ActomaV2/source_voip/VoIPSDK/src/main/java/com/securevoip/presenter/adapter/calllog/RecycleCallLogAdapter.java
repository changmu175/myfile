package com.securevoip.presenter.adapter.calllog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csipsimple.api.SipManager;
import com.securevoip.presenter.activity.CallDetailActivityPresenter;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.utils.ChatSourceHelper;
import com.securevoip.utils.RecyclerViewCursorAdapter;
import com.securevoip.voip.ClearReceiver;
import com.securevoip.voip.MissedCallOttoPost;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.AccountServer;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactBusinessProxy;
import com.xdja.voipsdk.R;

import webrelay.VOIPManager;

/**
 * Created by zjc on 2015/9/16.
 */
public class RecycleCallLogAdapter extends RecyclerViewCursorAdapter<RecycleCallLogAdapter.CallLogViewHolder> {

     private static final String THIS_FILE = "RecycleCallLogAdapter";
     //private SparseArray<String> showNameSparseArray;

     private Context context;
     private OnCallLogAction callLogListener = null;

     /**
      * Currently it accept {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
      */
     public RecycleCallLogAdapter(Context context, Cursor c, int flags) {
          super(context, c, flags);
          this.context = context;
         // showNameSparseArray = new SparseArray<>();
     }

     @Override
     public CallLogViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
          View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_log_list_item_new, viewGroup, false);
          CallLogViewHolder holder = new CallLogViewHolder(view);
          return holder;
     }

     @Override
     public int getItemViewType(int position) {
          return super.getItemViewType(position);
     }

     @Override
     public void onBindViewHolder(CallLogViewHolder holder, Cursor cursor) {
          //警告：如果没有特殊必要，请不要在此方法内做额外的查询数据库操作，尤其是CustContacts中和联系人有关的
          //如果非要查询数据库，请做缓存
          final String actomaAccount = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
          long dateTime = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
          String date = ChatSourceHelper.formatTimeWarnning(dateTime);
          int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
          String nickName = cursor.getString(cursor.getColumnIndex(SipManager.CALLLOG_NICKNAME));
          String imageUrl = cursor.getString(cursor.getColumnIndex(SipManager.CALLLOG_AVATAR_URL));

          /** 20160920-mengbo-start: 使用联系人缓存显示姓名、头像 **/
          ContactBusinessProxy contactBusinessProxy = new ContactBusinessProxy(context);
          if(contactBusinessProxy != null){
               ContactInfo contactInfo = contactBusinessProxy.getContactInfo(actomaAccount);
               if(contactInfo != null){
                    String showName = contactInfo.getName();
                    if(!TextUtils.isEmpty(showName)){
                         nickName = showName;
                    }

                    String showImage = contactInfo.getThumbnailUrl();
                    if(!TextUtils.isEmpty(showImage)){
                         imageUrl = showImage;
                    }
               }
          }
          /** 20160920-mengbo-end **/

          final String showNickName = nickName;

          //警告，现在的账号都是数字账号，如果之后变成字符串账号的话，需要修改此处实现
          //final int actomaAccountInt = Integer.parseInt(actomaAccount);

         /* if (!TextUtils.isEmpty(showNameSparseArray.get(actomaAccountInt))) {
               holder.name.setText(showNameSparseArray.get(actomaAccountInt));
               LogUtil.d(THIS_FILE, showNameSparseArray.get(actomaAccountInt));
          } else {*/
          if (!TextUtils.isEmpty(actomaAccount)){

               //mengbo@xdja.com 2016-09-01 start modify. 优化通话历史记录列表

               holder.name.setText(showNickName);
               /**origin code:
                  String showName = CustContacts.getFriendName(actomaAccount);
                  holder.name.setText(showName);
                */
               //mengbo@xdja.com 2016-09-01 end

              // showNameSparseArray.put(actomaAccountInt, showName);
          }
          holder.callDateTime.setText(date);

          switch (type) {
               case CallLog.Calls.INCOMING_TYPE:
                    holder.callTypeIcon.setBackgroundResource(R.drawable.ic_call_incoming_holo_dark);
                    break;
               case CallLog.Calls.MISSED_TYPE:
                    holder.callTypeIcon.setBackgroundResource(R.drawable.ic_call_missed_holo_dark);
                    break;
               case CallLog.Calls.OUTGOING_TYPE:
                    holder.callTypeIcon.setBackgroundResource(R.drawable.ic_call_outgoing_holo_dark);
                    break;
          }

          int missedCallCount = cursor.getInt(cursor.getColumnIndex("count"));
          if (missedCallCount > 0) {
               String missedCallCountText = missedCallCount + "";
               if (missedCallCount > 99) {
                    holder.missedCallRedPoint.setText("...");
               } else {
                    holder.missedCallRedPoint.setText(missedCallCountText);
               }
               holder.missedCallRedPoint.setVisibility(View.VISIBLE);
          } else {
               holder.missedCallRedPoint.setVisibility(View.INVISIBLE);
          }


          /*HeadImgParamsBean bean = HeadImgParamsBean.getParams(imageUrl);
          ((CircleImageView) holder.quickContactPhoto).loadImage(bean.getHost(), true, bean.getFileId(), bean.getSize(), R.drawable.ic_contact);*/
          //TODO
          ((CircleImageView) holder.quickContactPhoto).loadImage(imageUrl, true, R.drawable.ic_contact);

          holder.primaryActionView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    // mengbo@xdja.com 2016-08-11 start 在CallDetailActivityPresenter内清除
                    //clearMissedCall(actomaAccount);
                    // mengbo@xdja.com 2016-08-11 end
                    Intent intent = new Intent(context, CallDetailActivityPresenter.class);
                    intent.putExtra(CallDetailActivityPresenter.CONTACT_ID_FLAG, actomaAccount);
                    intent.putExtra(CallDetailActivityPresenter.ACCOUNT_NAME, actomaAccount);
                    intent.putExtra(CallDetailActivityPresenter.NICK_NAME, showNickName);
                    context.startActivity(intent);
               }
          });
          holder.primaryActionView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                    if (callLogListener != null) {
                         callLogListener.onItemLongClick(actomaAccount, actomaAccount);
                    }
                    return true;
               }
          });

          holder.callLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    //zjc 20150909 拨打电话时，一并清除未接来电通知和红点并通知主框架更新
                    clearMissedCall(actomaAccount);
                    String user = AccountServer.getAccount().getAccount();
                    if (user == null || actomaAccount == null)
                         return;
                    /*start:modify by wal@xdja.com for ckms 2016/8/19 */
                    VOIPManager.getInstance().makeCall(actomaAccount, user);
                    /*end:modify by wal@xdja.com for ckms 2016/8/19 */
               }
          });
     }

     private void clearMissedCall(String actomaAccount) {
          sendNotificationClearBroadCast(context, actomaAccount);
          CallLogHelper.clearMissedCall(actomaAccount);
          BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
     }

     @Override
     protected void onContentChanged() {

     }

     private void sendNotificationClearBroadCast(Context context, String actomaAccount) {
          Intent intent = new Intent();
          intent.setAction(ClearReceiver.ACTION_CANCEL_NOTIFICATION);
          intent.putExtra(ClearReceiver.FLAG_CANCEL_NOTIFICATION, actomaAccount);
          context.sendBroadcast(intent);
     }

     public interface OnCallLogAction {
          void onItemLongClick(String number, String name);
     }

     public void setCallLogListener(OnCallLogAction listener) {
          callLogListener = listener;
     }

     public void clearShowNameCache() {
          //showNameSparseArray.clear();
          notifyDataSetChanged();
     }

     public class CallLogViewHolder extends RecyclerView.ViewHolder {

          /**
           * 整个Item的Layout
           */
          RelativeLayout primaryActionView;
          /**
           * 联系人头像
           */
          ImageView quickContactPhoto;
          /**
           * 红点，未接来电数量
           */
          TextView missedCallRedPoint;
          /**
           * 联系人名称
           */
          TextView name;
          /**
           * 通话类型
           */
          ImageView callTypeIcon;
          /**
           * 通话时间
           */
          TextView callDateTime;
          /**
           * 电话按钮
           */
          ImageView callIcon;
          /**
           * 电话按钮所在的布局
           */
          RelativeLayout callLayout;

          public CallLogViewHolder(View itemView) {
               super(itemView);
               primaryActionView = (RelativeLayout) itemView.findViewById(R.id.primary_action_view);
               quickContactPhoto = (ImageView) itemView.findViewById(R.id.quick_contact_photo);
               missedCallRedPoint = (TextView) itemView.findViewById(R.id.missed_call_count);
               name = (TextView) itemView.findViewById(R.id.name);
               callTypeIcon = (ImageView) itemView.findViewById(R.id.call_type_icons);
               callDateTime = (TextView) itemView.findViewById(R.id.call_count_and_date);
               callIcon = (ImageView) itemView.findViewById(R.id.call_icon);
               callLayout = (RelativeLayout) itemView.findViewById(R.id.call_layout);
          }

     }
}

