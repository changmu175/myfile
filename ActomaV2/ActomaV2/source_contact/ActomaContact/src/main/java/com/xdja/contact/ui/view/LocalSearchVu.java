package com.xdja.contact.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.comm.uitl.TextUtil;
import com.xdja.contact.R;
import com.xdja.contact.presenter.adapter.LocalSearchAdapter2;
import com.xdja.contact.presenter.command.ILocalSearchCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.ILocalSearchVu;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/7/23.
 */
public class LocalSearchVu extends BaseActivityVu<ILocalSearchCommand> implements ILocalSearchVu {

    //private RecyclerView listView;
    private ListView listView;

    private TextView notResult;


    private EditText searchED;

    private String keyword;

    private LinearLayout searchTips;

    private TextView searchTipsTv;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        listView = ButterKnife.findById(getView(), R.id.friend_search_list);
        notResult = (TextView)getView().findViewById(R.id.search_no_result);
        searchTips = (LinearLayout) getView().findViewById(R.id.search_tips);
        searchTipsTv = (TextView) getView().findViewById(R.id.tv_search_tips);
        searchTips.setVisibility(View.VISIBLE);
        searchTipsTv.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0, 0, 0,
                getStringRes(R.string.search_more_tips)));
        listView.setEmptyView(notResult);//add by lwl 2556
        //initRecyclerList();
        //Start:add by wal@xdja.com for 4059
        listView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint( "ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onNavigateBackPressed();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        //End:add by wal@xdja.com for 4059
    }

    /**
     * 左上角返回按钮回调
     */
    @Override
    public void onNavigateBackPressed() {
        super.onNavigateBackPressed();
        hideSoftInputWindow();
    }

    /*private void initRecyclerList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(getContext().getResources().getColor(R.color.list_divider_color)).size(getContext().getResources().getDimensionPixelSize(R.dimen.list_divider_height)).build());
    }*/

    @Override
    protected View getCustomView(LayoutInflater inflater, ViewGroup container) {
        View customView = inflater.inflate(R.layout.local_search_tool_bar, container);
        searchED = ButterKnife.findById(customView, R.id.search_edittext);
        searchED.setFocusableInTouchMode(true);
        final ImageButton clearBtn = ButterKnife.findById(customView, R.id.btn_search_clear);
        clearBtn.setVisibility(View.GONE);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchED.getText().clear();
                clearBtn.setVisibility(View.GONE);
            }
        });
        searchED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                    searchTips.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.GONE);
                    searchTips.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                    searchTips.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.GONE);
                    searchTips.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyword = editable.toString();
                keyword = keyword.trim();
                getCommand().startSearch(keyword);
            }

        });
        showSoftInputWindow();
        return customView;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.friend_local_search;
    }

    @Override
    public void setAdapter(LocalSearchAdapter2 adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public void showNonDataView(boolean isShow) {
        if (isShow) {
            notResult.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            notResult.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void endSearch() {
        dismissCommonProgressDialog();
    }

    @Override
    public String key() {
        return keyword;
    }

    @Override
    public void setKeyWord(String keyWord) {
        searchED.setText(keyWord);
        searchED.setSelection(keyWord.length());
    }


    /**
     * 隐藏键盘
     */
    private void hideSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //Start:add by wal@xdja.com for 4059
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(searchED.getWindowToken(), 0); //强制隐藏键盘
        }
        //End:add by wal@xdja.com for 4059
    }

    /**
     * 显示键盘
     */
    private void showSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchED, InputMethodManager.SHOW_FORCED);
        imm.showSoftInputFromInputMethod(searchED.getWindowToken(), 0);//显示键盘
    }

    @Override
    public ListView getListView() {
        return listView;
    }
}
