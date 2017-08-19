package com.xdja.contact.task.friend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.contact.R;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangsha on 2017/2/22.
 */
public class TaskAcceptFriendReq extends AsyncTask<String,Integer,Integer>{
    private int flag = 0;
    private IAcceptFriendCallback acceptFriendCallback;
    private Context context;
    private String TAG = "TaskAcceptFriendReq";
    private IModuleHttpCallBack httpCallBack;

    public TaskAcceptFriendReq(Context con,IAcceptFriendCallback callback, IModuleHttpCallBack httpCallbackPara){
        acceptFriendCallback = callback;
        context = con;
        httpCallBack = httpCallbackPara;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (acceptFriendCallback != null) {
            acceptFriendCallback.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (flag==-1){
            if (acceptFriendCallback != null) {
                acceptFriendCallback.onPostExecute();
            }
            XToast.show(context, context.getString(R.string.accept_friend_error));
        }
    }

    @Override
    protected Integer doInBackground(String... accounts) {
        String currentAccount = ContactUtils.getCurrentAccount();
        String account=accounts[0];
        if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
            String ckmsGroupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount,account);
            boolean isInGroup = CkmsGpEnDecryptManager.isEntitySGroupsExist(currentAccount, ckmsGroupId);
            LogUtil.getUtils().e("Actoma contact FriendRequestHistory,Ckms is Entity SGroups Exist :"+isInGroup+" ckmsGroupId "+ckmsGroupId);
            if (!isInGroup){
                List<String> accountList = new ArrayList<>();
                accountList.add(currentAccount);
                accountList.add(account);
                String opSign= GroupHttpServiceHelper.syncGetCkmsGroupOpSign(ckmsGroupId,accountList,CkmsGpEnDecryptManager.CREATE_GROUP);
                int resultCode= CkmsGpEnDecryptManager.createSGroupWithFriend(ckmsGroupId,currentAccount,account,opSign);
                Log.d(TAG,"Ckms CREATE_SGROUP_SUCCEED Success  "+resultCode);
                if (CkmsGpEnDecryptManager.CREATE_SGROUP_SUCCEED==resultCode){
                    try {
                        FriendHttpServiceHelper.acceptFriend(httpCallBack, account);
                    } catch (Exception e) {
                        flag=-1;
                    }
                }else{
                    flag=-1;
                }
            }else{
                try {
                    FriendHttpServiceHelper.acceptFriend(httpCallBack, account);
                } catch (FriendHttpException e) {
                    flag=-1;
                }
            }
        }else{
            try {
                FriendHttpServiceHelper.acceptFriend(httpCallBack, account);
            } catch (FriendHttpException e) {
                flag=-1;
            }
        }
        return flag;
    }

    public interface IAcceptFriendCallback {

        void onPreExecute();

        void onPostExecute();

    }
}
