package util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class SPUtil{


    private static final String ACCOUN_ID="ACCOUNT_ID";
    private static final String SP_NAME_ACCOUNT="SP_NAME_ACCOUNT";


    private static final String VOIP_PUSH="VOIP_PUSH";
    //推送订阅主题
    private static final String VOIP_TOPIC="VOIP_TOPIC";

    //主叫VOIP服务器地址
    private static final String VOIP_LOCAL_ADDR="VOIP_LOCAL_ADDR";

    //来电的VOIP服务器地址
    private static final String VOIP_INCOMING_ADDR = "VOIP_INCOMING_ADDR";



    public static String getAccount(Context cxt){
      return getAccountSP(cxt).getString(ACCOUN_ID,null);
    }

    public static void saveAcctout(Context cxt,String account){
        SharedPreferences.Editor editor=getAccountSP(cxt).edit();
        editor.putString(ACCOUN_ID,account);
        editor.apply();
    }


    public static void saveIncomingAddr(Context cxt, String addr) {

    }

    public static void saveVoIPLocalAddr(Context cxt, String addr){
        SharedPreferences.Editor editor=getVoipSP(cxt).edit();
        editor.putString(VOIP_LOCAL_ADDR,addr);
        editor.apply();
    }

    public static String getVoIPLocalAddr(Context cxt){
        return getVoipSP(cxt).getString(VOIP_LOCAL_ADDR,null);
    }


    public static void saveVoIPIncomingAddr(Context cxt, String addr){
        SharedPreferences.Editor editor=getVoipSP(cxt).edit();
        editor.putString(VOIP_INCOMING_ADDR, addr);
        editor.apply();
    }

    public static String getVoIPIncomingAddr(Context cxt){
        return getVoipSP(cxt).getString(VOIP_INCOMING_ADDR,null);
    }


    public static void saveVoipTopic(Context cxt,String topic){
        SharedPreferences.Editor editor=getVoipSP(cxt).edit();
        editor.putString(VOIP_TOPIC,topic);
        editor.apply();
    }

    public static String getVoipTopic(Context cxt){
        return getVoipSP(cxt).getString(VOIP_TOPIC,null);
    }

    private static SharedPreferences getVoipSP(Context cxt){
        return getSharePreference(cxt,VOIP_PUSH);
    }

    private static SharedPreferences getAccountSP(Context cxt){
        return getSharePreference(cxt,SP_NAME_ACCOUNT);
    }
    private static SharedPreferences getSharePreference(Context  cxt,String name){
        return  cxt.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

}
