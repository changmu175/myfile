package util;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by MengBo on 2017/2/22.
 */
public class BuildConfigHelper {
    public static final String APPLICATION_ID = (String)getBuildConfigValue("APPLICATION_ID");

    private static Object getBuildConfigValue(String fieldName){
        try{
            Class c = Class.forName("com.xdja.presenter_mainframe.BuildConfig");
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);

            Log.e("mb","#-#-#-#  f.get(null):"+f.get(null));

            return f.get(null);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
