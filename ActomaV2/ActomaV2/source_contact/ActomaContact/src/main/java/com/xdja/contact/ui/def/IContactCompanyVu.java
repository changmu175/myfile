package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.adapter.CompanyTreeAdapter;
import com.xdja.contact.presenter.fragment.ContactCompanyPresenter;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public interface IContactCompanyVu extends FragmentVu<ContactCompanyPresenter> {

    void setCompanyTreeAdapter(CompanyTreeAdapter treeViewAdapter);

    void stopRefush();

    void refreshContactCount();
}
