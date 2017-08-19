package com.xdja.contact.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.presenter.activity.ChooseContactPresenter;
import com.xdja.contact.presenter.adapter.CustomViewPagerAdapter;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IChooseContactVu;
import com.xdja.contact.view.NoScrollViewPager;
import com.xdja.contact.view.SlidingTabLayout;

import butterknife.ButterKnife;

/**
 * @author hkb.
 * @since 2015/7/22/0022.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161116.
 */
public class ChooseContactVu extends BaseActivityVu<ChooseContactPresenter> implements IChooseContactVu {
    private SlidingTabLayout tabLayout = null;
    private NoScrollViewPager viewPager = null;

    private TextView confirm;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        confirm = ButterKnife.findById(getView(), R.id.confirm);
        tabLayout = ButterKnife.findById(getView(), R.id.choose_contact_header);
        viewPager = ButterKnife.findById(getView(), R.id.choose_contact_pager);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContactModuleService.checkNetWork()){
                    getCommand().createGroupOrAddGroupMember();
                }
            }
        });
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_contact;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_choose_contact, menu);
    }*/

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*@Override
    public void onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        this.item1 = item;
        int itemid = item.getItemId();
        if(itemid == R.id.action_ok){
            getCommand().createGroupOrAddGroupMember();
        }
    }*/

    //task 2632 [start]
    /**
     * 分享转发时更新按钮和标题
     * @param selectCount
     */
    @Override
    public void updateConfirmAndTitle(int selectCount) {
        //[s]modify by xienana for select count @20161201
        String content;
        String title = getActivity().getResources().getString(R.string.select_contacts_title);
        if(selectCount == 0){
            content = (getActivity().getResources().getString(R.string.select_contacts_init));
        }else{
            content = getActivity().getResources().getString(R.string.select_contacts_num, selectCount);
        }
        //[s]modify by xienana for select count @20161201
        confirm.setText(content);
        getActivity().setTitle(title);
    }
    //task 2632 [start]

    @Override
    public void setViewPageAdapter(CustomViewPagerAdapter viewPageAdapter) {
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setCustomTabView(R.layout.tablayout, R.id.tab_title);
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void setClickEnable(boolean flag) {
        if(flag){
            confirm.setClickable(true);
        }else{
            confirm.setClickable(false);
        }
    }

    @Override
    public void setSearchHeaderVisibility(int isVisible) {
        getView().findViewById(R.id.choose_contact_header).setVisibility(isVisible);
    }

}
