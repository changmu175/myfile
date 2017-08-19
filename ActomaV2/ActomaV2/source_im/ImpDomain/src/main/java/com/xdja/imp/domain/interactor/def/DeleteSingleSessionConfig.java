package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:删除单个会话设置业务接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:KongGuoguang</p>
 * <p>Date:2016/6/2</p>
 * <p>Time:14:35</p>
 */
public interface DeleteSingleSessionConfig extends Interactor<Boolean> {
    DeleteSingleSessionConfig delete(String talkFlag);
}
