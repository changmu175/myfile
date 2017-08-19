package com.securevoip.ui.view;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.securevoip.contacts.CustContacts;
import com.securevoip.presenter.adapter.calldetail.CallDetailsAdapter;
import com.securevoip.presenter.command.CallDetailActivityCommand;
import com.securevoip.ui.def.CallDetailActivityVu;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.server.ActomaController;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.voipsdk.R;

import util.CustomDialog;

/**
 * Created by gbc on 2015/7/24.
 */
public class ViewCallDetailActivity
        extends ActivitySuperView<CallDetailActivityCommand>
        implements CallDetailActivityVu {

     protected Toolbar toolbar;
     private TextView contact_name;
     private CircleImageView contactPhoto;
     private RelativeLayout contact_name_layout;
     private View contact_make_call;
     private View contact_send_chat;
     private ListView call_list_view;
     private PopupWindow popWind = null;
     private CustomDialog mMaterialDialog;

     private String actomaAccount;

     private View.OnClickListener clickListener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
               if (v.equals(contact_make_call)) {
                    getCommand().VoipCall(actomaAccount);
               } else if (v.equals(contact_send_chat)) {
                    getCommand().SendIMMsg();
               } else if (v.equals(contact_name_layout)) {
                    getCommand().toContactDetail(getContext(), actomaAccount);
               }
          }
     };


     @Override
     protected int getLayoutRes() {
          return R.layout.calllog_detail_activity;
     }

     @Override
     public void init(LayoutInflater inflater, ViewGroup container) {
          super.init(inflater, container);
          if (getView() != null) {
               toolbar = (Toolbar) getView().findViewById(R.id.detail_toolbar);
          }
          initView();
          initListener();
     }


     @Override
     public void onCreated() {
          super.onCreated();
          if (toolbar != null) {
               //设置toolbar显示
               ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

               ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
               ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
               ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
               ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
               toolbar.setContentInsetsRelative(15, 0);
               toolbar.setTitle(R.string.call_detail_title);
               toolbar.setNavigationIcon(R.drawable.af_abs_ic_back);
               toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         getActivity().finish();
                    }
               });

          }
     }


     @Override
     public void onResume() {
          getCommand().reloadCallLog();
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
          super.onCreateOptionsMenu(menu);
          getActivity().getMenuInflater().inflate(R.menu.calllog_details_menu, menu);
          return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          super.onOptionsItemSelected(item);
          if (item.getItemId() == R.id.action_overflow) {
               showDeleteDialog();
               if (null != popWind) {
                    popWind.dismiss();
               }
          }
          return true;
     }

     @Override
     public void setAdapter(CallDetailsAdapter adapter) {
          call_list_view.setAdapter(adapter);
     }

     @Override
     public void setDisplayName(String showName) {
          contact_name.setText(showName);
     }

     @Override
     public void setContactPhoto(String aactomaAccount) {
          String uri = CustContacts.getFriendThumbNailPhoto(aactomaAccount);
         // HeadImgParamsBean imageBean = HeadImgParamsBean.getParams(uri);
          /*(contactPhoto).loadImage(imageBean.getHost(), true, imageBean.getFileId(), imageBean.getSize(), R.drawable.ic_contact);*/
          //(contactPhoto).loadImage(imageBean.getHost(), true, R.drawable.ic_contact);
            (contactPhoto).loadImage(uri, true, R.drawable.ic_contact);
     }

     @Override
     public void setActomaAccount(String actomaAccount) {
          this.actomaAccount = actomaAccount;
     }

    /*-------------------------------------------------------------------------------------*/

     private void initView() {
          call_list_view = (ListView) getView().findViewById(R.id.call_list);
          contactPhoto = (CircleImageView) getView().findViewById(R.id.contact_photo);
          contact_name = (TextView) getView().findViewById(R.id.contact_name);
          contact_make_call = getView().findViewById(R.id.make_call);
          contact_send_chat = getView().findViewById(R.id.send_sms);
          contact_name_layout = (RelativeLayout) getView().findViewById(R.id.contact_name_layout);
     }

     private void initListener() {
          contact_make_call.setOnClickListener(clickListener);
          contact_send_chat.setOnClickListener(clickListener);
          contact_name_layout.setOnClickListener(clickListener);
     }

     private void showDeleteDialog() {
          mMaterialDialog = new CustomDialog(getActivity());
          mMaterialDialog.setTitle(ActomaController.getApp().getString(R.string.IFORNOT_DELETE_CALL_LOG) + " ")
                  .setPositiveButton(
                          ActomaController.getApp().getString(R.string.YES_DELETE_CALL_LOG), new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                    getCommand().ClearCallLog(actomaAccount);
                                    getActivity().finish();
                               }
                          }
                  )
                  .setNegativeButton(
                          ActomaController.getApp().getString(R.string.NO_DELETE_CALL_LOG), new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                               }
                          }
                  )
                  .setCanceledOnTouchOutside(true)
                  .setOnDismissListener(
                          new DialogInterface.OnDismissListener() {
                               @Override
                               public void onDismiss(DialogInterface dialog) {
                               }
                          }
                  )
                  .show();
     }

}
