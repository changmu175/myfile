package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.SelectImageBean;
import com.xdja.presenter_mainframe.bean.SelectUploadImageBean;
import com.xdja.presenter_mainframe.chooseImg.FindImgUtils;
import com.xdja.presenter_mainframe.chooseImg.ImageRelInfoBean;
import com.xdja.presenter_mainframe.cmd.SelectUploadImageCommand;
import com.xdja.presenter_mainframe.presenter.adapter.SelectUploadImageAdapter;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.SelectUploadImageView;
import com.xdja.presenter_mainframe.ui.uiInterface.SelectUploadImageVu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public class SelectUploadImagePresenter extends PresenterActivity<SelectUploadImageCommand, SelectUploadImageVu> implements SelectUploadImageCommand {
    /**
     * 图片可选择数量
     */
    private int MAX_IMAGES = 3;
    /**
     * 图片已选择数量
     */
    private int SELECT_IMAGES = 0;
    /**
     * 已选择图片集合
     */
    private List<ImageRelInfoBean> select_list = new ArrayList<>();
    /**
     * 照片地址
     */
    private String mPhotoPath;
    /**
     * 照相机
     */
    private final int CAMERA = 0;

    @Override
    protected Class<? extends SelectUploadImageVu> getVuClass() {
        return SelectUploadImageView.class;
    }

    @Override
    protected SelectUploadImageCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getIntent() != null) {
            //计算可选数量
            SELECT_IMAGES = MAX_IMAGES - getIntent().getIntExtra("size", -1);
            //设置标题
            setActivityTitle(0);
        }
        initGridView();
    }

    /**
     * 列表item点击事件处理
     *
     * @param list     数据集合
     * @param position item所对应的下标
     * @param adapter  适配器
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void clickItem(List<SelectUploadImageBean> list, int position, SelectUploadImageAdapter adapter) {
        //区分单击的是拍照还是图片
        switch (list.get(position).getType()) {
            case SelectUploadImageAdapter.TYPE_IMAGE://图片
                //获取点击的item数据对象
                SelectImageBean bean = (SelectImageBean) list.get(position).getObject();
                //判断当前所选图片是否超过限制
                if (select_list.size() < SELECT_IMAGES) {//未超出限制
                    //判断该图片选中状态
                    if (bean.isCheck()) {//已选中 取消选中状态 删除已选择集合内对应的数据
                        bean.setIsCheck(false);
                        for (int i = 0; i < select_list.size(); i++) {
                            if (select_list.get(i) == bean.getInfoBean()) {
                                select_list.remove(i);
                            }
                        }
                    } else {//未选中 显示选中状态 添加入选择集合内
                        bean.setIsCheck(true);
                        select_list.add(bean.getInfoBean());
                    }
                } else {//超出限制
                    if (bean.isCheck()) {//已选中 取消选中状态 删除已选择集合内对应的数据
                        bean.setIsCheck(false);
                        for (int i = 0; i < select_list.size(); i++) {
                            if (select_list.get(i) == bean.getInfoBean()) {
                                select_list.remove(i);
                            }
                        }
                    }
                }
                //修改集合对应的下标的数据
                list.set(position, list.get(position));
                //刷新适配器
                adapter.notifyDataSetChanged();
                //设置确定按钮是否可用
                getVu().isShowBtn(!select_list.isEmpty());
                //设置标题
                setActivityTitle(select_list.size());
                break;
            case SelectUploadImageAdapter.TYPE_CAMERA://拍照
                try {
                    //打开照相机
                    Intent intent = new Intent(
                            "android.media.action.IMAGE_CAPTURE");
                    //生成照片名称
                    long currentMillinSecond = System.currentTimeMillis();
                    String picName = currentMillinSecond + ".jpg";
                    //获取手机图片缓存地址
                    String mParentPath = SelectUploadImagePresenter.this.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    //获取父文件夹路径
                    File parentFile = new File(mParentPath);
                    //创建文件夹-文件
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    mPhotoPath = mParentPath + File.separator + picName;
                    //创建图片文件
                    File mPhotoFile = new File(mPhotoPath);
                    if (!mPhotoFile.exists()) {
                        mPhotoFile.createNewFile();
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(mPhotoFile));
                    startActivityForResult(intent, CAMERA);
                } catch (Exception e) {
                    LogUtil.getUtils().e(e);
                }
                break;
        }
    }

    /**
     * 点击确认按钮
     */
    @Override
    public void clickBtn() {
        //实例化事件对象
        PostSelectImages bean = new PostSelectImages();
        bean.setList(select_list);
        //发送事件
        BusProvider.getMainProvider().post(bean);
        //结束当前页面
        finish();
    }

    /**
     * 初始化列表数据
     */
    private void initGridView() {
        //获取本地图片集合
        ArrayList<ImageRelInfoBean> imgData = FindImgUtils.getImgData(this);
        //实例化适配器所需集合
        List<SelectUploadImageBean> list = new ArrayList<>();
        //适配器集合对象
        SelectUploadImageBean bean;
        //非照相机类型的图片对象
        SelectImageBean imgBean;
        //循环本地图片集合转换成适配器所需集合对象
        for (int i = 0; i < imgData.size(); i++) {
            if (!TextUtils.isEmpty(imgData.get(i).getImage_thumb_url()) &&
                    !TextUtils.isEmpty(imgData.get(i).getImage_original_url())) {
                //实例化非照相机类型的图片对象
                imgBean = new SelectImageBean();
                imgBean.setInfoBean(imgData.get(i));
                imgBean.setIsCheck(false);
                //实例化适配器集合对象
                bean = new SelectUploadImageBean();
                bean.setType(SelectUploadImageAdapter.TYPE_IMAGE);
                bean.setObject(imgBean);
                //加入适配器集合
                list.add(bean);
            }
        }

        //实例化适配器集合对象 添加一个照相机类型的数据
        bean = new SelectUploadImageBean();
        bean.setObject(null);
        bean.setType(SelectUploadImageAdapter.TYPE_CAMERA);
        list.add(0, bean);

        //实例化适配器
        SelectUploadImageAdapter adapter = new SelectUploadImageAdapter(list, this);
        //设置适配器
        getVu().initGridView(list, adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA) {
            //实例化事件对象
            PostCameraImages bean = new PostCameraImages();
            bean.setFilePath(mPhotoPath);
            //发送事件
            BusProvider.getMainProvider().post(bean);
            //结束当前页面
            finish();
        }
    }

    /**
     * 设置页面标题
     *
     * @param size 已选择图片数量
     */
    private void setActivityTitle(int size) {
        String title = getResources().getString(R.string.title_activity_view_select_upload_images);
        setTitle(title + "(" + size + "/" + SELECT_IMAGES + ")");
    }

    /**
     * 发送选择图片事件至意见反馈
     */
    public static class PostSelectImages {
        private List<ImageRelInfoBean> list;

        public List<ImageRelInfoBean> getList() {
            return list;
        }

        public void setList(List<ImageRelInfoBean> list) {
            this.list = list;
        }
    }

    /**
     * 发送拍照事件至意见反馈
     */
    public static class PostCameraImages {
        private String filePath;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
