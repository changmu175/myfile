package com.xdja.presenter_mainframe.enc3rd.utils;

import java.util.HashMap;

/**
 * Created by THZ on 2016/6/23.
 * 关于第三方短信的配置
 */
public class ThirdEncAppProperty {



    /**
     * 源生短信
     */
    public static final String ANDROID_MMS = "com.android.mms";

    /**
     * 微信
     */
    public static final String TENCENT_MM = "com.tencent.mm";

    /**
     * QQ
     */
    public static final String TENCENT_QQ = "com.tencent.mobileqq";

    /**
     * 钉钉
     */
    public static final String RIMET = "com.alibaba.android.rimet";

    /**
     * 钉钉
     */
    public static final String MOMO = "com.immomo.momo";

    /**
     * 酷派包名
     */
    public static final String YULONG_MMS = "com.yulong.coolmessage";

    /**
     * 酷派拉起activity的laucher
     */
    public static final String YULONG_MMS_LAUNCHER = "com.yulong.android.mms.ui.MmsConversationListActivity";

    /**
     * 短信应用的配置
     */
    public static HashMap<String, String> mmsHash = new HashMap<String, String>(){{put(YULONG_MMS, YULONG_MMS_LAUNCHER);
        put(ANDROID_MMS, "");}};


}
