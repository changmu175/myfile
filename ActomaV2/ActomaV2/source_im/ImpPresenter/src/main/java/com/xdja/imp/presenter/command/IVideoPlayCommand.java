package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频播发命令    <br>
 * 创建时间：2017/2/24        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public interface IVideoPlayCommand extends Command {
    /**
     *长按
     */
    void onLongClick();
}
