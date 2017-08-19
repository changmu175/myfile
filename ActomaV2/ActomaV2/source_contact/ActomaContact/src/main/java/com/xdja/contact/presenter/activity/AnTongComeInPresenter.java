package com.xdja.contact.presenter.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.uitl.PermissionUtil;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.command.IAnTongComeInCommand;
import com.xdja.contact.ui.def.IAnTongComeInVu;
import com.xdja.contact.ui.view.AnTongComeInVuVu;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by yangpeng on 2015/8/10.
 */
public class AnTongComeInPresenter extends ActivityPresenter<IAnTongComeInCommand,IAnTongComeInVu> implements IAnTongComeInCommand {

    private Friend friend;


    @Override
    protected Class<? extends IAnTongComeInVu> getVuClass() {
        return AnTongComeInVuVu.class;
    }


    @Override
    protected IAnTongComeInCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        friend = getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_ANTONG_FRIEND_DATA);
        if(friend == null){
            XToast.show(this,this.getString(R.string.contact_recevoce_data_error));//add by wal@xdja.com for string 数据接收出错
            finish();
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        getVu().setAnTongFtiendData(friend);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void startAtChat() {
//        Intent intent = new Intent();
//        intent.setAction("com.xdja.simcui._START_SEND_SMS_ACTIVITY");
//        intent.putExtra("user_id", friend.getAccount());
//        //chatType 1 : 单人聊天; 2  :群组聊天 ; 4 : 安通+团队
//        intent.putExtra("chatType", 4);
//        sendBroadcast(intent);

        try {
            //启动安通+团队
            Intent intent = new Intent("com.xdja.imp.simcui.AnTongTeamNotificationOperation");
            intent.putExtra("user_id", friend.getAccount());
            intent.putExtra("chatType", 4);
            startActivity(intent);
        }catch (Exception e){
           LogUtil.getUtils().e("AnTongComeInPresenter startAtChat excepion:"+e.getMessage());
        }
    }

    String phone = "";
    @Override
    public void callPhone(String phone) {
        this.phone = phone;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        int i;
        if (Build.VERSION.SDK_INT < 23) {
            i = PermissionUtil.ALL_PERMISSION_OBTAINED;
        } else {
            i = PermissionUtil.requestPermissions(this, PermissionUtil.DAIL_PERMISSION_REQUEST_CODE, Manifest.permission.CALL_PHONE);
        }
        switch (i) {
            case PermissionUtil.ALL_PERMISSION_OBTAINED:
                startActivity(intent);
                break;
            default:
                LogUtil.getUtils().e("AnTongComeInPresenter no CALL_PHONE permission");
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.getUtils().e("AnTongComeInPresenter onRequestPermissionsResult "+grantResults.length);
        if(grantResults.length <= 0){
            return;
        }
        if(requestCode == PermissionUtil.DAIL_PERMISSION_REQUEST_CODE){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(intent);
            }else{
                final CustomDialog customDialog = new CustomDialog(this);
                customDialog.setTitle(getString(R.string.none_phone_permission))
                        .setMessage(TextUtil.getActomaText(this, TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                                0, 0, 0, getString(R.string.none_phone_permission_hint)))
                        .setNegativeButton(getString(R.string.content_yes)
                                , new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialog.dismiss();
                                    }
                                }).show();
            }
        }
    }
}
