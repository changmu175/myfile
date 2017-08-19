package com.xdja.contactcommon;

import android.content.Context;
import android.content.Intent;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.activity.CommonDetailPresenter;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

/**
 * 搜索添加好友跳转控制类
 * Created by yangpeng on 2015/8/24.
 */
public class SearchFriendControler {
    private Context context;
    public SearchFriendControler(Context context){
        this.context = context;
    }

    public void skip(String account){
        if(ObjectUtil.stringIsEmpty(account))return;
        //add by lwl start for  是否有本地联系人  和 扫描自己的二维码
        String currentAccount = ContactUtils.getCurrentAccountAlias();
        String currentPhone = ContactUtils.getCurrentAccountPhone();
        if(ObjectUtil.stringIsEmpty(currentAccount))return;
        if(currentAccount.equals(account) || account.equals(currentPhone)){
            XToast.show(context, R.string.un_support_search_selft);
            return;
        }
        //add by lwl end
        FriendService service = new FriendService();
        Friend friend = service.searchFriend(account);
        if(ObjectUtil.objectIsEmpty(friend)){
            Intent intent = new Intent(context, CommonDetailPresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_SCAN_SEARH);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,account);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent(context, CommonDetailPresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,friend.getAccount());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
