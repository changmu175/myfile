package com.xdja.imp.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.presenter.activity.PictureSelectActivity;
import com.xdja.imp.presenter.activity.SinglePhotoPresenter;
import com.xdja.imp.presenter.adapter.ChatDetailMediaAdapter;
import com.xdja.imp.presenter.command.SinglePhotoCommand;
import com.xdja.imp.ui.vu.ISinglePhotoVu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Author:guorong</p>
 * <p>Date:2017/3/10</p>
 * <p>Time:22:28</p>
 */
public class SinglePhotoVu extends ActivitySuperView<SinglePhotoCommand> implements ISinglePhotoVu {
    private final static int PAGE_MARGIN = 60;

    private ViewPager viewPager;
    private ImageButton picListBtn;//查看图片列表按钮
    private ImageView picLoadingIv;//图片加载动画
    private Button checkOriginPicBtn;//查看原图按钮
    private TextView noPicTv;//无消息时显示的界面
    private int mCurPos;
    private ChatDetailMediaAdapter mAdapter;
    /**
     * 当前图片所属消息id
     */
    private static long currPicMsgId = -1;

    /**
     * 该会话中所有的文件信息
     */
    private List<TalkMessageBean> datasources = new ArrayList<>();
    private final List<ChatDetailPicInfo> imageInfos = new ArrayList<>();

