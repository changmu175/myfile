
package com.csipsimple.pjsip.sipclf;

import android.content.Context;

import com.csipsimple.api.SipProfile;
import com.csipsimple.pjsip.PjSipService.PjsipModule;

import org.pjsip.pjsua.pjsua;

public class SipClfModule implements PjsipModule {

    private static final String THIS_FILE = "SipClfModule";
    private boolean enableModule = false;

    public SipClfModule() {
    }

    @Override
    public void setContext(Context ctxt) {
        // TODO : set enableModule and settings in respect with settings
        
    }

    @Override
    public void onBeforeStartPjsip() {
        if(enableModule ) {
            int status = pjsua.sipclf_mod_init();
        }
    }

    @Override
    public void onBeforeAccountStartRegistration(int pjId, SipProfile acc) {
    }

    @Override
    public void release() {

    }
}
