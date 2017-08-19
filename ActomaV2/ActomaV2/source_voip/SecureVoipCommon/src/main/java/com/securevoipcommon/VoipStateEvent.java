package com.securevoipcommon;

/**
 * Created by gbc on 2015/7/27.
 */
public class VoipStateEvent {
    public final int VOIP_REG_FAILED_TICKET_VALID = 1;
    public final int VOIP_REG_SUCCESS = 2;
    public final int VOIP_UNREG_SUCCESS = 3;

    private int state = 0;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
}
