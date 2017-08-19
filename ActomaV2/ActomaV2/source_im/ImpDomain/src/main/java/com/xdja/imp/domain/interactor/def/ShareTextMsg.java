package com.xdja.imp.domain.interactor.def;

import android.app.Activity;

import com.xdja.imp.domain.model.TalkListBean;

import java.util.List;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，发送文本的UserCase
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public interface ShareTextMsg extends Interactor<List<TalkListBean>> {
    /**
     * 设置消息收发双发数据
     *
     * @return 业务用例
     */
    ShareTextMsg send(Activity context, String content, List<TalkListBean> dataSource);
}
