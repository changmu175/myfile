package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ChoiceLanguageCommand;
import com.xdja.presenter_mainframe.presenter.adapter.ChoiceLanguageAdapter;
import com.xdja.presenter_mainframe.ui.uiInterface.ChoiceLanguageVu;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by xdjaxa on 2016/10/11.
 */

@ContentView(value = R.layout.activity_choice_language)
public class ChoiceLanguageView extends ActivityView<ChoiceLanguageCommand> implements ChoiceLanguageVu{
    @Bind(R.id.choice_ok)
    Button button;

    @Bind(R.id.listview)
    ListView listView;

    private String[] strings;
    private ChoiceLanguageAdapter choiceLanguageAdapter;

    @Override
    public void onCreated() {
        super.onCreated();
        strings = new String[]{getStringRes(R.string.with_system), getStringRes(R.string.simple_chinese), "English"};
        choiceLanguageAdapter = new ChoiceLanguageAdapter(getContext(), strings,
                UniversalUtil.getLanguage(getContext()));
        listView.setAdapter(choiceLanguageAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChoiceLanguageAdapter.ViewHolder viewHolder = (ChoiceLanguageAdapter.ViewHolder)view.getTag();

                int i = choiceLanguageAdapter.getSelectIndex();
                View viewChild = listView.getChildAt(i);
                if(viewChild != null) {
                    CircleImageView circleImageView = (CircleImageView)viewChild.findViewById(R.id.language_radio);
                    circleImageView.setImageResource(R.drawable.list_ico_todo);
                }

                viewHolder.getCircleImageView().setImageResource(R.drawable.list_ico_completed);
                choiceLanguageAdapter.setSelectIndex(position);
                //choiceLanguageAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public ChoiceLanguageCommand getCommand() {
        return super.getCommand();
    }

    @OnClick(R.id.choice_ok)
    public void selectOk() {
        getCommand().setLanguage(choiceLanguageAdapter.getSelectIndex());
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.multi_language);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
