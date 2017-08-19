package com.csipsimple.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.securevoip.contacts.Contact;


/**
 * Created by xjq on 2015/5/28.
 */
public class ContactReceiver extends BroadcastReceiver {
    Context context = null;
    //联系人添加Action
    public static final String ACTION_CONTACT_INSERT = "com.xdja.contacts.ACTION_CONTACT_INSERT";

    //联系人删除Action
    public static final String ACTION_CONTACT_DEL = "com.xdja.contacts.ACTION_CONTACT_DEL";

    //联系人更新Action
    public static final String ACTION_CONTACT_UPDATE = "com.xdja.contacts.ACTION_CONTACT_UPDATE";
    public static final String KEY_CONTACT_BEAN = "key_contact_bean";
    @Override
    public void onReceive(Context mContext, Intent intent) {
        context = mContext;
        Contact contact = intent.getParcelableExtra(KEY_CONTACT_BEAN);
        if(contact != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_CONTACT_INSERT:
                    sendMessage2VoipSave(contact);
                    break;
                case ACTION_CONTACT_DEL:
                    sendMessage2VoipDelete(contact.getContactId());
                    break;
                case ACTION_CONTACT_UPDATE:
                    sendMessage2VoipUpdate(contact);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 保存联系人的时候通过content provider 告诉电话模块
     * @param contact
     */
    public void sendMessage2VoipSave(Contact contact){
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(Uri.parse(ContentResolver.SCHEME_CONTENT + "://com.xdja.contacts.db/insert/contacts"), contact.buildContentValues());

    }
    /**
     * 保存联系人的时候通过content provider 告诉电话模块
     * @param contact
     */
    public void sendMessage2VoipUpdate(Contact contact){
        ContentResolver resolver = context.getContentResolver();
        resolver.update(Uri.parse(ContentResolver.SCHEME_CONTENT+"://com.xdja.contacts.db/insert/contacts"), contact.buildContentValues(),null,null);

    }

    /**
     *
     * @param contactId
     */
    public void sendMessage2VoipDelete(String contactId){
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(Uri.parse(ContentResolver.SCHEME_CONTENT+"://com.xdja.contacts.db/insert/contacts"), contactId,null);

    }
}
