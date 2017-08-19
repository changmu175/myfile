package com.xdja.contact;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.presenter.activity.CommonDetailPresenter;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.DepartService;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.MemberService;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;

/**
 * Created by wanghao on 2015/8/13.
 */
public class ContactModuleService {

    /**
     * 根据账号判断是否是好友里面的人
     * 是否是通讯录里面的人
     * @param context
     * @param account 账号不会为空
     * wanghao
     */
    public static void startContactDetailActivity(Context context,String account){
        if(ObjectUtil.stringIsEmpty(ContactUtils.getCurrentAccount()))return;
        Friend friend = getFriend(context, account);
        if(isExistFriend(friend)){
            Intent intent = new Intent(context, CommonDetailPresenter.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,friend.getAccount());
            context.startActivity(intent);return;
        }
        if(ContactUtils.isHasCompany()){
            Member member = getMember(context, account);
            if(isExistInDepartment(member)){
                Intent intent = new Intent(context, CommonDetailPresenter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,member.getWorkId());
                context.startActivity(intent);return;
            }
        }
        ActomaAccount actomaAccount = getAccount(context, account);
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){

            CommonDetailDto commonDetailDto = new CommonDetailDto();
            commonDetailDto.setIsExist(true);
            commonDetailDto.setIsFriend(false);
            commonDetailDto.setIsMember(false);
            commonDetailDto.setActomaAccount(actomaAccount);

            Intent intent = new Intent(context, CommonDetailPresenter.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_GROUP_MEMBER);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,commonDetailDto);
            context.startActivity(intent);return;
        }else{
            //start:add for 2328 by wal@xdja.com
            CommonDetailDto commonDetailDto = new CommonDetailDto();
            commonDetailDto.setIsExist(false);
            commonDetailDto.setIsFriend(false);
            commonDetailDto.setIsMember(false);
            actomaAccount = new ActomaAccount();
            actomaAccount.setAccount(account);
            commonDetailDto.setActomaAccount(actomaAccount);
            Intent intent = new Intent(context, CommonDetailPresenter.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_GROUP_MEMBER);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,commonDetailDto);
            context.startActivity(intent);return;
            //end:add for 2328 by wal@xdja.com
        }
    }


    /**
     * 是否是好友
     * @return
     */
    public static boolean isExistFriend(Friend friend){
        if(!ObjectUtil.objectIsEmpty(friend) && friend.isShow()){
            return true;
        }
        return false;
    }
    /**
     * 在通讯录是否存在
     * @return
     */
    public static boolean isExistInDepartment(Member member){
        if(!ObjectUtil.objectIsEmpty(member)){
            return true;
        }
        return false;
    }

    public static Member getMember(Context context,String account){
        MemberService memberService = new MemberService();
        Member member = memberService.getMemberByAccount(account);
        return member;
    }

    public static Friend getFriend(Context context,String account){
        FriendService friendService = new FriendService();
        Friend friend = friendService.queryFriendByAccount(account);
        return friend;
    }

    public static ActomaAccount getAccount(Context context,String account){
        ActomAccountService actomAccountService = new ActomAccountService();
        return actomAccountService.queryByAccount(account);
    }

    public static Avatar getAvatar(Context context,String account){
        AvaterService avaterService = new AvaterService();
        return avaterService.queryByAccount(account);
    }


    /**
     * 判读是否有网络
     * @param context
     * @return
     */
    public static boolean isNetConnect(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetInfo.isConnected() || mobNetInfo.isConnected()) {//网络已开启
            return true;
        } else {//网络未开启
            return false;
        }
    }
    /**
     * 检查网络
     * @return
     */
    public static boolean checkNetWork(){
        //boolean bool = StateParams.getStateParams().isNetWorkOpen();
        boolean bool = isNetConnect(ActomaController.getApp());
        if(!bool){
            XToast.show(ActomaController.getApp(), R.string.check_net_work_state);
        }
        return bool;
    }

    public static boolean deleteDeparmentAndDeptMember(){
        Context context = ActomaController.getApp();
        boolean result = false;
        MemberService memberService = new MemberService();
        DepartService departService = new DepartService(context);
        boolean departExist = departService.existTable();
        boolean departMemberExist = memberService.existDeptMemeberTable();
        if(departExist && departMemberExist){
            LogUtil.getUtils().i("本地存在 部门表和部门成员表");
            boolean deleteDept = departService.deleteDeparment() ;
            boolean deteteDeptMemeber = memberService.deleteDeparmentMember();
            LogUtil.getUtils().i("删除部门表和部门成员表结果:deleteDept"+deleteDept+"---deteteDeptMemeber:"+deteteDeptMemeber);
            PreferenceUtils.savePersonLastUpdateId(context, 0);
            PreferenceUtils.savetDeptLastUpdateId(context, 0);
            if(deleteDept && deteteDeptMemeber){
                result = true;
            }
        }
        return result;
    }




}
