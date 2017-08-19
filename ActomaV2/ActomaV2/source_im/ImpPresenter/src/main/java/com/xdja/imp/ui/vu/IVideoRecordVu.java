package com.xdja.imp.ui.vu;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IVideoRecordCommand;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制View接口 <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public interface IVideoRecordVu extends ActivityVu<IVideoRecordCommand> {
    int onKeyBack();
}
