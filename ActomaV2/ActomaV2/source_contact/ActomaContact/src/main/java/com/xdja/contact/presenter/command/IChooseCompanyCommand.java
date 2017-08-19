package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public interface IChooseCompanyCommand extends Command {


    void startSearch(String keyWord);

    void endSearch();


    void onNodeClick(int postion);

}
