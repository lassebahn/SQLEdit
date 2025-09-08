package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * Event-Klasse, die ausgelöst wird, wenn SQL-Historie gelesen wird.
 * @author Lasse Schöttner
 */
public class EventSqlHistRead extends ApplicationEvent {
    private final int servernr;

    public EventSqlHistRead(Object source, int servernr) {
        super(source);
        this.servernr = servernr;
    }

    public int getServernr() {
		return servernr;
	}
}