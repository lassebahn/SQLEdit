package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * Event-Klasse für das Einfügen eines Feldes in eine SQL-Abfrage.
 * @author Lasse Schöttner
 */
public class EventEinfuegenFeldSql2 extends ApplicationEvent {
    private final int servernr;
    private final String sql;
    private final String feld;
    private final int cursor;
    private static final long serialVersionUID = 1L;

    public EventEinfuegenFeldSql2(Object source, int servernr, String sql, String feld, int cursor) {
        super(source);
        this.servernr = servernr;
        this.sql = sql;
        this.feld = feld;
        this.cursor = cursor;
   }

    public int getServernr() {
		return servernr;
	}
    
	public String getSql() {
		return sql;
	}

	public String getFeld() {
		return feld;
	}
	
	public int getCursor() {
		return cursor;
	}
}