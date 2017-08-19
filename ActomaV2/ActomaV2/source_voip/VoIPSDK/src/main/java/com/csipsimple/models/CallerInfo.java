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
 * Copyright (C) 2006 The Android Open Source Project 
 */

package com.csipsimple.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.securevoip.utils.OutInterfaceHelper;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.xdja.comm.server.ActomaController;

/**
 * Looks up caller information for the given phone number.
 */
public class CallerInfo {

    private static final String THIS_FILE = "CallerInfo";

    public static final CallerInfo EMPTY = new CallerInfo();

    public boolean contactExists;

    public long personId;
    public String name;

    public String phoneNumber;
    public String phoneLabel;
    public int numberType;
    public String numberLabel;
    /** The photo for the contact, if available. */
    public long photoId;
    /** The high-res photo for the contact, if available. */
    public Uri photoUri;

    // fields to hold individual contact preference data,
    // including the send to voicemail flag and the ringtone
    // uri reference.
    public Uri contactRingtoneUri;
    public Uri contactContentUri;
    

    private static LruCache<String, CallerInfo> callerCache;
    
    
    private static class CallerInfoLruCache extends LruCache<String, CallerInfo> {
        //final Context mContext;
        public CallerInfoLruCache(Context context) {
            super(4 * 1024 * 1024);
            //mContext = context;
        }
        
        @Override
        protected CallerInfo create(String sipUri) {
            Log.d(THIS_FILE, "####  create sipUri=" + sipUri );
            CallerInfo callerInfo = null;
            ParsedSipContactInfos uriInfos = SipUri.parseSipContact(sipUri);
            String phoneNumber = SipUri.getPhoneNumber(uriInfos);
            Log.d(THIS_FILE, "####  create phoneNumber=" + phoneNumber);
            if(callerInfo == null) {
                callerInfo = new CallerInfo();
            }

            if (null != OutInterfaceHelper.getNameFromContact(phoneNumber)) {
                callerInfo.name = OutInterfaceHelper.getNameFromContact(phoneNumber);
            } else {
                callerInfo.name = uriInfos.displayName.length()>0?
                        uriInfos.displayName : uriInfos.userName;
            }

            /*if (callerInfo == null || !callerInfo.contactExists) {
                // We can now search by sip uri
                callerInfo = ContactsWrapper.getInstance().findCallerInfoForUri(mContext,
                        uriInfos.getContactAddress());
            }*/
            

            
            return callerInfo;
        }
        
    }
    

    /**
     * Build and retrieve caller infos from contacts based on the caller sip uri
     * 
     * @param sipUri The remote contact sip uri
     * @return The caller info as CallerInfo object
     */
    @SuppressLint("SynchronizeOnNonFinalField")
    public static CallerInfo getCallerInfoFromSipUri(String sipUri) {
        Log.d(THIS_FILE, "####  getCallerInfoFromSipUri sipUri=" + sipUri);
        if (TextUtils.isEmpty(sipUri)) {
            Log.d(THIS_FILE, "####  getCallerInfoFromSipUri EMPTY");
            return EMPTY;
        }
        if(callerCache == null) {
            Log.d(THIS_FILE, "####  getCallerInfoFromSipUri new CallerInfoLruCache");
            callerCache = new CallerInfoLruCache(ActomaController.getApp());
        }
        synchronized (callerCache) {
            if (callerCache.get(sipUri) != null) {
                Log.d(THIS_FILE, "####  #####"+ callerCache.get(sipUri).name+" PhoneNumber: "+callerCache.get(sipUri).phoneNumber);
            }
            return callerCache.get(sipUri);
        }
    }

   /* public static CallerInfo getCallerInfoForSelf(Context context) {
        return ContactsWrapper.getInstance().findSelfInfo(context);
    }*/
}
