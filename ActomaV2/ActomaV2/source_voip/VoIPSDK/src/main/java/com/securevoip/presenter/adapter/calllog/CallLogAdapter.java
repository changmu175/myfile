/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.securevoip.presenter.adapter.calllog;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipUri;
import com.csipsimple.models.CallerInfo;
import com.securevoip.contacts.CustContacts;
import com.xdja.voipsdk.R;


/**
 * Adapter class to fill in data for the Call Log.
 */
public class CallLogAdapter extends GroupingListAdapter
        implements CallLogGroupBuilder.GroupCreator {
    private static String TAG = CallLogAdapter.class.getCanonicalName();
    /** Interface used to initiate a refresh of the content. */
    public interface CallFetcher {
        void fetchCalls();
        void dataSetChange(int count);
    }

    private final Context mContext;
    private final CallFetcher mCallFetcher;


    protected static final String THIS_FILE = "CallLogAdapter";

    /** Instance of helper class for managing views. */
    private final CallLogListItemHelper mCallLogViewsHelper;

    /** Helper to set up contact photos. */
    // private final ContactPhotoManager mContactPhotoManager;
    /** Helper to group call log entries. */
    private final CallLogGroupBuilder mCallLogGroupBuilder;

    private OnCallLogAction callLogActionListener = null;
    
//    private TxlDataControl txlDataControl;

    public void setOnCallLogActionListener(OnCallLogAction l) {
        callLogActionListener = l;
    }

    public interface OnCallLogAction {
        void viewDetails(int position, long[] callIds, String number, Long accId, String names);
        void placeCall(String number, Long accId);
        void onItemLongClickListener(String number, long[] callIds);
        void onPhotoClickListener(String number);
    }


    public class CallRowInfos {
        long[] callIds;
        int position;
        String number;
        Long accId;
        String name;
    }

    /** Listener for the primary action in the list, opens the call details. */
    private final View.OnClickListener mPrimaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CallRowInfos cri = (CallRowInfos) view.getTag();
            if (callLogActionListener != null) {
                callLogActionListener.viewDetails(cri.position, cri.callIds,cri.number, cri.accId, cri.name);
            }
        }
    };
    private final View.OnLongClickListener mItemLongClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            CallRowInfos cri = (CallRowInfos) v.getTag();
            if (callLogActionListener != null && cri!=null) {
                callLogActionListener.onItemLongClickListener(cri.number, cri.callIds);
            }
            return false;
        }
    };

    /** Listener for the secondary action in the list, either call or play. */
    private final View.OnClickListener mSecondaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CallRowInfos cri = (CallRowInfos) view.getTag();
            if (callLogActionListener != null && cri!=null) {
                callLogActionListener.placeCall(cri.number, cri.accId);
            }
        }
    };

    private final View.OnClickListener photoActionListener  = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            CallRowInfos cri = (CallRowInfos) v.getTag();
            if (callLogActionListener != null && cri!=null) {
                callLogActionListener.onPhotoClickListener(cri.number);
            }
        }
    };

    public CallLogAdapter(Context context, CallFetcher callFetcher) {
        super(context);

        mContext = context;
        mCallFetcher = callFetcher;

        Resources resources = mContext.getResources();

        // mContactPhotoManager = ContactPhotoManager.getInstance(mContext);
        PhoneCallDetailsHelper phoneCallDetailsHelper = new PhoneCallDetailsHelper(resources);
        mCallLogViewsHelper = new CallLogListItemHelper(phoneCallDetailsHelper, context);
        mCallLogGroupBuilder = new CallLogGroupBuilder(this);
//        txlDataControl=new TxlDataControl(context);

    }

    /**
     * Requery on background thread when {@link Cursor} changes.
     */
    @Override
    protected void onContentChanged() {
        mCallFetcher.fetchCalls();
    }

    @Override
    protected void onDataSetChange(int count) {
        mCallFetcher.dataSetChange(count);
    }

    @Override
    protected void addGroups(Cursor cursor) {
        mCallLogGroupBuilder.addGroups(cursor);
    }


    @Override
    public View newChildView(Context context, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_log_list_item, parent, false);
        findAndCacheViews(view);
        return view;
    }

    @Override
    protected void bindChildView(int position, View view, Context context, Cursor cursor, long[] ids) {
        bindView(position, view, cursor, ids.length, ids);
    }

    private void findAndCacheViews(View view) {
        // Get the views to bind to.
        CallLogListItemViews views = CallLogListItemViews.fromView(view);
        //views.primaryActionView.setLongClickable(true);
        //views.quickContactView.setClickable(true);
        //views.quickContactView.setOnClickListener(photoActionListener);
        views.primaryActionView.setOnLongClickListener(mItemLongClickListener);
        views.primaryActionView.setOnClickListener(mPrimaryActionListener);
        views.callLayout.setOnClickListener(mSecondaryActionListener);
        view.setTag(views);
    }


    /**
     * Binds the views in the entry to the data in the call log.
     * 
     * @param view the view corresponding to this entry
     * @param c the cursor pointing to the entry in the call log
     * @param count the number of entries in the current item, greater than 1 if
     *            it is a group
     */
    private void bindView(int position, View view, Cursor c, int count, long[] ids) {
        final CallLogListItemViews views = (CallLogListItemViews) view.getTag();

        // Default case: an item in the call log.
        views.primaryActionView.setVisibility(View.GONE);
//        views.bottomDivider.setVisibility(isLastOfSection(c) ? View.GONE : View.VISIBLE);

        int numberColIndex = c.getColumnIndex(CallLog.Calls.NUMBER);
        int dateColIndex = c.getColumnIndex(CallLog.Calls.DATE);
        int durationColIndex = c.getColumnIndex(CallLog.Calls.DURATION);
        int accIdIndex = c.getColumnIndex(SipManager.CALLLOG_PROFILE_ID_FIELD);
        // int typeColIndex = c.getColumnIndex(CallLog.Calls.TYPE);
        //Begin:ZhengJunchen 20150807 增加name，以提供删除通话记录使用
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

        // final CallerInfo cachedContactInfo = getContactInfoFromCallLog(c);

        CallRowInfos cri = new CallRowInfos();
        //cri.callIds = getCallIds(c, count);
        cri.callIds = ids;
        cri.position = position;
        cri.number = number;
        //cri.number = "sip:999999@192.168.200.120";
        cri.accId = accId;
        if (nickName != null && nickName.length() > 0) {
            cri.name = nickName;
        } else {
            cri.name = name;
        }
        views.primaryActionView.setTag(cri);
        views.callLayout.setTag(cri);

        //设置未接来电数量
        Integer nMCount = (Integer) view.getTag(R.id.new_missedcall_count);
        if (nMCount != null && nMCount > 0) {
            views.missCall.setVisibility(View.VISIBLE);
            //zjc 20150821 未接来电大于99时，显示99+
            //zjc 20150829 99+的字体从9变到8，否则显示不下
            if (nMCount > 99) {
                views.missCall.setTextSize(8);
                views.missCall.setText(R.string.call_count);
            } else {
                views.missCall.setTextSize(11);
                views.missCall.setText(String.valueOf(nMCount.intValue()));
            }

        } else {
            views.missCall.setVisibility(View.INVISIBLE);
        }

        String cachedNumber = (String) view.getTag(R.id.number);
        if (cachedNumber != null && cachedNumber.equals(number)) {
            // No need to go set details about the contact again this has
            // already been done
            return;
        }
        CallerInfo info = CallerInfo
                .getCallerInfoFromSipUri(number);
        final Uri lookupUri = info.contactContentUri;
        final int ntype = info.numberType;
        final String label = info.phoneLabel;
        CharSequence formattedNumber = SipUri.getCanonicalSipContact(number, false);
        final int[] callTypes = getCallTypes(position);
        PhoneCallDetails details;
        /**Begin：ZhengJunchen 20150819 通话记录显示昵称或备注**/
        //这个name其实是从整个number串通过正则分离出的
        if (TextUtils.isEmpty(name)) {
            details = new PhoneCallDetails(number, formattedNumber,
                    callTypes, date, duration);
        }
        //nickname是数据库存储的，在通话记录生成时记录
        else if (!TextUtils.isEmpty(nickName)) {
            details = new PhoneCallDetails(number, formattedNumber,
                    callTypes, date, duration, nickName, ntype, label, lookupUri, null);
        }
        //nickname，显示name
        else {
            // We do not pass a photo id since we do not need the high-res
            // picture.
            details = new PhoneCallDetails(number, formattedNumber,
                    callTypes, date, duration, name, ntype, label, lookupUri, null);
        }
        /**End:ZhengJunchen**/
        views.primaryActionView.setVisibility(View.VISIBLE);
        mCallLogViewsHelper.setPhoneCallDetails(views, details, mContext);
        setPhoto(views, info, avatarUri);
    }

    /** Returns true if this is the last item of a section. */
    private boolean isLastOfSection(Cursor c) {
//        if (c.isLast()) {
//            return true;
//        }
//        return false;
        /**2017-2-28 -wangzhen modify.Change the if way Simplifly**/
        return c.isLast();
    }


    private int[] getCallTypes(int position) {
        Integer cursorPos = (Integer) getItem(position);
        int[] array = new int[1];
        if (null != getCursor()) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(cursorPos);
            array[0] = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        } else {
            array[0] = -1;
        }
        return array;
    }

    /**
     * Retrieve call ids list of the item at a given position
     * 
     * @param position the position to look at
     * @return the list of call ids
     */
    public long[] getCallIdsAtPosition(int position) {
        return getCallIds(position);
    }

    /**
     * Returns the call ids for the given number of items in the cursor.
     * <p>
     * It uses the next {@code count} rows in the cursor to extract the types.
     * <p>
     * It position in the cursor is unchanged by this function.
     */
    private long[] getCallIds(int position) {
        return getSubIds(position);
    }
    
    /**
     * Retrieve the remote sip uri for a call log at the given position
     * @param position  the position to look at
     * @return the sip uri
     */
    public String getCallRemoteAtPostion(int position) {
        Integer pos = (Integer) getItem(position);
        Cursor cursor = getCursor();
        if(cursor != null) {
            cursor.moveToPosition(pos);
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            return SipUri.getCanonicalSipContact(number, false);
        }
        return "";
    }

    private void setPhoto(CallLogListItemViews views, CallerInfo ci, String avatarUri) {
        String uri = CustContacts.getFriendThumbNailPhoto(ci.name);
        if (null != avatarUri && avatarUri.length() > 0) {
            uri = avatarUri;
        }

        if (!TextUtils.isEmpty(uri)) {
            //HeadImgParamsBean imageBean = HeadImgParamsBean.getParams(uri);
            /*views.quickContactView.loadImage(imageBean.getHost(), true, imageBean.getFileId(), imageBean.getSize(), R.drawable.ic_contact);*/
            views.quickContactView.loadImage(uri, true, R.drawable.ic_contact);
        } else {
            views.quickContactView.setImageResource(R.drawable.ic_contact);
        }
    }

    @Override
    public void addGroup(int cursorPosition, int size, boolean expanded) {
        super.addGroup(cursorPosition, size, expanded);
    }

    @Override
    public void addGroup(int cursorPosition, int nMc, long[] callIds) {
        super.addGroup(cursorPosition, nMc, callIds);
    }


}
