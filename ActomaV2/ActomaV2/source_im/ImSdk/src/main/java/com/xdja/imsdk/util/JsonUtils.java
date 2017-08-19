package com.xdja.imsdk.util;

import com.xdja.google.gson.Gson;
import com.xdja.google.gson.JsonSyntaxException;
import com.xdja.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.xdja.google.gson.reflect.TypeToken;
import com.xdja.imsdk.exception.ImSdkException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：                 <br>
 * 创建时间：2016/11/16 15:39  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class JsonUtils {
    private static Gson gson = new Gson();
    /**
     * 在JSON对象中读取键key所对应的字符串
     * @param jsonObject 解析的JSON对象
     * @param key JSON的键值
     * @return String 返回键key所对应的字符串
     */
    public static String getString(JSONObject jsonObject, String key) {
        String value = "";
        try {
            if (jsonObject.has(key)) {
                value = jsonObject.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 在JSON对象中读取键key所对应的整数
     * @param jsonObject 解析的JSON对象
     * @param key JSON的键值
     * @return int 返回键key所对应的整数
     */
    public static int getInt(JSONObject jsonObject, String key) {
        int value = 0;
        try {
            value = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Gson getGson() {
        return gson;
    }

    public static <T> T mapGson(String json, Class<T> cls) {
        T result = null;
        try {
            result = gson.fromJson(json, TypeToken.get(cls).getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

}
