package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by xdjaxa on 2016/10/11.
 */
public interface ChoiceLanguageCommand extends Command {
    void setLanguage(int index);
}
