package com.xdja.imp.domain.interactor.def;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2017/2/15.
 * 修改人：yuchangmu
 * 修改时间：2017/2/15.
 * 修改备注：modified by ycm for share and forward function
 */
public interface GetVersion extends Interactor<Integer>{
    GetVersion setParam(String account, String ticket);
}
