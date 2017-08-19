package com.xdja.imp.domain.interactor.def;

import android.support.annotation.Nullable;

/**
 * <p>Summary:保存草稿业务接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:11:35</p>
 */
public interface SaveDraft extends Interactor<Boolean> {
    /**
     * 保存草稿内容
     *
     * @param talkerId 会话对象ID
     * @param draft    草稿内容
     * @return 业务接口
     */
    SaveDraft save(@Nullable String talkerId, CharSequence draft, long draftTime);
}
