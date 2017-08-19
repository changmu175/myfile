package com.xdja.presenter_mainframe.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.presenter_mainframe.R;

/**
 * Created by xdjaxa on 2016/10/11.
 */
public class ChoiceLanguageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mStrings;
    private int mSelectIndex;

    public ChoiceLanguageAdapter(Context context, String[] strings, int selIndex) {
        mContext = context;
        mStrings = strings;
        mSelectIndex = selIndex;
    }

    @Override
    public int getCount() {
        return mStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return mStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String language = mStrings[position];
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choice_language, null);

            viewHolder = new ViewHolder();
            viewHolder.languageName = (TextView)convertView.findViewById(R.id.language_text);
            viewHolder.circleImageView = (CircleImageView)convertView.findViewById(R.id.language_radio);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position == mSelectIndex) {
            viewHolder.circleImageView.setImageResource(R.drawable.list_ico_completed);
        } else {
            viewHolder.circleImageView.setImageResource(R.drawable.list_ico_todo);
        }
        viewHolder.languageName.setText(language);

        return convertView;
    }

    public class ViewHolder {
        TextView languageName;
        CircleImageView circleImageView;
        public CircleImageView getCircleImageView() {
            return circleImageView;
        }
        public TextView getLanguageName() {
            return languageName;
        }
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
    }

}
