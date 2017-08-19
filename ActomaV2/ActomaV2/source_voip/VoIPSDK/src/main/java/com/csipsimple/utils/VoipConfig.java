package com.csipsimple.utils;

import android.content.Context;

import com.securevoip.utils.AssetsProperties;


/**
 * Created by wyc on 2015/2/10.
 */
public class VoipConfig extends AssetsProperties {

    @Property public String sip_server_port;


    public VoipConfig(Context context) {
        super(context,"voipconfig.properties");
    }
}
