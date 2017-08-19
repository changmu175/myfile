package com.xdja.imp.ui.vu;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.presenter.command.IVideoPlayCommand;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频播发View接口 <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public interface IVideoPlayVu extends ActivityVu<IVideoPlayCommand> {

    /**
     * 设置数据
     * @param dataSource 短视频信息
     */
    void setDataSource(VideoFileInfo dataSource);
}
