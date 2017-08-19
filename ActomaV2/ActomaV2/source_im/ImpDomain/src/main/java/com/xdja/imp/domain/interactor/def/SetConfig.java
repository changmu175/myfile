package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:设置配置用例接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:14:53</p>
 */
public interface SetConfig extends Interactor<Boolean> {
    SetConfig putConfig(String key,String value);
}
