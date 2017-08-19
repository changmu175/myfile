package com.xdja.imp.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.frame.mvp.view.FragmentSuperView;
import com.xdja.imp.presenter.command.ILocalFileListCommand;
import com.xdja.imp.ui.vu.ILocalFileListVu;

/**
 * <p>Author: xdjaxa         </br>
 * <p>Date: 2016/12/5 9:53   </br>
 * <p>Package: com.xdja.imp.ui</br>
 * <p>Description: 本地文件列表界面  </br>
 */
public class ViewLocalFileList extends FragmentSuperView<ILocalFileListCommand>
        implements ILocalFileListVu<ILocalFileListCommand>, View.OnClickListener,
        ViewPager.OnPageChangeListener{

    /**
     * tab标签页数据
     */
    private final int[] mTabSrcId = new int[]{
            R.id.tv_local_video,
            R.id.tv_local_image,
            R.id.tv_local_document,
            R.id.tv_local_application,
            R.id.tv_local_other
    };

    private ViewPager mViewPager;

    /**
     * tab指示标签
     */
    private ImageView mCursorImg;

    /**
     * 指示标签宽度
     */
    private int mCursorW = 0;

    private TextView[] mTabTvs;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_local_file;
    }

    @Override
    protected void injectView() {
        super.injectView();

        if (getView() != null) {
            mViewPager = (ViewPager) getView().findViewById(R.id.pager_local_files);
            mCursorImg = (ImageView) getView().findViewById(R.id.img_cursor);
            TextView mTabVideoTv = (TextView) getView().findViewById(R.id.tv_local_video);
            TextView mTabImageTv = (TextView) getView().findViewById(R.id.tv_local_image);
            TextView mTabDocxTv = (TextView) getView().findViewById(R.id.tv_local_document);
            TextView mTabAppTv = (TextView) getView().findViewById(R.id.tv_local_application);
            TextView mTabOtherTv = (TextView) getView().findViewById(R.id.tv_local_other);

            mTabTvs = new TextView[]{
                    mTabVideoTv,
                    mTabImageTv,
                    mTabDocxTv,
                    mTabAppTv,
                    mTabOtherTv
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


            //tab点击事件监听器
            for (TextView mTabTv : mTabTvs) {
                mTabTv.setOnClickListener(this);
            }
            mViewPager.addOnPageChangeListener(this);
        }
    }

    @Override
    public void setFragmentAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int mCurrentIndex = indexOfTabIndex(view.getId());
        mViewPager.setCurrentItem(mCurrentIndex, false);
    }

    /**
     * 根据值获取数组索引值
     *
     * @param value 数组中的值
     * @return
     */
    private int indexOfTabIndex(int value) {
        for (int i = 0; i < mTabSrcId.length; i++)
            if (mTabSrcId[i] == value) {
                return i;
            }
        return 0;
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
