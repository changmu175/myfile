package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.SessionConfig;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/15</p>
 * <p>Time:16:22</p>
 */
public interface GetSingleSessionConfig extends Interactor<SessionConfig> {
    GetSingleSessionConfig get(String talkerId);
}
