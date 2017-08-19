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
/**
 * This file contains relicensed code from som Apache copyright of 
 * Copyright (C) 2010, The Android Open Source Project
 */

package com.securevoip.presenter.adapter.calllog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.csipsimple.utils.ArrayUtils;
import com.xdja.voipsdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a list that groups adjacent items sharing the same value of
 * a "group-by" field.  The list has three types of elements: stand-alone, group header and group
 * child. Groups are collapsible and collapsed by default.
 */
public abstract class GroupingListAdapter extends BaseAdapter {
    private static String TAG = GroupingListAdapter.class.getCanonicalName();
    private static final int GROUP_METADATA_ARRAY_INITIAL_SIZE = 16;
    private static final int GROUP_METADATA_ARRAY_INCREMENT = 128;
    private static final long GROUP_OFFSET_MASK    = 0x00000000FFFFFFFFL;
    private static final long GROUP_SIZE_MASK     = 0x7FFFFFFF00000000L;
    private static final long EXPANDED_GROUP_MASK = 0x8000000000000000L;

    public static final int ITEM_TYPE_STANDALONE = 0;
    public static final int ITEM_TYPE_GROUP_HEADER = 1;
    public static final int ITEM_TYPE_IN_GROUP = 2;
    public static final int ITEM_TYPE_LIST = 3;

    /**
     * Information about a specific list item: is it a group, if so is it expanded.
     * Otherwise, is it a stand-alone item or a group member.
     */
    protected static class PositionMetadata {
        public int itemType;
        public boolean isExpanded;
        public int cursorPosition;
        public int childCount;
        public int groupPosition;
        public int listPosition = -1;
    }

    private final Context mContext;
    private Cursor mCursor;

    /**
     * Count of list items.
     */
    private int mCount;

    private int mRowIdColumnIndex;

    /**
     * Count of groups in the list.
     */
    private int mGroupCount;

    /**
     * Information about where these groups are located in the list, how large they are
     * and whether they are expanded.
     */
    private long[] mGroupMetadata;

    private final SparseIntArray mPositionCache = new SparseIntArray();
    private int mLastCachedListPosition;
    private int mLastCachedCursorPosition;
    private int mLastCachedGroup;


    private List<Integer> mCursorPosList = new ArrayList<>();
    private List<long[]> mCallLogIdsList = new ArrayList<>();
    private List<Integer> mNMissedCount = new ArrayList<>();
    /**
     * A reusable temporary instance of PositionMetadata
     */
    private final PositionMetadata mPositionMetadata = new PositionMetadata();

    protected ContentObserver mChangeObserver = new ContentObserver(new Handler()) {

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    };

