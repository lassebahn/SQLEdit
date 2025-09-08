package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * Event-Klasse für das Einfügen eines Feldes in eine SQL-Abfrage. Antwort-Event.
 * @author Lasse Schöttner
 */
public class EventEinfuegenFeldSqlResponse extends ApplicationEvent {
    private final int servernr;
    private final String sql;
    private final int cursor;
    private static final long serialVersionUID = 1L;

    public EventEinfuegenFeldSqlResponse(Object source, int servernr, String sql, int cursor) {
        super(source);
        this.servernr = servernr;
        this.sql = sql;
        this.cursor = cursor;
   }

    public int getServernr() {
		return servernr;
	}
    
	public String getSql() {
		return sql;
	}

	public int getCursor() {
		return cursor;
	}
}