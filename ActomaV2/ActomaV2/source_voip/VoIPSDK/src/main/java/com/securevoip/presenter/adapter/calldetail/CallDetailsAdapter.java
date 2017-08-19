package com.securevoip.presenter.adapter.calldetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csipsimple.api.SipManager;
import com.securevoip.presenter.adapter.calllog.CallLogDetail;
import com.securevoip.presenter.adapter.calllog.CallTypeIconsView;
import com.securevoip.utils.ChatSourceHelper;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import webrelay.bean.StatusCode;


/**
 * Created by gbc on 2015/7/7.
 */
public class CallDetailsAdapter extends BaseAdapter {
    @SuppressLint("MismatchedCollectionQueryUpdate")
    private  final String TAG = CallDetailsAdapter.class.getCanonicalName();
    private List<CallLogDetail> mCallLogs;
    private List<View> mViewsList;
    private Set<CallLogDetail> mHeaderList;
    private Set<String> mHeaderDate;
    private Map<String, CallLogDetail> mTailMap;
    private Context mContext;
    private Cursor mCursor;

    private Comparator<CallLogDetail> mDetailComparator = new Comparator<CallLogDetail>() {
        @Override
        public int compare(CallLogDetail lhs, CallLogDetail rhs) {
            Date date1 = new Date(lhs.getDate());

            Date date2 = new Date(rhs.getDate());

            if (date1.getDate() == (date2.getDate())) {

            }
            return 0;
        }
    };

    public CallDetailsAdapter(Context context) {
        mContext = context;
        mCallLogs = new LinkedList<>();
        mHeaderList = new HashSet<>();
        mHeaderDate = new HashSet<>();
        mViewsList = new ArrayList<>();
        mTailMap = new HashMap<>();
        //sortCallLogList();

    }

    public void swapCursor(Cursor cursor) {
        if (null == cursor) {
            return;
        }
        if (cursor.equals(mCursor)) {
            return;
        }
        if (mCursor != null) {
            mCursor.unregisterDataSetObserver(mDataSetObserver);
            mCursor.close();
        }
        mCallLogs.clear();
        mHeaderList.clear();
        mHeaderDate.clear();
        mViewsList.clear();
        mTailMap.clear();
        mCursor = cursor;
        fetchHeader();

        /** 20160914-mengbo-start: 优化列表加载速度 **/
        //initAllViews();
        /** 20160914-mengbo-end **/

        if (cursor != null) {
            cursor.registerDataSetObserver(mDataSetObserver);
            notifyDataSetChanged();
        }
    }


