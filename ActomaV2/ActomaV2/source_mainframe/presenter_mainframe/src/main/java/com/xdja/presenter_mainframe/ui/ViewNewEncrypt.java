package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.NewEncryptCommand;
import com.xdja.presenter_mainframe.presenter.adapter.NewEncryptListAdapter;
import com.xdja.presenter_mainframe.ui.uiInterface.NewEncryptVu;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by geyao on 2015/11/23.
 * 重构-第三方应用列表view层
 */
@ContentView(value = R.layout.activity_view_other_app_encrypt)
public class ViewNewEncrypt extends ActivityView<NewEncryptCommand> implements NewEncryptVu {
    /**
     * 支持的应用列表-listview
     */
    @Bind(R.id.otherappencrypt_listview)
    ListView otherappencryptListview;
    /**
     * 支持的第三方应用-列表适配器
     */
    private NewEncryptListAdapter adapter;

    /**
     * 列表头视图
     */
    private View headView;

    private TextView textView;

    @Override
    public void onCreated() {
        super.onCreated();
        headView = getActivity().getLayoutInflater().
                inflate(R.layout.headview_other_app_encrypt, otherappencryptListview, false);
        textView = (TextView)headView.findViewById(R.id.head_other_app_encrypt_text);
        textView.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0,
                0, 0, getStringRes(R.string.other_app_encrypt_msg8)));
    }

    /**
     * 初始化视图
     *
     * @param list                 支持的应用列表数据
     */
    @Override
    public void initView(final List<EncryptAppBean> list) {
        //实例化列表适配器
        adapter = new NewEncryptListAdapter(list, getActivity());
        //设置支持的应用列表适配器
        otherappencryptListview.setAdapter(adapter);
        //添加列表头视图
        otherappencryptListview.addHeaderView(headView);
    }

    /**
     * 设置列表适配器
     *
     * @param list 数据
     */
    @Override
    public void setListAdapter(List<EncryptAppBean> list) {
        //实例化列表适配器
        adapter = new NewEncryptListAdapter(list, getActivity());
        //设置支持的应用列表适配器
        otherappencryptListview.setAdapter(adapter);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.setting_thirdpart);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
