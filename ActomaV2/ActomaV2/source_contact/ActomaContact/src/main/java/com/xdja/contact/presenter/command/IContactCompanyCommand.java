package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public interface IContactCompanyCommand extends Command {

    void onNodeClick(int postion);

    void updateContactCompanyData();

    int refreshCount();
}
