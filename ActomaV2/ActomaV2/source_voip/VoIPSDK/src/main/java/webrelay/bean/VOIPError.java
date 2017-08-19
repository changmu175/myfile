package webrelay.bean;


/**
 * Created by guoyaxin on 2016/1/6.
 */
public class VOIPError {

    private ErrorType errorType;

    public VOIPError(ErrorType errorType){

        this.errorType=errorType;

    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType{

        ERROR_INITINAL,
        ERROR_CALLING,
        ERROR_REJECT
    }
}
