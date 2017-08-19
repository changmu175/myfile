package webrelay;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;

import util.EncDecUtil;
import webrelay.bean.BaseParams;
import webrelay.bean.BaseWebBean;
import webrelay.bean.CallSession;
import webrelay.bean.FailedBean;
import webrelay.bean.GetAddressParams;
import webrelay.bean.SendMsgParams;
import webrelay.bean.SuccessBean;
import webrelay.bean.TicketError;
import webrelay.bean.VOIPGetAddress;
import webrelay.bean.VOIPSendMsg;


/**
 * Created by guoyaxin on 2015/12/4.
 */
public class VOIPBase {

     private static final String TAG = "VOIPBase";

     private static String COMMON_PATH = "";

     private static String SCHEMA = "https";

     private static String DEFAULT_PATH = "11.12.112.249:6699";


     private static String PATH_APPEND = "/webrelay/api";

     //发送消息的方法名
     private static final String METHOD_SEND_MSG = "voipsendmsg";

     //获取服务器地址的方法名
     private static final String METHOD_GET_ADDRESS = "voipgetaddr";

     //检测当前用户是否在线
     private static final String METHOD_CHECK_ONLINE = "voipcheckonline";


     private static VOIPBase voipBase = new VOIPBase();

     private static Context mCxt;

     public static VOIPBase getInstance() {
          mCxt = ActomaController.getApp();
          //获取Voip服务端URL
          COMMON_PATH = getVoipServerConf();
          return voipBase;
     }


     public void sendMsg(Context cxt, CallSession callSession, String type,String ticket,
                    JsonCallback<SuccessBean,FailedBean,TicketError> jsonCallback ) {

          callSession.setType(type);

          String src = callSession.getSrc();
          String user = callSession.getUser();


          /** 20161011-mengbo-start: 从缓存中获取加密秘钥 **/
          if(type.equals("VNCL")){
               callSession.setSecretkey(EncDecUtil.getCkmsSecretKeyString());
          }
          /** 20161011-mengbo-end **/

          SendMsgParams sendMsgParams = new SendMsgParams();
          sendMsgParams = initParams(sendMsgParams, callSession.getUser());
          sendMsgParams.setContent(callSession);
          sendMsgParams.setMode(4);

          VOIPSendMsg voipSendMsg = new VOIPSendMsg();
          voipSendMsg = initBaseBean(voipSendMsg, METHOD_SEND_MSG);
          voipSendMsg.setParams(sendMsgParams);

          String params = transferObjectToJson(voipSendMsg);

          LogUtil.getUtils(TAG).d("sendMsg:" + params);

          OKHttp3Util.getInstance(cxt).post(COMMON_PATH, params,ticket, jsonCallback);
     }

     /**
      * 获取服务器地址
      *
      * @param cxt
      * @param user 主叫号码
      */

     public void getAddress(Context cxt, String user,String ticket,
                            JsonCallback<SuccessBean,FailedBean,TicketError> jsonCallback) {
          GetAddressParams getAddressParams = new GetAddressParams();
          getAddressParams = initParams(getAddressParams, user);
          getAddressParams.setIp("1111");

          VOIPGetAddress voipGetAddress = new VOIPGetAddress();
          voipGetAddress = initBaseBean(voipGetAddress, METHOD_GET_ADDRESS);
          voipGetAddress.setParams(getAddressParams);

          String params = transferObjectToJson(voipGetAddress);

          LogUtil.getUtils(TAG).d("getAddress" + params);
          OKHttp3Util.getInstance(cxt).post(COMMON_PATH, params,ticket ,jsonCallback);
     }

     /**
      * 公共部分初始化
      *
      * @param t
      * @param method
      * @param <T>
      * @return
      */
     private static <T extends BaseWebBean> T initBaseBean(T t, String method) {
          t.setId("1");
          t.setJsonrpc("2.0");
          t.setMethod(method);
          return t;
     }


     private static <M extends BaseParams> M initParams(M m, String user) {
          m.setAppname("voipserver");
          m.setUser(user);
          m.setFlagid("22222222");
          return m;
     }


     /**
      * 将对象转化为Json
      *
      * @param object
      * @return
      */
     private static String transferObjectToJson(Object object) {
//        try {
//            String json=JacksonUtil.getInstance().objectToJson(object);
//            return json;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
          String json = GsonUtil.getInstance().objectToJson(object);
          return json;
     }

    /**
     * 获取voip服务器Url，先从应用框架配置信息中获取，没有则获取本地配置信息
     * @return url
     */
     private static String getVoipServerConf() {
          String url = null;

          url = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("voipUrl");
          if (TextUtils.isEmpty(url)) {
               ConfigurationServer configurationServer = ConfigurationServer.getAssetsConfig(mCxt, "webrelay.properties");
               String NPSWebApiServiceIp = "";
               String port = "";
               String apiUrl = "";
               if (configurationServer != null) {
                    NPSWebApiServiceIp = configurationServer.read("NPSWebApiServiceIp", "", String.class);
                    port = configurationServer.read("NPSWebApiServicePort", "", String.class);
                    apiUrl = configurationServer.read("ApiUrl","",String.class);
                    if ( NPSWebApiServiceIp.equals("") || port.equals("")) {
                         url = DEFAULT_PATH;
                    } else {
                         url = NPSWebApiServiceIp + ":" + port;
                    }

                    if (apiUrl.equals("")) {
                         url += PATH_APPEND;
                    } else {
                         url += apiUrl;
                    }
                    url = SCHEMA + "://" + url;
                    LogUtil.getUtils(TAG).d("VoipUrl"+url);
               }
          }
          return url;
     }

}
