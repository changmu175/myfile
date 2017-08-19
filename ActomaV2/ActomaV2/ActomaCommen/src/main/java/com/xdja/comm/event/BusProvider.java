package com.xdja.comm.event;

import com.squareup.otto.Bus;
import com.xdja.comm.server.ActomaController;
import com.xdja.frame.AndroidApplication;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.server</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/10</p>
 * <p>Time:9:20</p>
 */
public class BusProvider {

    public static Bus getMainProvider() {
        return ((AndroidApplication) ActomaController.getApp())
                .getApplicationComponent().busProvider().getMainBus();
    }

}
