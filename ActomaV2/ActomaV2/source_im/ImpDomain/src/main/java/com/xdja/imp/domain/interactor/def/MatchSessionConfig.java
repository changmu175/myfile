package com.xdja.imp.domain.interactor.def;

import android.support.annotation.Nullable;

import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;

import java.util.List;

/**
 * <p>Summary:匹配会话配置接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/4</p>
 * <p>Time:14:52</p>
 */
public interface MatchSessionConfig extends Interactor<List<TalkListBean>> {
    /**
     * 匹配会话配置
     *
     * @param configs      会话配置
     * @param talkListBeen 会话列表
     * @return 匹配会话句柄
     */
    MatchSessionConfig setConfigs(@Nullable List<SessionConfig> configs,
                                  @Nullable List<TalkListBean> talkListBeen);
}
