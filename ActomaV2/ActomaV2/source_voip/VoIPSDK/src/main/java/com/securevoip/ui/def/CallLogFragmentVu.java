package com.securevoip.ui.def;

import android.database.Cursor;

import com.securevoip.presenter.adapter.calllog.RecycleCallLogAdapter;
import com.securevoip.presenter.command.CallLogFragmentCommand;
import com.xdja.frame.presenter.mvp.view.FragmentVu;


/**
 * Created by gbc on 2015/7/24.
 */
public interface CallLogFragmentVu extends FragmentVu<CallLogFragmentCommand> {

    void setAdapter(RecycleCallLogAdapter adapter);
    void setEmptyView(Cursor cursor);
}
