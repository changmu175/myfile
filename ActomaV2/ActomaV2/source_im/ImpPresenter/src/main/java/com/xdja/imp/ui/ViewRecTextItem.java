package com.xdja.imp.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.util.RecognizeHyperlink;

/**
 * Created by jing on 2015/12/28.
 * 功能描述
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for hyperlink click by ycm at 20161104.
 * 3)Task 2632, modify for hyperlink click by ycm at 20161130.
 */
public class ViewRecTextItem extends ViewChatDetailRecItem {
    /**
     * 接收内容
     */
    private TextView recContentTextView;

    /**
     * 闪信动画
     */
    private ImageView bombAnimImageView;


    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_rectext;
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            recContentTextView = (TextView) view.findViewById(R.id.txt_chat_sendcontent);
            bombAnimImageView = (ImageView) view.findViewById(R.id.bomb_anim);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);

        initView();
    }

    private void initView() {
        if (dataSource != null) {
            //如果消息已经销毁
            if (dataSource.getMessageState() == ConstDef.STATE_DESTROY) {
                setMessageDestroy(true);
                setRecContentTextColor(getCommand().getLimitTextColor());
                setRecContentText(R.string.textMessageIsDestoryed);
            } else {
                setMessageDestroy(false);
                setRecContentTextColor(getCommand().getNormalTextColor());
                if (dataSource.getContent() != null && dataSource.getContent().length() > 0) {
                    setRecContentText(getCommand().getShowContentFromString(dataSource));
                    //add by ycm for Task 2632 [start]
                    new RecognizeHyperlink().recognizeHyperlinks(new MyLongClick() {
                        @Override
                        public void onLongClick() {
                            longClickMsg();
                        }
                    }, recContentTextView,getActivity(), ConstDef.ALL);
                    //add by ycm for Task 2632 [end]
                    LogUtil.getUtils().d("接收内容：dataSource =  " + getCommand().getShowContentFromString(dataSource));
                } else {
                    setRecContentText("");
                }
                LogUtil.getUtils().d(dataSource.getMessageState());
                //开始执行销毁动画
                if (dataSource.getMessageState() == ConstDef.STATE_DESTROYING) {
                    //add by gr@xdja.com ,fix bug NACTOMA-413
                    final TalkMessageBean cloneSource = obtainCloneObj(dataSource);
                    //end
                    startBombAnim();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            getCommand().postDestroyAnimate(cloneSource);
                        }
                    }, 700);

                }
                if (dataSource.getMessageState() < ConstDef.STATE_READED) {
                    //如果当前界面正在显示，并且消息状态是初始状态，发送已阅读回执
                    if (getCommand().getActivityIsShowing()) {
                        //发送阅读回执
                        getCommand().sendReadReceipt(dataSource);
                    }
                }
            }
        }
    }

    /**
     * @param bean
     * @return
     */
    private TalkMessageBean obtainCloneObj(TalkMessageBean bean) {
        return new TalkMessageBean(bean);
    }

    /**
     * 设置文本内容
     *
     * @param contentText
     */
    private void setRecContentText(CharSequence contentText) {
        if (recContentTextView != null) {
            recContentTextView.setText(contentText);
        }
    }

    /**
     * 设置文本内容
     *
     * @param contentTextId
     */
    private void setRecContentText(int contentTextId) {
        if (recContentTextView != null) {
            recContentTextView.setText(contentTextId);
        }
    }

    private void setRecContentTextColor(int colorId) {
        if (recContentTextView != null) {
            recContentTextView.setTextColor(colorId);
        }
    }

    /**
     * 开始闪信动画
     */
    private void startBombAnim() {
        if (bombAnimImageView != null) {
            AnimationDrawable boomAnim = (AnimationDrawable) bombAnimImageView.getBackground();
            boomAnim.stop();
            boomAnim.start();
            LogUtil.getUtils().d("开始播放闪信动画");
        }

    }

    //add by zya 20170322
    @Override
    public void onViewReused() {
        super.onViewReused();
        contentLayout.setBackgroundResource(0);
    }
}
