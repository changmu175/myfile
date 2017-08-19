package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.ConstDef;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:09</p>
 */
public interface SaveRoamSetting extends Interactor<Boolean> {
    /**
     * 设置漫游状态
     *
     * @param state 漫游状态
     * @param time  漫游时间
     * @return 漫游操作业务对象
     */
    SaveRoamSetting save(@ConstDef.RoamState int state, int time);

}
