package webrelay.bean;

/**
 * Created by xjq on 15-12-9.
 */
public class ResponseBean {
    private int id;
    private String sid;
    private int exp;
    private String st;
    private String con;

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSt() {

        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

}
