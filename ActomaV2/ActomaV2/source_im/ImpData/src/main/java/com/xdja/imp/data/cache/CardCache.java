package com.xdja.imp.data.cache;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:22</p>
 */
public interface CardCache {
    /**
     * 缓存安全卡信息
     *
     * @param cardEntity 安全卡信息实体
     */
    void put(CardEntity cardEntity);

    /**
     * 获取安全卡信息
     *
     * @return 安全卡信息实体
     */
    CardEntity get();
}
