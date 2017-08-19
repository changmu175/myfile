package webrelay.bean;

/**
 * Created by guoyaxin on 2016/6/2.
 */
public class TicketError implements TicketErrorBase{

    private String hostId;
    private String requestId;
    private String errCode;
    private String message;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostId() {

        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}
