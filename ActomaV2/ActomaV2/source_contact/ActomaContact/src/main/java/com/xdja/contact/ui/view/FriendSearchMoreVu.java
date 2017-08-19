package com.xdja.contact.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.contact.R;
import com.xdja.contact.presenter.adapter.LocalSearchMoreAdapter;
import com.xdja.contact.presenter.command.IFriendSearchMoreCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IFriendSearchMoreVu;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/10/23.
 */
public class FriendSearchMoreVu extends BaseActivityVu<IFriendSearchMoreCommand> implements IFriendSearchMoreVu {

    private ListView listView;

    private TextView notResult;

    private EditText searchED;

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
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString();
                getCommand().startSearch(keyword);
            }

        });
        return customView;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        listView = ButterKnife.findById(getView(), R.id.friend_search_more_list);
        notResult = (TextView)getView().findViewById(R.id.search_no_result);
    }

    public  void resultNotFound(boolean isShow) {
        if(isShow){
            notResult.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            notResult.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
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
        return R.layout.friend_search_more;
    }

    @Override
    public void setAdapter(LocalSearchMoreAdapter adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public void setKeyWord(String keyWord) {
        searchED.setText(keyWord);
        searchED.setSelection(keyWord.length());
    }

    @Override
    public String getKeyword() {
        return searchED.getText().toString();
    }
}
