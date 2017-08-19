package com.xdja.imp.domain.repository;

import java.util.Map;

/**
 * <p>Summary:加解密模块接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/5</p>
 * <p>Time:17:22</p>
 *  Modify history description:
 * 1)Task for 2632, modify for share and forward function by ycm at 20161103.
 * 1)BUG for 5618, modify for share and forward function by ycm at 20161103.
 */
public interface SecurityRepository {

    /**
     * 文本加密
     * @param source 加密的文本
     * @param to 接收方
     * @param isGroup 是否群组
     * @return 加密后的文本
     */
    Map<String,Object> encryptText(String source, String to, boolean isGroup);

    /**
     * 文件加密
     * @param source 加密的文件路径
     * @param dest 加密后的文件路径
     * @param to 接收方
     * @param isGroup 是否群组
     * @return 加密后的文件路径
     */
    Map<String,Object> encryptAsync(String source, String dest, String to, boolean isGroup);

    /**
     * 文本解密
     * @param source 解密前的文本
     * @param to 接收方
     * @param isGroup 是否群组
     * @return 解密后的文本
     */
    Map<String,Object> decryptText(String source, long msgId, String from, String to, boolean isGroup);

    /**
     * 文件解密
     * @param source 解密前的文件路径
     * @param dest 解密后的文件路径
     * @param to 接收方
     * @param isGroup 是否群组
     * @return 解密后的文件路径
     */
    Map<String,Object> decryptAsync(String source, String dest, String to, boolean isGroup);

}
