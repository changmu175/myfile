package com.securevoip.voip;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.csipsimple.api.SipManager;
import com.csipsimple.db.DatabaseHelper;
import com.securevoip.presenter.adapter.calllog.RecycleCallLogAdapter;
import com.securevoip.utils.RecyclerViewCursorAdapter;
import com.xdja.voipsdk.R;

@SuppressLint("AndroidLintRegistered")
public class RecycleCallLogActivity extends AppCompatActivity {

    private RecyclerView list;
    private RecycleCallLogAdapter adapter;
    private Cursor cursor;

    @SuppressLint("MismatchedArrayReadWrite")
    private static final String[] CALL_LOG_FULL_PROJECTION = new String[]{
            CallLog.Calls._ID,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.CACHED_NUMBER_LABEL,
            CallLog.Calls.CACHED_NUMBER_TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NEW,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            /**Modify by:gbc, 20150817 数据库增加四个字段，此处做相应修改**/
            SipManager.CALLLOG_NICKNAME,
            SipManager.CALLLOG_NICKNAME_PYF,
            SipManager.CALLLOG_NICKNAME_PY,
            SipManager.CALLLOG_AVATAR_URL,
            /**End:gbc**/
            SipManager.CALLLOG_PROFILE_ID_FIELD,
            SipManager.CALLLOG_STATUS_CODE_FIELD,
            SipManager.CALLLOG_STATUS_TEXT_FIELD
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_call_log);
        initView();
    }

    private void initView() {
        list = (RecyclerView) findViewById(android.R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        /*cursor = getContentResolver().query(
                SipManager.CALLLOG_URI, CALL_LOG_FULL_PROJECTION, null,
                null, CallLog.Calls.DEFAULT_SORT_ORDER);
        */
        cursor = query();
        adapter = new RecycleCallLogAdapter(this, cursor, RecyclerViewCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list.setAdapter(adapter);
        registObserver();
        //initLoaderManager();
    }

    private Cursor query() {
        DatabaseHelper mOpenHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        cursor = db.rawQuery(buildQuerySql(), null);
        return cursor;
    }

    @SuppressLint("StringBufferReplaceableByStringBuilder")
    private String buildQuerySql() {
        //示例 select * from  ( select * from calllogs order by date desc ) t group by name
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");
        sb.append(" ( ");
        sb.append("SELECT * FROM ");
        sb.append(SipManager.CALLLOGS_TABLE_NAME);
        sb.append(" ORDER BY ");
        sb.append(CallLog.Calls.DATE);
        sb.append(" DESC");
        sb.append(" ) ");
        sb.append(" t "); //t是别名，可有可无
        sb.append(" GROUP BY ");
        sb.append(CallLog.Calls.CACHED_NAME);
//        LogUtil.e("zjc", sb.toString());
        return sb.toString();
    }

    private void registObserver() {
        //20170224-mengbo : 动态获取URI
        //getContentResolver().registerContentObserver(SipManager.CALLLOG_URI, true, new CallLogObserver(new Handler()));
        getContentResolver().registerContentObserver(SipManager.getCalllogUri(RecycleCallLogActivity.this)
                , true, new CallLogObserver(new Handler()));
    }

    private void initLoaderManager() {
        getLoaderManager().initLoader(0, null, mCallLogLoader);
    }

    LoaderManager.LoaderCallbacks<Cursor> mCallLogLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //20170224-mengbo : 动态获取URI
            //return new CursorLoader(RecycleCallLogActivity.this, SipManager.CALLLOG_URI
            return new CursorLoader(RecycleCallLogActivity.this, SipManager.getCalllogUri(RecycleCallLogActivity.this)
                    , new String[]{CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL
                    , CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE, CallLog.Calls.NEW
                    , CallLog.Calls.NUMBER, CallLog.Calls.TYPE, SipManager.CALLLOG_NICKNAME, SipManager.CALLLOG_NICKNAME_PYF
                    , SipManager.CALLLOG_NICKNAME_PY, SipManager.CALLLOG_AVATAR_URL, SipManager.CALLLOG_PROFILE_ID_FIELD}
                    , null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.changeCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.changeCursor(null);
        }

    };

    public class CallLogObserver extends ContentObserver {

        public CallLogObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            adapter.notifyDataSetChanged();
        }
    }

}
