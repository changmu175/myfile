package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by yangpeng on 2015/7/25.
 */
public interface IChooseFriendsCommand extends Command {
    /**
     * 开始搜索
     * @param keyWord
     */
    void startSearch(String keyWord);


    void endSearch();

    /**
     * 退出搜索
     */
    void exitSearch();

    void onLetterChanged(String key);

	// modified by ycm 2016/12/22:[文件转发或分享][start]
    boolean getCheckBox();

    void clickItem(int position);
	// modified by ycm 2016/12/22:[文件转发或分享][end]
}
