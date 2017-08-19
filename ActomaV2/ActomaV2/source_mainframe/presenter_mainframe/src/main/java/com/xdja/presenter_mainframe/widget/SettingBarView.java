package com.xdja.presenter_mainframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.presenter_mainframe.R;
import com.xdja.comm.circleimageview.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldy on 16/4/28.
 * 用于设置相关,一般样式为左侧为栏目信息,偏右侧是细节文字(可选),最右侧是一个向右的箭头(可选,可更改为其它图片),
 * 下侧有一个分隔线,上侧可能有一个下划线
 * <p>调用{@link #setImgIsShow(boolean)}隐藏右侧的图片后,偏右侧的文字会自动移动到右边</p>
 */
public class SettingBarView extends LinearLayout {

    @Bind(R.id.view_setting_right_image)
    CircleImageView viewSettingRightImg;
    @Bind(R.id.tv_setting_bar_second_text)
    TextView tvSettingBarSecondText;
    @Bind(R.id.tv_setting_bar_first_text)
    TextView tvSettingBarFirstText;
    @Bind(R.id.view_setting_top_divider)
    View viewSettingTopDivider;

    public SettingBarView(Context context) {
        this(context,null);
    }


    public SettingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @SuppressWarnings("NumericCastThatLosesPrecision")
    public SettingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        if (attrs == null)
            return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingBarView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SettingBarView_android_text:
                    tvSettingBarFirstText.setText(a.getString(attr));
                    break;
                case R.styleable.SettingBarView_secondText:
                    setSecondText(a.getString(attr));
                    break;
                case R.styleable.SettingBarView_android_src:
                    viewSettingRightImg.setImageDrawable(a.getDrawable(attr));
                    break;
                case R.styleable.SettingBarView_imageWidth:
                    viewSettingRightImg.getLayoutParams().width = (int) a.getDimension(attr, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
                case R.styleable.SettingBarView_imageHeight:
                    viewSettingRightImg.getLayoutParams().height = (int) a.getDimension(attr, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
                case R.styleable.SettingBarView_isShowArrow:
                    setImgIsShow(a.getBoolean(attr,true));
                    break;
                case R.styleable.SettingBarView_isShowTopLine:
                    setTopLineIsShow(a.getBoolean(attr, false));
                    break;
            }
        }

        a.recycle();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_setting_bar, this);
        ButterKnife.bind(this);
    }

    public TextView getTvSettingBarFirstText(){
        return tvSettingBarFirstText;
    }

    public void setText(CharSequence text){
        tvSettingBarFirstText.setText(text);
    }

    public void setSecondText(CharSequence text){
        tvSettingBarSecondText.setText(text);
    }

    public void setImgIsShow(boolean isShow){
        if (isShow){
            viewSettingRightImg.setVisibility(VISIBLE);
        }else {
            viewSettingRightImg.setVisibility(GONE);
        }
    }

    public void setTopLineIsShow(boolean isShow){
        if (isShow){
            viewSettingTopDivider.setVisibility(VISIBLE);
        }else {
            viewSettingTopDivider.setVisibility(GONE);
        }
    }

    public CircleImageView getRightImage(){
        return viewSettingRightImg;
    }
}
