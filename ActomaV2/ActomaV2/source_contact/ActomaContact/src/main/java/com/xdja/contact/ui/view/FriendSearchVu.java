package com.xdja.contact.ui.view;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xdja.contact.R;
import com.xdja.contact.presenter.adapter.LocalSearchAdapter;
import com.xdja.contact.presenter.command.IFriendSearchCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IFriendSearchVu;
import com.xdja.contact.view.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/7/23.
 */
public class FriendSearchVu extends BaseActivityVu<IFriendSearchCommand> implements IFriendSearchVu{

    private RecyclerView listView;

    private TextView notResult;

    private View customView;

    private EditText searchED;

    private Button startSearchBtn;

    private ImageButton clearBtn;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        listView = ButterKnife.findById(getView(), R.id.friend_search_list);
        notResult = (TextView)getView().findViewById(R.id.search_no_result);
        initRecyclerList();
    }

    private void initRecyclerList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(getContext().getResources().getColor(R.color.list_divider_color)).size(getContext().getResources().getDimensionPixelSize(R.dimen.list_divider_height)).build());
    }

    @Override
    protected View getCustomView(LayoutInflater inflater, ViewGroup container) {
        customView = inflater.inflate(R.layout.search_tool_bar, container);
        searchED = ButterKnife.findById(customView, R.id.search_edittext);
        startSearchBtn = ButterKnife.findById(customView, R.id.search_btn);
        startSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWord = searchED.getText().toString().replaceAll("\\s*", "");
                getCommand().startSearch(keyWord);
            }
        });
        clearBtn = ButterKnife.findById(customView, R.id.btn_search_clear);
        clearBtn.setVisibility(View.GONE);
        startSearchBtn.setVisibility(View.GONE);//add by lwl 663
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchED.getText().clear();
                clearBtn.setVisibility(View.GONE);
            }
        });
        //add by yangpeng 通过对editext进行监控,从而控制清除按钮的显示与隐藏
        searchED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    clearBtn.setVisibility(View.VISIBLE);
                }else{
                    clearBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    clearBtn.setVisibility(View.VISIBLE);
                    startSearchBtn.setVisibility(View.VISIBLE);//add by lwl 663 miss
                }else{
                    clearBtn.setVisibility(View.GONE);
                    startSearchBtn.setVisibility(View.GONE);//add by lwl 663
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        return R.layout.friend_search;
    }

    @Override
    public void setAdapter(LocalSearchAdapter adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public  void showNonDataView(boolean isShow) {
        if(isShow){
            notResult.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            notResult.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissLoading() {
        dismissCommonProgressDialog();
    }

    @Override
    public void showLoading() {
        showCommonProgressDialog(R.string.search_tips);
    }

}
