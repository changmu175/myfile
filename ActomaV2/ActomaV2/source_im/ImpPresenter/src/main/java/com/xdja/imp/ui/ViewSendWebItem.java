package com.xdja.imp.ui;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.WebPageInfo;
import com.xdja.imp.util.BitmapUtils;

import java.io.File;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2016/12/19.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ViewSendWebItem extends ViewChatDetailSendItem {
    private TextView sendWebTitle;
    private TextView sendWebDes;
    private ImageView sendWebThumb;
    public ViewSendWebItem() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendweb;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null) {
            sendWebTitle = (TextView) view.findViewById(R.id.web_title);
            sendWebDes = (TextView) view.findViewById(R.id.web_des);
            sendWebThumb = (ImageView) view.findViewById(R.id.web_thumb);
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().clickWebMessage(dataSource);
                }
            });
        }
    }

    @Override
    public void bindDataSource(int position, TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }

    private void initView() {
        //发送内容
        FileInfo fileInfo = dataSource.getFileInfo();

        if (fileInfo == null || ! (fileInfo instanceof WebPageInfo)) {
            return;
        }
        WebPageInfo webPageInfo = (WebPageInfo) dataSource.getFileInfo();
        String title = webPageInfo.getTitle();
        String descrip = webPageInfo.getDescription();
        sendWebTitle.setText(title);
        sendWebDes.setText(descrip);
		FileInfo imageFileInfo =  dataSource.getFileInfo();
        if (imageFileInfo == null){
            loadImageResource(R.drawable.ic_jpg);
            return ;
        }
        /**
         * 异常情况：
         * 1）数据库被清除：先判断图片是否已经在本地存在（一般情况下，存在），不存在则网络请求
         * 2）本地图片缓存文件被清理：直接显示失败图片
         */

        //本地图片存在
        if (isFileExist(imageFileInfo.getFilePath())){
            //加载本地已经下载好的图片
            if (contentLayout.getVisibility() == View.VISIBLE){
                loadImage(imageFileInfo.getFilePath());
            }
        }
        //如果本地不存在，则表示图片缓存被清理，直接显示失败图片
        else {
            //显示失败图片
            loadImageResource(R.drawable.ic_jpg);
        }
    }

    /**
     * 加载URL指定图片
     * @param url
     */

    private void loadImage(String url) {
        int compressRatio = 1;
        Bitmap bitmap = BitmapUtils.getZoomedDrawable(url, compressRatio);
        if (bitmap != null) {
            sendWebThumb.setImageBitmap(bitmap);
        } else {
            loadImageResource(R.drawable.ic_jpg);
        }
    }


    /**
     * 加载本地资源图片
     * @param srcId
     */
    private void loadImageResource(int srcId){
        sendWebThumb.setImageResource(srcId);
    }

    @Override
    public void onViewReused() {
        sendWebThumb.setImageBitmap(null);
        sendWebThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * 文件是否存在
     * @param fileURL
     * @return
     */
    public boolean isFileExist(String fileURL){
        if (TextUtils.isEmpty(fileURL)){
            return false;
        }
        File file = new File(fileURL);
        if (file.exists()){
            return true;
        }
        return false;
    }
}
