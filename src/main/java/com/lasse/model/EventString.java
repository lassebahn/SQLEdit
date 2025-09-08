package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * @author Lasse Schöttner
 * 
 */
public class EventString extends ApplicationEvent {
    private final String message;
    private final int servernr;

    public EventString(Object source, String message, int servernr) {
        super(source);
        this.message = message;
        this.servernr = servernr;
    }

    public String getMessage() {
        return message;
    }
    
    public int getServernr() {
		return servernr;
	}
}