package com.xdja.contact.presenter.activity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.account.QueryAccountInfoParam;
import com.xdja.contact.presenter.command.IFriendSearchCommand;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.ui.def.IFriendSearchVu;
import com.xdja.contact.ui.view.FriendSearchVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.frame.presenter.mvp.annotation.StackInto;


/**
 * 添加好友搜索界面
 * @author hkb
 * @since 2015年8月21日09:59:32
 * wanghao  2016-02-17 重构
 */
@StackInto
public class FriendSearchPresenter extends ActivityPresenter<IFriendSearchCommand, IFriendSearchVu> implements IFriendSearchCommand {

    @Override
    protected Class<? extends IFriendSearchVu> getVuClass() {
        return FriendSearchVu.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityContoller.getInstanse().addActivity(true,this);
    }

    @Override
    protected IFriendSearchCommand getCommand() {
        return this;
    }

    @Override
    public void startFriendDetail(Friend friend) {
        Intent intent = new Intent(this, CommonDetailPresenter.class);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,friend.getAccount());
        startActivity(intent);
    }

    private Friend localFriend;

    @Override
    public void startSearch(String keyWord) {
        //
        if (ObjectUtil.stringIsEmpty(keyWord)) {
            XToast.show(this, R.string.input_actom_account_or_phone);
            return;
        }

        //
        String currentAccount = ContactUtils.getCurrentAccountAlias();
        String currentPhone = ContactUtils.getCurrentAccountPhone();
        String compareKeywork=keyWord.toLowerCase();//add by lwl 3422
        if(ObjectUtil.stringIsEmpty(currentAccount))return;
        currentAccount=currentAccount.toLowerCase();
        if(currentAccount.equals(compareKeywork) || compareKeywork.equals(currentPhone)){
            XToast.show(this, R.string.un_support_search_selft);
            getVu().showNonDataView(true);
            return;
        }
        //if(matchNumeric(keyWord)){
        getVu().showLoading();
        //先执行本地查找 如果本地数据存在就不执行网络请求
        if(existLocale(keyWord)){
            //跳转到好友详情
            getVu().dismissLoading();
            getVu().showNonDataView(false);//add by lwl 3101
            startFriendDetail(localFriend);
        }else{
            if(!ContactModuleService.checkNetWork()) {
                getVu().dismissLoading();
                getVu().showNonDataView(false);//add by lwl 3101
                return;
            }
            serverSearch(keyWord);
        }
        /*}else {
            XToast.show(this, R.string.support_numeric_or_phone);
            return;
        }*/
    }

    //这里目前通过程序过滤，以后的需求有可能账号会是中英文
    /*private boolean matchNumeric(String keyword){
        String regex = "^[0-9]*[1-9][0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(keyword);
        return matcher.matches();
    }*/

    /**
     * 只接收绑定手机号和安通账号搜索
     * @param keyWord
     * @return false : 本地不存在 true : 本地存在
     */
    private boolean existLocale(String keyWord){
        FriendService friendService = new FriendService();
        localFriend = friendService.searchFriend(keyWord);
        if (!ObjectUtil.objectIsEmpty(localFriend)){
            if(localFriend.getAlias()!=null){
                if(keyWord.equals(localFriend.getAccount())){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void serverSearch(final String keyWord){
        //网络搜索好友
        new HttpRequestWrap().request(new QueryAccountInfoParam(new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
                getVu().showNonDataView(true);
                getVu().dismissLoading();
                // XToast.show(getApplication(), R.string.server_is_busy);
                //测试说不要toast  by lwl bugid 651
                
            }

            @Override
            public void onSuccess(String body) {
                getVu().dismissLoading();
                if (ObjectUtil.stringIsEmpty(body)) {
                    LogUtil.getUtils().e("Actoma contact FriendSearchPresenter serverSearch:搜索添加好友，server data is empty ");
                    getVu().showNonDataView(true);
                    return;
                }
                try {
                    ResponseActomaAccount responseActomaAccount = JSON.parseObject(body, ResponseActomaAccount.class);
                    if (ObjectUtil.objectIsEmpty(responseActomaAccount) || ObjectUtil.stringIsEmpty(responseActomaAccount.getAccount())) {
                        getVu().showNonDataView(true);
                        return;
                    } else {
                        getVu().showNonDataView(false);
                        CommonDetailDto commonDetailDto = new CommonDetailDto();
                        commonDetailDto.setIsExist(false);
                        commonDetailDto.setServerActomaAccount(responseActomaAccount);
                        //modify by lwl start
                        FriendService service = new FriendService();
                        String account = responseActomaAccount.getAccount();
                        Friend friend = service.queryFriendByAccountNonDeleted(account);
                        if(ObjectUtil.objectIsEmpty(friend)){
                            Intent intent = new Intent(FriendSearchPresenter.this, CommonDetailPresenter.class);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_SERVER_SEARCH);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY, commonDetailDto);
                            startActivity(intent);
                        }else {
                            /*[S]modify by tangsha@20161103 for 5661*/
                            ContactUtils.updateLocalAccountInfo(account,responseActomaAccount);
                            friend = service.queryFriendByAccountNonDeleted(account);
                            if(ObjectUtil.objectIsEmpty(friend) == false){
                                startFriendDetail(friend);
                            }else{
                                LogUtil.getUtils().e("Actoma contact FriendSearchPresenter friend is null!!!");
                            }
                            /*[E]modify by tangsha@20161103 for 5661*/
                        }
                        //modify by lwl end
                    }
                }catch (Exception e){
                    LogUtil.getUtils().e("Actoma contact FriendSearchPresenter serverSearch:搜索添加好友解析服务器返回数据出错，json parse is error ");
                    XToast.show(getApplication(), R.string.server_is_busy);
                    getVu().showNonDataView(true);
                    return;
                }
            }

            @Override
            public void onErr() {
                getVu().dismissLoading();
                getVu().showNonDataView(false);
            }
        }, keyWord));
    }
}
