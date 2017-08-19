package com.xdja.imp.ui;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.xdja.comm.widget.LazyLoadGridView;
import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IPictureSelectCommand;
import com.xdja.imp.ui.vu.IPictureSelectVu;

/**
 * 图片选择界面View
 * Created by leill on 2016/6/16.
 */
public class ViewPictureSelect extends ImpActivitySuperView<IPictureSelectCommand>
        implements IPictureSelectVu, View.OnClickListener {

    /**
     * 默认支持最大选择9张图片
     */
    private static final int MAX_SELECT_CNT = 9;

    /**
     * 图片显示列表控件
     */
    private LazyLoadGridView mPictureGv;

    /**
     * 预览控件
     */
    private Button mPreviewBtn;

    /**
     * 发送按钮控件
     */
    private Button mSendBtn;

    /**无图片是的布局*/
    private LinearLayout emptyLayout;

    /**有图片时的布局*/
    private RelativeLayout gvLayout;

    /**底部布局，需要获取并在从会话详情界面跳转的情况下隐藏背景*/
    private RelativeLayout bottomLayout;

    /**进度，点击发送按钮后，需要生成需要发送的图片得相关数据，比如缩略图、高清缩略图等信息*/
    private ProgressBar mLoadProgressBar;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_picture_select;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null) {
            mPictureGv = (LazyLoadGridView) view.findViewById(R.id.gv_pic_select);
            mPreviewBtn = (Button) view.findViewById(R.id.btn_preview);
            mSendBtn = (Button) view.findViewById(R.id.btn_send);
            emptyLayout = (LinearLayout)view.findViewById(R.id.empty_list_layout);
            gvLayout = (RelativeLayout)view.findViewById(R.id.gv_pic_layout);
            bottomLayout = (RelativeLayout)view.findViewById(R.id.bottombar_layout);
            mLoadProgressBar = (ProgressBar) view.findViewById(R.id.loadProgress);

            mPreviewBtn.setOnClickListener(this);
            mSendBtn.setOnClickListener(this);

            //设置为空时提示View；
            View emptyListView = LayoutInflater.from(getContext()).inflate(R.layout.empty_listview_layout, null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            ((ViewGroup) mPictureGv.getParent()).addView(emptyListView, params);
            ((ViewGroup) mPictureGv.getParent()).setPadding(0, 0, 0, 0);
            mPictureGv.setEmptyView(emptyListView);
        }
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
    }


    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void initListView(BaseAdapter adapter) {
        mPictureGv.setAdapter(adapter);
        if(adapter.getCount() == 0){
            mSendBtn.setEnabled(false);
            mPreviewBtn.setEnabled(false);
            mSendBtn.setClickable(false);
            mPreviewBtn.setClickable(false);
        }
    }

    @Override
    public void refreshSelectPictureIndicator(int selectCnt) {

        if (selectCnt > 0) {
            //设置可点击
            mSendBtn.setClickable(true);
            mPreviewBtn.setClickable(true);
            mSendBtn.setEnabled(true);
            mPreviewBtn.setEnabled(true);
            //动态设置大小
            ViewGroup.LayoutParams params = mSendBtn.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mSendBtn.setLayoutParams(params);
            //文字内容和颜色
            mSendBtn.setText(String.format(getStringRes(R.string.picture_send_indicator), selectCnt,
                    MAX_SELECT_CNT));
            mSendBtn.setTextColor(Color.parseColor("#F3000000"));
            mPreviewBtn.setTextColor(Color.parseColor("#F3000000"));
        } else {
            //动态设置大小，默认长度 46dp
            ViewGroup.LayoutParams params = mSendBtn.getLayoutParams();
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
                    getContext().getResources().getDisplayMetrics());
            mSendBtn.setLayoutParams(params);
            //文字内容和颜色
            mSendBtn.setText(R.string.send);
            mSendBtn.setTextColor(Color.parseColor("#77000000"));
            mPreviewBtn.setTextColor(Color.parseColor("#77000000"));
            //设置不可点击
            mSendBtn.setEnabled(false);
            mPreviewBtn.setEnabled(false);
            mSendBtn.setClickable(false);
            mPreviewBtn.setClickable(false);
        }
    }

    @Override
    public void localPicLoadFinish(boolean isSelectEmpty) {
        if(mPictureGv.getAdapter().getCount() != 0 && !isSelectEmpty){
            mSendBtn.setEnabled(true);
            mPreviewBtn.setEnabled(true);
            mSendBtn.setClickable(true);
            mPreviewBtn.setClickable(true);
        }
    }

    @Override
    public void showEmptyImage() {
        emptyLayout.setVisibility(View.VISIBLE);
        gvLayout.setVisibility(View.GONE);
    }

    @Override
    public void hidePreAndSendBtn() {
        mSendBtn.setVisibility(View.GONE);
        mPreviewBtn.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
    }

    @Override
    public void resetSendStatus() {
        mLoadProgressBar.setVisibility(View.GONE);
        mSendBtn.setText(R.string.send);
        mSendBtn.setEnabled(true);
        mPreviewBtn.setEnabled(true);
    }

    /**
     * “发送”按钮点击事件
     */
    @Override
    public void onClick(View v) {

        int resId = v.getId();
        if (resId == R.id.btn_send) { //发送按钮

            //图片加载进度
            mLoadProgressBar.setVisibility(View.VISIBLE);
            mSendBtn.setText(R.string.sending);
            mSendBtn.setEnabled(false);
            mPreviewBtn.setEnabled(false);

            getCommand().sendPictureMessage();

        } else if (resId == R.id.btn_preview) {//预览按钮

            getCommand().startToPreviewPictures();
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.picture_select);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
