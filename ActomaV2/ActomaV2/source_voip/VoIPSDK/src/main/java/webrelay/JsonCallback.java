package webrelay;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import webrelay.bean.FailureBase;
import webrelay.bean.SuccessBase;
import webrelay.bean.TicketErrorBase;

/**
 * Created by guoyaxin on 2016/6/2.
 */
public abstract class JsonCallback<S extends SuccessBase,F extends FailureBase,T extends TicketErrorBase> {

    public abstract void onNetworkError(Request request, Exception e);
    public abstract void onNetworkError(Exception e);
    public abstract void onSuccess(S s);

    public abstract void onFailure(F f) ;

    public abstract void onTicketError(T t);


    Type getSuccessType(){
        return getType(0);
    }

    Type getFailureType(){
       return getType(1);
    }

    Type getTicketFailureType(){
        return getType(2);
    }


    //获取Json类型，因为数据可能是集合也可能是Object
    private Type getType(int index){
        Type type=((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[index];
        if (type instanceof Class){
            return type; //如果是Object直接返回
        }else {
            return new TypeToken<T>(){}.getType();//如果是集合，获取集合的类型map或list
        }
    }

}
