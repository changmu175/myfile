package com.xdja.contact.presenter.activity;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.friend.ResponseRemarkFriend;
import com.xdja.contact.presenter.command.IFriendRemarkCommand;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.ui.def.IFriendRemarkVu;
import com.xdja.contact.ui.view.FriendRemarkVu;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

/**
 * Created by wanghao on 2015/7/23.
 */
public class FriendRemarkPresenter extends ActivityPresenter<IFriendRemarkCommand,IFriendRemarkVu> implements IFriendRemarkCommand {

    private Friend friend;

    @Override
    protected Class<? extends IFriendRemarkVu> getVuClass() {
        return FriendRemarkVu.class;
    }

    @Override
    protected IFriendRemarkCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        this.friend = getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_INTENT_FRIEND);
    }

    @Override
    public Friend getFriend() {
        return friend;
    }

    @Override
    public void saveRemark() {
        final String remark = getVu().getRemark();
        try{
            FriendHttpServiceHelper.saveRemark(new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    getVu().dismissLoading();
                    String errorCode = httpErrorBean.getErrCode();
                    if (errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_NOT_VALID.getCode())
                            || errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_ERROR.getCode())) {
                        XToast.show(getApplication(), R.string.params_err);
                        return;
                    } else if (errorCode.equals(ServiceErrorCode.INTERNAL_SERVER_ERROR.getCode())
                            || errorCode.equals(ServiceErrorCode.EXCEPTION_HANDLE_ERROR.getCode())) {
                        XToast.show(getApplication(), R.string.server_is_busy);
                        return;
                    // } else if (errorCode.equals("not_friend")) { modify by lwl
                    } else if (ServiceErrorCode.EXCEPTION_NOT_FRIEND.getCode().equals(errorCode)) {
                        XToast.show(getApplication(), R.string.user_is_not_friend);
                        return;
                    } else {
                        XToast.show(getApplication(), getString(R.string.set_remark_fail));
                        LogUtil.getUtils().e("Actoma contact FriendRemarkPresenter saveRemark:修改好友备注出错，errorCode:" + errorCode + "");
                        return;
                    }
                }

                @Override
                public void onSuccess(String body) {
                    getVu().dismissLoading();
                    try {
                        ResponseRemarkFriend remakFriend = JSON.parseObject(body, ResponseRemarkFriend.class);
                        if (ObjectUtil.objectIsEmpty(remakFriend)) {
                            return;
                        }
                        if(ObjectUtil.stringIsEmpty(remark.trim())){
                            friend.setRemark(null);
                            friend.setRemarkPy(null);
                            friend.setRemarkPinyin(null);
                        }else{
                            String remakpy = remakFriend.getRemarkPy();
                            String remakpinyin = remakFriend.getRemarkPinyin();
                            friend.setRemark(remark);
                            friend.setRemarkPy(remakpy);
                            friend.setRemarkPinyin(remakpinyin);
                        }

                        boolean bool = new FriendService().update(friend);
                        if (bool) {
                            //修改备注后,通知主框架更改名称
                            BroadcastManager.sendBroadcastRefreshName(friend.getAccount());
                            FireEventUtils.pushFriendUpdateRemarkButton(friend.getAccount(), friend.showName());
                            //FriendProxy.sendRemarkUpdateBroadcast(getApplication(), friend);
                            getVu().dismissLoading();
                            BroadcastManager.refreshFriendList();
                            finish();
                        } else {
                            XToast.show(getApplication(), getString(R.string.set_remark_fail));
                            LogUtil.getUtils().e("Actoma contact FriendRemarkPresenter saveRemark:修改好友备注失败,终端保存更新数据出错");
                        }
                    } catch (Exception e) {
                        LogUtil.getUtils().e("Actoma contact FriendRemarkPresenter saveRemark:修改好友备注,解析服务器返回数据为空");
                        XToast.show(getApplication(), R.string.server_is_busy);
                    }
                }

                @Override
                public void onErr() {
                    getVu().dismissLoading();
                }
            }, friend.getAccount(), remark);
        }catch (Exception e){
            getVu().dismissLoading();
        }

    }

}
