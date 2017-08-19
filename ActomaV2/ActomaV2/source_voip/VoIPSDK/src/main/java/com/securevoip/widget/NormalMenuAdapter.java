package com.securevoip.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guobinchang on 2015/6/12.
 */


public class NormalMenuAdapter extends BaseAdapter {
    private Context mContext;
    private int mListDataId = 0;
    private ArrayList<Integer> mMenuData = new ArrayList<>();
    private ArrayList<String> mListsData = new ArrayList<>();
    private ArrayList<MenuItemState> mMenuItemStates = new ArrayList<>();

    /*
    *  if in contacts, isKnow is true
    * */
    @SuppressLint("UnnecessaryBoxing")
    public NormalMenuAdapter (Context context, int listArrayId) {
        if (listArrayId != 0) {
            mListDataId = listArrayId;
            mContext = context;
            TypedArray array = mContext.getResources().obtainTypedArray(listArrayId);
            final int count = array.length();
            for (int i = 0; i < count; i++) {
                mMenuData.add(Integer.valueOf(array.getResourceId(i, 0)));
            }
            /**2017-3-7 -wangzhen modify.Solve the problem of lint for TypeArray Using**/
            array.recycle();
            LogUtil.getUtils().d("WZ NormalMenuAdapter TypeArray Recycled");
        }
    }
    @SuppressLint("UnnecessaryBoxing")
    public NormalMenuAdapter (Context context, int listArrayId, List<MenuItemState> status) {
        mMenuItemStates = (ArrayList<MenuItemState>) status;
        if (listArrayId != 0) {
            mListDataId = listArrayId;
            mContext = context;
            TypedArray array = mContext.getResources().obtainTypedArray(listArrayId);
            final int count = array.length();
            for (int i = 0; i < count; i++) {
                mMenuData.add(Integer.valueOf(array.getResourceId(i, 0)));
            }
            for (MenuItemState state: mMenuItemStates) {
                if (state.isHidden) {
                    mMenuData.remove(state.menuItemId);
                }
            }
            array.recycle();
        }
    }
    @Override
    public int getCount() {
        return mMenuData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMenuData.get(position);
    }

    @Override
    @SuppressLint("AndroidLintViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = ((LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_item, parent, false);

        item.setTag(R.id.tag_first, Integer.valueOf(mListDataId));  //add a flag 0, group flag
        item.setTag(R.id.tag_second, mMenuData.get(position));
        TextView text = (TextView)item.findViewById(R.id.text);
        String content = mContext.getResources().getString(mMenuData.get(position).intValue());
        mListsData.add(content);
        text.setText(content);
        for (MenuItemState state: mMenuItemStates) {
            if (state.menuItemId.equals(mMenuData.get(position))) {
                CheckBox cbox = (CheckBox) item.findViewById(R.id.checkbox);
                cbox.setChecked(state.isSelect);
                cbox.setVisibility(View.VISIBLE);
                item.setTag(cbox);
            }
        }

        return item!=null?item:convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
