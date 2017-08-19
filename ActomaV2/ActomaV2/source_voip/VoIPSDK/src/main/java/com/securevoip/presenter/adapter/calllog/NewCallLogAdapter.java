package com.securevoip.presenter.adapter.calllog;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csipsimple.api.SipManager;
import com.csipsimple.models.CallerInfo;
import com.xdja.voipsdk.R;

/**
 * Created by zjc on 2015/9/15.
 */
public class NewCallLogAdapter extends CursorAdapter {

    private final Context mContext;
    private Cursor c;

    public NewCallLogAdapter(Context context, Cursor cursor) {
        this(context, cursor, true);
        c = cursor;
    }

    public NewCallLogAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mContext = context;
        c = cursor;
    }

    public NewCallLogAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        c = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.call_log_list_item, parent, false);
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        if (cursor == null) {
            return;
        }

        int numberColIndex = c.getColumnIndex(CallLog.Calls.NUMBER);
        int dateColIndex = c.getColumnIndex(CallLog.Calls.DATE);
        int durationColIndex = c.getColumnIndex(CallLog.Calls.DURATION);
        int accIdIndex = c.getColumnIndex(SipManager.CALLLOG_PROFILE_ID_FIELD);
        int nameIndex = c.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int nickNameIndex = c.getColumnIndex(SipManager.CALLLOG_NICKNAME);
        int avatarUriIndex = c.getColumnIndex(SipManager.CALLLOG_AVATAR_URL);

        String number = c.getString(numberColIndex);
        final long date = c.getLong(dateColIndex);
        final long duration = c.getLong(durationColIndex);
        final Long accId = c.getLong(accIdIndex);
        // final int callType = c.getInt(typeColIndex);
        final String name = c.getString(nameIndex);
        final String nickName = c.getString(nickNameIndex);
        final String avatarUri = c.getString(avatarUriIndex);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.call_log_list_item, null);
            holder.quickContactView = (ImageView) convertView.findViewById(R.id.quick_contact_photo);
            holder.primaryActionView = convertView.findViewById(R.id.primary_action_view);
            holder.dividerView = convertView.findViewById(R.id.divider);
            holder.callLayout = (RelativeLayout) convertView.findViewById(R.id.call_layout);
            holder.callTypeIcon = (CallTypeIconsView) convertView.findViewById(R.id.call_type_icons);
            holder.call = (ImageView) convertView.findViewById(R.id.call_icon);
            holder.date = (TextView) convertView.findViewById(R.id.call_count_and_date);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.missCallAccount = (TextView) convertView.findViewById(R.id.missed_call_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CallerInfo info = CallerInfo.getCallerInfoFromSipUri(number);

        CallRowInfos cri = new CallRowInfos();
        if (nickName != null && nickName.length() > 0) {
            cri.name = nickName;
        } else {
            cri.name = name;
        }

    }

    public class CallRowInfos {
        long[] callIds;
        int position;
        String number;
        Long accId;
        String name;
    }

    class ViewHolder {
        /**
         * 头像
         */
        ImageView quickContactView;
        /**
         * 整个布局
         */
        View primaryActionView;
        /**
         * 分割线
         */
        View dividerView;
        /**
         * 通话所在布局
         */
        RelativeLayout callLayout;
        /**
         * 通话按钮
         */
        ImageView call;
        /**
         * 类型按钮
         */
        CallTypeIconsView callTypeIcon;
        /**
         * 时间
         */
        TextView date;
        /**
         * 显示名称
         */
        TextView name;
        /**
         * 未接来电数量
         */
        TextView missCallAccount;
    }


}
