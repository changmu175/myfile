package com.xdja.imp.data.error;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp_data.R;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/21</p>
 * <p>Time:16:29</p>
 */
public class OkIMMatcher implements OkMatcher<OkIMException> {

    //add by zya
    private static final String EXCEPTION_HINT = ActomaController.getApp().getString(R.string.im_decrpto_error);

    //fix bug 4151 , by zya 20160918
    private static final String FAILED_CODE = "-1";

    private static final String FAILED_VOICE_HINT = ActomaController.getApp().getString(R.string.im_record_time_short);
    //end
    public OkIMMatcher(){}

    //modify by zya@xdja.com,fix OkIMException problem,20160909
    @Nullable
    @Override
    public String match(@Nullable OkIMException exception) {

        //modify by zya@xdja.com,fix bug 4151.当点击频繁的时候，会报出失败，参数校验失败的提示。修改为时间过短.20160918
        if(exception != null && FAILED_CODE.equals(exception.getOkCode().trim())){// modified by ycm for lint 2017/02/16
            return FAILED_VOICE_HINT;
        }//end

        return exception != null && !TextUtils.isEmpty(exception.getOkMessage())? exception.getOkMessage() : EXCEPTION_HINT;
    }//end
}
