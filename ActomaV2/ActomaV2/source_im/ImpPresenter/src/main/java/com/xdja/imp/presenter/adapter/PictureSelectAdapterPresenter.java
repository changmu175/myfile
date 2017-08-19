package com.xdja.imp.presenter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.frame.mvp.presenter.BasePresenterAdapter;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.PictureSelectCommand;
import com.xdja.imp.presenter.holder.PictureListViewHolder;
import com.xdja.imp.util.PicInfoCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * 本地图片列表显示适配器
 * Created by leill on 2016/6/20.
 */
public class PictureSelectAdapterPresenter
        extends BasePresenterAdapter<PictureSelectCommand, LocalPictureInfo>
        implements PictureSelectCommand {

    private final Context mContext;

    /** 图片选中最大个数，默认9张*/
    private static final int MAX_SELECT_COUNT = 9;

    /** 事件总线 */
    @Inject
    BusProvider busProvider;

    /** 当前已选中图片个数 */
    private int mSelectedCount = 0;

    /**
     * 用户选择的图片，存储为图片的完整信息
     */
    private Map<String , LocalPictureInfo>  pictureInfos;

    private List<LocalPictureInfo> mDataSource = new ArrayList<>();

    /** 控件id */
    private final int mItemLayoutId;

    private boolean isFromChatdetail = false;

    /** 发送图片时，图片的加载过程状态位*/
    private boolean bLoadingProgress = false;

    public PictureSelectAdapterPresenter(Context context, List<LocalPictureInfo> mDataSource,
                                        BusProvider busProvider , boolean isFromChatdetail) {
        this.mContext = context;
        this.mDataSource = mDataSource;
        this.busProvider = busProvider;
        this.mItemLayoutId = R.layout.picture_select_item;
        this.isFromChatdetail = isFromChatdetail;
        pictureInfos = PicInfoCollection.getLocalPicInfo();
        bLoadingProgress = false;
    }

    @Override
    protected PictureSelectCommand getCommand() {
        return this;
    }

    @Override
    protected LocalPictureInfo getDataSource(int position) {
        return mDataSource.get(position);
    }

    @Override
    @SuppressLint("ConstantConditions")
    protected Class<? extends AdapterVu<PictureSelectCommand, LocalPictureInfo>> getVuClass() {
        return null;
    }

    @Override
    public void setData(Map<String , LocalPictureInfo> pictureInfos) {
        this.pictureInfos = pictureInfos;
        //已选数据初始化
        mSelectedCount = getCommand().getSelectedCount();
        bLoadingProgress = false;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final LocalPictureInfo pictureInfo = mDataSource.get(position);
        if (pictureInfo == null){
            return convertView;
        }
        //加载控件
        final PictureListViewHolder viewHolder = getViewHolder(position, convertView, parent);
        final ImageView mContentImg = viewHolder.getView(R.id.img_local_image);
        final ImageButton mSelectImgBtn = viewHolder.getView(R.id.imgbtn_item_select);
        final ImageView mVideoLogoImg = viewHolder.getView(R.id.img_video_logo);

        if(isFromChatdetail){
            mSelectImgBtn.setVisibility(View.GONE);
            if(pictureInfo.getType() == ConstDef.TYPE_TINY_VIDEO){
                mVideoLogoImg.setVisibility(View.VISIBLE);
            }else {
                mVideoLogoImg.setVisibility(View.GONE);
            }
        }
        //控件初始化
        //set no picture
        //viewHolder.setImageResource(R.id.img_local_image, R.drawable.pic_failed);
        //set selected
        if (pictureInfo.getStatue() == LocalPictureInfo.Statue.STATUE_SELECTED){
            viewHolder.setImageResource(R.id.imgbtn_item_select, R.drawable.icon_selected_on);
            mContentImg.setColorFilter(Color.parseColor("#77000000"));
        } else {
            viewHolder.setImageResource(R.id.imgbtn_item_select, R.drawable.icon_selected_off);
            mContentImg.setColorFilter(null);
        }
        //set image
        viewHolder.setImageByUrl((Activity) mContext,
                R.id.img_local_image,
                pictureInfo.getLocalPath() ,
                pictureInfo.getStatue());

        //选择按钮点击事件
        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bLoadingProgress) {
                    return;
                }

                if (isPicSelected(pictureInfo)){
                    if (mSelectedCount <= 0){
                        return;
                    }
                    mSelectImgBtn.setImageResource(R.drawable.icon_selected_off);
                    mContentImg.setColorFilter(null);
                    //如果图片是已经被选择状态，则更新其被选择状态为未选择，更新其已经选择原图状态为false
                    pictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_UNCHECKED);
                    pictureInfo.setOriginalPic(false);
                    pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
                    mSelectedCount--;
                } else {
                    if (mSelectedCount >= MAX_SELECT_COUNT){
                        XToast.show(mContext, String.format(mContext.getResources()
                                .getString(R.string.select_pic_hint), MAX_SELECT_COUNT));
                        return ;
                    }
                    //如果图片是未被选择状态，则更新其被选择状态为被选择
                    pictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_SELECTED);
                    pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
                    mSelectImgBtn.setImageResource(R.drawable.icon_selected_on);
                    mContentImg.setColorFilter(Color.parseColor("#77000000"));
                    mSelectedCount++;
                }
                //更新集合列表信息
                mDataSource.set(position, pictureInfo);
                //通知更新标题指示器
                notifySelectedChanged();
            }
        });
        //图片点击事件
        mContentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLoadingProgress) {
                    return;
                }
                //点击预览所有图片
                if(isFromChatdetail){
                    startToChatDetailPreview(pictureInfo.getLocalPath());
                }else{
                    startToPreviewPicture(position);
                }
            }
        });
        return viewHolder.getConvertView();
    }

    private PictureListViewHolder getViewHolder(int position, View convertView,
                                                ViewGroup parent){
        return PictureListViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

    @Override
    public void notifySelectedChanged() {
        IMProxyEvent.PicSelectEvent event = new IMProxyEvent.PicSelectEvent();
        busProvider.post(event);
    }

    @Override
    public int getSelectedCount() {
        int i = 0;
        for(String path : pictureInfos.keySet()){
            //过滤出所有图片中状态为已经被选择的
            if(LocalPictureInfo.Statue.STATUE_SELECTED == pictureInfos.get(path).getStatue()){
                i++;
            }
        }
        return i;
    }

    @Override
    public boolean isPicSelected(LocalPictureInfo info) {
        return LocalPictureInfo.Statue.STATUE_SELECTED == pictureInfos.get(info.getLocalPath()).getStatue();
    }

    @Override
    public void startToChatDetailPreview(String filePath) {
        IMProxyEvent.PictureToChatdetailEvent event = new IMProxyEvent.PictureToChatdetailEvent();
        event.setFilePath(filePath);
        busProvider.post(event);
    }

    @Override
    public void setLoadingProgress(boolean bLoading) {
        bLoadingProgress = bLoading;
    }

    @Override
    public void startToPreviewPicture(int index) {
        //进入图片预览界面
        IMProxyEvent.PicturePreviewEvent event = new IMProxyEvent.PicturePreviewEvent();
        event.setCurrentIndex(index);
        busProvider.post(event);
    }
}
