package com.xdja.imp.messageNotification;


import java.util.HashMap;
import java.util.Map;


/**
 * Created by liyingqing on 15-8-8.
 */
public class ChatNotifiSetedCache {

    /**
     * 单人消息免打扰设置集合
     * key:会话(string)；value:是否免打扰值(boolean)
     */
    private static final Map<String,Boolean> settingSingleMap = new HashMap<>();

    public static boolean getSettingValueByTalkId(String key ){
        boolean result = true;
        if (!settingSingleMap.containsKey(key)) {
            settingSingleMap.put(key,true);
        } else {
            result = settingSingleMap.get(key);
        }
        return result;
    }



    /**
     * 保存缓存记录
     * @param key     key:会话(string)；
     * @param value   value:是否免打扰值(boolean)
     */
    public  static void setSettingValue(String key,Boolean value){
            settingSingleMap.put(key, value);
    }
}
