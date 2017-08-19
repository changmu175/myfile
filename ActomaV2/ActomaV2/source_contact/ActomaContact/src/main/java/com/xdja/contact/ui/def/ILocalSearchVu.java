package com.xdja.contact.ui.def;

import android.widget.ListView;

import com.xdja.contact.presenter.adapter.LocalSearchAdapter2;
import com.xdja.contact.presenter.command.ILocalSearchCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/7/23.
 */
public interface ILocalSearchVu extends ActivityVu<ILocalSearchCommand> {

    void setAdapter(LocalSearchAdapter2 adapter);

    void showNonDataView(boolean isShow);

    void endSearch();

    String key();

    void setKeyWord(String keyWord);

    ListView getListView();

}
