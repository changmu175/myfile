package com.xdja.contact.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.contact.R;
import com.xdja.contact.presenter.activity.MultiDelPresenter;
import com.xdja.contact.presenter.adapter.GroupDelMulMemberAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupMemberAdapter;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IMultiDelVu;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.frame.widget.XDialog;

import java.util.Calendar;

import butterknife.ButterKnife;

/**
 * Created by xdjaxa on 2016/11/1.
 */

public class MultiDelVu extends BaseActivityVu<MultiDelPresenter>
        implements IMultiDelVu, View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private EditText editText;
    private ListView listview;
    private TextView actionBtn;  //既是批量删除也是添加群成员
    private final long MIN_CLICK_DELAY_TIME = 1000;  //防止1s内重复点击
    private long lastClickTime = 0;

    @Override
    public void onCreated() {
        super.onCreated();
        initView();
        actionBtn.setOnClickListener(this);
        listview.setOnItemLongClickListener(this);
        listview.setOnItemClickListener(this);
    }

    public void initView() {
        RelativeLayout searchLayout = ButterKnife.findById(getView(), R.id.del_member_search);
        editText = ButterKnife.findById(searchLayout, R.id.search_ed);
        editText.addTextChangedListener(new OnSearchEditListener());
        listview = ButterKnife.findById(getView(), R.id.del_member_listview);
        actionBtn = ButterKnife.findById(getView(), R.id.action_tv);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setToolBarTitle();
    }

    @Override
    public void updateTitle(int selectCount) {
        if(selectCount > 0) {
            actionBtn.setText(String.format(getStringRes(R.string.delete_btn), selectCount));
            actionBtn.setClickable(true);
        } else {
            actionBtn.setText(getStringRes(R.string.delete_btn_text));
            actionBtn.setClickable(false);
        }
    }

    @Override
    public void setMultiDelAdapter(GroupDelMulMemberAdapter adapter) {
        listview.setAdapter(adapter);
    }

    @Override
    protected int getToolBarId() {
        return R.id.multi_del_member_toolbar;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.multi_del_group_mem;
    }

    @Override
    public void setSearchGroupMemberAdapter(SearchGroupMemberAdapter adapter) {
        listview.setAdapter(adapter);
    }

    private class OnSearchEditListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable key) {
            String keyWord = key.toString();
            if (ObjectUtil.stringIsEmpty(keyWord)) {
                getCommand().endSearch();
            } else {
                getCommand().startSearch(keyWord);
            }

        }

    }

    @Override
    public void setClickState(boolean state) {
        actionBtn.setEnabled(state);
    }

    @Override
    public void showCommonProgressDialog(String msg) {
        super.showCommonProgressDialog(msg);
    }

    @Override
    public void dismissCommonProgressDialog() {
        super.dismissCommonProgressDialog();
    }

    @Override
    public void setToolBarTitle(int count) {
        getActivity().setTitle(String.format(getStringRes(R.string.chat_members), count));
        if (getCommand().getOpenType() == 1) {
            actionBtn.setText(getStringRes(R.string.add_members));
        } else {
            actionBtn.setClickable(false);
        }
    }

    /**
     * 单击右上角按钮
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == actionBtn) {
            if (getCommand().getOpenType() == 1) {
                getCommand().startChooseContact();
                return;
            }
            showActionDialog(actionBtn, getStringRes(R.string.sure_del_members),
                    getStringRes(R.string.content_yes), getStringRes(R.string.content_no), -1);
        }
    }

    /**
     * 点击条目进入好友详情界面
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if(currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            getCommand().startCommonDetail(view);
            lastClickTime = currentTime;
        }

    }

    /**
     * 长按条目单个删除群成员
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //批量删除界面没有长按删除功能,群成员没有删除功能，群主不能对自己进行长按操作
        if (getCommand().getOpenType() == 0 || !getCommand().isGroupOwner() || GroupUtils.isGroupOwner(getActivity(), getCommand().getGroupId(), getCommand().getCurrentAdapterSource().get(position).getAccount())) {
            return true;
        }
        showActionDialog(listview, getStringRes(R.string.sure_del_members), getStringRes(R.string.content_yes), getStringRes(R.string.content_no), position);
        return true;
    }

    /**
     * 显示对应的行为对话框
     * @param view
     * @param message
     * @param ok
     * @param cancel
     * @param position
     */
    public void showActionDialog(final View view, String message, String ok, String cancel, final int position) {
        final XDialog confirmDialog = new XDialog(getActivity());
        confirmDialog.setMessage(message);
        confirmDialog.setPositiveButton(ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                if(view == actionBtn) {
                    getCommand().delMemberMulti();
                } else if(view == listview) {
                    getCommand().delMemberSingle(getCommand().getCurrentAdapterSource().get(position).getAccount()); //要进行判断
                }
            }
        });
        confirmDialog.setNegativeButton(cancel, null);
        confirmDialog.show();
    }

    @Override
    public void setSearchEtContent(String msg) {
        editText.setText(msg);
    }
}
