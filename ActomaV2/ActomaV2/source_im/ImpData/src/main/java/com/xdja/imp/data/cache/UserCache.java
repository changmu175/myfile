package com.xdja.imp.data.cache;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:19</p>
 */
public interface UserCache {
    /**
     * 设置User信息
     *
     * @param userEntity User信息
     */
    void put(UserEntity userEntity);

    /**
     * 获取User信息
     *
     * @return 目标User信息
     */
    UserEntity get();

    /**
     * 匹配某个用户是否为当前用户
     * @param user 待匹配用户
     * @return 匹配结果
     */
    boolean isMine(String user);

    /**
     * 判断当前用户是否处于前台可视界面
     * @return 判断结果
     */
    boolean isUserForeground();

    /**
     * 设置当前用户是否处于前台可视界面
     * @param isForeground 待设置的判断结果
     */
    void setUserForeground(boolean isForeground);

    /**
     * 设置当前正在聊天的对象的ID
     * @param talkerId 当前聊天对象的ID
     */
    void setIMPartener(String talkerId);

    /**
     * 获取正在聊天的对象的ID
     * @return 正在聊天的对象的ID
     */
    String getIMPartener();

    //add by zya,20161027
    void putCacheText(long key,String value);

    String getCacheText(long key);

    void clearCacheText();
    //end

    //add by zya ,add file download progress cache,20170103

    /**
     * 下载进度缓存的数据put
     * @param msgId 消息id
     * @param percent 下载进度
     */
    void putProgress(long msgId,int percent);

    /**
     * 根据消息id获取之前下载进度
     * @param msgId
     */
    int getProgress(long msgId);

    void removeProgress(long msgId);

    boolean containKey(long msgId);
    /**
     * 清除下载进度缓存
     */
    void clearAllProgress();
    //end by zya
}
