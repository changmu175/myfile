package com.xdja.imp.ui;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IFileExplorerCommand;
import com.xdja.imp.ui.vu.IFileExplorerVu;
import com.xdja.imp.util.FileSizeUtils;

/**
 * <p>Author: leiliangliang </p>
 * <p>Date: 2016/11/29 10:45</p>
 * <p>Package: com.xdja.imp.ui</p>
 * <p>Description: 文件浏览定义View层</p>
 */
public class ViewFileExplorer extends ImpActivitySuperView<IFileExplorerCommand>
        implements IFileExplorerVu<IFileExplorerCommand>, ViewPager.OnPageChangeListener {

    /**
     * 滑动页
     */
    private ViewPager mViewPager;
    /**
     * 加载进度
     */
    private ProgressBar mLoadingPBar;
    /**
     * 已选文件大小
     */
    private TextView mSelectedFileSizeTv;
    /**
     * 发送按钮
     */
    private Button mSendBtn;

    /**
     * tab指示标签
     */
    private ImageView mCursorImg;

    /**
     * 指示标签宽度
     */
    private int mCursorW = 0;

    /**
     * 所有tab标签集合
     */
    private TextView[] mTabTvs;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_file_explorer;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public String getTitleStr() {
        return getStringRes(R.string.file_select);
    }

    @Override
    protected void injectView() {
        super.injectView();
        mViewPager = (ViewPager) getView().findViewById(R.id.pager_file_explorer);
        mLoadingPBar = (ProgressBar) getView().findViewById(R.id.pbar_loading);
        mSelectedFileSizeTv = (TextView) getView().findViewById(R.id.tv_file_select_size);
        mSendBtn = (Button) getView().findViewById(R.id.btn_send);
        mCursorImg = (ImageView) getView().findViewById(R.id.img_cursor);
        /*
      标签
     */
        TextView mTabLastFileTv = (TextView) getView().findViewById(R.id.tv_last_files);
        TextView mTabLocalFileTv = (TextView) getView().findViewById(R.id.tv_local_files);

        mTabTvs = new TextView[]{
                mTabLastFileTv,
                mTabLocalFileTv
        };
        mTabTvs[0].setTextColor(getColorRes(R.color.base_black_95));

        //设置tab指示器大小
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        mCursorW = screenW / mTabTvs.length;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCursorImg.getLayoutParams();
        params.width = mCursorW;
        mCursorImg.setLayoutParams(params);

        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        mLoadingPBar.setVisibility(visibility);
    }

    @Override
    public void setFragmentAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void setViewPagerCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
        resetTabColor();
        mTabTvs[item].setTextColor(getColorRes(R.color.base_black_95));
        cursorAnim(item);
    }

    @Override
    public void setCurrentSelectedFileSize(long size) {
        mSelectedFileSizeTv.setText(String.format(getStringRes(R.string.file_select_size),
                FileSizeUtils.FormetFileSize(size)));
    }

    @Override
    public void setCurrentSelectedFileCount(int selectCount) {
        if (selectCount == 0) {
            mSendBtn.setText(R.string.send);
            mSendBtn.setClickable(false);
            mSendBtn.setTextColor(Color.parseColor("#77000000"));
        } else {
            mSendBtn.setClickable(true);
            mSendBtn.setTextColor(Color.parseColor("#F3000000"));
            mSendBtn.setText(String.format(getStringRes(R.string.send_indicator), selectCount));
        }
    }

    /**
     * 指示器的跳转
     *
     * @param curItem 当前所处的页面的下标
     */
    private void cursorAnim(int curItem) {
        /*
      指示标签的横坐标
     */
        int mCursorX = 0;
        mCursorImg.setX(mCursorX + mCursorW * curItem);
    }

    private void resetTabColor() {
        for (TextView mTabTv : mTabTvs) {
            mTabTv.setTextColor(getColorRes(R.color.base_black_37));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTabColor();
        mTabTvs[position].setTextColor(getColorRes(R.color.base_black_95));
        cursorAnim(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
