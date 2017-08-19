package com.xdja.imp.domain.interactor.def;

/**
 * Created by Administrator on 2016/3/23.
 * 功能描述 清空所有数据
 */
public interface ClearAllData extends Interactor<Integer>  {

    ClearAllData deleteAllSession();
}