    protected DataSetObserver mDataSetObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
            onDataSetChange(mCursor.getCount());
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };

    public GroupingListAdapter(Context context) {
    	super();
        mContext = context;
        resetCache();
    }

    /**
     * Finds all groups of adjacent items in the cursor and calls {@link #addGroup} for
     * each of them.
     */
    protected abstract void addGroups(Cursor cursor);

    protected abstract View newChildView(Context context, ViewGroup parent);
    protected abstract void bindChildView(int position, View view, Context context, Cursor cursor, long[] ids);

    /**
     * Cache should be reset whenever the cursor changes or groups are expanded or collapsed.
     */
    private void resetCache() {
        mCount = -1;
        mLastCachedListPosition = -1;
        mLastCachedCursorPosition = -1;
        mLastCachedGroup = -1;
        mPositionMetadata.listPosition = -1;
        mPositionCache.clear();
    }

    protected void onContentChanged() {
    	// By default nothing to do
        //Override by sub class
    }

    protected void  onDataSetChange(int count) {
        //Override by sub class

    }

    public void changeCursor(Cursor cursor) {
        Log.d(TAG, "GroupingListAdapter    changeCursor: " + cursor);
        if (null == cursor) {
            mCursorPosList.clear();
            mCallLogIdsList.clear();
            mNMissedCount.clear();
            return;
        }

        if (null != mCursor) {
            mCursor.unregisterContentObserver(mChangeObserver);
            mCursor.unregisterDataSetObserver(mDataSetObserver);
            //zjc 20150908 据说4.0以上的版本cursor会自动关闭，不用用户自己关闭
            if (Build.VERSION.SDK_INT < 14) {
                mCursor.close();
//                LogUtil.e(TAG, "cursor被手动关闭了");
            }
        }
        mCursor = cursor;
        //resetCache();
        findGroups();
        if (cursor != null) {
            cursor.registerContentObserver(mChangeObserver);
            cursor.registerDataSetObserver(mDataSetObserver);
            mRowIdColumnIndex = cursor.getColumnIndexOrThrow("_id");
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Scans over the entire cursor looking for duplicate phone numbers that need
     * to be collapsed.
     */
    private void findGroups() {
        mGroupCount = 0;
        mGroupMetadata = new long[GROUP_METADATA_ARRAY_INITIAL_SIZE];

        if (mCursor == null) {
            return;
        }
        mCursorPosList.clear();
        mCallLogIdsList.clear();
        mNMissedCount.clear();
        addGroups(mCursor);
    }

    /**
     * Records information about grouping in the list.  Should be called by the overridden
     * {@link #addGroups} method.
     */
    protected void addGroup(int cursorPosition, int size, boolean expanded) {
        if (mGroupCount >= mGroupMetadata.length) {
            int newSize = ArrayUtils.idealLongArraySize(
                    mGroupMetadata.length + GROUP_METADATA_ARRAY_INCREMENT);
            long[] array = new long[newSize];
            System.arraycopy(mGroupMetadata, 0, array, 0, mGroupCount);
            mGroupMetadata = array;
        }

        long metadata = ((long)size << 32) | cursorPosition;
        if (expanded) {
            metadata |= EXPANDED_GROUP_MASK;
        }
        mGroupMetadata[mGroupCount++] = metadata;
    }

    @SuppressLint("UnnecessaryBoxing")
    protected void addGroup(int cursorPosition, int nMc, long[] callIds) {
        mCursorPosList.add(Integer.valueOf(cursorPosition));
        mCallLogIdsList.add(callIds);
        mNMissedCount.add(Integer.valueOf(nMc));
    }

    /**
     * Returns true if the specified position in the list corresponds to a
     * group header.
     */
    public boolean isGroupHeader(int position) {
        return mPositionMetadata.itemType == ITEM_TYPE_GROUP_HEADER;
    }

    /**
     * Given a position of a groups header in the list, returns the size of
     * the corresponding group.
     */
    public int getGroupSize(int position) {

        return mCursorPosList.size();
    }


    /*
    * return item's call ids
    * */
    public long[] getSubIds(int position) {
        return mCallLogIdsList.get(position);
    }

    /***********************************************************************************************/
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }

        mCount = mCursorPosList.size();
        return mCount;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_LIST;
    }

    @Override
    public Object getItem(int position) {
        if (mCursor == null || mCursor.isClosed()) {
            return null;
        }
        return mCursorPosList.get(position);
    }

    @Override
    @SuppressLint("UnnecessaryUnboxing")
    public long getItemId(int position) {
        Object item = getItem(position);
        if (item != null) {
            mCursor.moveToPosition(((Integer)item).intValue());
            return mCursor.getLong(mRowIdColumnIndex);
        } else {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            view = newChildView(mContext, parent);
        }
        view.setTag(R.id.new_missedcall_count, mNMissedCount.get(position));
        if(!mCursor.isClosed()) {
            mCursor.moveToPosition(mCursorPosList.get(position));
            bindChildView(position, view, mContext,
                    mCursor,
                    mCallLogIdsList.get(position));
        }
        return view;
    }
    /***********************************************************************************************/

    public void closeCursor() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

}