package com.xdja.contact.usereditor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.data.CommonHeadBitmap;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.contact.presenter.activity.MultiDelPresenter;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.exception.ATUpdateGroupNameException;
import com.xdja.contact.exception.ATUploadGroupAvatarException;
import com.xdja.contact.exception.ContactServiceException;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.http.response.group.ModifyNickNameResponse;
import com.xdja.contact.http.response.group.ResponseUpdateGroupName;
import com.xdja.contact.http.response.group.ResponseUploadGroupAvatar;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.usereditor.adapter.ContactEditorAdapter;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.view.CommonInputDialog;
import com.xdja.contact.view.GridViewForScroll;
import com.xdja.frame.data.net.OkHttpsBuilder;
import com.xdja.frame.data.net.ServiceGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 通用人员编辑控件
 * Created by hkb.
 * 2015/7/9/0009.
 *
 *
 * 2016-01-28 wanghao 重构
 */
public class ContactEditorFragment extends Fragment implements View.OnClickListener, IContactEvent {

    public static final String KEY_GROUP_ID = "groupId";

    //人员编辑组件
    private GridViewForScroll editorGridView;

    private View groupNameView;

    private View groupAvatarView;

    private View myNickNameView;

    private TextView groupNameTv;

    private TextView nickNameTv;

    private CircleImageView avatarIv;
    //耗时动画提示框
    private CustomDialog progressDialog;

    private ImageView arrow1, arrow3;






    private List<UserInfo> userInfoList;

    private String loginAccount;

    private String currentGroupId;          //群组ID

    private Group currentGroup;

    private GroupMember currentGroupMember;

    private ContactEditorAdapter editorAdapter;

