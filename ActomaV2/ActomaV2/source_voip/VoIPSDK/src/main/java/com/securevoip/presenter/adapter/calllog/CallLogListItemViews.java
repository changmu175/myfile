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
 * Copyright (C) 2011, The Android Open Source Project
 */

package com.securevoip.presenter.adapter.calllog;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.voipsdk.R;

/**
 * Simple value object containing the various views within a call log entry.
 */
public final class CallLogListItemViews {
	
	/**Begin:SUNYUNLEI 去掉系统联系人标识**/
    /** The quick contact badge for the contact. */
    //public final QuickContactBadge quickContactView;
	public final CircleImageView quickContactView;
    /**End:SUNYUNLEI 去掉系统联系人标识**/
    /** The primary action view of the entry. */
    public final View primaryActionView;

    /** The divider between the primary and secondary actions. */
    public final View dividerView;
    /** The details of the phone call. */
    public final PhoneCallDetailsViews phoneCallDetailsViews;
    /**
     * 快速拨号按钮
     */
    //public final ImageView callView;

    /**
     * 快速拨号按钮所在布局
      */
    public final RelativeLayout callLayout;

    public final TextView missCall;


    private CallLogListItemViews(CircleImageView quickContactView, View primaryActionView,
            View dividerView,
            RelativeLayout callLayout,
            PhoneCallDetailsViews phoneCallDetailsViews,
            TextView missCall) {
        this.quickContactView = quickContactView;
        this.primaryActionView = primaryActionView;
        this.dividerView = dividerView;
        this.phoneCallDetailsViews = phoneCallDetailsViews;
        this.callLayout = callLayout;
        this.missCall = missCall;
    }

    public static CallLogListItemViews fromView(View view) {
        return new CallLogListItemViews(
                (CircleImageView) view.findViewById(R.id.quick_contact_photo),
                view.findViewById(R.id.primary_action_view),
                view.findViewById(R.id.divider),
                (RelativeLayout)view.findViewById(R.id.call_layout),
                PhoneCallDetailsViews.fromView(view),
                (TextView)view.findViewById(R.id.missed_call_count));
    }
}
