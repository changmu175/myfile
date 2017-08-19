package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class SendMsgParams extends BaseParams {

    private int mode;

    private CallSession content;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public CallSession getContent() {
        return content;
    }

    public void setContent(CallSession content) {
        this.content = content;
    }
}
