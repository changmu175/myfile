package com.xdja.contact.ui.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.presenter.command.ICommonDetailCommand;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.ICommonDetailVu;
import com.xdja.comm.uitl.ObjectUtil;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/10/23.
 * 陌生人界面
 * 好友界面
 * 联系人界面
 */
public class CommonDetailVu extends BaseActivityVu<ICommonDetailCommand> implements ICommonDetailVu {
    //头像
    private CircleImageView commonAvatar;
    //显示名称
    private TextView commonShowName;
    //集团名称
    private LinearLayout commonNameLayout;
    private TextView commonName;
    //昵称
    private LinearLayout commonNickNameLayout;
    private TextView commonNickName;
    //帐号
    private LinearLayout commonAccountLayout;
    private TextView commonAccount;
    //底部需要填充的控件
    private LinearLayout bottomLayout;

    /*********陌生人界面底部元素****************/
    //添加好友 按钮布局
    private LinearLayout strangerLayout;
    //添加好友 按钮
    private Button friendAddBtn;

    /*********集团联系人底部元素****************************/
    private LinearLayout memberInfoLayout;

    private LinearLayout[] phoneLayouts;
    private TextView[] phoneTvs;
    private ImageButton[] imageButtons;
    //集团电话组件
    private LinearLayout phoneLayout1;
    private LinearLayout phoneLayout2;
    private LinearLayout phoneLayout3;
    private LinearLayout phoneLayout4;
    private LinearLayout phoneLayout5;


    private TextView phone1;
    private TextView phone2;
    private TextView phone3;
    private TextView phone4;
    private TextView phone5;

    private ImageButton phoneIcon1;
    private ImageButton phoneIcon2;
    private ImageButton phoneIcon3;
    private ImageButton phoneIcon4;
    private ImageButton phoneIcon5;

    private TextView phone_number_colon;//电话标签
    //人员名称
    private TextView memberName;
    //部门名称
    private TextView departName;


    /*********好友底部元素***************/
    private LinearLayout friendLayout;

    private RelativeLayout sendEncriptionMesage;
    private RelativeLayout callWithEncription;
//    private RelativeLayout openEncriptionServe;

    private View rootView;

