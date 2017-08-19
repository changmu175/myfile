package com.xdja.imp.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guorong on 2017/1/3.
 * 用于保存文件下载过程中百分比
 */
public class TranslateInfoUtil {
    private static Map<Long , Integer> translateInfos;
    private static Map<Long , Integer> getTranslateInfos(){
        if(translateInfos == null){
            synchronized (TranslateInfoUtil.class){
                translateInfos = new ConcurrentHashMap<>();
            }
        }
        return translateInfos;
    }

    public static void putInfo(long msgId , int percent){
        getTranslateInfos().put(msgId , percent);
    }

    public static int getPercent(long msgId){
        if(getTranslateInfos().containsKey(msgId)){
            return getTranslateInfos().get(msgId);
        }else{
            return 0;
        }
    }

    public static void remove(long msgId){
        if(getTranslateInfos().containsKey(msgId)){
            getTranslateInfos().remove(msgId);
        }
    }
}
