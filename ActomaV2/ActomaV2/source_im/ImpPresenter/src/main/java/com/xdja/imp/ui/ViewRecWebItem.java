package com.xdja.imp.ui;

import android.graphics.Bitmap;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.WebPageInfo;
import com.xdja.imp.util.BitmapUtils;

import java.io.File;
import java.util.logging.Handler;

/**
 * 项目名称：ActomaV2
 * 类描述：网页消息视图Item
 * 创建人：yuchangmu
 * 创建时间：2016/12/19.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ViewRecWebItem extends ViewChatDetailRecItem {

    /**
     * 接收内容
     */
    private TextView recWebTitle;
    private TextView recWebDes;
    private ImageView recWebThumb;
    private TextView recSource;
    private LinearLayout source_ll;

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_recweb;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null) {
            recWebTitle = (TextView) view.findViewById(R.id.web_title);
            recWebDes = (TextView) view.findViewById(R.id.web_des);
            recWebThumb = (ImageView) view.findViewById(R.id.web_thumb);
            source_ll = (LinearLayout) view.findViewById(R.id.source_ll);
            recSource = (TextView) view.findViewById(R.id.source);
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().clickWebMessage(dataSource);
                }
            });
        }
    }

    @Override
    public void onViewReused() {
        super.onViewReused();
        recWebThumb.setImageBitmap(null);
        recWebThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
        sendReadReceipt();
    }

    private void initView() {
        Log.d("yyyyyyy", "initView");
        if (dataSource != null) {
            contentLayout.setVisibility(View.VISIBLE);
            FileInfo fileInfo = dataSource.getFileInfo();
            if (fileInfo instanceof WebPageInfo) {
                WebPageInfo webPageInfo = (WebPageInfo) fileInfo;
                String title = webPageInfo.getTitle();
                String description = webPageInfo.getDescription();
                String source = webPageInfo.getSource();
                recWebTitle.setText(title);
                recWebDes.setText(description);
                if (!TextUtils.isEmpty(source)) {
                    source_ll.setVisibility(View.VISIBLE);
                    recSource.setText("from:" + source);
                }
                if (TextUtils.isEmpty(webPageInfo.getFilePath())) {
                    loadImageResource(R.drawable.ic_jpg);
                    return;
                }
                //文件未下载，或者下载失败，或者数据请被清除
                File file = new File(webPageInfo.getFilePath());
                Log.d("yyyyyyy", "path:" + webPageInfo.getFilePath());
                if (!file.exists() || (file.length() != webPageInfo.getFileSize())) {
                    boolean a = file.exists();
                    boolean b = file.length() == webPageInfo.getFileSize();
                    Log.d("yyyyyyy", "a : " + a + "  b:  " + b);
                    if (webPageInfo.getFileState() == ConstDef.FAIL) {
                        loadImageResource(R.drawable.ic_jpg);
                        getCommand().loadImage(dataSource);
                    } else if (isFileExist(webPageInfo.getFilePath())) { //本地图片存在
                        if (contentLayout.getVisibility() == View.VISIBLE) {
                            loadImage(webPageInfo.getFilePath());
                        }
                    } else { //本地图片不存在，网路请求,并且判断当前item是否可见
                        if (contentLayout.getVisibility() == View.VISIBLE) {
                            loadImageResource(R.drawable.ic_jpg);
                            getCommand().loadImage(dataSource);
                        }
                    }
                } else { //已经已经下载成功（存在被清除的可能性）
                    if (isFileExist(webPageInfo.getFilePath())) { //加载本地图片
                        if (contentLayout.getVisibility() == View.VISIBLE) {
                            loadImage(webPageInfo.getFilePath());
                        }
                    } else {
                        //显示失败图片
                        loadImageResource(R.drawable.ic_jpg);
                    }
                }
                LogUtil.getUtils().d(dataSource.getMessageState());
            }
        }
    }

    /**
     * 根据资源文件，加载图片
     */
    private void loadImageResource(int srcId) {
        recWebThumb.setImageResource(srcId);
    }

    /**
     * 文件是否存在
     *
     * @param fileURL 文件url
     * @return true 存在
     *          false 不存在
     */
    public boolean isFileExist(String fileURL) {
        File file = new File(fileURL);
        return file.exists();
    }

    /**
     * 根据URL加载图片
     *
     * @param url 文件url
     */
    private void loadImage(String url) {
        Log.d("yyyyyyy", "web");
        Bitmap bitmap = BitmapUtils.getZoomedDrawable(url, 1);
        if (bitmap != null) {
            recWebThumb.setImageBitmap(bitmap);
        } else {
            loadImageResource(R.drawable.ic_jpg);
        }
    }

    private void sendReadReceipt() {
        if (dataSource.getMessageState() < ConstDef.STATE_READED) {
            //如果当前界面正在显示，并且消息状态是初始状态，发送已阅读回执
            if (getCommand().getActivityIsShowing()) {
                //发送阅读回执
                getCommand().sendReadReceipt(dataSource);
            }
        }
    }
}
