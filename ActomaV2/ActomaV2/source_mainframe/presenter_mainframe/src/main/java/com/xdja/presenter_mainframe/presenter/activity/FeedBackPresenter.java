package com.xdja.presenter_mainframe.presenter.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.otto.Subscribe;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.AccountServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FeedBackCommand;
import com.xdja.presenter_mainframe.presenter.activity.setting.SelectUploadImagePresenter;
import com.xdja.presenter_mainframe.presenter.adapter.UploadImageAdapter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.FeedBackView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuFeedBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import util.CustomDialog;

/**
 * Created by ALH on 2016/8/12.
 */

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class FeedBackPresenter extends PresenterActivity<FeedBackCommand, VuFeedBack> implements FeedBackCommand {
    /**
     * 问题截图列表适配器
     */
    private UploadImageAdapter adapter;
    /**
     * 截图大图弹窗
     */
    private PopupWindow window;
    /**
     * 问题截图图片信息集合
     */
    private List<String> list;
    /**
     * 服务器回传的上传图片文件id集合
     */
    private List<String> attachments;
    /**
     * 图片最大选择数
     */
    private final int MAX_IMAGES = 3;

    /**
     * 停止提交信息的标识
     */
    public static final String STOP_SUBMIT_TAG = "stop_submit";


    /**
     * 是否停止提交请求
     */
    public boolean isStopSubmit = false;


    @Override
    protected Class<? extends VuFeedBack> getVuClass() {
        return FeedBackView.class;
    }

    @Override
    protected FeedBackCommand getCommand() {
        return this;
    }

    @Inject
    @InteractorSpe(DomainConfig.FEED_BACK)
    Lazy<Ext2Interactor<Context, UploadFeedBackUseCase.FeedBackRequestBean, UploadFeedBackUseCase
            .UploadFeedBackResponeBean>> useCase;

    @Inject
    @InteractorSpe(DomainConfig.FEED_BACK_IMAGE)
    Lazy<Ext2Interactor<Context, List<String>, String>> imageuseCase;

    /**
     * 选择上传图片
     *
     * @param size 当前已选择的图片集合size
     */
    @Override
    public void selectUploadImages(int size) {
        //跳转到图片选择页面
        Intent intent = new Intent(this, SelectUploadImagePresenter.class);
        intent.putExtra("size", size);
        startActivity(intent);
    }

    /**
     * 显示图片大图
     *
     * @param imagePath 图片地址
     */
    @Override
    public void showBigImage(String imagePath) {
        //PopupWindow布局
        View view = getLayoutInflater().inflate(R.layout.big_head_portrait_popupwindow_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.head_portrait_image);
        //设置头像大图
        Glide.with(this).load(new File(imagePath)).placeholder(R.drawable.ic_launcher).diskCacheStrategy
                (DiskCacheStrategy.ALL).error(R.drawable.ic_launcher).into(imageView);
        //显示头像大图的弹框
        window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setOutsideTouchable(true);
        window.showAtLocation(new View(this), Gravity.CENTER, 0, 0);
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (window != null) {
                    if (window.isShowing()) {
                        window.dismiss();
                        window = null;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 是否删除图片对话框
     *
     * @param adapter  适配器
     * @param list     图片集合
     * @param position 图片所在集合下标
     */
    @Override
    public void isDeleteImage(UploadImageAdapter adapter, List<String> list, int position) {
        deleteImageDialog(adapter, list, position);
    }


    /**
     * 提交
     *
     * @param opinion 问题和意见
     * @param mobile  联系电话
     */
    @Override
    public void submit(final String opinion, final String mobile) {
        isStopSubmit = false;

        //显示上传等待框
        getVu().showDialog(getString(R.string.submiting));
        //移除占位所添加的空数据
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i))) {
                list.remove(i);
            }
        }
        executeInteractorNoRepeat(imageuseCase.get().fill(this, list), new LoadingDialogSubscriber<String>(this, this, true) {
            @Override
            public void onNext(String s) {
                super.onNext(s);
                //如果已经停止操作，就不再往下进行
                if (s.equals(STOP_SUBMIT_TAG)) return;

                if (!TextUtils.isEmpty(s)) attachments.add(s);
                if (attachments.size() == list.size()) {
                    //实例化请求参数对象
                    UploadFeedBackUseCase.FeedBackRequestBean.Data data = new UploadFeedBackUseCase
                            .FeedBackRequestBean.Data();
                    data.setContent(opinion);
                    AccountBean account = AccountServer.getAccount();
                    if (account != null) {
                        data.setAccount(account.getAccount());
                    }
                    data.setType(1);
                    UploadFeedBackUseCase.FeedBackRequestBean bean = new UploadFeedBackUseCase.FeedBackRequestBean();
                    bean.setAppId("AT+");
                    bean.setContact(mobile);
                    bean.setData(data);
                    bean.setAttachments(attachments);
                    executeInteractorNoRepeat(useCase.get().fill(FeedBackPresenter.this, bean), new LoadingDialogSubscriber<UploadFeedBackUseCase.UploadFeedBackResponeBean>(FeedBackPresenter.this, FeedBackPresenter.this){

                        @Override
                        public void onNext(UploadFeedBackUseCase.UploadFeedBackResponeBean uploadFeedBackResponeBean) {
                            super.onNext(uploadFeedBackResponeBean);
                            getVu().closeDialog();
                            if (uploadFeedBackResponeBean.getResult().equals("0")) {
                                getVu().isShowSubmitSuccessLayout(true);

                                //清除缓存的压缩图
                                String newImagePath = FeedBackPresenter.this.getExternalCacheDir() + "/test";
                                File file = new File(newImagePath);
                                deleteFile(file);

                            } else {
                                XToast.show(FeedBackPresenter.this, uploadFeedBackResponeBean.getMsg());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            getVu().closeDialog();
                            XToast.show(FeedBackPresenter.this, getString(R.string.submit_failed));
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getVu().closeDialog();
                XToast.show(FeedBackPresenter.this, getString(R.string.submit_failed));
            }
        });

//
//
//
//        //实例化上传图片UseCase
//        imageuseCase = new UploadFeedBackImageUseCase(list, this);
//
//
//        //执行上传图片操作
//        imageuseCase.execute(new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                getVu().closeDialog();
//                XToast.show(FeedBackPresenter.this, "提交失败，请重试");
//            }
//
//            @Override
//            public void onNext(String s) {
//                //如果已经停止操作，就不再往下进行
//                if (s.equals(STOP_SUBMIT_TAG)) return;
//
//                if (!TextUtils.isEmpty(s)) attachments.add(s);
//                if (attachments.size() == list.size()) {
//                    //实例化请求参数对象
//                    UploadFeedBackUseCase.FeedBackRequestBean.Data data = new UploadFeedBackUseCase
//                            .FeedBackRequestBean.Data();
//                    data.setContent(opinion);
//                    AccountBean account = AccountServer.getAccount(FeedBackPresenter.this);
//                    if (account != null) {
//                        data.setAccount(account.getAccount());
//                    }
//                    data.setType(1);
//                    UploadFeedBackUseCase.FeedBackRequestBean bean = new UploadFeedBackUseCase.FeedBackRequestBean();
//                    bean.setAppId("AT+");
//                    bean.setContact(mobile);
//                    bean.setData(data);
//                    bean.setAttachments(attachments);
//                    //实例化问题反馈UseCase
//                    executeInteractorNoRepeat(useCase.get().fill(), null);
//                    useCase = new UploadFeedBackUseCase(bean, FeedBackPresenter.this);
//                    useCase.execute(new ActomaUseCase.ActomaSub<UploadFeedBackResponeBean>() {
//                        @Override
//                        public void onCompleted() {
//                        }
//
//                        @Override
//                        public void onNext(UploadFeedBackResponeBean uploadFeedBackResponeBean) {
//                            getVu().closeDialog();
//                            if (uploadFeedBackResponeBean.getResult().equals("0")) {
//                                getVu().isShowSubmitSuccessLayout(true);
//
//                                //清除缓存的压缩图
//                                String newImagePath = FeedBackPresenter.this.getExternalCacheDir() + "/test";
//                                File file = new File(newImagePath);
//                                deleteFile(file);
//
//                            } else {
//                                XToast.show(FeedBackPresenter.this, uploadFeedBackResponeBean.getMsg());
//                            }
//                        }
//
//                        @Override
//                        protected String obtainCommonErrorMsg() {
//                            getVu().closeDialog();
//                            return super.obtainCommonErrorMsg();
//                        }
//
//                        @Override
//                        protected String obtainErrorMsg_UNKNOWN_ERROR_CODE(String message) {
//                            getVu().closeDialog();
//                            return "提交失败，请重试";
//                        }
//
//                        @Override
//                        protected void obtainErrorMsg_REQUEST_PARAMS_NETWORK_ERROR() {
//                            getVu().closeDialog();
//                            XToast.show(FeedBackPresenter.this, "提交失败，请重试");
//                        }
//                    });
//                }
//            }
//        });
    }

    /**
     * 删除文件
     *
     * @param file
     */
    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {//如果是文件
                LogUtil.getUtils().i("要删除的文件名称=====》" + file.getAbsolutePath());
                boolean isOk = file.delete();
                LogUtil.getUtils().i("删除本地数据是否成功=====》" + isOk);
            } else if (file.isDirectory()) {//如果是文件夹
                for (File item : file.listFiles()) {
                    deleteFile(item);
                }
            }
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
        BusProvider.getMainProvider().register(this);
        initGridView();
    }

    /**
     * 初始化问题截图列表
     */
    private void initGridView() {
        list = new ArrayList<>();
        attachments = new ArrayList<>();
        list.add("");
        adapter = new UploadImageAdapter(list, this);
        getVu().initGridView(adapter, list);
    }

    /**
     * 删除图片对话框
     *
     * @param adapter  适配器
     * @param list     适配器数据信息集合
     * @param position 需删除的信息所在集合的下标
     */
    private void deleteImageDialog(final UploadImageAdapter adapter, final List<String> list, final int position) {
        final CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(getString(R.string.is_delete_image));
        dialog.setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                if (!TextUtils.isEmpty(list.get(list.size() - 1))) {
                    list.add("");
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 接收选择图片发送来的事件
     *
     * @param bean 事件对象
     */
    @Subscribe
    public void getSelctUploadIamge(SelectUploadImagePresenter.PostSelectImages bean) {
        try {
            //将接收到的数据加入到问题截图集合中
            for (int i = 0; i < bean.getList().size(); i++) {
                list.set(list.size() - 1, bean.getList().get(i).getImage_original_url());
                if (list.size() != MAX_IMAGES) {
                    list.add("");
                }
            }
            //实例化问题截图列表适配器
            adapter = new UploadImageAdapter(list, this);
            //设置问题截图列表适配器
            getVu().initGridView(adapter, list);
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        }
    }

    /**
     * 接收拍照图片发送来的事件
     *
     * @param bean 事件对象
     */
    @Subscribe
    public void getCameraImage(SelectUploadImagePresenter.PostCameraImages bean) {
        //将接收到的数据加入到问题截图集合中
        list.set(list.size() - 1, bean.getFilePath());
        if (list.size() != MAX_IMAGES) {
            list.add("");
        }
        //实例化问题截图列表适配器
        adapter = new UploadImageAdapter(list, this);
        //设置问题截图列表适配器
        getVu().initGridView(adapter, list);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isStopSubmit = true;
            if (window != null) {
                if (window.isShowing()) {
                    window.dismiss();
                    window = null;
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getMainProvider().unregister(this);
    }
}
