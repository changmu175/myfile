package com.securevoip.presenter.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;

import com.csipsimple.api.SipManager;
import com.csipsimple.service.SipNotifications;
import com.securevoip.contacts.CustContacts;
import com.securevoip.presenter.adapter.calldetail.CallDetailsAdapter;
import com.securevoip.presenter.command.CallDetailActivityCommand;
import com.securevoip.ui.def.CallDetailActivityVu;
import com.securevoip.ui.view.ViewCallDetailActivity;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.voip.ClearReceiver;
import com.securevoip.voip.MissedCallOttoPost;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.ChangeTabIndexEvent;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.server.AccountServer;
import com.xdja.contactcommon.ContactModuleProxy;

import webrelay.VOIPManager;

/**
 * Created by gbc on 2015/7/24.
 */
public class CallDetailActivityPresenter
        extends ActivityPresenter<CallDetailActivityCommand, CallDetailActivityVu>
        implements CallDetailActivityCommand{

    public static final String CONTACT_ID_FLAG = "user_id";
    public static final String ACCOUNT_NAME = "name";
    public static final String NICK_NAME = "nickname";

    private String number;
    private String actomaAccount;
    private String nickName;

    private CallDetailsAdapter mDetailsAdapter;

    LoaderManager.LoaderCallbacks<Cursor> mCallLogLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            //20170224-mengbo : 动态获取URI
            //return new CursorLoader(getApplicationContext(), SipManager.CALLLOG_URI, null
            //        , "name = ?", new String[]{actomaAccount}, CallLog.Calls.DEFAULT_SORT_ORDER);
            return new CursorLoader(getApplicationContext(), SipManager.getCalllogUri(CallDetailActivityPresenter.this), null
                    , "name = ?", new String[]{actomaAccount}, CallLog.Calls.DEFAULT_SORT_ORDER);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mDetailsAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mDetailsAdapter.swapCursor(null);
        }
    };

    @Override
    protected Class<? extends CallDetailActivityVu> getVuClass() {
        return ViewCallDetailActivity.class;
    }

    @Override
    protected CallDetailActivityCommand getCommand() {
        return this;
    }


    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        number = getIntent().getStringExtra(CONTACT_ID_FLAG);
        actomaAccount = getIntent().getStringExtra(ACCOUNT_NAME);

        if (TextUtils.isEmpty(number)){
            finish();
            return;
        }

        mDetailsAdapter = new CallDetailsAdapter(this);

        /** 20160914-mengbo-start: reloadCallLog()会调用，此处可省去 **/
        //getLoaderManager().initLoader(0, null, mCallLogLoader);
        /** 20160914-mengbo-end **/

        /**20160616-mengbo-start:从通知栏未接来电进入，清除此号码未接来电各种状态**/
        clearMissedCall(actomaAccount);
        /**20160616-mengbo-end**/
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        getVu().setAdapter(mDetailsAdapter);
        /**20160902-mengbo-start:onResume()内已经显示，去掉重复代码**/
        //getVu().setDisplayName(!TextUtils.isEmpty(actomaAccount) ? CustContacts.getFriendName(actomaAccount) : actomaAccount);
        /**20160902-mengbo-end**/
        getVu().setActomaAccount(number);
        getVu().setContactPhoto(actomaAccount);
    }


    @Override
    protected void onResume() {
        super.onResume();
        /**20160902-mengbo-start:效率优化，进入页面Intent传参NickName,避免数据库重新查询**/
        if(getIntent() != null && getIntent().hasExtra(NICK_NAME)){
            nickName = getIntent().getStringExtra(NICK_NAME);
            getIntent().removeExtra(NICK_NAME);
        }else{
            //zjc 20150826 如果备注更改了，再次进入走到onResume()时，可以刷新
            nickName = (!TextUtils.isEmpty(actomaAccount) ? CustContacts.getFriendName(actomaAccount) : actomaAccount);
        }

        getVu().setDisplayName(nickName);
        /**20160902-mengbo-end**/
    }

    @Override
    public void VoipCall(String actomaAccount) {
        String user= AccountServer.getAccount().getAccount();
        if (user==null || user.equals("") || actomaAccount ==null || actomaAccount.equals(""))
            return;
        /*start:modify by wal@xdja.com for ckms 2016/8/19 */
        VOIPManager.getInstance().makeCall(actomaAccount, user);
        /*end:modify by wal@xdja.com for ckms 2016/8/19 */
    }

    @Override
    public void SendIMMsg() {
        try {
            /*start:modify by wal@xdja.com for ckms 2016/8/19 */
            Intent intent = new Intent("com.xdja.imp.presenter.activity.ChatDetailActivity");
            intent.putExtra("talkerId", actomaAccount);
            intent.putExtra("talkType", 1);
            startActivity(intent);
            /*start:modify by wal@xdja.com for ckms 2016/8/3 */
            //点击加密消息，进入消息会话界面，当前界面结束，点击返回键或者顶部返回按钮，主框架显示会话列表页面
            changeMainFrameCurrentTab(TabTipsEvent.INDEX_CHAT);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示主框架某个页面
     * @param index
     */
    private void changeMainFrameCurrentTab(@TabTipsEvent.POINT_DEF int index) {
        ChangeTabIndexEvent event = new ChangeTabIndexEvent();
        event.setIndex(index);
        BusProvider.getMainProvider().post(event);
    }

    @Override
    public void ClearCallLog(String actomaAccount) {
        SipNotifications nm = new SipNotifications();
        nm.cancelSpecificMissedCall(this.actomaAccount);
        CallLogHelper.removeCallLogs(this.actomaAccount);
    }

    @Override
    public void reloadCallLog() {
        getLoaderManager().restartLoader(0, null, mCallLogLoader);
    }

    @Override
    public void toContactDetail(Context context, String actomaAccount) {
        ContactModuleProxy.startContactDetailActivity(context, actomaAccount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getMainProvider().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getMainProvider().unregister(this);
    }

    private void clearMissedCall(String actomaAccount) {
        sendNotificationClearBroadCast(this, actomaAccount);
        CallLogHelper.clearMissedCall(actomaAccount);
        BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
    }

    private void sendNotificationClearBroadCast(Context context, String actomaAccount) {
        Intent intent = new Intent();
        intent.setAction(ClearReceiver.ACTION_CANCEL_NOTIFICATION);
        intent.putExtra(ClearReceiver.FLAG_CANCEL_NOTIFICATION, actomaAccount);
        context.sendBroadcast(intent);
    }
}
