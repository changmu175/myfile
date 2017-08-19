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
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups together calls in the call log.
 * <p>
 * This class is meant to be used in conjunction with {@link GroupingListAdapter}.
 */
public class CallLogGroupBuilder {
    private static final String TAG = CallLogGroupBuilder.class.getCanonicalName();
    public interface GroupCreator {
        void addGroup(int cursorPosition, int size, boolean expanded);
        void addGroup(int cursorPosition, int nMc, long[] callIds);
    }


    /** The object on which the groups are created. */
    private final GroupCreator mGroupCreator;

    public CallLogGroupBuilder(GroupCreator groupCreator) {
        mGroupCreator = groupCreator;
    }

    @SuppressLint("UnnecessaryUnboxing")
   public void addGroups(Cursor cursor) {
        final int count = cursor.getCount();
        if (count == 0) {
            return;
        }


        int numberColIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int nameColindex = cursor.getColumnIndex(Calls.CACHED_NAME);
        int typeColIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int currentGroupSize = 1;

        cursor.moveToFirst();
        // The number of the first entry in the group.
        String firstNumber = cursor.getString(numberColIndex);
        String firstName = cursor.getString(nameColindex);

        Map<String, List<Long>> personMap = new HashMap<>();  //phone number  ----> all call ids
        ArrayList<Long> callLogIdList = new ArrayList<>();        //call ids list
        ArrayList<Integer> groupListCursorPos = new ArrayList<>();       //show in call log list, cursor pos
        Map<String, Integer> newMissedCallMap = new HashMap<>();

        callLogIdList.add(cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID)));
        personMap.put(firstName, callLogIdList);
        groupListCursorPos.add(cursor.getPosition());

        if (cursor.getInt(cursor.getColumnIndex(Calls.NEW)) == 1) {
            newMissedCallMap.put(firstName, Integer.valueOf(1));
        }
        while (cursor.moveToNext()) {
            // The number of the current row in the cursor.
            final String currentNumber = cursor.getString(numberColIndex);
            final String currentName = cursor.getString(nameColindex);
            final boolean sameNumber = equalNumbers(firstNumber, currentNumber);
            final boolean sameName = firstName.equals(currentName);
            firstNumber = currentNumber;

            callLogIdList = (ArrayList<Long>) personMap.get(currentName);
            if (null != callLogIdList) {
                callLogIdList.add(cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID)));
            } else {
                callLogIdList = new ArrayList<>();
                callLogIdList.add(cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID)));
                groupListCursorPos.add(cursor.getPosition());
            }
            if (cursor.getInt(cursor.getColumnIndex(Calls.NEW)) == 1) {
                Integer nMc = newMissedCallMap.get(currentName);
                if (null == nMc) {
                    nMc = Integer.valueOf(0);
                }
                //newMissedCallMap.put(currentName, Integer.valueOf(++nMc));
                /**2017-2-28 -wangzhen modify.Simplify the boxing class**/
                newMissedCallMap.put(currentName, ++nMc);
            }
            personMap.put(currentName, callLogIdList);
        }
        for (Integer pos: groupListCursorPos) {
            cursor.moveToPosition(pos);
            Integer missCount = newMissedCallMap.get(cursor.getString(nameColindex));
            addGroup(pos, missCount != null ? missCount.intValue() : 0,
                    toPrimitive((ArrayList<Long>) personMap.get(cursor.getString(nameColindex))));
        }
    }

    /**
     * Creates a group of items in the cursor.
     * <p>
     * The group is always unexpanded.
     *
     * @see CallLogAdapter#addGroup(int, int, boolean)
     */
    private void addGroup(int cursorPosition, int size) {
        mGroupCreator.addGroup(cursorPosition, size, false);
    }

    private void addGroup(int cursorPosition, int nMc, long[] callIds) {
        mGroupCreator.addGroup(cursorPosition, nMc, callIds);
    }


    private boolean equalNumbers(String number1, String number2) {
//        if (PhoneNumberUtils.isUriNumber(number1) || PhoneNumberUtils.isUriNumber(number2)) {
//            return compareSipAddresses(number1, number2);
//        } else {
        // Optim -- first try to compare very simply
        if(number1 != null && number2 != null && number1.equals(number2)) {
            return true;
        }
        return PhoneNumberUtils.compare(number1, number2);
//        }
    }
    /*
    boolean compareSipAddresses(String number1, String number2) {
        if (number1 == null || number2 == null) return number1 == number2;

        int index1 = number1.indexOf('@');
        final String userinfo1;
        final String rest1;
        if (index1 != -1) {
            userinfo1 = number1.substring(0, index1);
            rest1 = number1.substring(index1);
        } else {
            userinfo1 = number1;
            rest1 = "";
        }

        int index2 = number2.indexOf('@');
        final String userinfo2;
        final String rest2;
        if (index2 != -1) {
            userinfo2 = number2.substring(0, index2);
            rest2 = number2.substring(index2);
        } else {
            userinfo2 = number2;
            rest2 = "";
        }

        return userinfo1.equals(userinfo2) && rest1.equalsIgnoreCase(rest2);
    }
    */

    @SuppressLint("UnnecessaryUnboxing")
    private long[] toPrimitive(ArrayList<Long> LongList) {
        Log.d(TAG, "toPrimitive   LongList.size:" + LongList.size());
        long[] result = new long[LongList.size()];
        for (int i = 0; i < LongList.size(); i++) {
            result[i] = LongList.get(i).longValue();
        }
        return result;
    }
}
