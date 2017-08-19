package com.xdja.imp.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IPicturePreviewCommand;
import com.xdja.imp.ui.vu.IPicturePreviewVu;
import com.xdja.imp.util.FileSizeUtils;
import com.xdja.imp.util.PicInfoCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片预览界面
 */
public class ViewPicturePreview extends ImpActivitySuperView<IPicturePreviewCommand>
        implements IPicturePreviewVu , ViewPager.OnPageChangeListener, View.OnClickListener{

    /** 图片选中最大个数，默认9张*/
    private static final int MAX_SELECT_COUNT = 9;
    /** ViewPager加载图片*/
    private ViewPager mViewPager;
    /** 选择控件*/
    private ImageButton mSelectImgBtn;
    /** 原图选择控件*/
    private Button mOriginalImgBtn;
    /** 发送控件*/
    private Button mSendBtn;

    /** 所有图片集合*/
    private Map<String , LocalPictureInfo> pictureInfos;

    private List<LocalPictureInfo> dataSources = new ArrayList<>();

    /** 当前选中item索引*/
    private int mCurrentItemIndex ;

    /** 图片总数*/
    private int mTotalItemCount ;
    /** 发送图片加载进度条*/
    private ProgressBar mLoadProgressBar;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_picture_preview;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null){
            mViewPager = (ViewPager) view.findViewById(R.id.vp_picture_preview);
            mSendBtn = (Button) view.findViewById(R.id.btn_send);
            mSelectImgBtn = (ImageButton) view.findViewById(R.id.btn_select);
            mOriginalImgBtn = (Button) view.findViewById(R.id.btn_original_pic_select);
            mLoadProgressBar = (ProgressBar) view.findViewById(R.id.loadProgress);

            //设置监听器
            mSendBtn.setOnClickListener(this);
            mSelectImgBtn.setOnClickListener(this);
            mViewPager.setOnPageChangeListener(this);
            mOriginalImgBtn.setOnClickListener(this);
        }
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        pictureInfos = PicInfoCollection.getLocalPicInfo();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void initViewPager(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void setDataSource(List<LocalPictureInfo> dataSource) {
        this.dataSources = dataSource;
        mTotalItemCount = dataSources.size();
        //刷新标题指示器内容
        refreshTitleIndicator();
        //刷新已选图片指示器
        refreshSelectPictureIndicator();
    }

    @Override
    public void setCurrentItem(int item) {
        LogUtil.getUtils().d("setCurrentItem...item:" + item);
        mViewPager.setCurrentItem(item);
        refreshLocalPictureInfo(item);
    }

    @Override
    public void refreshTitleIndicator() {
        getActivity().setTitle(String.format(getStringRes(R.string.picture_select_indicator2),
                (mCurrentItemIndex + 1), mTotalItemCount));
    }

    @Override
    public void refreshSelectPictureIndicator() {
        //当前已经选择的图片个数
		/**
     * 修改人 guorong
     * 时间 2016-8-2 15:38:02
     * 修改从照相界面跳转到预览界面后仍然有图片勾选按钮的bug
     * bug号 ： 无
     * */
        int selectCnt = getSelectedCount();
        if (selectCnt > 0 && !getCommand().isFromTakePhoto()) {
            setSendBtnClickable(true);
            mSelectImgBtn.setVisibility(View.VISIBLE);
            mSendBtn.setText(String.format(getStringRes(R.string.picture_send_indicator), selectCnt,
                    MAX_SELECT_COUNT));
        } else if (selectCnt > 0 && getCommand().isFromTakePhoto()) {
            mSelectImgBtn.setVisibility(View.GONE);
            setSendBtnClickable(true);
            mSendBtn.setText(R.string.send);
        } else {
            mSelectImgBtn.setVisibility(View.VISIBLE);
            setSendBtnClickable(false);
            mSendBtn.setText(R.string.send);
        }
    }

    @Override
    public void refreshLocalPictureInfo(int position) {

        LogUtil.getUtils().d("refreshLocalPictureInfo position:" + position);

        LocalPictureInfo pictureInfo = dataSources.get(position);

        LogUtil.getUtils().d("refreshLocalPictureInfo:" + pictureInfo.toString());

        //图片大小信息
        String fileSizeContent = FileSizeUtils.getAutoFileOrFilesSize(pictureInfo.getLocalPath());
        mOriginalImgBtn.setText(String.format(getStringRes(R.string.original_image2),
                fileSizeContent));
        //图片选中状态信息
        mSelectImgBtn.setImageResource(pictureInfo.getStatue() == LocalPictureInfo.Statue.STATUE_SELECTED ?
                    R.drawable.icon_selected_on :
                    R.drawable.icon_selected_off);

        //图片原图和非原图状态
        if (pictureInfo.isOriginalPic()){ //已经选择原图
            //更新原图显示图片
            //guorong@xdja.com 选择原图时字体为高亮
			//todo guorong 使用color
            mOriginalImgBtn.setTextColor(Color.parseColor("#F3000000"));
            setOriginalPictureResource(R.drawable.icon_selected_on);
        } else {
            //更新原图显示图片
            mOriginalImgBtn.setTextColor(Color.parseColor("#77000000"));
            setOriginalPictureResource(R.drawable.icon_selected_off);
        }
    }

    @Override
    public int getSelectedCount() {
        int i = 0;
        for(LocalPictureInfo info: dataSources){
            //过滤出所有图片中状态为已经被选择的
            if(LocalPictureInfo.Statue.STATUE_SELECTED == info.getStatue()){
                i++;
            }
        }
        return i;
    }

    //start: add by ycm 2016/9/5
    @Override
    public void resetSendStatus() {
        mLoadProgressBar.setVisibility(View.GONE);
        mSendBtn.setEnabled(true);
        mOriginalImgBtn.setEnabled(true);
    }
    //end: add by ycm 2016/9/5

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //LogUtil.getUtils().d("onPageScrolled...position:" + position);
    }

    @Override
    public void onPageSelected(int position) {

        LogUtil.getUtils().d("onPageSelected...position:" + position);

        mCurrentItemIndex = position;
        //更新标题指示器
        refreshTitleIndicator();
        //更新当前已选图片信息
        refreshLocalPictureInfo(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        LogUtil.getUtils().d("onPageScrollStateChanged...state:" + state);
    }

    @Override
    public void onClick(View v) {

        int resId = v.getId();

        if (resId == R.id.btn_send){
            mLoadProgressBar.setVisibility(View.VISIBLE);
            mSendBtn.setEnabled(false);
            mOriginalImgBtn.setEnabled(false);
            //发送按钮
            getCommand().sendPictureMessage();

        } else if (resId == R.id.btn_select) {//选择按钮

            //获取当前显示图片
            LocalPictureInfo curPicInfo = dataSources.get(mCurrentItemIndex);
            if (LocalPictureInfo.Statue.STATUE_SELECTED == curPicInfo.getStatue()) { //图片已选
                cancelSelectPicture(curPicInfo);
                setOriginalPictureResource(R.drawable.icon_selected_off);
            } else {                                      //图片未选择
                selectPicture(curPicInfo);
            }
            refreshSelectPictureIndicator();
        } else if (resId == R.id.btn_original_pic_select ){ //原图选择按钮

            //获取当前显示图片
            LocalPictureInfo curPicInfo = dataSources.get(mCurrentItemIndex);

            if (curPicInfo.isOriginalPic()){ //已经选择原图
                //取消选择原图
                mOriginalImgBtn.setTextColor(Color.parseColor("#77000000"));
                unCheckOriginalPicture(curPicInfo);
            } else {                         //未选择原图
                //选择原图
                mOriginalImgBtn.setTextColor(Color.parseColor("#F3000000"));
                checkOriginalPicture(curPicInfo);
                refreshSelectPictureIndicator();
            }
        }
    }


    /******************************相关状态封装接口****************************/
    /**
     * 设置图片选中状态
     * @param pictureInfo
     */
    private void selectPicture(LocalPictureInfo pictureInfo){
        if (!isSelectable()){
            return ;
        }
        //设置状态
        pictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_SELECTED);
        pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
        dataSources.set(mCurrentItemIndex , pictureInfo);
        mSelectImgBtn.setImageResource(R.drawable.icon_selected_on);
    }

    /**
     * 取消图片选中状态
     * @param pictureInfo
     */
    private void cancelSelectPicture(LocalPictureInfo pictureInfo){
        //重新添加数据
        pictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_UNCHECKED);
        pictureInfo.setOriginalPic(false);
        pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
        dataSources.set(mCurrentItemIndex , pictureInfo);
        mSelectImgBtn.setImageResource(R.drawable.icon_selected_off);
    }

    /**
     * 选中原图
     * @param pictureInfo
     */
    private void checkOriginalPicture(LocalPictureInfo pictureInfo) {
        /**
         * 修改人 guorong
         * 时间 2016-8-5 15:11:08
         * 解决可以选择多于九张图片并发送的bug
         * bug号 ：2451
         * */

        if(pictureInfo.getStatue() != LocalPictureInfo.Statue.STATUE_SELECTED){
            if (!isSelectable()){
                return ;
            }
        }
        LogUtil.getUtils().d("checkOriginalPicture pictureInfo:" + pictureInfo.toString());
        pictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_SELECTED);
        pictureInfo.setOriginalPic(true);
        pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
        dataSources.set(mCurrentItemIndex, pictureInfo);
        //更新选择图标
        mSelectImgBtn.setImageResource(R.drawable.icon_selected_on);
        //更新原图显示图片
        setOriginalPictureResource(R.drawable.icon_selected_on);
    }

    /**
     * 取消选中原图
     * @param pictureInfo
     */
    private void unCheckOriginalPicture(LocalPictureInfo pictureInfo){

        LogUtil.getUtils().d("unCheckOriginalPicture pictureInfo:" + pictureInfo.toString());

        //当前图片已经选择，并且已经选择原图
        pictureInfo.setOriginalPic(false);
        pictureInfos.put(pictureInfo.getLocalPath() , pictureInfo);
        dataSources.set(mCurrentItemIndex, pictureInfo);
        //更新显示图片
        setOriginalPictureResource(R.drawable.icon_selected_off);
    }

    /**
     *  是否可以继续选择图片，默认最多支持@MAX_SELECT_COUNT 张图片
     * @return
     */
    private boolean isSelectable(){
        if (getSelectedCount() >= MAX_SELECT_COUNT) {
            XToast.show(getContext(), String.format(getStringRes(R.string.select_pic_hint),
                    MAX_SELECT_COUNT));
            return false;
        }
        return true;
    }

    /**
     * 更改原图选中图标
     * @param resId
     */
    private void setOriginalPictureResource(int resId){
        //更新原图显示图片
        Drawable unSelectDrawable = getDrawableRes(resId);
        if (unSelectDrawable != null) {
            unSelectDrawable.setBounds(0, 0, unSelectDrawable.getIntrinsicWidth(),
                    unSelectDrawable.getIntrinsicHeight());
            mOriginalImgBtn.setCompoundDrawables(unSelectDrawable, null, null, null);
        }
    }


    /**
     * 发送按钮是否可点击
     * @param clickable
     */
    private void setSendBtnClickable(boolean clickable){
        if (clickable){
            //设置可点击
            mSendBtn.setClickable(true);
            //动态设置大小
            ViewGroup.LayoutParams params = mSendBtn.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mSendBtn.setLayoutParams(params);
            mSendBtn.setTextColor(Color.parseColor("#F3000000"));
        } else {
            //设置不可点击
            mSendBtn.setClickable(false);
            //动态设置大小，默认长度 46dp
            ViewGroup.LayoutParams params = mSendBtn.getLayoutParams();
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
                    getContext().getResources().getDisplayMetrics());
            mSendBtn.setLayoutParams(params);
            //文字颜色
            mSendBtn.setTextColor(Color.parseColor("#77000000"));
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.picture_preview);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
