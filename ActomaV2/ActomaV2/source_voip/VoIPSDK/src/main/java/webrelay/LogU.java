package webrelay;
import com.xdja.dependence.uitls.LogUtil;


/**
 * Created by admin on 16/3/17.
 */
public class LogU {

        public static void LogVOIPApi(String TAG,String msg){
            LOG("VOIPApi",TAG,msg);
        }


        public static void LogVOIPBase(String TAG,String msg){
            LOG("VOIPBase",TAG,msg);
        }


        public static void LogVOIPPushOnLine(String TAG,String msg){
            LOG("VOIPPushOnLine",TAG,msg);
        }


        private static void LOG(String TG,String TAG,String msg){
            LogUtil.getUtils().e(TG+" "+" "+TAG+msg);
        }

}
