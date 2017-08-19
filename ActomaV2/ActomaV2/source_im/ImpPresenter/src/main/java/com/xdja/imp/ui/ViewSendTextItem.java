package com.xdja.imp.ui;

import android.support.annotation.NonNull;

import com.xdja.imp.R;
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
//@ContentView(R.layout.chatdetail_item_sendtext)
public class ViewSendTextItem extends ViewChatDetailSendItem {
    public ViewSendTextItem() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendtext;
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }
    private void initView() {
        //发送内容
        setSendContentText(getCommand().getShowContentFromString(dataSource));
        // add by ycm for Task 2632 [start]
        new RecognizeHyperlink().recognizeHyperlinks(new ViewChatDetailBaseItem.MyLongClick() {
            @Override
            public void onLongClick() {
                longClickMsg();
            }
        }, sendContentTextView, getActivity(), ConstDef.ALL);
        // add by ycm for Task 2632 [end]
    }

    /**
     * 设置发送图片的缩略图
     * @param contentText
     */
    private void setSendContentText(CharSequence contentText){
        if(sendContentTextView != null){
            sendContentTextView.setText(contentText);
        }
    }
}
