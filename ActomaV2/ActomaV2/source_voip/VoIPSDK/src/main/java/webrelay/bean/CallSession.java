package webrelay.bean;


import java.io.Serializable;
import java.util.Random;

/**
 * Created by guoyaxin on 2015/12/4.
 */

//@JsonIgnoreProperties(ignoreUnknown = true)
public class CallSession implements Serializable{

    //被叫号码
    private String user;

    //主叫号码
    private String src;

    //主叫号码-时间戳-随机两位数
    private String callid;

    //时间戳
    private String time;

    //通话密钥
    private String secretkey;

    //通话开始时间
//    @JsonIgnore
    private transient long startTime;

    //主叫、被叫,默认主叫
//    @JsonIgnore
    private transient Role role=Role.CALLER;

    //当前状态
//    @JsonIgnore
    private transient State state=State.INVALID;

    //错误码
//    @JsonIgnore
    private transient int lastErrCode = StatusCode.SUCCESS;

    public boolean isSipOnline() {
        return sipOnline;
    }

    public void setSipOnline(boolean sipOnline) {
        this.sipOnline = sipOnline;
    }

    // VoIP账号是否上线成功 xjq
    private boolean sipOnline = false;


    private boolean missed;

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    //错误码
    private String code = String.valueOf(StatusCode.SUCCESS);

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setCallid(String callid) {
        this.callid = callid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //消息类型
    private String type;

    public CallSession(String user, String src){
        this.user=user;
        this.src=src;
        this.time=getCurrentTime()+"";
        this.callid=this.src+"-"+this.time+"-"+String.format("%02d", getRandomNum());
        this.lastErrCode = StatusCode.SUCCESS;
    }

    public CallSession(){

    }

    //主叫注册的服务器和端口
    private String as;

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getUser() {
        return user;
    }

    public String getSrc() {
        return src;
    }

    public String getCallid() {
        return callid;
    }

    public String getTime() {
        return time;
    }

    //获取当前时间，以秒为单位
    private long getCurrentTime(){
       return System.currentTimeMillis()/1000;
    }

    //获取随机两位数
    private int getRandomNum(){
        Random random=new Random();
       return random.nextInt(100);
    }

    @Override
    public String toString() {
        return "user="+getUser()+" ;; src="+getSrc()+" ;; callid="+getCallid()+" ;; time="+getTime()+" as="+getAs()+" ";
    }

    public int getLastErrCode() {
        return lastErrCode;
    }

    public void setLastErrCode(int lastErrCode) {
        this.lastErrCode = lastErrCode;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }
}
