package webrelay;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson 解析json数据
 * Created by admin on 16/3/18.
 */
public class GsonUtil {


    private GsonUtil(){
        gson=new Gson();
    }

    private Gson gson=null;
    private static GsonUtil gsonUtil=null;

    public static GsonUtil getInstance(){
        if (gsonUtil==null){
            gsonUtil=new GsonUtil();
        }

        return gsonUtil;
    }


    /**
     * 将json数据转化为JavaBean对象
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    @SuppressLint("TypeParameterExtendsObject")
    public <T extends Object> T jsonToObject(String json,Class<T> tClass){
        T t= gson.fromJson(json,tClass);
        return t;
    }

    public <T> T jsonToObject(String json,Type type){
       return gson.fromJson(json,type);
    }

    /**
     * 将JavaBean对象转化为json数据
     * @param obj
     * @return
     */
    public String objectToJson(Object obj){
       return gson.toJson(obj);
    }

}
