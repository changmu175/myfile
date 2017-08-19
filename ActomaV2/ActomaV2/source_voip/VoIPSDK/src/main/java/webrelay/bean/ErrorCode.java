package webrelay.bean;

/**
 * Created by admin on 16/4/12.
 */
public class ErrorCode {

    public static final String SUCCESS="200";  //成功

    public static final String NOT_WORKING="550";//服务器处于非Working状态

    public static final String RESULT_NOT_EXISITS="551";//请求结果不存在

    public static final String PARAMS_ERROR="552";//参数错误

    public static final String INNER_ERROR="553";//内部错误

    public static final String VOIP_SEVER_ERROR="800";//VOIP服务器发生异常

    public static final String VOIP_NOT_FRIENDS="801";//MXS认证失败，包括好友关系、集团好友等

    public static final String GET_DEVICE_ERROR="802";//

    public static final String MX_SEVER_ERROR="803";//连接密信服务器失败

    public static final String PM_ERROR="804";//连接PM失败


    public static final String VOIP_CENTER_ERROR="805";//连接voipcenter失败

    public static final String USER_OFFLINE="806";//用户离线

    public static final String USER_STATE_ERROR="807";//获取用户状态失败


}
