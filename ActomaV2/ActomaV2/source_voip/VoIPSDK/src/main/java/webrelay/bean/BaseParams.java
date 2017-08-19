package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class BaseParams {
    protected String flagid;
    protected String appname;
    protected String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }
}
