package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class FailedBean extends Bean implements FailureBase {

    private ErrorBean error;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }
}
