package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class BaseResult {

    private String desc;
    private  int flag;
    private String flagid;
    private String user;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
