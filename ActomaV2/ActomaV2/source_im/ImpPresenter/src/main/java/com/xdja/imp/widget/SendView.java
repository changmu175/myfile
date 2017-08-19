package com.xdja.imp.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.xdja.imp.R;
import com.xdja.imp.util.DisplayUtils;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频发送控件     <br>
 * 创建时间：2017/1/20       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class SendView extends RelativeLayout {

    public RelativeLayout backLayout,selectLayout;

    public SendView(Context context) {
        super(context);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        @SuppressLint("InflateParams") RelativeLayout layout = (RelativeLayout) LayoutInflater.
                from(context).inflate(R.layout.widget_view_send_btn,null,false);
        layout.setLayoutParams(params);
        backLayout = (RelativeLayout) layout.findViewById(R.id.btn_rerecored);
        selectLayout = (RelativeLayout) layout.findViewById(R.id.btn_send);
        addView(layout);
        setVisibility(GONE);
    }

    public void startAnim(Context context){
        setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        int width = DisplayUtils.getWidthPixels(context);
        int viewPad = DisplayUtils.dp2px(context, 58);
        set.playTogether(
                ObjectAnimator.ofFloat(backLayout,"translationX",0, 191/2 + viewPad - width/2 ),
                ObjectAnimator.ofFloat(selectLayout,"translationX",0, width/2 - 191/2 - viewPad )
        );
        set.setDuration(250).start();
    }

    public void stopAnim(Context context){
        AnimatorSet set = new AnimatorSet();
        int width = DisplayUtils.getWidthPixels(context);
        int viewPad = DisplayUtils.dp2px(context, 58);
        set.playTogether(
                ObjectAnimator.ofFloat(backLayout,"translationX", 191/2 + viewPad - width/2, 0),
                ObjectAnimator.ofFloat(selectLayout,"translationX", width/2 - 191/2 - viewPad, 0)
        );
        set.setDuration(250).start();
        setVisibility(GONE);
    }

}
