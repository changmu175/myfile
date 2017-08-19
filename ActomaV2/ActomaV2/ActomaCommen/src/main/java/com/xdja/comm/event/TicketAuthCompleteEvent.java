package com.xdja.comm.event;

/**
 * Created by gbc on 2016/10/21.
 */
public class TicketAuthCompleteEvent {
    /**
     * 新的ticket
     */
    private String newTicket;

    public String getNewTicket() {
        return newTicket;
    }

    public void setNewTicket(String newTicket) {
        this.newTicket = newTicket;
    }
}
