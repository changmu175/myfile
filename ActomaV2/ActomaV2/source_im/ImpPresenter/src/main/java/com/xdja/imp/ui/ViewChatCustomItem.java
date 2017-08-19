package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * Created by Administrator on 2016/3/17.
 * 功能描述
 */

//@ContentView (R.layout.chatdetail_item_presentation)
public class ViewChatCustomItem extends ViewChatDetailBaseItem {

//    @InjectView(R.id.presentationText)
private TextView presentation;
	
	@Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_presentation;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null) {
            presentation = (TextView)view.findViewById(R.id.presentationText);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView(dataSource);
    }

    private  void initView(final TalkMessageBean dataSource){
        if(dataSource != null){
            presentation.setText(dataSource.getContent());
        }
    }
}
