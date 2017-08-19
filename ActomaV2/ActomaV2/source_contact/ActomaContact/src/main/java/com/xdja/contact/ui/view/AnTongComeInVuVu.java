package com.xdja.contact.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.uitl.TextUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.command.IAnTongComeInCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IAnTongComeInVu;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * Created by yangpeng on 2015/8/10.
 */
public class AnTongComeInVuVu extends BaseActivityVu<IAnTongComeInCommand> implements IAnTongComeInVu {

    private RelativeLayout comeInBtn;
    private LinearLayout callLay;


    private ImageButton call;

    private TextView phoneNumber;

    private TextView name;

    @Override
    public void onCreated() {
        super.onCreated();
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        toolbar.setBackgroundColor(getContext().getResources().getColor(R.color.toolbar_alpha));
        name = ButterKnife.findById(getView(), R.id.contact_person_name);
        comeInBtn = ButterKnife.findById(getView(), R.id.come_in);
        call = ButterKnife.findById(getView(), R.id.phone);
        callLay = ButterKnife.findById(getView(), R.id.call_phone_layout);
        phoneNumber = ButterKnife.findById(getView(), R.id.phone_number_text_1);
        Spanned ss = TextUtil.getActomaText(getActivity(), TextUtil.ActomaImage.IMAGE_NOTIFICATION_BIG,
                0, 0, 0, getActivity().getString(R.string.actoma_team_title));
       // String str = getActivity().getString(R.string.actoma_team_title);//modify by wal@xdja.com for string 安通+团队
       // SpannableString ss = formatAnTongSpanContent(str, getActivity(), (float) 1.0);
        name.setText(ss);
        initListener();
    }

    public static Bitmap getBitmapWithName(String fieldName, Resources resource)
            throws NoSuchFieldException, NumberFormatException,
            IllegalArgumentException, IllegalAccessException {
        Field field = com.xdja.contact.R.drawable.class.getDeclaredField(fieldName);
        int resouseId = Integer.parseInt(field.get(null).toString());
        return BitmapFactory.decodeResource(resource, resouseId);
    }

    public static Bitmap small(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private void initListener() {

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNumber.getText().toString();
                getCommand().callPhone(phone);
            }
        });

        callLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNumber.getText().toString();
                getCommand().callPhone(phone);

            }
        });

        comeInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().startAtChat();
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.contact_addressbook;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void setAnTongFtiendData(Friend ftiend) {

    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.detail_info_title);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}