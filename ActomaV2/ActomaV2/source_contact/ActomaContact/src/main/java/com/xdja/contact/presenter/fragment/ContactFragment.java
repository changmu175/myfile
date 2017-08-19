package com.xdja.contact.presenter.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.event.UpdateContactShowNameEvent;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.presenter.adapter.CustomViewPagerAdapter;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.view.NoScrollViewPager;
import com.xdja.contact.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/7/23.
 */
public class ContactFragment extends Fragment {

    private View view;

    private SlidingTabLayout tabLayout;

    private NoScrollViewPager viewPager;

    private TextView selectedFriendName;

    private FrameLayout relativeLayout;

    private RelativeLayout friendOpenTransferLayout;

    private CircleImageView selectedHeadIcon;

    private EncryptRecordService encryptRecordService;

    private FriendRequestService service;

    private ActomAccountService actomAccountService;

    private String appShowName;

    private Map map;

    private HashMap<String,String> hashMap = new HashMap<>();

    private String account;

    private LinearLayout red_tishi_layout;

    private TextView tishi_text;

    private TextView yu;

    private TextView nick_name;

    private TextView appName;



    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        RegisterActionUtil.registerOpenTransferAction(getActivity(), receiver);
        RegisterActionUtil.registerOpenSafeService(getActivity(), receiver);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_PUSH_CLOSE_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_FRIEND_HAS_DELETED);
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);
        filter.addAction(RegisterActionUtil.ACTION_FRIEND_REQUEST);
        filter.addAction(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_TO_VIEWPAGER_FIRST);
        filter.addAction(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME);
        filter.addAction(RegisterActionUtil.ACTION_DELETE_FRIEND_OR_DEPARTMEMBER);//modify by xnn for bug 9932
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_TAB);
        filter.addAction(RegisterActionUtil.ACTION_CLOSE_TANSFER);
        filter.addAction(RegisterActionUtil.ACTION_CONTACT_REFRESH_TAB_NAME);
        activity.registerReceiver(receiver, filter);
        this.service = new FriendRequestService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(ObjectUtil.objectIsEmpty(actomAccountService)){
            actomAccountService = new ActomAccountService();
        }
        if(ObjectUtil.objectIsEmpty(encryptRecordService)){
            encryptRecordService = new EncryptRecordService(getActivity());
        }
         map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if(!ObjectUtil.mapIsEmpty(map)){
            if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                account = (String)map.get("destAccount");
                hashMap.put("destAccount", (String) map.get("destAccount"));
            }
            if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                appShowName = (String) map.get("appPackage");
                hashMap.put("appPackage", (String) map.get("appPackage"));
            }
        }

        //初始化事件总线
        BusProvider.getMainProvider().register(this);

        LogUtil.getUtils().e("Actoma contact ContactFragment ------onCreateView-----------");
        view = inflater.inflate(R.layout.contact_main, container, false);
        initView();
        buildFragments();
        initSafeTransfer();
        return view;
    }

    private void initView() {
        relativeLayout = (FrameLayout) view.findViewById(R.id.not_choose_layout);
        selectedFriendName = (TextView) view.findViewById(R.id.selected_friend_name);
        red_tishi_layout = (LinearLayout) view.findViewById(R.id.red_tishi_lyout);
        yu = (TextView) view.findViewById(R.id.show_name_layout_name0);
        nick_name = (TextView)view.findViewById(R.id.show_name_layout_name1);
        appName = (TextView)view.findViewById(R.id.show_name_layout_name2);
        tishi_text = (TextView) view.findViewById(R.id.red_tishi);
        tabLayout = (SlidingTabLayout) view.findViewById(R.id.contact_slidingTab);
        viewPager = (NoScrollViewPager) view.findViewById(R.id.contact_viewpager);
        tabLayout.setCustomTabView(R.layout.tablayout, R.id.tab_title);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tool_bar_title_color);
            }
        });
        viewPager.setOffscreenPageLimit(3);//add by lwl 703
        viewPager.setCurrentItem(0);
        friendOpenTransferLayout = (RelativeLayout) view.findViewById(R.id.friend_open_transfer_layout);
        selectedHeadIcon = (CircleImageView) view.findViewById(R.id.friend_open_transfer_head_icon);
        friendOpenTransferLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultSafeTips();
            }
        });
    }


    private void initSafeTransfer(){
        if(StateParams.getStateParams().isSeverOpen()){
            if (map != null) {
                new GetContactShowName().execute();
            }else {
                relativeLayout.setVisibility(View.VISIBLE);
                red_tishi_layout.setVisibility(View.VISIBLE);
                friendOpenTransferLayout.setVisibility(View.GONE);
            }
        }else {
            relativeLayout.setVisibility(View.GONE);
            red_tishi_layout.setVisibility(View.GONE);
            friendOpenTransferLayout.setVisibility(View.GONE);
        }
    }

    FriendListPresenter friendListPresenter;


    private void buildFriendFragment(List<Fragment> fragmentList,List<String> titles){
        friendListPresenter = new FriendListPresenter();
        fragmentList.add(friendListPresenter);
        titles.add(getActivity().getString(R.string.contact_fragment_friend_title));//modify by wal@xdja.com for string 好友
    }

    private void buildGroupFragment(List<Fragment> fragmentList,List<String> titles){
        GroupListPresenter groupListPresenter = new GroupListPresenter();
        fragmentList.add(groupListPresenter);
        titles.add(getActivity().getString(R.string.contact_fragment_group_title));//modify by wal@xdja.com for string 群组
    }

    private void buildCompanyFragment(List<Fragment> fragmentList,List<String> titles){
        if(ContactUtils.isHasCompany()){
            ContactCompanyPresenter companyPresenter = new ContactCompanyPresenter();
            fragmentList.add(companyPresenter);
            titles.add(getActivity().getString(R.string.contact_fragment_company_title));//modify by wal@xdja.com for string 集团通讯录
        }
    }

    private void buildFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        List<String> titles = new ArrayList<String>();
        buildFriendFragment(fragmentList,titles);
        buildGroupFragment(fragmentList,titles);
        buildCompanyFragment(fragmentList,titles);
        viewPager.setAdapter(new CustomViewPagerAdapter(getFragmentManager(), fragmentList, titles));
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if (!ObjectUtil.mapIsEmpty(map)) {
            if (!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))) {
                account = (String) map.get("destAccount");
                hashMap.put("destAccount", (String) map.get("destAccount"));
            }
            if (!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))) {
                appShowName = (String) map.get("appPackage");
                hashMap.put("appPackage", (String) map.get("appPackage"));
            }
            new GetContactShowName().execute();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //统计未读消息  显示红点
            if (RegisterActionUtil.ACTION_FRIEND_REQUEST.equals(intent.getAction())) {
                LogUtil.getUtils().i("Actoma contact ContactFragment,receive push info,------show red item------");
                showRedItem();
            }

            if(RegisterActionUtil.ACTION_TO_VIEWPAGER_FIRST.equals(intent.getAction())){
                viewPager.setCurrentItem(0);
            }


            if(intent.getAction().equals(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME)){
                appShowName = intent.getStringExtra("appShowName");
                account = intent.getStringExtra("account");
                hashMap.put("destAccount",account);
                hashMap.put("appPackage", appShowName);
                new GetContactShowName().execute();
            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_DELETE_FRIEND_OR_DEPARTMEMBER)){
                //[S]modify by lixiaolong on 20161010. fix bug 4858. review by gbc.
                if (StateParams.getStateParams().isSeverOpen()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    red_tishi_layout.setVisibility(View.VISIBLE);
                    friendOpenTransferLayout.setVisibility(View.GONE);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    red_tishi_layout.setVisibility(View.GONE);
                    friendOpenTransferLayout.setVisibility(View.GONE);
                }
                //[E]modify by lixiaolong on 20161010. fix bug 4858. review by gbc.
            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_REFRESH_TAB)){
                relativeLayout.setVisibility(View.VISIBLE);
                red_tishi_layout.setVisibility(View.VISIBLE);
                friendOpenTransferLayout.setVisibility(View.GONE);
            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_CLOSE_TANSFER)){
                //[S]fix bug 7778 by licong
                if (UniversalUtil.getLanguageType(getActivity()) != UniversalUtil.LANGUAGE_EN) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    red_tishi_layout.setVisibility(View.VISIBLE);
                }
                //[E]fix bug 7778 by licong
                LogUtil.getUtils().d("2017/1/3 ACTION_CLOSE_TANSFER red_tishi_layout");
                friendOpenTransferLayout.setVisibility(View.GONE);
                map = null;
            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_CONTACT_REFRESH_TAB_NAME)){
                if(StateParams.getStateParams().isSeverOpen()) {
                    map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
                    if (map != null) {
                        new GetContactShowName().execute();
                    }
                }
            }
        }
    };

    /**
     * 统计未读消息  显示红点
     */
    private void showRedItem(){

        int count = service.countNewFriend();
        //这里暂时不添加红点提醒了
        /*if(!ObjectUtil.objectIsEmpty(tabLayout)) {
            tabLayout.setTabNewCountVisible(0, true);
        }*/
        TabTipsEvent event = new TabTipsEvent();
        event.setIndex(TabTipsEvent.INDEX_CONTACT);
        if (count > 99) {
            event.setContent("99");
            event.setIsShowPoint(true);
        } else if(count > 0){
            event.setContent(String.valueOf(count));
            event.setIsShowPoint(true);
        }else  if(count <= 0){
            event.setIsShowPoint(false);
        }
        BusProvider.getMainProvider().post(event);
    }


    private class GetContactShowName extends AsyncTask<String,Integer,CommonDetailDto> {

        @Override
        protected void onPostExecute(CommonDetailDto commonDetailDto) {
            super.onPostExecute(commonDetailDto);
            if(ObjectUtil.objectIsEmpty(commonDetailDto)){
                defaultSafeTips();
            }else{
                String showName = commonDetailDto.getShowName();
                Avatar avatar = commonDetailDto.getAvatar();
                relativeLayout.setVisibility(View.VISIBLE);
                red_tishi_layout.setVisibility(View.GONE);
                friendOpenTransferLayout.setVisibility(View.VISIBLE);
                if (!ObjectUtil.objectIsEmpty(avatar) && !ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                    selectedHeadIcon.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                }else {
                    selectedHeadIcon.loadImage( R.drawable.img_avater_40,true);
                }
                //[S] modify by lixiaolong on 20160918. fix bug 3191. review by wangchao1.
                if (showName != null && !"".equals(showName)) {
                    nick_name.setText(showName);
                } else {
                    nick_name.setText(account);
                }
                //[E] modify by lixiaolong on 20160918. fix bug 3191. review by wangchao1.
//                appName.setText(IEncryptUtils.getAppName(appShowName) + "安全通信中");
                appName.setText(getString(R.string.selected_open_transfer_top, IEncryptUtils.getAppName(appShowName)));
                BroadcastManager.sendBroadcastRefreshName(account);

            }
        }

        @Override
        protected CommonDetailDto doInBackground(String... params) {
            if(ObjectUtil.stringIsEmpty(account)) return null;
            if(ObjectUtil.objectIsEmpty(actomAccountService))return null;
            return actomAccountService.queryCommonDetailByAccount(account);
        }
    }

    private void defaultSafeTips(){
        relativeLayout.setVisibility(View.VISIBLE);
        red_tishi_layout.setVisibility(View.VISIBLE);
        friendOpenTransferLayout.setVisibility(View.GONE);
        //关闭通道
        if(!ObjectUtil.mapIsEmpty(map)){
            BroadcastManager.sendBroadcastCloseTransfer();
        }
        BroadcastManager.openFrameSafeSwitch("");
    }

    //wxf@xdja.com 2016-10-24 add. fix bug 5078、5449. review by mengbo. Start
    @Subscribe
    public void onReceiveUpdateContactShowNameEvent(UpdateContactShowNameEvent event) {
        //收到删除好友刷新事件时候再次去数据库查询
        CommonDetailDto detailDto = actomAccountService.queryCommonDetailByAccount(account);
        String showName = detailDto.getShowName();
        if (showName != null && !"".equals(showName)) {
            nick_name.setText(showName);
        } else {
            nick_name.setText(account);
        }
    }
    //wxf@xdja.com 2016-10-24 add. fix bug 5078、5449. review by mengbo. End


    /*private String getRedSpanned(String showName,String appShowName){
        StringBuffer value = new StringBuffer("与");
        StringBuffer sb = new StringBuffer();
        sb.append("<font color = ");
        sb.append(getActivity().getResources().getColor(R.color.highlight_read));
        sb.append(">");
        if(!ObjectUtil.stringIsEmpty(showName)){
            sb.append(TextUtils.htmlEncode(showName));
        }
        sb.append("</font>");
        sb.append("<font color = ");
        sb.append(getActivity().getResources().getColor(R.color.alpha_title));
        sb.append(">");
        if(!ObjectUtil.stringIsEmpty(appShowName)){
            sb.append(TextUtils.htmlEncode(appShowName));
        }
        sb.append("</font>");
        String temp = getString(R.string.selected_open_transfer);
        value.append(sb);
        value.append(temp);
        return value.toString();
    }*/

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getMainProvider().unregister(this);
    }
}
