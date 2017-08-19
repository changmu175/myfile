package com.xdja.presenter_mainframe.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ProductIntroduceCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.ProductIntroduceVu;
import com.xdja.presenter_mainframe.widget.FlipPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenbing 2015-7-28
 * 产品介绍页
 */
@ContentView(value = R.layout.activity_productintroduce)
public class ViewProductIntroduce extends ActivityView<ProductIntroduceCommand> implements ProductIntroduceVu {

    @Bind(R.id.productintroduce_root)
    View pageRoot;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.welcome_start_step_btn)
    TextView welcomeStartStepBtn;
    @Bind(R.id.flippoint)
    FlipPoint flippoint;


    //介绍界面
    private ArrayList<View> views;

    private int[] images = null;

    private BasePagerAdapter pagerAdapter;

    //是否隐藏欢迎按钮
    private boolean isHideWelcomeBtn = false;

    @Override
    public void onCreated() {
        super.onCreated();
        //[s]modify by xnn for private version @20170221
        if (CustInfo.isCustom()) {
            images = new int[]{R.mipmap.guide_1, R.mipmap.guide_2};
        }else {
           if(CommonUtils.isZH(getContext())){
               if(UniversalUtil.isXposed()){
                   images = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3,R.mipmap.guide_4};
               }else{
                   images = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_4};
               }
           }else{
               images = new int[]{R.mipmap.guide_1, R.mipmap.guide_2};
           }
        }//[e]modify by xnn for private version @20170221
        //设置toolbar背景颜色透明
        //toolbar.setBackgroundColor(getContext().getResources().getColor(com.xdja.contact.R.color.transparent));
        initView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    // 初始化视图
    @SuppressWarnings("deprecation")
    private void initView() {
//        if (!UniversalUtil.isXposed()) {
//            images = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_4};
//            flippoint.setTotal(3);
//        }
        // 实例化视图控件
        flippoint.setTotal(images.length);
        views = new ArrayList<View>();
        for (int i = 0; i < images.length; i++) {
            // 循环加入图片
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(images[i]);
            views.add(imageView);
        }

        pagerAdapter = new BasePagerAdapter(views);
        viewpager.setAdapter(pagerAdapter); // 设置适配器
        viewpager.setOnPageChangeListener(pageChangeListener);
    }

    //viewpager的改变事件
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //如果欢迎按钮显示，就进行淡入淡出功能
//            if (!isHideWelcomeBtn) {
//                if (position == images.length - 2) {
//                    if (positionOffset >= 0) {
//                        welcomeStartStepBtn.setAlpha(positionOffset);
//                        flippoint.setAlpha(1 - positionOffset);
//                    }
//                }
//                if (position != images.length -1){
//                    welcomeStartStepBtn.setEnabled(false);
//                }
//                else {
//                    welcomeStartStepBtn.setEnabled(true);
//                }
//            } else {
//                welcomeStartStepBtn.setEnabled(false);
//            }
        }

        @Override
        public void onPageSelected(int position) {
            flippoint.setIndex(position);
            if (position == images.length -1) {
                welcomeStartStepBtn.setAlpha(1);
                welcomeStartStepBtn.setVisibility(View.VISIBLE);
				//[s]add by xnn for bug 9530 review by tangsha @20170307
                if(UniversalUtil.getLanguageType(getActivity()) == UniversalUtil.LANGUAGE_CH_SIMPLE){
                    welcomeStartStepBtn.setBackgroundResource(R.drawable.btn_welcome_enter_selector);
                }else {
                    welcomeStartStepBtn.setBackgroundResource(R.drawable.btn_welcome_enter_selector_en);
                }
				//[e]add by xnn for bug 9530 review by tangsha @20170307
                welcomeStartStepBtn.setEnabled(true);
                flippoint.setVisibility(View.GONE);
            }else{
                welcomeStartStepBtn.setVisibility(View.GONE);
                welcomeStartStepBtn.setEnabled(false);
                flippoint.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    //判断是否从设置该中进入欢迎页
    @OnClick(R.id.welcome_start_step_btn)
    public void startUseBtnClick() {
        getCommand().finishIntroduce();
//        boolean isSettingWelcome = getActivity().getIntent().getBooleanExtra(ProductIntroductionAnimPresenter.IS_SETTING_WELCOME, false);
//        if(isSettingWelcome){
//            //从设置中进入欢迎页
//            getActivity().finish();
//        }else {
//            //首次进入安通+欢迎页
//            if (LoginPresenter.isConnect(getContext())) {
//            showCommonProgressDialog("正在加载...");
//            getCommand().finishIntroduce();}
//            else {
//            XToast.show(getContext(), ATErrorHandler.EXCEPTION_NET_UNREACHABLE_MESSAGE);
//        }
//        }
    }


    @Override
    public void isHideWelcomeBtn(boolean isHide) {
        isHideWelcomeBtn = isHide;
    }

    @Override
    public void setHideFlippoint(boolean isHide) {
        if (flippoint != null) flippoint.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        dismissCommonProgressDialog();
    }

//    /**
//     * 是否显示导航栏
//     *
//     * @param isShow
//     */
//    @Override
//    public void showToolBar(boolean isShow) {
//        if (isShow) {
//            ((ActionBarActivity) getActivity()).getSupportActionBar().show();
//        }
//    }

    //引导页使用的pageview适配器
    public class BasePagerAdapter extends PagerAdapter {
        private List<View> views = new ArrayList<View>();

        public BasePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GcMemoryUtil.clearMemory(pageRoot);
    }

    //    @Override
//    protected int getToolbarType() {
//        return ToolbarDef.NAVIGATE_BACK;
//    }
}