    private LayoutInflater inflater;
    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        this.inflater = inflater;
        toolbar.setTitle(getContext().getString(R.string.contact_detail_common_info));//modify by wal@xdja.com for string 详细资料
        toolbar.setBackgroundColor(getContext().getResources().getColor(R.color.toolbar_alpha));
        bottomLayout = ButterKnife.findById(getView(), R.id.account_bottom_layout);
        initCommonComponent();
    }

    //初始化公共组件
    private void initCommonComponent(){
        //上半部分控件
        commonAvatar = ButterKnife.findById(getView(), R.id.contact_detail_avater);
        //显示名称
        commonShowName = ButterKnife.findById(getView(), R.id.contact_person_name);
        //集团名称
        commonNameLayout = ButterKnife.findById(getView(), R.id.name_lay);
        commonName = ButterKnife.findById(getView(), R.id.name);
        //账户昵称
        commonNickNameLayout = ButterKnife.findById(getView(), R.id.ninkname_lay);
        commonNickName = ButterKnife.findById(getView(), R.id.nickname);
        //帐号
        commonAccountLayout = ButterKnife.findById(getView(), R.id.account_number_lay);
        commonAccount = ButterKnife.findById(getView(), R.id.account_number);

        rootView = ButterKnife.findById(getView(), R.id.root_view);
    }

    //陌生人底部元素组件
    private void initStrangerBottomLayout(){
        if(getCommand().isFirstComeIn()) {
            strangerLayout = (LinearLayout) inflater.inflate(R.layout.friend_add, null);
            friendAddBtn = ButterKnife.findById(strangerLayout, R.id.friend_add_btn);
            friendAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().startRequestInfo();
                }
            });
            bottomLayout.addView(strangerLayout);
        }
    }

    //集团通讯录底部布局
    private void initMemberBottomLayout(){
        if(getCommand().isFirstComeIn()) {
            memberInfoLayout = (LinearLayout) inflater.inflate(R.layout.contact_bottom_addressbook_sublayout, null);
            bottomLayout.addView(memberInfoLayout);

            phoneLayout1 = ButterKnife.findById(memberInfoLayout, R.id.phone_layout_1);
            phoneLayout2 = ButterKnife.findById(memberInfoLayout, R.id.phone_layout_2);
            phoneLayout3 = ButterKnife.findById(memberInfoLayout, R.id.phone_layout_3);
            phoneLayout4 = ButterKnife.findById(memberInfoLayout, R.id.phone_layout_4);
            phoneLayout4.setVisibility(View.GONE);
            phoneLayout5 = ButterKnife.findById(memberInfoLayout, R.id.phone_layout_5);
            phoneLayout5.setVisibility(View.GONE);
            phoneLayouts = new LinearLayout[]{phoneLayout1, phoneLayout2, phoneLayout3, phoneLayout4, phoneLayout5};

            phone1 = ButterKnife.findById(phoneLayout1, R.id.phone_number_text);
            phone2 = ButterKnife.findById(phoneLayout2, R.id.phone_number_text);
            phone3 = ButterKnife.findById(phoneLayout3, R.id.phone_number_text);
            phone4 = ButterKnife.findById(phoneLayout4, R.id.phone_number_text);
            phone5 = ButterKnife.findById(phoneLayout5, R.id.phone_number_text);
            phoneTvs = new TextView[]{phone1, phone2, phone3, phone4, phone5};

            phoneIcon1 = ButterKnife.findById(phoneLayout1, R.id.common_phone);
            phoneIcon2 = ButterKnife.findById(phoneLayout2, R.id.common_phone);
            phoneIcon3 = ButterKnife.findById(phoneLayout3, R.id.common_phone);
            phoneIcon4 = ButterKnife.findById(phoneLayout4, R.id.common_phone);
            phoneIcon5 = ButterKnife.findById(phoneLayout5, R.id.common_phone);
            imageButtons = new ImageButton[]{phoneIcon1, phoneIcon2, phoneIcon3, phoneIcon4, phoneIcon5};

            phone_number_colon = ButterKnife.findById(phoneLayout1,R.id.address_book_phone_number_colon);
            phone_number_colon.setVisibility(View.VISIBLE);

            memberName = ButterKnife.findById(memberInfoLayout, R.id.address_book_name_text);
            departName = ButterKnife.findById(memberInfoLayout, R.id.address_book_department_text);
        }
    }

    /**
     * 初始化好友底部布局
     */
    private void initFriendBottomLayout(){
        //好友界面
        if(getCommand().isFirstComeIn()) {
            friendLayout = (LinearLayout) inflater.inflate(R.layout.contact_bottom_friend_sublayout, null);
            bottomLayout.addView(friendLayout);

            sendEncriptionMesage = ButterKnife.findById(friendLayout, R.id.send_encription_mesage);
            sendEncriptionMesage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().sendEncryptionMessage();
                }
            });

            callWithEncription = ButterKnife.findById(friendLayout, R.id.call_encription_phone);
            callWithEncription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().callWithEncryption();
                }
            });
        }
    }

    private boolean hideToolBar = false;

    private int dataType = -1;

    private final int stranger = 0;

    private final int friend = 1;

    private final int memberType = 2;

    private final int friendAndMember = 3;

    private final int accountNotFound = 4;

    @Override
    public void setDetailData(CommonDetailDto params) {
      if(!params.isExist()){//服务端搜索的数据
            //要加载添加好友的界面
            dataType = stranger;
            initStrangerBottomLayout();
            setStrangerComponentData(params.getServerActomaAccount());
        }else{
            if(params.isMember() && params.isFriend()){
                dataType = friendAndMember;
                initMemberBottomLayout();
                initFriendBottomLayout();
                setComponentData(params);
            }else{
                if(params.isFriend()){
                    dataType = friend;
                    initFriendBottomLayout();
                    setComponentData(params);
                } else if(params.isMember()){
                    initMemberBottomLayout();
                    String memberAccount = params.getMember().getAccount();
                    if(!ObjectUtil.stringIsEmpty(memberAccount)) {
                        dataType = memberType;
                        initFriendBottomLayout();
                    }else{
                        dataType = accountNotFound;
                    }
                    setComponentData(params);
                } else {//在本地存在数据 但不属于好友和集团  只是个群组成员
                    dataType = stranger;
                    initStrangerBottomLayout();
                    setNormalPerson(params.getActomaAccount());
                }
            }
        }
        hideToolBar();//隐藏更多按钮
    }

    //既不是好友也不是集团人员 普通群组成员人员信息
    private void setNormalPerson(ActomaAccount actomaAccount){
        if (ObjectUtil.objectIsEmpty(actomaAccount)) return;
        //顶部控件隐藏
        commonNickNameLayout.setVisibility(View.GONE);
        commonNickName.setVisibility(View.GONE);
        commonName.setVisibility(View.GONE);
        commonNameLayout.setVisibility(View.GONE);

        loadHeadImage(actomaAccount.getAccount());
        loadHeadIcon(actomaAccount.getAccount());
        if(ObjectUtil.stringIsEmpty(actomaAccount.getNickname())){
            commonShowName.setText(actomaAccount.showAccount());
            commonAccountLayout.setVisibility(View.GONE);
        }else{
            commonShowName.setText(actomaAccount.getNickname());
            commonAccountLayout.setVisibility(View.VISIBLE);
        }
        commonAccount.setText(actomaAccount.showAccount());
        //add by wal@xdja.com for 1482
//        friendAddBtn.setVisibility("1".equals(actomaAccount.getStatus()) ? View.GONE : View.VISIBLE);
      //  friendAddBtn.setVisibility("1".equals(actomaAccount.getStatus()) ? View.VISIBLE : View.GONE);
        //Start:add by wal@xdja.com for 4288
        if (!ObjectUtil.objectIsEmpty(friendAddBtn)){
            friendAddBtn.setVisibility( View.VISIBLE );//modify by lwl 3621
        }
        //end:add by wal@xdja.com for 4288
    }


    private void setComponentData(CommonDetailDto detailDto){
        if(dataType == friendAndMember){
            Avatar avatar = detailDto.getAvatar();
            if(!ObjectUtil.objectIsEmpty(avatar)) {
                loadHeadImage(getCommand().getAccount());
                loadHeadIcon(getCommand().getAccount());
            }
            setCommonComponentVisibility(detailDto);
            setCompanyComponent(detailDto);
        }else if(dataType == friend){
            loadHeadImage(getCommand().getAccount());
            loadHeadIcon(getCommand().getAccount());
            setCommonComponentVisibility(detailDto);
        }else if(dataType == memberType){
            loadHeadImage(getCommand().getAccount());
            loadHeadIcon(getCommand().getAccount());
            setCommonComponentVisibility(detailDto);
            setCompanyComponent(detailDto);
        }else if(dataType == accountNotFound){
            loadHeadImage(getCommand().getAccount());
            loadHeadIcon(getCommand().getAccount());
            setCommonComponentVisibility(detailDto);
            setCompanyComponent(detailDto);
        }
    }

    private void setCommonComponentVisibility(CommonDetailDto detailDto){
        ActomaAccount actomaAccount = detailDto.getActomaAccount();
        Friend friend = detailDto.getFriend();
        Member member = detailDto.getMember();

        String remark = "";
        String name = "";
        String nickname = "" ;
        //start:add by wal@xdja.com for 684
//        String account = getCommand().getAccount();
        String account = null;
        if (!ObjectUtil.objectIsEmpty(actomaAccount)){
            account = actomaAccount.showAccount();
        }
        //end:add by wal@xdja.com for 684

        if(!ObjectUtil.objectIsEmpty(friend)){
            remark = friend.getRemark();
        }
        if(!ObjectUtil.objectIsEmpty(member)){
            name = member.getName();
        }
        if(!ObjectUtil.objectIsEmpty(actomaAccount)){
            nickname = actomaAccount.getNickname();
        }
        if (!ObjectUtil.stringIsEmpty(remark)){
            commonShowName.setText(remark);
            if(!ObjectUtil.stringIsEmpty(name)){
                commonName.setText(name);
                commonNameLayout.setVisibility(View.VISIBLE);
            }
            if(!ObjectUtil.stringIsEmpty(nickname)){
                commonNickName.setText(nickname);
                commonNickNameLayout.setVisibility(View.VISIBLE);
            }
            if(!ObjectUtil.stringIsEmpty(account)) {
                commonAccount.setText(account);
                commonAccountLayout.setVisibility(View.VISIBLE);
            }
        }else{
            if(!ObjectUtil.stringIsEmpty(name)){
                commonShowName.setText(name);
                commonNameLayout.setVisibility(View.GONE);
                if(!ObjectUtil.stringIsEmpty(nickname)){
                    commonNickName.setText(nickname);
                    commonNickNameLayout.setVisibility(View.VISIBLE);
                }
                if(!ObjectUtil.stringIsEmpty(account)) {
                    commonAccount.setText(account);
                    commonAccountLayout.setVisibility(View.VISIBLE);
                }
            }else{
                if(!ObjectUtil.stringIsEmpty(nickname)){
                    commonShowName.setText(nickname);
                    commonNickNameLayout.setVisibility(View.GONE);
                }else{
                    commonShowName.setText(account);
                }
                if(!ObjectUtil.stringIsEmpty(account)) {
                    commonAccount.setText(account);
                    commonAccountLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setCompanyComponent(CommonDetailDto detailDto){
        Department department = detailDto.getDepartment();
        Member member = detailDto.getMember();
        //设置手机号
        String[] phones = getPhones(member.getMobile());
        if (phones != null && phones.length > 0) {
            //手机号最多为5个,终端显示三个
            for (int i = 0; i < phones.length; i++) {
                if(i <= 2){
                    String phone = phones[i];
                    OnCallPhoneListener onCallPhoneListener = new OnCallPhoneListener(phone);

                    phoneLayouts[i].setVisibility(View.VISIBLE);
                    phoneTvs[i].setText(phone);
                    phoneLayouts[i].setOnClickListener(onCallPhoneListener);
                    imageButtons[i].setOnClickListener(onCallPhoneListener);
                }

            }
        }
        if(!ObjectUtil.objectIsEmpty(member)) {
            memberName.setText(member.getName());
        }
        if(!ObjectUtil.objectIsEmpty(department)) {
            departName.setText(department.getDepartmentName());
        }
    }





    //设置添加好友界面元素内容
    private void setStrangerComponentData(ResponseActomaAccount actomaAccount){
        if (ObjectUtil.objectIsEmpty(actomaAccount)) return;
        //顶部控件隐藏
        commonNickNameLayout.setVisibility(View.GONE);
        commonNickName.setVisibility(View.GONE);
        commonName.setVisibility(View.GONE);
        commonNameLayout.setVisibility(View.GONE);
        //Note: 每次重新加载头像数据
        loadHeadImage(actomaAccount);
        if(ObjectUtil.stringIsEmpty(actomaAccount.getNickname())){
            commonShowName.setText(actomaAccount.showAccount());
            commonAccountLayout.setVisibility(View.GONE);
        }else{
            commonShowName.setText(actomaAccount.getNickname());
            commonAccountLayout.setVisibility(View.VISIBLE);
        }
        commonAccount.setText(actomaAccount.showAccount());
       // friendAddBtn.setVisibility("1".equals(actomaAccount.getStatus()) ? View.VISIBLE: View.GONE
       // );
        friendAddBtn.setVisibility( View.VISIBLE );//modify by lwl 3621
        commonAvatar.loadImage(actomaAccount.getThumbnailId(),true,R.drawable.img_avater_40);
    }



    /**
     * 隐藏ToolBar按钮
     */
    private void hideToolBar(){
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //[S]modify by lixiaolong on 20160921. fix memory leak. review by wangchao1.
        getActivity().getMenuInflater().inflate(R.menu.menu_common_detail, menu);
//        if(dataType == stranger){
//            hideToolBar = true;
////            hideToolBar();
//        }else if(dataType == friend || dataType == friendAndMember ){
//            hideToolBar = false;
//            getActivity().getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
//        }else if(dataType == memberType){
//            hideToolBar = false;
//            getActivity().getMenuInflater().inflate(R.menu.menu_company_detail, menu);
//        }else if(dataType == accountNotFound){
//            hideToolBar = true;
////            hideToolBar();
//            //hideToolBar = true;
//            //getActivity().getMenuInflater().inflate(R.menu.menu_company_detail, menu);
//        }
        //[E]modify by lixiaolong on 20160921. fix memory leak. review by wangchao1.
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //[S]modify by lixiaolong on 20160921. fix memory leak. review by wangchao1.
        if (dataType == stranger) {
            hideMenuItemById(menu, R.id.action_more);
        } else if (dataType == friend || dataType == friendAndMember) {
            hideMenuItemById(menu, R.id.action_add);
        } else if (dataType == memberType) {
            hideMenuItemById(menu, R.id.action_remark, R.id.action_del);
        } else if (dataType == accountNotFound) {
            hideMenuItemById(menu, R.id.action_more);
        }
//        if (hideToolBar) {
//            for (int i = 0; i < menu.size(); i++) {
//                MenuItem item = menu.getItem(i);
//                if(item.getItemId() == R.id.action_more){
//                    item.setVisible(false);
//                }
//            }
//        }
        //[E]modify by lixiaolong on 20160921. fix memory leak. review by wangchao1.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add) {
            getCommand().toolBarAddFriend();
        }else if (item.getItemId() == R.id.action_remark) {
            getCommand().editRemark();
        } else if (item.getItemId() == R.id.action_del) {
            showDialog();
        }
        return true;
    }

    //[S]add by lixiaolong on 20160921. fix memory leak. review by wangchao1.
    private void hideMenuItemById(Menu menu, int... ids){
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                hideMenuItemById(item.getSubMenu(), ids);
            }
            boolean isVisible = true;
            for (int id : ids) {
                if (item.getItemId() == id) {
                    isVisible = false;
                    break;
                }
            }
            item.setVisible(isVisible);
        }
    }
    //[E]add by lixiaolong on 20160921. fix memory leak. review by wangchao1.

    private void showDialog(){
        final CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.setTitle(getStringRes(R.string.delete_confirm_title)).setMessage(getStringRes(R.string.content_dialog_message))
                .setNegativeButton(getStringRes(R.string.content_no), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                }).setPositiveButton(getStringRes(R.string.content_yes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                getCommand().deleteFriend();
            }
        }).show();
    }
    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    private class OnCallPhoneListener implements View.OnClickListener{

        private String phoneNumber;
        private OnCallPhoneListener(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        public void onClick(View v) {
            getCommand().callPhone(phoneNumber);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.contact_detail;
    }


    private void loadHeadImage(final ResponseActomaAccount account){
        final Avatar avatarBean = new AvaterService().queryByAccount(account.getAccount());
        //点击查看大图
        commonAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先从本地查找如果存在直接加载本地
                if(ObjectUtil.objectIsEmpty(avatarBean)){
                    commonAvatar.showImageDetail(account.getAvatarId());
                } else {
                    String avatarHash = avatarBean.getAvatar();
                    if (ObjectUtil.stringIsEmpty(avatarHash)){
                        commonAvatar.showImageDetail(account.getAvatarId());
                    }else{
                        commonAvatar.showImageDetail(avatarBean.getAvatar());
                    }
                }
            }
        });
    }

    @Override
    public void loadingDialogController(boolean open,String msg) {
        if (open){
            showCommonProgressDialog(msg);
        }else {
            dismissCommonProgressDialog();
        }
    }

    @Override
    public void showCommonProgressDialog(String msg) {
        super.showCommonProgressDialog(msg,false,false);//modify by lwl 2997
    }

    //modify by lwl start  load correct imgurl
    private void loadHeadImage(final String account){
        if(TextUtils.isEmpty(account)){
            commonAvatar.setImageResource(R.drawable.img_avater_40);
            return;
        }
        final Avatar avatarBean = new AvaterService().queryByAccount(account);
        if (!ObjectUtil.objectIsEmpty(avatarBean)) {
            commonAvatar.showImageDetailAble(avatarBean.getAvatar());
            commonAvatar.loadImage(avatarBean.getThumbnail(),true,R.drawable.img_avater_40);
        }else{
            commonAvatar.setImageResource(R.drawable.img_avater_40);
        }
    }

    private void loadHeadIcon(String account){
//        if(TextUtils.isEmpty(account)){
//            return;
//        }
//        Avatar avatar = ContactModuleService.getAvatar(getActivity(), account);
//        if(ObjectUtil.objectIsEmpty(avatar))return ;
//        if (ObjectUtil.stringIsEmpty(avatar.getAvatar())) return;
//        Uri uri = Uri.parse(avatar.getAvatar());
//        commonAvatar.setImageURI(uri);
    }
    //modify by lwl end  load correct imgurl
    private String[] getPhones(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        } else {
            return phone.split("#");
        }
    }

    //[S]YangShaoPeng<mailto://ysp@xdja.com> 2016-09-05 add. fix bug #3580 . review by LiXiaoLong.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(commonAvatar != null) {
            commonAvatar.setImageResource(0);
            commonAvatar.setImageBitmap(null);
            commonAvatar.setImageDrawable(null);
        }
        if(rootView != null) {
            rootView.setBackgroundResource(0);
            rootView.setBackground(null);
            GcMemoryUtil.clearMemory(rootView);
        }
    }
    //[E]YangShaoPeng<mailto://ysp@xdja.com> 2016-09-05 add. fix bug #3580 . review by LiXiaoLong.

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.detail_info_title);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
