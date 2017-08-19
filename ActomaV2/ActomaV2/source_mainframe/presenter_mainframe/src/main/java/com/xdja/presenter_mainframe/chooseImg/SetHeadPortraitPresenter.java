package com.xdja.presenter_mainframe.chooseImg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.xdja.comm.uitl.handler.SafeLockUtil;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * Created by geyao on 2015/7/7.
 * 设置头像
 */
@InjectOption.Options(InjectOption.OPTION_ACTIVITY)
public class SetHeadPortraitPresenter extends PresenterActivity<SetHeadPortraitCommand, SetHeadPortraitVu> implements SetHeadPortraitCommand {
    private static final int REQ_CAMERA_CODE = 2;
    /**
     * 照相机
     */
    private final int CAMERA = 0;

    /**
     * 图片
     */
    private final int PICTURE = 1;
    /**
     * 图片地址
     */
    private String mPhotoPath;
    /**
     * 图片文件地址
     */
    private File mPhotoFile;

    /**
     * 图片信息集合(要展示的)
     */
    private ArrayList<ImageRelInfoBean> list = null;


    @Override
    protected Class<? extends SetHeadPortraitVu> getVuClass() {
        return ViewSetHeadPortrait.class;
    }

    @Override
    protected SetHeadPortraitCommand getCommand() {
        return this;
    }

    private final int MAX_SIZE = 30 * 1024 * 1024;

    /**
     * 设置gridview item点击事件
     *
     * @param parent
     * @param view     当前点击的item的师徒
     * @param position item的下标
     * @param id
     */
    @Override
    public void setGridItemClick(AdapterView<?> parent, View view, int position, long id) {
        //计算导航栏高度
        ViewStatus.getViewStatus().setStatuBarHeight(getStatusBarHeight(this));
        ViewStatus.getViewStatus().setContentTop(getSupportActionBar().getHeight() + ViewStatus.getViewStatus().getStatuBarHeight());
        ViewStatus.getViewStatus().setTitleBarHeight(getSupportActionBar().getHeight());
        //设置监听
        ImageRelInfoBean info = list.get(position);
        //判断是否点击的是照相机
        if (position == 0 && info != null) {//选择拍照
            if (isMNC()) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                            REQ_CAMERA_CODE);
                    return;
                }
            }
            openCamera();
            //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2073 . review by guobinchang. End
        } else {//未选择照相机,选择其他图片
            //跳转到图片裁剪页面
            //alh@xdja.com<mailto://alh@xdja.com> 2017-08-02 add. fix bug 2073 . review by guobinchang. End
            if (info.getFile_size() > MAX_SIZE) {
                Toast.makeText(this, R.string.image_big, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CutImagePresenter.class);
            intent.putExtra("image_url", info.getImage_original_url());
            startActivityForResult(intent, PICTURE);
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void openCamera() {
        try {
            //打开照相机
//                Intent intent = new Intent(
//                        "android.media.action.IMAGE_CAPTURE");
            //生成照片名称
            long currentMillinSecond = System.currentTimeMillis();
            String picName = currentMillinSecond + ".jpg";
            //获取手机图片缓存地址
            String mParentPath = SetHeadPortraitPresenter.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath();
            //获取父文件夹路径
            File parentFile = new File(mParentPath);
            //创建文件夹-文件
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            mPhotoPath = mParentPath + File.separator + picName;
            //创建图片文件
            mPhotoFile = new File(mPhotoPath);
            if (!mPhotoFile.exists()) {
                mPhotoFile.createNewFile();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, CAMERA);
            //[S] fix bug 7706 by licong for safeLock
            SafeLockUtil.setUseCameraOrFile(true);
            //[E] fix bug 7706 by licong for safeLock
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == REQ_CAMERA_CODE){
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                openCamera();
            }else{
                XToast.show(this, getString(R.string.none_camera_permission_hint));
            }
        }
    }
    /**
     * View初始化之后
     *
     * @param savedInstanceState
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //获取手机内所有图片
        list = new FindImgUtils().getImgData(this);
        //实例化适配器
        SetHeadPortraitAdapter adapter = new SetHeadPortraitAdapter(list, this);
        //设置列表适配器
        getVu().setGridAdapter(adapter);
    }

    /**
     * View初始化之前
     *
     * @param savedInstanceState
     */
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    /**
     * 处理页面返回结果
     *
     * @param requestCode 处理结果状态码
     * @param resultCode  处理结果状态
     * @param data        结果返回的意图
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA://照相机
                    //跳转到图片裁剪页面
                    Intent intent = new Intent(this, CutImagePresenter.class);
                    intent.putExtra("image_url", mPhotoPath);
                    startActivityForResult(intent, PICTURE);
                    break;
                case PICTURE://图片
                    finish();
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA://照相机
//                    ActomaApp.STOP_DEB_LOCKING = true;
                    break;
            }
        }

    }

    /**
     * 计算状态栏高度
     *
     * @param context 上下文句柄
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().onDestroy();
    }
}
