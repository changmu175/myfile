package com.xdja.imp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IHistoryFileListCommand;
import com.xdja.imp.ui.vu.IHistoryFileListVu;

/**
 * 项目名称：Blade
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/8 17:10
 * 修改人：xdjaxa
 * 修改时间：2016/12/8 17:10
 * 修改备注：
 */
public class ViewHistoryFileList extends ImpActivitySuperView<IHistoryFileListCommand> implements IHistoryFileListVu,
    View.OnClickListener{

    private LinearLayout mSelectLayout;

    private TextView mBtnSelect;

    private ExpandableListView mListView;

    private LinearLayout mEmptyLayout;

    private TextView mSelectHint;

    //fix bug 7749 add by zya 20170102
    private LinearLayout mDownloadLayout,mTransmitLayout,mDeleteLayout;
    //end by zya
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_history_file;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public String getTitleStr(){
        return getStringRes(R.string.chat_file_history_list);
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if(getView() != null){
            mSelectHint = (TextView) view.findViewById(R.id.history_file_selected_tv);
            mEmptyLayout = (LinearLayout) view.findViewById(R.id.history_file_empty_layout);
            mListView = (ExpandableListView) view.findViewById(R.id.lv_chat_file_history);
            mSelectLayout = (LinearLayout) view.findViewById(R.id.layout_file_select);
            mBtnSelect = (TextView) view.findViewById(R.id.select);

            //fix bug 7749 add by zya 20170102
            mDownloadLayout = (LinearLayout) view.findViewById(R.id.btn_history_file_download_layout);
            mTransmitLayout = (LinearLayout) view.findViewById(R.id.btn_history_file_resend_layout);
            mDeleteLayout = (LinearLayout) view.findViewById(R.id.btn_history_file_remove_layout);
            //end by zya
        }
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        addListeners();
        initView();
    }

    private void initView(){
        mListView.setGroupIndicator(getDrawableRes(R.drawable.item_group_indeicator_selector));
    }

    public void showEmpty(boolean isShowEmpty){
        mEmptyLayout.setVisibility(isShowEmpty ? View.VISIBLE : View.GONE);
        mBtnSelect.setVisibility(isShowEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void refreshSelectHint(int num) {
        boolean isShow = mSelectLayout.getVisibility() == View.GONE;
        if(!isShow) {
            mSelectHint.setVisibility(View.VISIBLE);
            String title = getStringRes(R.string.history_file_select_hint);
            mSelectHint.setText(String.format(title, num));
        }
    }

    private void addListeners(){
        mBtnSelect.setOnClickListener(this);
        //fix bug 7749 modify by zya 20170102
        mDownloadLayout.setOnClickListener(this);
        mTransmitLayout.setOnClickListener(this);
        mDeleteLayout.setOnClickListener(this);
        //end by zya
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select) {
            getCommand().refreshUI(refreshUI());
        } else if(v.getId() == R.id.btn_history_file_download_layout){
            getCommand().downloadFiles();
            getCommand().refreshUI(refreshUI());
        } else if(v.getId() == R.id.btn_history_file_resend_layout){
            getCommand().transmitFiles();
        } else if(v.getId() == R.id.btn_history_file_remove_layout){
            getCommand().removeFiles();
        }
    }

    @Override
    public boolean refreshUI(){
        boolean isShow = mSelectLayout.getVisibility() == View.GONE;
        if(isShow){
            mSelectHint.setVisibility(View.VISIBLE);
            String title = getStringRes(R.string.history_file_select_hint);
            mSelectHint.setText(String.format(title, 0));

            mSelectLayout.setVisibility(View.VISIBLE);
            mBtnSelect.setText(getStringRes(R.string.chat_file_hisotry_select_cancel));
        } else {
            mSelectHint.setVisibility(View.GONE);
            mSelectLayout.setVisibility(View.GONE);
            mBtnSelect.setText(getStringRes(R.string.file_select_btn));
        }
        return isShow;
    }

    @Override
    public ExpandableListView getListView() {
        return mListView;
    }
}
