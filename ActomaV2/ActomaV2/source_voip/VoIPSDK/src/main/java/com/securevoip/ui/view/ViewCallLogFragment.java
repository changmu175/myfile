package com.securevoip.ui.view;

import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.securevoip.presenter.adapter.calllog.RecycleCallLogAdapter;
import com.securevoip.presenter.command.CallLogFragmentCommand;
import com.securevoip.ui.def.CallLogFragmentVu;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;
import com.xdja.voipsdk.R;

/**
 * Created by gbc on 2015/7/24.
 */
public class ViewCallLogFragment extends FragmentSuperView<CallLogFragmentCommand>
        implements CallLogFragmentVu {
     private RecyclerView mCallLogList;
     private ViewStub emptyViewStub;
     private View emptyView;

     @Override
     public void init(LayoutInflater inflater, ViewGroup container) {
          super.init(inflater, container);
          LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
          mCallLogList = (RecyclerView) getView().findViewById(R.id.list);
          mCallLogList.setLayoutManager(layoutManager);
          emptyViewStub = (ViewStub) getView().findViewById(R.id.empty_view_stub);
     }

     @Override
     protected int getLayoutRes() {
          return R.layout.calllog_list_fragment;
     }


     @Override
     public void setAdapter(RecycleCallLogAdapter adapter) {
          mCallLogList.setAdapter(adapter);
     }

     @Override
     public void setEmptyView(Cursor cursor) {
          if (cursor != null && cursor.getCount() != 0) {
               if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
               }
               if (emptyViewStub != null) {
                    emptyViewStub.setVisibility(View.GONE);
               }
               mCallLogList.setVisibility(View.VISIBLE);
          } else {
               //wxf@xdja.com 2016-08-02 add. fix bug 1791 . review by mengbo. Start
               mCallLogList.setVisibility(View.GONE);
                //View emptyViewLayout = emptyViewStub.inflate();
               emptyViewStub.setVisibility(View.VISIBLE);
               //emptyView = emptyViewLayout.findViewById(R.id.empty_view);
               //wxf@xdja.com 2016-08-02 add. fix bug 1791 . review by mengbo. End
          }
     }
}
