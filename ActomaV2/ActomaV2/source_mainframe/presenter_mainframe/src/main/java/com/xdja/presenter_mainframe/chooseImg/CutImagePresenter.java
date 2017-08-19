package com.xdja.presenter_mainframe.chooseImg;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.CommonHeadBitmap;
import com.xdja.comm.event.UpdateImgBitmap;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;

import javax.inject.Inject;


/**
 * Created by geyao on 2015/7/7.
 * 裁剪图片
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class CutImagePresenter extends PresenterActivity<CutImageCommand, CutImageVu> implements CutImageCommand {

    @Inject
    BusProvider busProvider;

    @Override
    protected Class<? extends CutImageVu> getVuClass() {
        return ViewCutImage.class;
    }

    @Override
    protected CutImageCommand getCommand() {
        return this;
    }

    //[S]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
//    /**
//     * 裁剪图片
//     *
//     * @param imageView 图片
//     * @param x         x坐标
//     * @param y         y坐标
//     * @param width     宽度
//     * @param height    高度
//     */
//    @Override
//    public void cutImage(ImageView imageView, int x, int y, int width, int height) {
//        //判断网络连接是否正常,如果网络异常就不需要发送otto事件了
//        if (NetworkUtil.isNetworkConnect(this)) {
//            //获取矩形区域内的截图
//            imageView.setDrawingCacheEnabled(true);
//            imageView.buildDrawingCache();
//            Bitmap screenShoot = imageView.getDrawingCache();
//            //获取Activity的截屏(裁剪后的图)
//            Bitmap bm = Bitmap.createBitmap(screenShoot, x, y, width, height);
//            imageView.destroyDrawingCache();
//            imageView.setDrawingCacheEnabled(false);
//            //实例化事件对象bean
//            UpdateImgBitmap updateImgBitmap = new UpdateImgBitmap();
//            updateImgBitmap.setBm(bm);
//            //发送事件
//            busProvider.post(updateImgBitmap);
//            //CommonHeadBitmap
//            CommonHeadBitmap headBitmap = new  CommonHeadBitmap();
//            headBitmap.setBm(bm);
//            busProvider.post(headBitmap);
//            //结束当前activity 设置结果让上层处理
//            setResult(RESULT_OK);
//            finish();
//        } else {
//            Toast.makeText(this,"网络异常，请检查网络设置",Toast.LENGTH_LONG).show();
//        }
//    }
    @Override
    public void cutImage(Bitmap bmp) {
        //判断网络连接是否正常,如果网络异常就不需要发送otto事件了
        if (NetworkUtil.isNetworkConnect(this)) {
            //实例化事件对象bean
            UpdateImgBitmap updateImgBitmap = new UpdateImgBitmap();
            updateImgBitmap.setBm(bmp);
            //发送事件
            busProvider.post(updateImgBitmap);
            //CommonHeadBitmap
            CommonHeadBitmap headBitmap = new  CommonHeadBitmap();
            headBitmap.setBm(bmp);
            busProvider.post(headBitmap);
            //结束当前activity 设置结果让上层处理
            setResult(RESULT_OK);
            finish();
        } else {
            XToast.show(this,getString(R.string.feedback_network_error));
        }
    }
    //[E]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.

    /**
     * View初始化之后
     *
     * @param savedInstanceState
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getIntent() != null) {
            //获取传值
            String image_url = getIntent().getStringExtra("image_url");
            if (TextUtils.isEmpty(image_url)) {
                Toast.makeText(this, R.string.image_load_failed, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            //设置显示图片
            getVu().setImage(image_url);
        }

        //2016-4-22 ldy
        this.getActivityPreUseCaseComponent().inject(this);
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

//    /**
//     * 更新头像的事件
//     */
//    public static class UpdateImgBitmap {
//        private Bitmap bm;
//
//        public Bitmap getBm() {
//            return bm;
//        }
//
//        public void setBm(Bitmap bm) {
//            this.bm = bm;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().onDestroy();
    }

}
