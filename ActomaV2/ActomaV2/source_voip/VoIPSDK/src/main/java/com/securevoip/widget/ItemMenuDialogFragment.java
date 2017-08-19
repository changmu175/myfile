package com.securevoip.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.voipsdk.R;


/**
 * Created by xdja.gbc on 2015/5/13.
 */
public class ItemMenuDialogFragment extends DialogFragment implements DialogInterface.OnDismissListener {
    private static final String TAG = ItemMenuDialogFragment.class.getCanonicalName();
    private ListView mMenuList;
    private NormalMenuAdapter mDataAdapter;
    private TextView mTitle;
    private int mArrayId;
    private String mTitleStr;
    private AdapterView.OnItemClickListener mListener;
    private View mContent;
    private LayoutInflater mInflater;
    private static ItemMenuDialogFragment fragment;
    @SuppressLint("AndroidLintHandlerLeak")
    private static Handler mMyHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            mIsShow = false;
            super.handleMessage(msg);
        }
    };
    private static boolean mIsShow;
    private static final int CANCLICK_SHOWLOG_EXPEROD = 300;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Activity activity = getActivity();
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContent = mInflater.inflate(R.layout.item_list_menu, null);
        mTitle = (TextView)mContent.findViewById(R.id.title);
        if (null != mTitleStr) {
            mTitle.setText(mTitleStr);
        } else {
            LinearLayout titlelayout = (LinearLayout)mContent.findViewById(R.id.title_layout);
            titlelayout.setVisibility(View.GONE);
            View line = mContent.findViewById(R.id.separate);
            line.setVisibility(View.GONE);
        }
        mMenuList = (ListView)mContent.findViewById(R.id.fun_list);
        if (null != mDataAdapter) {
            mMenuList.setAdapter(mDataAdapter);
        } else {
            mMenuList.setAdapter(new NormalMenuAdapter(activity, mArrayId));
        }
        if (null != mListener) {
            mMenuList.setOnItemClickListener(mListener);
        }


        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();

        Dialog lDialog = new AlertDialog.Builder(activity).setView(mContent).create();
        return lDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        fragment = null;
    }

    public void setMenuArrayId(int arrayId) {
        mArrayId = arrayId;
    }

    public void setDataAdapter(NormalMenuAdapter adapter) {
        mDataAdapter = adapter;
    }

    public void setTitle(String title) {
        mTitleStr = title;
    }

    public void setListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }



    public static ItemMenuDialogFragment show(FragmentManager fragmentManager, int menuArray,
                                     String title, AdapterView.OnItemClickListener aListener) {
        if (fragment != null && mIsShow) {
            fragment.dismiss();
        } else if (null == fragment && !mIsShow) {
            fragment = new ItemMenuDialogFragment();
            fragment.setTitle(title);
            fragment.setMenuArrayId(menuArray);
            fragment.setListener(aListener);
            fragment.show(fragmentManager, "ItemMenuDialogFragment");
            mIsShow = true;
            mMyHandler.sendMessageDelayed(new Message(), CANCLICK_SHOWLOG_EXPEROD);
        }
        return fragment;
    }

    /*
    * 显示菜单
    * */
    public static ItemMenuDialogFragment show(FragmentManager fragmentManager,
                                              NormalMenuAdapter adapter,
                                              String title,
                                              AdapterView.OnItemClickListener aListener) {
        if (fragment != null && mIsShow) {
            fragment.dismiss();
        } else if (null == fragment && !mIsShow) {
            fragment = new ItemMenuDialogFragment();
            fragment.setTitle(title);
            fragment.setDataAdapter(adapter);
            fragment.setListener(aListener);
            fragment.show(fragmentManager, "ItemMenuDialogFragment");
            mIsShow = true;
            mMyHandler.sendMessageDelayed(new Message(), CANCLICK_SHOWLOG_EXPEROD);
        }
        return fragment;
    }
}
