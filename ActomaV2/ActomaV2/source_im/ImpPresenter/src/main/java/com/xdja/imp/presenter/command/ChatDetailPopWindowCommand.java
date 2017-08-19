package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * Created by xrj on 2015/7/30.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface ChatDetailPopWindowCommand extends Command {

    /**
     * 复制
     */
    void copy(TalkMessageBean bean);

    /**
     * 重发
     */
    void repeat(TalkMessageBean bean);

    /**
     * 删除
     */
    void delete(TalkMessageBean bean);

    // 2014-06-06 09:57:09 xrj 添加使用听筒播放

    /**
     * 使用听筒播放音频
     */
    void playMediaInCall(TalkMessageBean bean);

    /**
     * 使用扬声器播放音频
     */
    void playMediaInLoudspeakers(TalkMessageBean bean);

    // 2014-06-18 lyq 文件操作

    /**
     * 暂停下载
     */
    void suspend(TalkMessageBean bean);

    /**
     * 重新接收
     */
    void reDown(TalkMessageBean bean);

    /**
     * 打电话
     */
    void callPhone(TalkMessageBean bean);

    //Task 2632
    /**
     * 转发
     */
    void forwardMessage(TalkMessageBean bean);

    void openFile(TalkMessageBean bean);
}
