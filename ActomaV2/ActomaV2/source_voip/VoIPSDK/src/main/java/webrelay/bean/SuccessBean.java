package webrelay.bean;

/**
 * Created by guoyaxin on 2015/12/4.
 */
public class SuccessBean extends Bean implements SuccessBase{

    private BaseResult result;

    public BaseResult getResult() {
        return result;
    }

    public void setResult(BaseResult result) {
        this.result = result;
    }
}