    @Override
    protected int getLayoutRes() {
        return R.layout.imageshower;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        noPicTv = (TextView) view.findViewById(R.id.no_pic);
        checkOriginPicBtn = (Button) view.findViewById(R.id.originPicGetBtn);
        checkOriginPicBtn.setAlpha(0.7f);
        picLoadingIv = (ImageView) view.findViewById(R.id.pic_loading_iv);
        //用于显示图片的viewPager
        viewPager = ((ViewPager) view.findViewById(R.id.imageShowerViewPager));
        viewPager.setPageMargin(PAGE_MARGIN);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkOriginPicBtn.setVisibility(View.GONE);
                /*if(datasources.get(position).getMessageType() == ConstDef.MSG_TYPE_PHOTO){
                    picListBtn.setVisibility(View.VISIBLE);
                }else if(datasources.get(position).getMessageType() == ConstDef.MSG_TYPE_VIDEO){
                    picListBtn.setVisibility(View.GONE);
                }*/
                showLoading(false);
                getCommand().setCurMsgId(datasources.get(position).get_id());
                refreshPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               /*if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    getCommand().makePhotoViewEnable(false);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    getCommand().makePhotoViewEnable(true);
                }*/

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mCurPos = viewPager.getCurrentItem();
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    if (mCurPos != viewPager.getCurrentItem()) {
                        mAdapter.onPageSelected(mCurPos, viewPager.getCurrentItem());
                        mCurPos = viewPager.getCurrentItem();
                    }
                }
            }
        });
        //设置当前显示的条目

        //用于查看图片高清缩略图列表
        picListBtn = (ImageButton) view.findViewById(R.id.picListBtn);
        picListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PictureSelectActivity.class);
                ArrayList<TalkMessageBean> tempInfos = new ArrayList<>();
                if (datasources.size() != 0) {
                    tempInfos.addAll(datasources);
                }
                intent.putParcelableArrayListExtra(ConstDef.SEESION_FILE_INFOS, tempInfos);
                intent.putExtra(ConstDef.FROM_CHAT_DETAIL, true);
                getActivity().startActivityForResult(intent, ConstDef.REQUEST_CODE_SELECT);

            }
        });

        checkOriginPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据记录的当前页显示的消息的id，查找到该消息对应的图片信息，如果查找到，下载该图片
                for (ChatDetailPicInfo info : imageInfos) {
                    if (info.getMsgId() == currPicMsgId) {
                        //更新该按钮上显示的信息
                        if (getRawLoadInfo().containsKey(currPicMsgId)) {
                            //如果当前图片正在下载，则暂停，如果没有在下载，则继续下载
                            if (getRawLoadInfo().get(currPicMsgId).isLoading) {
                                //如果当前图片在记录下载的map中，并且状态为下载中，则暂停
                                getCommand().pauseDownloadPic(info, true);

                            } else {
                                //如果当前图片在记录下载的map中，并且状态为暂停，则继续下载
                                String percentStr = getRawLoadInfo().get(currPicMsgId).percent + "%";
                                checkOriginPicBtn.setText(percentStr);
                                Drawable pauseIcon = getActivity().getResources().getDrawable(R.drawable
                                        .origin_pic_pause_selector);
                                pauseIcon.setBounds(0, 0, pauseIcon.getMinimumWidth(), pauseIcon.getMinimumHeight());
                                checkOriginPicBtn.setCompoundDrawables(null, null, pauseIcon, null);
                                getCommand().resumeDownloadPic(info, true);
                            }
                            SinglePhotoPresenter.DownloadInfo downloadInfo = getRawLoadInfo().get(currPicMsgId);
                            downloadInfo.isLoading = !downloadInfo.isLoading;
                            getRawLoadInfo().put(currPicMsgId, downloadInfo);
                        } else {
                            //如果当前图片不在记录下载的map中,则将其加入到map中，并开始下载
                            Drawable pauseIcon = getActivity().getResources().getDrawable(R.drawable
                                    .origin_pic_pause_selector);
                            pauseIcon.setBounds(0, 0, pauseIcon.getMinimumWidth(), pauseIcon.getMinimumHeight());
                            checkOriginPicBtn.setCompoundDrawables(null, null, pauseIcon, null);
                            String percent = "0";
                            if (info.getRawSize() > 0) {
                                File file = new File(info.getRawPath() + ".tmp");
                                if (file.exists()) {
                                    long size = file.length();
                                    percent = (size / info.getRawSize()) * 100 + "";
                                }
                            }
                            String percentStr = percent + "%";
                            checkOriginPicBtn.setText(percentStr);
                            getRawLoadInfo().put(info.getMsgId(), new SinglePhotoPresenter.DownloadInfo(true, 0));
                            getCommand().downloadPic(info, true);
                        }
                        break;
                    }
                }
            }
        });

    }

    private void refreshPage(int position) {
        FileInfo fileInfo = datasources.get(position).getFileInfo();
        ChatDetailPicInfo info;
        if(fileInfo != null && fileInfo instanceof ChatDetailPicInfo){
            info = (ChatDetailPicInfo) fileInfo;
        }else{
            return;
        }
        currPicMsgId = datasources.get(position).get_id();
        if (!info.isBoom() && !info.isMine()) {
            //如果不是已销毁的闪信，并且高清缩略图和原图都未下载完，则下载高清缩略图
            if (!getCommand().isFileDownload(info.getRawPath())
                    && (!getCommand().isFileDownload(info.getHdThumPath()))) {
                checkOriginPicBtn.setVisibility(View.GONE);
                showLoading(true);
                getCommand().downloadPic(info, false);
                getHdLoadInfo().put(currPicMsgId, true);
            } else {
                if (info.getRawSize() > 0 && !getCommand().isFileDownload(info.getRawPath()) &&
                        (!getHdLoadInfo().containsKey(currPicMsgId) || (getHdLoadInfo().containsKey(currPicMsgId) &&
                                !getHdLoadInfo().get(currPicMsgId))) && !getHdLoadInfo().containsKey(currPicMsgId)) {
                    //如果有原图fid，原图不是本人发送并且高清图已下载完原图未下载完，则显示查看原图按钮
                    long size = info.getRawSize();
                    String sizeStr;
                    sizeStr = getActivity().getResources().getString(R.string.view_original_image) + "(" + getCommand().getFileSize(size) + ")";
                    checkOriginPicBtn.setText(sizeStr);
                    checkOriginPicBtn.setVisibility(View.VISIBLE);
                    checkOriginPicBtn.setCompoundDrawables(null, null, null, null);
                } else if (!getCommand().isFileDownload(info.getRawPath())
                        && !info.isMine() && getRawLoadInfo()
                        .containsKey(currPicMsgId)) {
                    //guorong 2016-8-30 16:28:29 fix bug none begin
                    if (getRawLoadInfo().get(currPicMsgId).isLoading) {
                        //如果有原图，并且原图在下载中，则显示按钮并显示当前下载进度
                        String percent = getRawLoadInfo().get(currPicMsgId).percent + "%";
                        checkOriginPicBtn.setText(percent);
                        checkOriginPicBtn.setVisibility(View.VISIBLE);
                        Drawable pauseIcon = getActivity().getResources().getDrawable(R.drawable.origin_pic_pause_selector);
                        pauseIcon.setBounds(0, 0, pauseIcon.getMinimumWidth(), pauseIcon.getMinimumHeight());
                        checkOriginPicBtn.setCompoundDrawables(null, null, pauseIcon, null);
                    } else {
                        //如果有原图，并且原图在暂停中，则显示按钮并显示继续下载
                        checkOriginPicBtn.setText("继续下载");
                        checkOriginPicBtn.setVisibility(View.VISIBLE);
                        Drawable pauseIcon = getActivity().getResources().getDrawable(R.drawable.origin_pic_pause_selector);
                        pauseIcon.setBounds(0, 0, pauseIcon.getMinimumWidth(), pauseIcon.getMinimumHeight());
                        checkOriginPicBtn.setCompoundDrawables(null, null, null, null);
                    }
                } else {
                    checkOriginPicBtn.setVisibility(View.GONE);
                }
            }
        } else {
            checkOriginPicBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismissPopupwindow() {
        getActivity().finish();
        //弹框消失，如果有图片在下载，则暂停下载

        if (getHdLoadInfo().containsKey(currPicMsgId) && getHdLoadInfo().get(currPicMsgId)) {
            //如果当前在下载高清缩略图
            for (ChatDetailPicInfo info : imageInfos) {
                if (info.getMsgId() == currPicMsgId) {
                    getCommand().pauseDownloadPic(info, false);
                    break;
                }
            }
        } else if (getRawLoadInfo().containsKey(currPicMsgId)
                && getRawLoadInfo().get(currPicMsgId).isLoading) {
            //如果当前在下载原图
            for (ChatDetailPicInfo info : imageInfos) {
                if (info.getMsgId() == currPicMsgId) {
                    getCommand().pauseDownloadPic(info, true);
                    break;
                }
            }
        }

    }


    @Override
    public void removeMsg(TalkMessageBean talkMessageBean) {
        int index = 0;
        for(int i=0; i<datasources.size();i++){
            if(talkMessageBean.get_id() == datasources.get(i).get_id()){
                index = i;
                if( datasources.size() > 1){
                    if(i == 0){
                        getCommand().setCurMsgId(datasources.get(1).get_id());
                    }else{
                        getCommand().setCurMsgId(datasources.get(i - 1).get_id());
                    }

                }
                break;
            }
        }
        if(datasources != null && datasources.contains(talkMessageBean)){
            datasources.remove(talkMessageBean);
        }
        if(mAdapter != null){
            setAdapter(mAdapter);
        }
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if(fileInfo instanceof ChatDetailPicInfo){
            if(imageInfos.contains(fileInfo)){
                imageInfos.remove(fileInfo);
            }

        }
        if(!isViewPagerNull()){
            if(datasources.size() == 0){
                showNopic();
            }else{
                selectPage(index);
            }
        }
    }

    public void showLoading(boolean flag) {
        if (picLoadingIv == null) {
            return;
        }
        Animation loadAnima = AnimationUtils.loadAnimation(getActivity(), R.anim.pic_loading);
        LinearInterpolator lin = new LinearInterpolator();
        loadAnima.setInterpolator(lin);
        if (flag) {
            picLoadingIv.setVisibility(View.VISIBLE);
            picLoadingIv.startAnimation(loadAnima);
        } else {
            picLoadingIv.setVisibility(View.GONE);
            picLoadingIv.clearAnimation();
        }
    }

    public static long getCurmsgid() {
        return currPicMsgId;
    }

    @Override
    public void showNopic() {
        noPicTv.setVisibility(View.VISIBLE);
        checkOriginPicBtn.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        picListBtn.setVisibility(View.GONE);
    }

    @Override
    public void selectPage(int index) {
        if (index > 0) {
            viewPager.setCurrentItem(index - 1);
        } else if (index == 0) {
            viewPager.setCurrentItem(index);
            if(datasources.get(index) != null){
                FileInfo fileInfo = datasources.get(index).getFileInfo();
                if(fileInfo instanceof ChatDetailPicInfo){
                    refreshPage(0);
                }
            }
        }
    }

    @Override
    public boolean isViewPagerNull() {
        return viewPager == null;
    }

    @Override
    public void setCurPage(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
            mAdapter.setFirstPos(index);
            refreshPage(index);
        }
    }

    @Override
    public void setDatasource(List<TalkMessageBean> datasource) {
        this.datasources = datasource;
        /*for(TalkMessageBean bean : datasource){
            if(bean.get_id() == getCommand().getCurMsgId() && bean.getMessageType() == ConstDef.MSG_TYPE_VIDEO){
                picListBtn.setVisibility(View.GONE);
            }
        }*/
        FileInfo fileInfo;
        for(TalkMessageBean talkMessageBean : datasource){
            fileInfo = talkMessageBean.getFileInfo();
            if(fileInfo != null && fileInfo instanceof ChatDetailPicInfo){
                imageInfos.add((ChatDetailPicInfo) fileInfo);
            }
        }
    }

    @Override
    public List<ChatDetailPicInfo> getImageInfos() {
        return imageInfos;
    }

    @Override
    public void deleteMsg(TalkMessageBean talkMessageBean) {
        getCommand().deleteMsgs(talkMessageBean);
    }

    @Override
    public void setAdapter(ChatDetailMediaAdapter adapter) {
        mAdapter = adapter;
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityFinish() {
        getCommand().notifyAdapter();
        for (Long msgid : getRawLoadInfo().keySet()) {
            if (getRawLoadInfo().get(msgid).isLoading) {
                for (ChatDetailPicInfo info : imageInfos) {
                    if (info.getMsgId() == msgid) {
                        getCommand().pauseDownloadPic(info, true);
                    }
                }
            }
        }

        for (Long msgid : getHdLoadInfo().keySet()) {
            if (getHdLoadInfo().get(msgid)) {
                for (ChatDetailPicInfo info : imageInfos) {
                    if (info.getMsgId() == msgid) {
                        getCommand().pauseDownloadPic(info, false);
                    }
                }
            }
        }
        getHdLoadInfo().clear();
        getRawLoadInfo().clear();
        //Glide清除缓存
        Glide.get(getActivity()).clearMemory();
    }


    @Override
    public void hideOriginBtn() {
        if (checkOriginPicBtn != null) {
            checkOriginPicBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getMsgState(long msgId) {
        return getCommand().getMsgState(msgId);
    }

    @Override
    public void sendReadedState(long msgId) {
        getCommand().sendReadedState(msgId);
    }

    @Override
    public void showOriginPicBtn(String sizeStr) {
        checkOriginPicBtn.setText(sizeStr);
        checkOriginPicBtn.setVisibility(View.VISIBLE);
        checkOriginPicBtn.setCompoundDrawables(null, null,
                null, null);
    }

    @Override
    public void hideOriginPicBtn() {
        checkOriginPicBtn.setVisibility(View.GONE);
    }

    @Override
    public void updateOriginBtnPercent(int perent) {
        if (checkOriginPicBtn != null) {
            String percentStr = perent + "%";
            checkOriginPicBtn.setText(percentStr);
        }
    }

    @Override
    public void updateOriginBtnPause(Drawable icon) {
        checkOriginPicBtn.setText(getActivity().getResources().getString(R.string.view_original_image));
        checkOriginPicBtn.setCompoundDrawables(null, null, icon, null);
    }


    private Map<Long, Boolean> getHdLoadInfo() {
        return getCommand().getHdLoadMap();
    }

    private Map<Long, SinglePhotoPresenter.DownloadInfo> getRawLoadInfo() {
        return getCommand().getRawLoadMap();
    }

    @Override
    public void onPause() {
        if (mAdapter != null) {
            mAdapter.onPause(viewPager.getCurrentItem());
        }
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onResume(viewPager.getCurrentItem());
        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
        super.onDestroy();
    }
}
