package util;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import webrelay.GsonUtil;

/**
 * Created by admin on 16/4/8.
 */
public class JsonUtil {

    @SuppressLint("TypeParameterExtendsObject")
    public static <T extends Object> T jsonToObject(String json, Class<T> cls)  {

        return GsonUtil.getInstance().jsonToObject(json,cls);
    }


    public static <T> T jsonToObject(String json, Type type)  {

        return GsonUtil.getInstance().jsonToObject(json,type);
    }


    public static JSONObject getJsonObject(String json){
        JSONObject jsonObject=null;
        try {
            jsonObject=new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
