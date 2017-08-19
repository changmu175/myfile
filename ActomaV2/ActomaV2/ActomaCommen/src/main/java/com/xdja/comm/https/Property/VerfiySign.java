package com.xdja.comm.https.Property;

/**
 * Created by gbc on 2016/10/21.
 */
public enum VerfiySign {
    /**
     *
     */
    SIGN(0),

    /**
     * get
     */
    NOSIGN(1);

    private int type;

    public int getType() {
        return type;
    }
    VerfiySign(int type) {// modified by ycm for lint
        this.type = type;
    }
}
