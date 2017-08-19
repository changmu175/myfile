package com.xdja.imp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.adapter.ChooseIMSessionAdapterPresenter;
import com.xdja.imp.presenter.adapter.SearchResultAdapter;
import com.xdja.imp.presenter.command.SessionListCommand;
import com.xdja.imp.ui.vu.ISessionListVu;
import com.xdja.imp.widget.SharePopWindow;

import java.util.List;
import java.util.Map;

/**
 * Created by yuchangmu on 2016/10/10.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Bug 5684, modify for share and forward function by ycm at 20161103.
 */
public class ViewChooseIMSession extends ImpActivitySuperView<SessionListCommand>
        implements ISessionListVu, View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listview;
    /**
     * 头像
     */
    private CircleImageView circleImageView;
    private LinearLayout createSession_ll;
    private View headView;
    private RelativeLayout more_contact;
    private TextView noResult; ///for bug 5684 by ycm
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chooseimsession;
    }

    @Override
    protected int getToolbarType() {
        return ImpActivitySuperView.ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void init(@NonNull LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        View view = getView();
        if (view != null) {
            View search_view = view.findViewById(R.id.search_layout_et);
            EditText editText = (EditText) search_view.findViewById(R.id.search_ed);
            editText.requestFocus();
            editText.setFocusable(true);
            editText.addTextChangedListener(new TextChangedListener());
            listview = (ListView) view.findViewById(R.id.chatList);
            noResult = (TextView) view.findViewById(R.id.search_no_result); //for bug 5684
            headView = LayoutInflater.from(getContext()).inflate(R.layout.header_chooseimsession, null);
            listview.addHeaderView(headView);
            RelativeLayout create_new_session = (RelativeLayout) headView.findViewById(R.id.create_session_rl);
            more_contact = (RelativeLayout) headView.findViewById(R.id.more_contact_rl);
            createSession_ll = (LinearLayout) headView.findViewById(R.id.create_session);
            circleImageView = (CircleImageView) view.findViewById(R.id.self);
            create_new_session.setOnClickListener(this);
            more_contact.setOnClickListener(this);
            listview.setOnItemClickListener(this);
        }

    }
	
    @Override
    public void initListView(BaseAdapter adapter) {
        if (this.listview != null) {
            this.listview.setAdapter(adapter);
        }
    }

    @Override
    public ListView getDisplayList() {
        return this.listview;
    }

    /**
     * 加载自己的图像，此图像不显示，为了进入会话详情界面快速加载图像
     */
    @Override
    public void loadSelfImage() {
        AccountBean accountBean = AccountServer.getAccount();
        if (accountBean != null) {
            String url = accountBean.getThumbnail();
            circleImageView.loadImage(url, true, R.drawable.corp_user_40dp);
        }
    }


    @Override
    public void sharePopuOptionWindow(List<TalkListBean> talkListBean,
                                      SharePopWindow.PopWindowEvent<TalkListBean> event,
                                      Map<String, String> contactInfo, Intent intent) {
        boolean isRight = checkData(intent);
        if (!isRight) {
            LogUtil.getUtils().d("intent数据未空");
            return;
        }
        if (event != null) {
            new SharePopWindow().showSingleSharePopWindow(getActivity(), event,
                    talkListBean, contactInfo, intent);
        }
    }

    @Override
    public void handOutSharePopuOptionWindow(List<TalkListBean> talkListBeans,
                                             SharePopWindow.PopWindowEvent<TalkListBean> event,
                                             Map<String, List<String>> contactInfo, Intent intent) {
        boolean isRight = checkData(intent);
        if (!isRight) {
            LogUtil.getUtils().d("intent数据未空");
            return;
        }
        if (event != null) {
            new SharePopWindow().showHandOutSharePopWindow(getActivity(), event,
                    talkListBeans, contactInfo, intent);
        }

    }

    @Override
    public void showSelectPopWindow(Activity activity) {
        new SharePopWindow().selectActionPopWindow(activity);
    }

    /**
     *
     * @param intent 需检测的数据
     * @return isRight 数据是否正确
     */
    private boolean checkData(Intent intent) {
        boolean isRight = false;
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (!TextUtils.isEmpty(action) && !TextUtils.isEmpty(type)) {
                isRight = true;
            }
        }
        return isRight;
    }

    @Override
    public void dismissPopuDialog() {
        new SharePopWindow().dismissDialog();
    }

    @Override
    public void setChooseIMSessionAdapter(ChooseIMSessionAdapterPresenter chooseIMSessionAdapterPresenter) {
        listview.addHeaderView(headView);
        listview.setAdapter(chooseIMSessionAdapterPresenter);
    }

    @Override
    public void setLocalSearchAdapter(SearchResultAdapter searchResultAdapterAdapter) {
        listview.removeHeaderView(headView);
        listview.setAdapter(searchResultAdapterAdapter);
    }
	// 根据文件类型隐藏更多联系人选项
    private boolean type = false;
    @Override
    public void setType(boolean isFile) {
        if (isFile) {
            more_contact.setVisibility(View.GONE);
        } else {
            more_contact.setVisibility(View.VISIBLE);
        }
        type = isFile;
    }

    @Override
    public boolean getType() {
        return type;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_session_rl) {
            getCommand().createNewSession();
        }
        else if (v.getId() == R.id.more_contact_rl) {
            getCommand().moreContact();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getCommand().onListItemClick(position);
    }


    class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString().trim();
            if (ObjectUtil.stringIsEmpty(keyword)) {
                getCommand().endSearch();
            } else {
                listview.setEmptyView(noResult);  //for bug 5684 by ycm
                getCommand().preSearch(keyword);
                getCommand().startSearch(keyword);
            }
        }
    }
}
