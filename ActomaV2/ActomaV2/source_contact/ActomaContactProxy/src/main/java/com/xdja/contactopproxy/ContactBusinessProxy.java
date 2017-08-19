package com.xdja.contactopproxy;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xdja.comm.event.BusProvider;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactcommon.dto.ContactDto;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.data.di.annotation.Scoped;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by liyingqing on 16-3-22.
 */
public class ContactBusinessProxy implements ContactService {

    Context mContext;

    @Inject
    public ContactBusinessProxy(@Scoped(com.xdja.imp.data.di.DiConfig.CONTEXT_SCOPE_APP) Context context){
        mContext = context;
    }
    @Override
    public boolean isFriendRelated(String account) {
        return ContactModuleProxy.isFriendRelated(account);
    }

    @Override
    public boolean isExistDepartment(String account) {
        return ContactModuleProxy.isExistDepartment(account);
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        ContactInfo contactInfo = ContactCache.getInstance().getContact(null, account);
        if (contactInfo == null) {
            ContactDto contactDto  = ContactModuleProxy.getContactInfo(account);
            contactInfo = getInfoFromDTO(contactDto);
            ContactCache.getInstance().putContact(null, contactInfo);
        }
        return contactInfo;
    }

    @Override
    public void getGroupInfoFromServer(String groupId) {
        ContactModuleProxy.getGroupInfoFromServer(groupId);
    }

    @Override
    public boolean isAccountInGroup(String account, String groupId) {
        return ContactModuleProxy.isAccountInGroup(account,groupId);
    }

    @Override
    public void startChooseActivity(String groupId, ArrayList<String> accounts) {
        ContactModuleProxy.startChooseActivity(mContext,groupId,accounts);
    }

    @Override
    public Fragment getGroupInfoDetailManager(String groupId) {
        return ContactModuleProxy.groupInfoDetailManager(groupId);
    }

    @Override
    public void startContactDetailActivity(String account) {
        ContactModuleProxy.startContactDetailActivity(mContext,account);
    }

    @Override
    public void quitOrDismissGroup(String groupId, String account) {
        ContactModuleProxy.quitOrDismissGroup(mContext, groupId, account, new ContactModuleProxy.ContactModuleResult() {
            @Override
            public void result(boolean bool) {
                BusProvider.getMainProvider().post(
                        new ContactProxyEvent.QuitAndDismissEvent(bool)
                );
            }
        });
    }

    @Override
    public boolean isGroupOwner(String groupId) {
        return ContactModuleProxy.isGroupOwner(mContext,groupId);
    }

    @Override
    public ContactInfo GetGroupMemberInfo(String groupId, String account) {
        ContactInfo contactInfo = ContactCache.getInstance().getContact(groupId, account);
        if (contactInfo == null) {
            ContactDto contactDto  = ContactModuleProxy.getGroupMemberInfo(groupId,account);
            contactInfo = getInfoFromDTO(contactDto);
            ContactCache.getInstance().putContact(groupId, contactInfo);
        }
        return contactInfo;
    }

    @Override
    public ContactInfo getGroupInfo(String groupId) {
        ContactInfo contactInfo = ContactCache.getInstance().getGroup(groupId);
        if (contactInfo == null) {
            ContactDto contactDto  = ContactModuleProxy.getGroupInfo(groupId);
            contactInfo = getInfoFromDTO(contactDto);
            ContactCache.getInstance().putGroup(groupId, contactInfo);
        }
        return contactInfo;
    }

    private ContactInfo getInfoFromDTO(ContactDto contactDto){
        ContactInfo contactInfo = null;
        if(contactDto!=null){
            contactInfo = new ContactInfo();
            contactInfo.setAccount(contactDto.getAccount());
            contactInfo.setName(contactDto.getName());
            contactInfo.setAvatarUrl(contactDto.getAvatarUrl());
            contactInfo.setThumbnailUrl(contactDto.getThumbnailUrl());
        }
        return contactInfo;
    }

}
