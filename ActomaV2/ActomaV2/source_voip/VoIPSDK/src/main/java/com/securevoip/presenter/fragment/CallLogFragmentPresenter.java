package com.securevoip.presenter.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipUri;
import com.csipsimple.db.DatabaseHelper;
import com.csipsimple.service.SipNotifications;
import com.securevoip.contacts.CustContacts;
import com.securevoip.presenter.adapter.calllog.RecycleCallLogAdapter;
import com.securevoip.presenter.command.CallLogFragmentCommand;
import com.securevoip.ui.def.CallLogFragmentVu;
import com.securevoip.ui.view.ViewCallLogFragment;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.utils.ObjectUtil;
import com.securevoip.voip.MissedCallOttoPost;
import com.securevoip.widget.ItemMenuDialogFragment;
import com.squareup.otto.Subscribe;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.CloseAppEvent;
import com.xdja.comm.event.CloseAppReceiverMsg;
import com.xdja.comm.event.ExitAppEvent;
import com.xdja.comm.event.ForceExitAppEvent;
import com.xdja.comm.event.UpdateContactShowNameEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import java.util.ArrayList;

import webrelay.VOIPManager;
import webrelay.VOIPPush;

/**
 * Created by gbc on 2015/7/24.
 */
public class CallLogFragmentPresenter
        extends FragmentPresenter<CallLogFragmentCommand, CallLogFragmentVu>
        implements CallLogFragmentCommand {
    private final String THIS_FILE = CallLogFragmentPresenter.class.getName();
    private String TAG = THIS_FILE;
    private final static Object syncLock =  new Object();
    private static RecycleCallLogAdapter mAdapter;

    private ItemMenuDialogFragment mMenuDialog;

    public static final String ACTION_ACCOUNT_DOWNLOAD_SUCCESS = "com.xdja.actoma.account.download.success";

    private RecycleCallLogAdapter.OnCallLogAction mOnCallLogAction = new RecycleCallLogAdapter.OnCallLogAction() {
        @Override
        public void onItemLongClick(final String number, final String name) {
            mMenuDialog = ItemMenuDialogFragment.show(getActivity().getFragmentManager(),
                    R.array.calllog_action_menu,
                    null,
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CallLogHelper.removeCallLogs(name);
                            //zjc 20150823 删除之后，通知主框架更新未接来电数量
                            BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
                            SipNotifications nm = new SipNotifications();
                            //zjc 20150906 删除通话记录，一并删除未接来电通知
                            nm.cancelSpecificMissedCall(SipUri.parseSipContact(number).userName);
                            mMenuDialog.dismiss();
                        }
                    });
        }
    };

    LoaderManager.LoaderCallbacks<Cursor> mCallLogLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Log.d("mb", "CallLogFragment onCreateLoader");

            //20170224-mengbo : 动态获取URI
            //return new CursorLoader(getActivity(), SipManager.CUST_CALLLOG_URI, null, null, null, null);
            return new CursorLoader(getActivity(), SipManager.getCustCalllogUri(getContext()), null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d("mb", "CallLogFragment onLoadFinished");
//            LogUtil.d(TAG, "共有" + data.getCount() + "条通话记录");
            if (mAdapter != null) {
                //20160905-mengbo-start 加入同步锁，解决通话记录显示空白问题
                synchronized (syncLock){
                    if (!data.isClosed()) {
                        try {
                            getVu().setEmptyView(data);
                            mAdapter.changeCursor(data);
                            mAdapter.notifyDataSetChanged();
                            //更新主框架红点儿
                            BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //20160905-mengbo-end
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (mAdapter != null) {
                mAdapter.changeCursor(null);
            }
        }
    };

    @Override
    protected Class<? extends CallLogFragmentVu> getVuClass() {
        return ViewCallLogFragment.class;
    }

    @Override
    protected CallLogFragmentCommand getCommand() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mb 2016-07-27 mCallLogLoader持有cursor 持久开启VOIP数据库，直到应用退出。可避免频繁数据库操作，打开、关闭产生的异常。
        DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
        //zjc 20150831 fragment的oncreate()只执行一次，确保loader初始化仅初始化一次
        getLoaderManager().initLoader(0, null, mCallLogLoader);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);

        mAdapter = new RecycleCallLogAdapter(getActivity(), null, 0);
        mAdapter.setCallLogListener(mOnCallLogAction);
        getVu().setAdapter(mAdapter);
        /**
         zjc 20150831 onBindView是在fragment的onCreateView的生命周期中执行的，
         因为onCreateView()有可能执行不只一次，所以有可能多次执行下面的初始化方法
         造成android.database.StaleDataException: Attempting to access a closed CursorWindow
         */
        BusProvider.getMainProvider().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter.getCursor() != null) {
            mAdapter.getCursor().close();
        }

        DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
        BusProvider.getMainProvider().unregister(this);
    }

    //TODO

	//wxf@xdja.com 2016-08-05 add. fix bug 2457 . review by mengbo. Start
     //备注更新回调事件分发
     @Subscribe
     public void receiveRemarkUpdateEvent(ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent) {
          String account = remarkUpdateEvent.getAccount();
          String showName = remarkUpdateEvent.getShowName();
          if (TextUtils.isEmpty(account)) {
               return;
          }
          CallLogHelper.updateCallLogNickName(account, showName);
          clearShowNameCache();
     }
	 //wxf@xdja.com 2016-08-05 add. fix bug 2457 . review by mengbo. End


     //删除好友的同时删除相应的通话记录
     @Subscribe
     public void receiveFirendClickedDelete(ContactProxyEvent.DeletFriendClearTalkEvent deletFriendClearTalkEvent) {
          String account = deletFriendClearTalkEvent.getAccount();
          //zjc 20150901 删除好友之后加个判断
          //删除好友之后，但是是集团通讯录联系人。更新名称
          if (ContactModuleProxy.isExistDepartment(account)) {
               CallLogHelper.updateCallLogNickName(account, CustContacts.getFriendName(account));
          } else {
               //删除好友之后，不是集团通讯录联系人，则删除通话记录
               //删除本地通话记录
               CallLogHelper.removeCallLogs(account);
              //wxf@xdja.com 2016-08-03 add. fix bug 1542 . review by mengbo. Start
              //添加删除未接来电通知栏
               SipNotifications snf = new SipNotifications();
               snf.cancelMissedCall(account);
              //wxf@xdja.com 2016-08-03 add. fix bug 1542 . review by mengbo. End
               //zjc 20150823 删除之后，通知主框架更新未接来电数量
               BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
          }
     }

	//wxf@xdja.com 2016-08-05 add. fix bug 2457 . review by mengbo. Start
    //好友数据更新时
     @Subscribe
     public void receiveFriendUpdateNickname(ContactProxyEvent.NickNameUpdateEvent nickNameUpdateEvent) {
          LogUtil.getUtils(TAG).d("receiveFriendUpdateNickname threadName "+Thread.currentThread().getName());

          ArrayList<String> accounts = nickNameUpdateEvent.getAccounts();
          if (!ObjectUtil.collectionIsEmpty(accounts)) {
		   //[S]modify by tangsha@20161101 for 5748
             //  for (int i = 0; i < accounts.size(); i++) {
                    CallLogHelper.updateWithCV(accounts);
              // }
			//[E]modify by tangsha@20161101 for 5748
          }
         getActivity().runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 clearShowNameCache();
             }
         });

     }
	//wxf@xdja.com 2016-08-05 add. fix bug 2457 . review by mengbo. End

    private void clearShowNameCache() {
        if (mAdapter != null) {
            mAdapter.clearShowNameCache();
        }
    }

    //mengbo@xdja.com 2016-08-05 add. fix bug 1275
    /**
     * 接收关闭安通+复选框没有选中事件
     */
    @Subscribe
    public void onReceiveCloseEvent(CloseAppEvent event){
        //挂断电话
        VOIPManager.getInstance().hangup();

        //需要执行的代码
        VOIPPush.getInstance().realease(ActomaController.getApp().getApplicationContext());

        //添加删除未接来电通知栏
        SipNotifications snf = new SipNotifications();
        snf.exitCancelCalls();
    }
    //mengbo@xdja.com 2016-08-05 add. fix bug 1275

    //wxf@xdja.com 2016-09-21 add. fix bug 1864、2394、4272. review by mengbo. Start
    /**
     * 接收关闭安通+复选框选中事件
     * @param event
     */
    @Subscribe
    public void onReceiveCloseEvent(CloseAppReceiverMsg event){
        //挂断电话
        VOIPManager.getInstance().hangup();

        //添加删除未接来电通知栏
        SipNotifications snf = new SipNotifications();
        snf.exitCancelCalls();
    }

    /**
     * 接收退出账号的事件
     */
    @Subscribe
    public void onReceiveExitEvent(ExitAppEvent event){
        //挂断电话
        VOIPManager.getInstance().hangup();

        //添加删除未接来电通知栏
        SipNotifications snf = new SipNotifications();
        snf.exitCancelCalls();
    }

    /**
     * 同一账号不同设备切换登录时
     * @param event
     */
    @Subscribe
    public void onForceExitAppEvent(ForceExitAppEvent event){
        //挂断电话
        VOIPManager.getInstance().hangup();

        //添加删除未接来电通知栏
        SipNotifications snf = new SipNotifications();
        snf.exitCancelCalls();
    }
    //wxf@xdja.com 2016-09-21 add. fix bug 1864、2394、4272 . review by mengbo. End

    /** 20160920-mengbo-start: 联系人刷新后，刷新通话记录列表用户名称 **/
    @Subscribe
    public void onReceiveUpdateContactShowNameEvent(UpdateContactShowNameEvent event){
        LogUtil.getUtils(TAG).d("onReceiveUpdateContactShowNameEvent");

        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
    /** 20160920-mengbo-end **/

}
