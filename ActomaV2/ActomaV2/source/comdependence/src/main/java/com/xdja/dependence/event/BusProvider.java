package com.xdja.dependence.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * <p>Summary:事件总线</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.eventbus</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/19</p>
 * <p>Time:18:21</p>
 */
public class BusProvider {

    private Bus mainBus;

//    @Inject
    public BusProvider(){
        this.mainBus = new Bus(ThreadEnforcer.ANY);
    }

    public void register(Object object){
        this.mainBus.register(object);
    }

    public void unregister(Object object){
        this.mainBus.unregister(object);
    }

    public void post(Object event){
        this.mainBus.post(event);
    }

    public Bus getMainBus() {
        return mainBus;
    }
}