    protected DataSetObserver mDataSetObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };

    private void sortCallLogList() {
        Collections.sort(mCallLogs, mDetailComparator);
    }

    /*
    * parse mCallLogs list, and find header in every group
    * */
    private void fetchHeader() {
        if (mCursor == null || mCursor.getCount() == 0){
            return;
        }
        LogUtil.getUtils(TAG).d("Cursor count:" + mCursor.getCount());
        mCursor.moveToFirst();

        do {
            CallLogDetail detail = new CallLogDetail();
            String name = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER));
            long date = mCursor.getLong(mCursor.getColumnIndex(CallLog.Calls.DATE));
            long duration = mCursor.getLong(mCursor.getColumnIndex(CallLog.Calls.DURATION));
            int accountId = mCursor.getInt(mCursor.getColumnIndex(SipManager.CALLLOG_PROFILE_ID_FIELD));
            int type = mCursor.getInt(mCursor.getColumnIndex(CallLog.Calls.TYPE));
            //zjc 20150906 增加一个numberType，只标识呼入和呼出
            int numberType = mCursor.getInt(mCursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE));
            int stauts_code = mCursor.getInt(mCursor.getColumnIndex(SipManager.CALLLOG_STATUS_CODE_FIELD));
            String stauts_text = mCursor.getString(mCursor.getColumnIndex(SipManager.CALLLOG_STATUS_TEXT_FIELD));

            detail.setName(name);
            detail.setNumber(number);
            detail.setAccountId(accountId);
            detail.setDate(date);
            detail.setStatus_code(stauts_code);
            detail.setStatus_text(stauts_text);
            detail.setDuration(duration);
            detail.setType(type);
            detail.setNumberType(numberType);

            String dateDsp = ChatSourceHelper.formatTimeDayFragment(detail.getDate());
            if (!mHeaderList.contains(detail) && !mHeaderDate.contains(dateDsp)) {
                mHeaderList.add(detail);
                mHeaderDate.add(dateDsp);
            }
            mTailMap.put(dateDsp, detail);
            mCallLogs.add(detail);
        }while (mCursor.moveToNext());
    }

    private void initAllViews() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        for (CallLogDetail detail : mCallLogs) {
            View detailView = inflater.inflate(R.layout.call_detail_item_new, null);
            TextView callTime = (TextView) detailView.findViewById(R.id.call_begin_time);
            callTime.setText(ChatSourceHelper.formatTimeHM(detail.getDate()));

            //来电蓝色图标，去电绿色图标，红色未接来电
            CallTypeIconsView icon = (CallTypeIconsView) detailView.findViewById(R.id.call_type_icons);
            icon.add(detail.getType());
            icon.setVisibility(View.VISIBLE);

            TextView callType = (TextView) detailView.findViewById(R.id.type_desp);
            if (detail.getType() == CallLog.Calls.INCOMING_TYPE) {
                callType.setText(R.string.type_incoming);
            } else if (detail.getType() == CallLog.Calls.OUTGOING_TYPE) {
                callType.setText(R.string.type_outgoing);
            } else if (detail.getType() == CallLog.Calls.MISSED_TYPE) {
                callType.setText(R.string.type_missed);
            } else {
                callType.setText(R.string.unknown);
            }

            TextView duration = (TextView) detailView.findViewById(R.id.duration_desp);
            if (detail.getDuration() != 0) {
                duration.setText(ChatSourceHelper.makeDurationString(mContext.getResources(), detail.getDuration()));
            } else {
                //通话时间为0，根据不同情况设置文字

                //超时挂断，主叫显示未接通，被叫显示未接听
                // 主叫方生成通话记录：type是CallLog.Calls.OUTGOING_TYPE(值是2)，code是487，status_text是电话中止；主叫界面应该显示未接通
                // 被叫方生成的通话记录：type是CallLog.MISSED_TYPE(值是3)，code是487，status_text是电话中止；被叫界面应该显示红色未接听

                int type = detail.getType();
                int code = detail.getStatus_code();

                switch(detail.getType()) {
                    case CallLog.Calls.OUTGOING_TYPE://主叫
                        switch(code) {
                            case StatusCode.CALLEE_BUSY: // 对方正在通话中
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_BUSY_OUT));
                                break;
                            case StatusCode.CALLEE_OFFLINE:// 对方不在线
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_OFFLINE));
                                break;
                            case StatusCode.CALLEE_REJECT: // 对方拒绝接听
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_REJECT_OUT));
                                break;
                            case StatusCode.CALLEE_TIMEOUT://被叫长时间不接听
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_TIMEOUT_OUT));
                                break;
                            case StatusCode.CALLER_TIMEOUT:// 主叫呼叫超时
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_TIMEOUT_OUT));
                                break;
                            case StatusCode.CALLER_CANCEL://主叫取消呼叫
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_CANCEL_OUT));
                                break;
                            case StatusCode.CALLER_ERROR_INITIAL:// 主叫初始化呼叫失败
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_ERROR_INITIAL));
                                break;
                            case StatusCode.CALLER_ERROR_CALLING:// 主叫发送呼叫请求失败
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_ERROR_CALLING));
                                break;

                        }
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                    case CallLog.Calls.INCOMING_TYPE:// 被叫
                        switch(code) {
                            case StatusCode.CALLEE_BUSY:
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_BUSY_IN));
                                break;
                            case StatusCode.CALLEE_OFFLINE:
                                break;
                            case StatusCode.CALLEE_REJECT: //被叫拒绝接听
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_REJECT_IN));
                                break;
                            case StatusCode.CALLEE_TIMEOUT: //被叫长时间不接听
                                duration.setText(ActomaController.getApp().getString(R.string.CALLEE_TIMEOUT_IN));
                                break;
                            case StatusCode.CALLER_TIMEOUT:// 主叫呼叫超时
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_TIMEOUT_IN));// 不应该走到这里
                                break;
                            case StatusCode.CALLER_CANCEL://主叫取消呼叫
                                duration.setText(ActomaController.getApp().getString(R.string.CALLER_CANCEL_IN));
                                break;
                        }
                        break;
                }
            }

            if (mHeaderList.contains(detail)) {
                /**2017-03-13 -wangzhen modify.Fix the Bug 9759 to optimize the layout**/
                //View header = detailView.findViewById(R.id.date_layout);
                //header.setVisibility(View.VISIBLE);

                TextView headerText = (TextView) detailView.findViewById(R.id.header_text);
                headerText.setVisibility(View.VISIBLE);
                headerText.setText(ChatSourceHelper.formatTimeDayUnit(detail.getDate()));
                LogUtil.getUtils().d(TAG+ detail.getDate() + "\n" + ChatSourceHelper.formatTimeDayUnit(detail.getDate()));
            }
            ++ count;
            if (mTailMap.containsValue(detail) && mCallLogs.size() != count) {
                View tail = detailView.findViewById(R.id.tail);
                tail.setVisibility(View.VISIBLE);
            }

            mViewsList.add(detailView);
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    public int getCount() {

        if(mCallLogs != null){
            return mCallLogs.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
//        return mViewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** 20160914-mengbo-start: 优化列表加载速度 **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /**2017-03-13 -wangzhen modify.Fix the Bug 9759 to optimize the layout**/
            //convertView = inflater.inflate(R.layout.call_detail_item, null);
            convertView = inflater.inflate(R.layout.call_detail_item_new, null);
            holder.callTime = (TextView) convertView.findViewById(R.id.call_begin_time);
            holder.icon = (CallTypeIconsView) convertView.findViewById(R.id.call_type_icons);
            holder.callType = (TextView) convertView.findViewById(R.id.type_desp);
            holder.duration = (TextView) convertView.findViewById(R.id.duration_desp);

            /**2017-03-13 -wangzhen modify.Fix the Bug 9759 to optimize the layout**/
            //holder.header = convertView.findViewById(R.id.date_layout);
            holder.headerText = (TextView) convertView.findViewById(R.id.header_text);
            holder.tail = convertView.findViewById(R.id.tail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mCallLogs != null && mCallLogs.size() > 0) {

            final CallLogDetail detail = mCallLogs.get(position);

            holder.callTime.setText(ChatSourceHelper.formatTimeHM(detail.getDate()));
            //来电蓝色图标，去电绿色图标，红色未接来电
            holder.icon.clear();
            holder.icon.add(detail.getType());
            holder.icon.setVisibility(View.VISIBLE);

            if (detail.getType() == CallLog.Calls.INCOMING_TYPE) {
                holder.callType.setText(R.string.type_incoming);
            } else if (detail.getType() == CallLog.Calls.OUTGOING_TYPE) {
                holder.callType.setText(R.string.type_outgoing);
            } else if (detail.getType() == CallLog.Calls.MISSED_TYPE) {
                holder.callType.setText(R.string.type_missed);
            } else {
                holder.callType.setText(R.string.unknown);
            }

            if (detail.getDuration() != 0) {
                holder.duration.setText(ChatSourceHelper.makeDurationString(mContext.getResources(), detail.getDuration()));
            } else {
                //通话时间为0，根据不同情况设置文字

                //超时挂断，主叫显示未接通，被叫显示未接听
                // 主叫方生成通话记录：type是CallLog.Calls.OUTGOING_TYPE(值是2)，code是487，status_text是电话中止；主叫界面应该显示未接通
                // 被叫方生成的通话记录：type是CallLog.MISSED_TYPE(值是3)，code是487，status_text是电话中止；被叫界面应该显示红色未接听

                int type = detail.getType();
                int code = detail.getStatus_code();

                switch(type) {
                    case CallLog.Calls.OUTGOING_TYPE://主叫
                        switch(code) {
                            case StatusCode.CALLEE_BUSY: // 对方正在通话中
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_BUSY_OUT));
                                break;
                            case StatusCode.CALLEE_OFFLINE:// 对方不在线
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_OFFLINE));
                                break;
                            case StatusCode.CALLEE_REJECT: // 对方拒绝接听
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_REJECT_OUT));
                                break;
                            case StatusCode.CALLEE_TIMEOUT://被叫长时间不接听
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_TIMEOUT_OUT));
                                break;
                            case StatusCode.CALLER_TIMEOUT:// 主叫呼叫超时
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_TIMEOUT_OUT));
                                break;
                            case StatusCode.CALLER_CANCEL://主叫取消呼叫
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_CANCEL_OUT));
                                break;
                            case StatusCode.CALLER_ERROR_INITIAL:// 主叫初始化呼叫失败
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_ERROR_INITIAL));
                                break;
                            case StatusCode.CALLER_ERROR_CALLING:// 主叫发送呼叫请求失败
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_ERROR_CALLING));
                                break;
                            default :
                                /** 20161204-mengbo-start: 主叫默认Code为：已取消 **/
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_DEFAULT_CODE));
                                //holder.duration.setText(ActomaController.getApp().getString(R.string.CALL_UNKNOW));
                                /** 20161204-mengbo-end **/
                        }
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                    case CallLog.Calls.INCOMING_TYPE:// 被叫
                        switch(code) {
                            case StatusCode.CALLEE_BUSY:
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_TIMEOUT_IN_2));
                                break;
                            case StatusCode.CALLEE_OFFLINE:
                                break;
                            case StatusCode.CALLEE_REJECT: //被叫拒绝接听
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_REJECT_IN));
                                break;
                            case StatusCode.CALLEE_TIMEOUT: //被叫长时间不接听
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_TIMEOUT_IN));
                                break;
                            case StatusCode.CALLER_TIMEOUT:// 主叫呼叫超时
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_TIMEOUT_IN));// 不应该走到这里
                                break;
                            case StatusCode.CALLER_CANCEL://主叫取消呼叫
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLER_CANCEL_IN));
                                break;
                            default :
                                /** 20161204-mengbo-start: 被叫默认Code为：未接听 **/
                                holder.duration.setText(ActomaController.getApp().getString(R.string.CALLEE_DEFAULT_CODE));
                                //holder.duration.setText(ActomaController.getApp().getString(R.string.CALL_UNKNOW));
                                /** 20161204-mengbo-end **/
                        }
                        break;
                    default :
                        holder.duration.setText(ActomaController.getApp().getString(R.string.CALL_UNKNOW));
                }
            }
            if (mHeaderList.contains(detail)) {
                /**2017-03-13 -wangzhen modify.Fix the Bug 9759 to optimize the layout**/
                //holder.header.setVisibility(View.VISIBLE);
                holder.headerText.setVisibility(View.VISIBLE);
                holder.headerText.setText(ChatSourceHelper.formatTimeDayUnit(detail.getDate()));
                LogUtil.getUtils().d(TAG + detail.getDate() + "\n" + ChatSourceHelper.formatTimeDayUnit(detail.getDate()));
            }else{
                holder.headerText.setVisibility(View.GONE);
            }

            if (mTailMap.containsValue(detail) && mCallLogs.size() != position+1) {
                holder.tail.setVisibility(View.VISIBLE);
            }else{
                holder.tail.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView callTime;
        private CallTypeIconsView icon;
        private TextView callType;
        private TextView duration;
        /**2017-03-13 -wangzhen modify.Fix the Bug 9759 to optimize the layout**/
        //private View header;
        private TextView headerText;
        private View tail;
    }

    /** 20160914-mengbo-end **/

}
