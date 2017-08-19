package com.xdja.imp.domain.interactor.def;

import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/3/12.
 * 功能描述 保存用户账号
 */
public interface AddUserAccount extends Interactor<Boolean> {
    /**
     * 添加用户账号
     *
     * @return
     */
    AddUserAccount add(@Nullable String String);
}
