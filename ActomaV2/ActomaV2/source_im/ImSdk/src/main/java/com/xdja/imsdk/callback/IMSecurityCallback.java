package com.xdja.imsdk.callback;

import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.security.SecurityPara;
import com.xdja.imsdk.security.SecurityResult;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：加解密回调接口                     <br>
 * 创建时间：2016/12/20 14:51                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public interface IMSecurityCallback {
    /**
     * 加密回调接口
     * @param source 待加密数据
     * @param para 加密参数
     * @return 加密结果
     */
    SecurityResult EncryptText(String source, SecurityPara para);

    /**
     * 加密文件回调接口
     * @param source 待加密文件
     * @param dest 加密后文件路径
     * @param para 加密参数
     * @return 加密结果
     */
    SecurityResult EncryptFile(String source, String dest, SecurityPara para);

    /**
     * 解密文本回调接口
     * @param source 待解密数据
     * @param para 解密参数
     * @return 解密结果
     */
    SecurityResult DecryptText(String source, SecurityPara para);

    /**
     * 解密文件回调接口
     * @param source 待解密文件
     * @param dest 解密后文件路径
     * @param para 解密参数
     * @return 解密结果
     */
    SecurityResult DecryptFile(String source, String dest, SecurityPara para);
}
