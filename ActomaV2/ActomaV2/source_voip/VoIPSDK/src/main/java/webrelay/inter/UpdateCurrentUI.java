package webrelay.inter;

import com.csipsimple.api.MediaState;

import webrelay.bean.CallSession;

/**
 * Created by guoyaxin on 2016/1/4.
 */
public interface UpdateCurrentUI {

    void updateMediaState(MediaState mediaState);

    void updateCallState(CallSession callSession);

    // 更新一些异常状态的提示
    void updateStatusText(CallSession callSession);

}
