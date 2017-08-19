package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;

import java.util.List;

/**
 * Created by ldy on 16/5/3.
 * 使用了{@link FillInMessageView}当做布局的界面需要使用的命令
 */
public interface FillMessageCommand extends Command{
    /**
     * 完成按钮的点击事件
     * @param stringList {@link FillInMessageView}中的所有inputView的字符串集合
     */
    void complete(List<String> stringList);
}
