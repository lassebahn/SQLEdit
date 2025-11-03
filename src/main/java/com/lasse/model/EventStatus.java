package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * Status Datenbank-Verbindung
 * @author Lasse Schöttner
 * 
 */
public class EventStatus extends ApplicationEvent {
    private final String status;
    private final int servernr;

    public EventStatus(Object source, String status, int servernr) {
        super(source);
        this.status = status;
        this.servernr = servernr;
    }

    public String getStatus() {
        return status;
    }
    
    public int getServernr() {
		return servernr;
	}
}