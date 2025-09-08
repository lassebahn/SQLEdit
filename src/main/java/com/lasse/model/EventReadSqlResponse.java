package com.lasse.model;

import java.io.File;

import org.springframework.context.ApplicationEvent;


/**
 * Event-Klasse für das Lesen von SQL-Abfragen. Antwort-Event.
 * @author Lasse Schöttner
 */
public class EventReadSqlResponse extends ApplicationEvent {
    private final int servernr;
    private final String sql;
    private static final long serialVersionUID = 1L;

    public EventReadSqlResponse(Object source, int servernr, String sql) {
        super(source);
        this.servernr = servernr;
        this.sql = sql;
   }

    public int getServernr() {
		return servernr;
	}
    
   	public String getSql() {
		return sql;
	}
	
}