    private View allMembersView;  // add by ysp@xdja.com

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.getUtils().i("ContactEditorFragment----------------onCreate-----------------");
        registerBroadcast();
        FireEventUtils.addGroupListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.getUtils().i("ContactEditorFragment----------------onCreateView-----------------");
        View view = inflater.inflate(R.layout.contact_editor_fragment, null);
        initView(view);
       // setOnTouchListener();//remove for modify delete mode
        return view;
    }

    private void initView(View parent) {
        editorGridView = (GridViewForScroll) parent.findViewById(R.id.editor_gv);
        groupNameView = parent.findViewById(R.id.group_name_layout);
        groupAvatarView = parent.findViewById(R.id.group_avatar_layout);
        myNickNameView = parent.findViewById(R.id.group_nickname_layout);
        groupNameTv = (TextView) parent.findViewById(R.id.group_name_content);
        avatarIv = (CircleImageView) parent.findViewById(R.id.group_avatar);
        nickNameTv = (TextView) parent.findViewById(R.id.group_nickname_content);
        arrow1 = (ImageView) parent.findViewById(R.id.right_arrow1);
        arrow3 = (ImageView) parent.findViewById(R.id.right_arrow3);
        myNickNameView.setOnClickListener(this);
        allMembersView = parent.findViewById(R.id.show_all_group_members);  //add by ysp@xdja.com
    }

    //[S]remove for modify delete mode
   /* private void setOnTouchListener(){
        editorGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!ObjectUtil.objectIsEmpty(userInfoList) && !ObjectUtil.objectIsEmpty(editorAdapter) && editorAdapter.isEditMode()) {
                    final int position = editorGridView.pointToPosition((int) event.getX(), (int) event.getY());
                    if (position == AdapterView.INVALID_POSITION) {
                        editorAdapter.exitEditModel();
                        return true;
                    }
                }
                return false;
            }
        });
    }*/
    //[E]remove for modify delete mode

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.getUtils().i("ContactEditorFragment----------------onResume-----------------");
        template();
    }

    private void template() {
        try {
            initParams();
        } catch (ContactServiceException e) {
            LogUtil.getUtils().e("ContactEditorFragment  template error:"+e.getMessage());
        }
        initData();
        initEditorGridViewAdapter();
    }




    /**
     * <pre>
     * 2016-01-28 wanghao
     * 设置成员参数(需要在Fragment被加入布局前设置初始化完成)
     * </pre>
     */
    public void initParams() throws ContactServiceException {
        this.currentGroupId = getArguments().getString(KEY_GROUP_ID);
        this.currentGroup = GroupInternalService.getInstance().queryByGroupId(currentGroupId);
        this.loginAccount = GroupUtils.getCurrentAccount(getActivity());
        this.currentGroupMember = GroupInternalService.getInstance().queryMember(currentGroupId, loginAccount);
        convertGroupMembers2UserInfos();
    }

    /**<pre>
     * 2016-01-28 wanghao
     * 群成员转换成编辑头像对象
     * 并且把群主放在集合的首个位置
     * </pre>
     */
    private void convertGroupMembers2UserInfos(){
        List<UserInfo> userInfos = new ArrayList<>();
        //这里本来需要通过map来过滤数据但是群内我们认为成员每个都是唯一的所以这里不再需要map过滤
        List<UserInfo> dataSource = new GroupExternalService(getActivity()).getUserInfosByGroupId(currentGroupId);
        for(UserInfo userInfo : dataSource){
            //start:add by wal@xdja.com for 3312,3497
            if (userInfo.getAccount().equals(loginAccount)){
                AccountBean currentAccountBean= AccountServer.getAccount();
                if (currentAccountBean!=null){
                    String url =currentAccountBean.getThumbnail();
                    String nickName =currentAccountBean.getNickname();
                    String alias =currentAccountBean.getAlias();//add by wal@xdja.com for 4554
                    userInfo.getAvatarBean().setThumbnail(url);
                    userInfo.setNickName(nickName);
                    userInfo.setAccountNickname(nickName);
                    userInfo.setAlias(alias);//add by wal@xdja.com for 4554
                }
            }
            //[S] add by ysp, fix bug #8525
            if(userInfos.contains(userInfo)) {
                continue;
            }
            //[S] add by ysp, fix bug #8525
            //end:add by wal@xdja.com for 3312,3497
            if(userInfo.getAccount().equals(currentGroup.getGroupOwner())){
                userInfos.add(0,userInfo);
            }else{
                userInfos.add(userInfo);
            }
        }
        this.userInfoList = userInfos;
        //start:add by wal@xdja.com for 2933
        String title = getActivity().getResources().getString(R.string.group_chat_number_title, userInfoList.size());
        getActivity().setTitle(title);
        //end:add by wal@xdja.com for 2933
    }


    private void initData(){
        //只有群主有权修改群组名称、群组头像
        String groupOwner = currentGroup.getGroupOwner();
        allMembersView.setOnClickListener(this); //add by ysp@xdja.com 群主和成员都可以查看全部群成员
        if(!ObjectUtil.objectIsEmpty(groupOwner) && groupOwner.equals(loginAccount)){
            arrow1.setVisibility(View.VISIBLE);
            arrow3.setVisibility(View.VISIBLE);
            groupNameView.setOnClickListener(this);
            groupAvatarView.setOnClickListener(this);
        }else{
            arrow1.setVisibility(View.GONE);
            arrow3.setVisibility(View.GONE);
        }
        loadComponentData();
    }

    private void initEditorGridViewAdapter(){
        editorAdapter = new ContactEditorAdapter(getActivity(),userInfoList,currentGroup);
        editorGridView.setAdapter(editorAdapter);
        editorAdapter.setOnAddUserBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> accounts = new HashSet<String>();
                for (UserInfo member : userInfoList) {
                    if(!ObjectUtil.stringIsEmpty(member.getAccount())){
                        accounts.add(member.getAccount());
                    }
                }
                //modify by ysp@xdja.com start
                int groupMemberMax = PreferenceUtils.getGroupMemberLimitConfiguration();
                if(accounts.size() >= groupMemberMax) {
                    XToast.show(getActivity(), String.format(getResources().getString(R.string.group_max_member), groupMemberMax));
                } else {
                    GroupUtils.launchChooseContactActivity(getActivity(), currentGroupId, new ArrayList<String>(accounts));
                }
                //modify by ysp@xdja.com end
            }
        });
    }


    /**
     * 加载界面上填充的数据
     */
    private void loadComponentData() {
        if (!ObjectUtil.objectIsEmpty(currentGroup)) {
            if (ObjectUtil.stringIsEmpty(currentGroup.getGroupName())) {
                groupNameTv.setText(getActivity().getString(R.string.no_name));
            } else {
                groupNameTv.setText(currentGroup.getGroupName());
            }
        }
        if(!ObjectUtil.objectIsEmpty(currentGroupMember)){
            nickNameTv.setText(currentGroupMember.getNickName());
        }
        if(!ObjectUtil.objectIsEmpty(currentGroup)) {
            GroupUtils.loadAvatarToImgView(avatarIv, currentGroup, R.drawable.group_avatar_40);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == groupNameView) {
            processModifyGroupName();
        } else if (v == myNickNameView) {
            processModifyNickname();
        } else if (v == groupAvatarView) {
            try {
                Intent intent = new Intent("com.xdja.actoma.ACTION_SET_AVATAR");
                intent.addCategory("com.xdja.intent.category.SET_AVATAR");
                getActivity().startActivity(intent);
            } catch (Exception e) {
            }
        }else if(v == allMembersView) {
            //跳转到所有群成员界面。 add by ysp@xdja.com
            startActivity(new Intent(getActivity(), MultiDelPresenter.class)
                    .putExtra(MultiDelPresenter.GROUP_ID, getArguments().getString(ContactEditorFragment.KEY_GROUP_ID))
                    .putExtra(MultiDelPresenter.OPEN_TYPE, MultiDelPresenter.ALL_GROUP_MEMBER));
        }
    }

    /**
     * 修改群名称
     */
    private void processModifyGroupName() {
        String groupName = groupNameTv.getText().toString();
        new CommonInputDialog(getActivity(), groupName, CommonInputDialog.TYPE_GROUP_NAME,
                new CommonInputDialog.InputDialogInterface() {
                    @Override
                    public void onOk(final String groupName, final CustomDialog dialog) {
                        //start:add by wal@xdja,com for 3792
//                        showProgressDialog(getActivity().getString(R.string.updating_group_name));
                        showDialogHandler.sendEmptyMessage(UPDATE_GROUP_NAME);
                        //end:add by wal@xdja,com for 3792
                        try {
                            GroupHttpServiceHelper.updateGroupName(currentGroupId, groupName, new IModuleHttpCallBack() {
                                @Override
                                public void onFail(HttpErrorBean httpErrorBean) {
                                    dismissProgressDialog();
                                    dialog.getmPositiveButton().setClickable(true);
                                    XToast.show(ActomaController.getApp(), R.string.update_group_name_error);
                                }

                                @Override
                                public void onSuccess(String body) {
                                    try {
                                        ResponseUpdateGroupName response = JSON.parseObject(body, ResponseUpdateGroupName.class);
                                        currentGroup.setGroupName(groupName);
                                        currentGroup.setNameFullPY(response.getGroupNamePinyin());
                                        currentGroup.setNamePY(response.getGroupNamePy());
                                        //保存到数据库
                                        //add by lwl  start
                                        FireEventUtils.fireGroupNameUpdateEvent(currentGroup.getGroupId(),currentGroup.getGroupName());
                                        //add by lwl end
                                        boolean result = GroupInternalService.getInstance().updateGroup(currentGroup);
                                        if (result) {
                                            //Note: 对于群主端目前不在发送变更提示
                                            //FireEventUtils.fireGroupNameUpdateEvent(currentGroupId,groupName);
                                            List<Group> updateGroups = new ArrayList<Group>();
                                            updateGroups.add(currentGroup);
                                            FireEventUtils.pushFireUpdateGroupList(updateGroups);
                                            loadComponentData();
                                        } else {
                                            XToast.show(ActomaController.getApp(), R.string.update_group_name_error);
                                        }
                                    } catch (Exception e) {
                                        LogUtil.getUtils().e("Actoma contact ContactEditorAdapter processModifyGroupName.onSuccess:修改群名称，解析Json返回数据异常");
                                        XToast.show(ActomaController.getApp(), R.string.update_group_name_error);
                                    } finally {
                                        dialog.getmPositiveButton().setClickable(true);
                                        dismissProgressDialog();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onErr() {
                                    dismissProgressDialog();
                                }
                            });
                        } catch (ATUpdateGroupNameException e) {
                            XToast.show(ActomaController.getApp(), R.string.update_group_name_error);
                            LogUtil.getUtils().e("Actoma contact ContactEditorAdapter processModifyGroupName.onOk:修改群名称，群id或者群名称为空");
                        } finally {
                            dialog.getmPositiveButton().setClickable(true);
                            dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onCancel() {}
                });
    }

    /**
     * 修改群昵称
     */
    private void processModifyNickname() {
        String groupNickName = "";
        if(!ObjectUtil.objectIsEmpty(currentGroupMember)){
            groupNickName = currentGroupMember.getNickName();
        }
        new CommonInputDialog(getActivity(), groupNickName, CommonInputDialog.TYPE_GROUP_NICKNAME,
                new CommonInputDialog.InputDialogInterface() {
                    @Override
                    public void onOk(final String nickName, final CustomDialog dialog) {
                        //start:add by wal@xdja,com for 3792
//                        showProgressDialog(getActivity().getString(R.string.updating_nickname));
                        showDialogHandler.sendEmptyMessage(UPDATE_GROUP_NICKNAME);
                        //end:add by wal@xdja,com for 3792
                        try {
                            GroupHttpServiceHelper.updateNickName(currentGroupId, nickName, new IModuleHttpCallBack() {
                                @Override
                                public void onFail(HttpErrorBean httpErrorBean) {
                                    dismissProgressDialog();
                                    dialog.getmPositiveButton().setClickable(true);
                                    XToast.show(ActomaController.getApp(), R.string.update_nickname_error);
                                }

                                @Override
                                public void onSuccess(String body) {
                                    try {
                                        ModifyNickNameResponse response = JSON.parseObject(body, ModifyNickNameResponse.class);
                                        currentGroupMember.setNickName(nickName);
                                        currentGroupMember.setNickNameFullPY(response.getNicknamePinyin());
                                        currentGroupMember.setNickNamePY(response.getNicknamePy());
                                        boolean result = GroupInternalService.getInstance().updateGroupMember(currentGroupMember);
                                        if (result) {
                                            List<GroupMember> updateMembers = new ArrayList<>();
                                            updateMembers.add(currentGroupMember);
                                            FireEventUtils.fireUpdateMemberEvent(updateMembers, null);
                                            loadComponentData();
                                        } else {
                                            XToast.show(ActomaController.getApp(), R.string.update_nickname_error);
                                        }
                                    } catch (Exception e) {
                                        LogUtil.getUtils().e("Actoma contact ContactEditorAdapter processModifyNickname:群昵称修改返回数据json解析出错");
                                        XToast.show(ActomaController.getApp(), R.string.update_nickname_error);
                                    } finally {
                                        dismissProgressDialog();
                                        dialog.getmPositiveButton().setClickable(false);
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onErr() {
                                    dismissProgressDialog();
                                }
                            });
                        } catch (ATUpdateGroupNameException e) {
                            XToast.show(ActomaController.getApp(), R.string.update_nickname_error);
                            LogUtil.getUtils().e("Actoma contact ContactEditorAdapter processModifyNickname:修改群昵称，群id为空");
                        } finally {
                            dialog.getmPositiveButton().setClickable(true);
                            dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onCancel() {}
                });
    }

    @Override
    public void onEvent(int event, Object param1, Object param2, Object param3) {
        switch (event) {
            case EVENT_GROUP_QUIT:  //群主移除成员  刷新Adapter界面
            case EVENT_MEMBER_ADDED:
                //add by lwl 1857 start
                if(param3 instanceof  Integer){
                    if(EVENT_GROUP_QUIT==event&&(IContactEvent.REMOVED==(int)param3||IContactEvent.DISMISS==(int)param3)){
                        String changeGroupId = (String)param1;
                        if(GroupUtils.isGroupOwner(getActivity(), changeGroupId) == false
                                && currentGroupId.compareTo(changeGroupId) == 0) {
                             getActivity().finish();  //被移除的群成员退出群信息界面
                        }

                        return;
                    }
                }
                //add by lwl 1857 end
                convertGroupMembers2UserInfos();
                editorAdapter.setDataSource(userInfoList);
                break;
            case EVENT_MEMBER_UPDATED: //群成员修改昵称  刷新Adapter界面
                template();
                /*convertGroupMembers2UserInfos();
                editorAdapter.setDataSource(userInfoList);*/
                break;
            case EVENT_GROUP_LIST_REFRESH:
            case EVENT_FRIEND_UPDATE_REMARK:
                template();
                break;

        }
    }








    /**
     * 上传头像
     * @param headImg
     */
    @Subscribe
    public void uploadAvatarBitmap(final CommonHeadBitmap headImg) {
        groupAvatarView.setEnabled(false);
        showProgressDialog(getActivity().getString(R.string.uploading_group_avatar));
        uploadImg(headImg.getBm())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, String>>() {
            @Override
            public void onCompleted() {
               // dismissProgressDialog(); remove by lwl 3350
                groupAvatarView.setEnabled(true);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().e("ContactEditorFragment  uploadAvatarBitmap error:"+e.getMessage());
                //modify by lwl start 2529
                dismissProgressDialog();
                groupAvatarView.setEnabled(true);
                if (e instanceof java.net.SocketTimeoutException || e instanceof java.net.ConnectException) {
					//ysp@xdja.com 20161020 fixed bug 5109.
                    XToast.show(ActomaController.getApp().getApplicationContext(), ActomaController.getApp().getApplicationContext().getString(R.string.contact_time_out));
                }else{
					//ysp@xdja.com 20161020 fixed bug 5109.
                    XToast.show(ActomaController.getApp().getApplicationContext(), ActomaController.getApp().getApplicationContext().getString(R.string.upload_group_avatar_error));
                }
                //modify by lwl end

            }

            @Override
            public void onNext(Map<String, String> stringStringMap) {
                CommonHeadBitmap commonHeadBitmap=new CommonHeadBitmap();
                commonHeadBitmap.setAvatarId(stringStringMap.get(AVATAR_ID));
                commonHeadBitmap.setThumbnailId(stringStringMap.get(THUMBNAIL_ID));
                uploadAvatar(commonHeadBitmap);

            }
        });
    }
    public void uploadAvatar(final CommonHeadBitmap headImg) {
//        groupAvatarView.setEnabled(false);
//        showProgressDialog(getActivity().getString(R.string.uploading_group_avatar));
        try {
            GroupHttpServiceHelper.uploadGroupAvatar(currentGroupId, headImg, new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    dismissProgressDialog();
                    groupAvatarView.setEnabled(true);
                    XToast.show(getActivity(), getActivity().getString(R.string.upload_group_avatar_error));
                }

                @Override
                public void onSuccess(String body) {
                    try {
                        ResponseUploadGroupAvatar response = JSON.parseObject(body, ResponseUploadGroupAvatar.class);
                        currentGroup.setAvatar(headImg.getAvatarId());
                        currentGroup.setThumbnail(headImg.getThumbnailId());
//                        currentGroup.setAvatarHash(response.getAvatarHash());
//                        currentGroup.setThumbnailHash(response.getThumbnailHash());
                        boolean result = GroupInternalService.getInstance().updateGroup(currentGroup);
                        if (result) {
                            List<Group> updateGroups = new ArrayList<Group>();
                            updateGroups.add(currentGroup);
                            FireEventUtils.pushFireUpdateGroupList(updateGroups);
                            loadComponentData();
                        } else {
                            XToast.show(getActivity(), R.string.upload_group_avatar_error);
                        }
                    } catch (Exception e) {
                        XToast.show(getActivity(), R.string.upload_group_avatar_error);
                    } finally {
                        groupAvatarView.setEnabled(true);
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onErr() {
                    dismissProgressDialog();
                }
            });
        } catch (ATUploadGroupAvatarException e) {
            groupAvatarView.setEnabled(true);
            dismissProgressDialog();
            LogUtil.getUtils().e("群组上传头像包装请求体数据出错");
            XToast.show(getActivity(), R.string.upload_group_avatar_error);
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getMainProvider().unregister(this);
        FireEventUtils.removeGroupListener(this);
        if(!ObjectUtil.objectIsEmpty(broadcastReceiver)){
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    //start:add by wal@xdja,com for 3792
    private static final int UPDATE_GROUP_NICKNAME = 0x11;
    private static final int UPDATE_GROUP_NAME = 0x12;

    private static class ContactEditorHandler extends Handler{
        WeakReference<ContactEditorFragment> mFragment;
        ContactEditorHandler(ContactEditorFragment fragment){
            mFragment = new WeakReference<ContactEditorFragment>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            ContactEditorFragment fragment = mFragment.get();
            if(fragment != null) {
                switch (msg.what) {
                    case UPDATE_GROUP_NICKNAME:
                        fragment.showProgressDialog(fragment.getActivity().getString(R.string.updating_nickname));
                        break;
                    case UPDATE_GROUP_NAME:
                        fragment.showProgressDialog(fragment.getActivity().getString(R.string.updating_group_name));
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        }

    }
    private ContactEditorHandler showDialogHandler = new ContactEditorHandler(this);
    //end:add by wal@xdja,com for 3792


    private void showProgressDialog(String msg) {
        showProgressDialog(msg, false, true);//modify by wal@xdja,com for 3792
    }

    /**
     * 显示耗时动画
     */
    private void showProgressDialog(String msg, boolean isTouch, boolean onKey) {
        if (progressDialog == null) {
            progressDialog = new CustomDialog(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_progress_dialog, null);
            progressDialog.setView(view);
        }
        //填写标题
        View view = progressDialog.getView();
        if (view != null) {
            TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
            messageView.setText(msg);
        }
        progressDialog.setCanceledOnTouchOutside(isTouch);
        progressDialog.setCancelable(onKey);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    /**
     * 取消耗时动画
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);
        //intentFilter.addAction(RegisterActionUtil.ACTION_REMARK_UPDATE);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        BusProvider.getMainProvider().register(this);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(RegisterActionUtil.ACTION_REFRESH_LIST.equals(intent.getAction())){
                editorAdapter.notifyDataSetChanged();
            }/*else if(RegisterActionUtil.ACTION_REMARK_UPDATE.equals(intent.getAction())){
                template();
            }*/
        }
    };

    public Observable<Map<String, String>> uploadImg(Bitmap p){
      return compressBitmap2jpg(p)
                //分别上传压缩所得的原图和缩略图
                .concatMap(new Func1<ImgCompressResult, Observable<Map<String, String>>>() {
                    @Override
                    public Observable<Map<String, String>> call(ImgCompressResult imgCompressResult) {
                        return Observable.zip(uploadImgString(imgCompressResult.getImgFile()), uploadImgString(imgCompressResult.getThumbnailImgFile()),
                                new Func2<String, String, Map<String, String>>() {
                                    @Override
                                    public Map<String, String> call(String s, String s2) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put(AVATAR_ID, s);
                                        map.put(THUMBNAIL_ID, s2);
                                        return map;
                                    }
                                });

                    }
                });
    }
    public Observable<Response<Map<String, String>>> uploadImgMap(@NonNull File imgFile) {
        //String url = "http://11.12.112.218:80/upload";
        String url = PreferencesServer.getWrapper(getActivity()).gPrefStringValue("fastDfs");
        if (url.contains("upload")) {
            int upload = url.indexOf("upload");
            url = url.substring(0, upload);
        }

        final String fUrl = url;
        //add by lwl start setting timeout
        OkHttpClient.Builder build=new OkHttpClient.Builder();
        build.connectTimeout(OkHttpsBuilder.CONN_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
            .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS);
        ServiceGenerator serviceGenerator=new ServiceGenerator(build,url);
        //add by lwl end setting timeout
        final UserInfoRestApi service= serviceGenerator.createService(UserInfoRestApi.class);
        RequestBody fileBody = null;
        if (imgFile != null) {
            fileBody = RequestBody.create(null, imgFile);
        }
        return service.uploadFile(fileBody)
                .map(new Func1<Response<Map<String, String>>, Response<Map<String, String>>>() {
                    @Override
                    public Response<Map<String, String>> call(Response<Map<String, String>> mapResponse) {
                        Map<String, String> body = mapResponse.body();
                        if (body != null) {
                            String fileid = body.get(FILEID);
                            if (fileid != null) {
                                body.put(FILEID, fUrl + "download/" + fileid);
                               // body.put(FILEID,  fileid);
                            }
                        }
                        return mapResponse;
                    }
                });
    }
    public Observable<String> uploadImgString(@NonNull File imgFile) {
        return uploadImgMap(imgFile)
                .map(new Func1<Response<Map<String, String>>, String>() {
                    @Override
                    public String call(Response<Map<String, String>> mapResponse) {
                        return mapResponse.body().get(FILEID);
                    }
                });
    }
    public static final String FILEID = "fileid";
    public static final String AVATAR_ID = "avatarId";
    public static final String THUMBNAIL_ID = "thumbnailId";
    public interface UserInfoRestApi {

        @Multipart
        @POST("upload")
        Observable<Response<Map<String,String>>> uploadFile(
                @Part("file; filename=\"111.jpg") RequestBody file
        );

    }
//    public class ServiceGenerator {
//
//        public String API_BASE_URL;
//
//        private Retrofit.Builder retrofitBuilder;
//
//        private OkHttpClient.Builder okHttpBuilder;
//
//        public ServiceGenerator(@NonNull OkHttpClient.Builder builder, @NonNull String baseUrl) {
//            this.okHttpBuilder = builder;
//            this.API_BASE_URL = baseUrl;
//        }
//
//        public void resetService(@NonNull String baseUrl){
//            this.retrofitBuilder = null;
//            this.API_BASE_URL = baseUrl;
//        }
//
//        public <S> S createService(Class<S> serviceClass, String baseUrl) {
//
//            if (this.retrofitBuilder == null) {
//                if (this.okHttpBuilder == null) {
//                    throw new NetworkException("OkhttpBuilder为空");
//                }
//                OkHttpClient client = this.okHttpBuilder.build();
//                this.retrofitBuilder = new Retrofit.Builder()
//                        .baseUrl(baseUrl)
//                        .client(client)
//                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create());
//            } else {
//                this.retrofitBuilder.baseUrl(baseUrl);
//            }
//
//            Retrofit retrofit = retrofitBuilder.build();
//            return retrofit.create(serviceClass);
//        }
//
//        public <S> S createService(Class<S> serviceClass) {
//            return createService(serviceClass, API_BASE_URL);
//        }
//    }

    public Observable<ImgCompressResult> compressBitmap2jpg(@NonNull final Bitmap bitmap) {
        return Observable.create(new Observable.OnSubscribe<ImgCompressResult>() {
            @Override
            public void call(Subscriber<? super ImgCompressResult> subscriber) {
                try {
                    Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, 150, 150);
                    subscriber.onNext(new ImgCompressResult(
                            compressBitmap2jpg(getContext(), bitmap, "image"),
                            compressBitmap2jpg(getContext(), thumbnailBitmap, "thumbnail_image")));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    public class ImgCompressResult {
        private File imgFile;
        private File thumbnailImgFile;

        public ImgCompressResult(File imgFile, File thumbnailImgFile) {
            this.imgFile = imgFile;
            this.thumbnailImgFile = thumbnailImgFile;
        }

        public File getImgFile() {
            return imgFile;
        }

        public void setImgFile(File imgFile) {
            this.imgFile = imgFile;
        }

        public File getThumbnailImgFile() {
            return thumbnailImgFile;
        }

        public void setThumbnailImgFile(File thumbnailImgFile) {
            this.thumbnailImgFile = thumbnailImgFile;
        }
    }
    public  File compressBitmap2jpg(Context context,Bitmap bitmap, String fileName) {
        File file = new File(context.getExternalCacheDir().getPath() + "/" + fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            LogUtil.getUtils().e("ContactEditorFragment  compressBitmap2jpg FileNotFoundException:"+e.getMessage());
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            LogUtil.getUtils().e("ContactEditorFragment  compressBitmap2jpg IOException flush:"+e.getMessage());
        }
        try {
            fOut.close();
        } catch (IOException e) {
           LogUtil.getUtils().e("ContactEditorFragment  compressBitmap2jpg IOException close:"+e.getMessage());
        }
        return file;
    }


}
