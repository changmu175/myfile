package com.xdja.dependence.exeptions.matcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by xdja-fanjiandong on 2016/3/9.
 * 异常匹配处理类（可匹配用户信息和异常处理动作）
 */
public class OkMatcher {

    private Map<String, Map<String, String>> userMessgeMapper;

    @Inject
    public OkMatcher() {
        this.userMessgeMapper = new HashMap<>();
    }

    /**
     * 注册指定的异常信息匹配
     *
     * @param cls       异常类型
     * @param errorCode 错误码
     * @param userMsg   错误描述
     * @param <T>
     * @return 注册结果
     */
    public <T extends OkException> boolean registUserMessage(@NonNull Class<T> cls,
                                                             @NonNull String errorCode,
                                                             @NonNull String userMsg) {
        Map<String, String> msgMapper = this.userMessgeMapper.get(cls.getName());
        if (msgMapper == null) {
            msgMapper = new HashMap<>();
            msgMapper.put(errorCode, userMsg);
            return this.registUserMessageMapper(cls, msgMapper);
        }
        msgMapper.put(errorCode, userMsg);
        return true;
    }

    public <T extends OkException> boolean registUserMessageMapper(@NonNull Map<String, Map<String, String>> stringMapMap){
        boolean result = false;
        try {
            this.userMessgeMapper.putAll(stringMapMap);
            result = true;
        } catch (UnsupportedOperationException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (ClassCastException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (NullPointerException e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return result;
    }

    /**
     * 注册指定的异常向用户信息转换的映射器
     *
     * @param exeCls 指定的异常类型
     * @param mapper 用户信息映射对象
     * @param <T>    指定的异常类型定义
     * @return 注册结果
     */
    public <T extends OkException> boolean registUserMessageMapper(@NonNull Class<T> exeCls,
                                                                   @NonNull Map<String, String> mapper) {
        boolean result = false;
        try {
            this.userMessgeMapper.put(exeCls.getName(), mapper);
            result = true;
        } catch (UnsupportedOperationException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (ClassCastException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().e(e.getMessage());
        } catch (NullPointerException e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return result;
    }

    /**
     * 注销指定的异常向用户信息转换的映射器
     *
     * @param exeCls 指定的异常类型
     * @param <T>    指定的异常类型定义
     * @return 注销结果
     */
    public <T extends OkException> boolean unRegistUserMessageMapper(@NonNull Class<T> exeCls) {
        boolean result = false;
        try {
            this.userMessgeMapper.remove(exeCls.getName());
            result = true;
        } catch (UnsupportedOperationException e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return result;
    }

    /**
     * 注销所有异常向用户信息转换的映射器
     *
     * @return 注销结果
     */
    public boolean unRegistAllUserMessageMapper() {
        boolean result = false;
        try {
            this.userMessgeMapper.clear();
            result = true;
        } catch (UnsupportedOperationException e) {
            LogUtil.getUtils().e(e.getMessage());
        }
        return result;
    }

    /**
     * 匹配异常对应的用户信息
     *
     * @param ex 异常对象
     * @return 匹配到的用户信息
     */
    @Nullable
    public String match(@Nullable OkException ex) {
        if (ex == null) {
            LogUtil.getUtils().d("异常对象为空");
            return null;
        }
        Map<String, String> stringStringMap = this.userMessgeMapper.get(ex.getClass().getName());
        if (stringStringMap == null) {
            LogUtil.getUtils().d("该异常对应的用户信息匹配器为空");
            return null;
        }
        String okCode = ex.getOkCode();
        if (TextUtils.isEmpty(okCode)) {
            LogUtil.getUtils().d("该异常错误码为空");
            return null;
        }
        String result = stringStringMap.get(okCode);
        if (result == null) {
            LogUtil.getUtils().d("未找到该异常信息对应的用户信息匹配");
            return null;
        }
        return result;
    }
}
